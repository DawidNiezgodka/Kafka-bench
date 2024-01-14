package datasender

import java.util.Properties
import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import commons.config.{Configs, QueryConfig}
import commons.util.Logging
import datasender.DataProducer.{benchmarkEndTime, benchmarkStartTime}
import datasender.config.{ConfigHandler, Configurable, DataReaderConfig, KafkaProducerConfig}
import datasender.output.writers.DatasenderRunResultWriter
import util.OffsetManagement

import scala.io.Source

object DataProducer {

  private val kafkaProducer = new KafkaProducer[String, String](createKafkaProducerProperties(ConfigHandler.config.kafkaProducerConfig))
  var benchmarkStartTime = -1L
  var benchmarkEndTime = -1L
  def createKafkaProducerProperties(kafkaProducerConfig: KafkaProducerConfig): Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Configs.benchmarkConfig.kafkaBootstrapServers)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.keySerializerClass.get)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfig.valueSerializerClass.get)
    props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfig.acks.get)
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfig.batchSize.get.toString)
    props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfig.lingerTime.toString)
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaProducerConfig.bufferMemorySize.toString)
    props
  }
}

class DataProducer(resultHandler: DatasenderRunResultWriter,
                   dataReaderConfig: DataReaderConfig,
                   sourceTopics: List[String],
                   sendingInterval: Int,
                   sendingIntervalTimeUnit: TimeUnit,
                   duration: Long,
                   durationTimeUnit: TimeUnit) extends Logging with Configurable {

  val numberOfInputTopics: Int = sourceTopics.length
  val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(numberOfInputTopics)
  var producerThreads: List[DataProducerThread] = List[DataProducerThread]()

  sourceTopics.foreach(sourceTopic => {
    println(s"Reading from topic: $sourceTopic")
    val sourceTopicSplit = sourceTopic.split(Configs.TopicNameSeparator)
    var dataInputFileIdx = sourceTopicSplit(sourceTopicSplit.length - 1).toInt - 1
    println(s"Reading from file index: $dataInputFileIdx")
    var dataReader: DataReader = null
    if (dataInputFileIdx == 2) {
      dataInputFileIdx = dataReaderConfig.dataInputPath.length - 1
      dataReader = new DataReader(
        Source.fromFile(dataReaderConfig.dataInputPath(dataInputFileIdx)),
        false,
        true)
    } else {
      println(s"Reading from file: ${dataReaderConfig.dataInputPath(dataInputFileIdx)}")
      dataReader = new DataReader(
        Source.fromFile(dataReaderConfig.dataInputPath(dataInputFileIdx)), dataReaderConfig.readInRam)
    }

    producerThreads = new DataProducerThread(
      this,
      dataReader,
      sourceTopic,
      duration,
      durationTimeUnit) :: producerThreads
  }
  )

  var ts: List[ScheduledFuture[_]] = _

  def shutDown(): Unit = {
    ts.foreach(t => t.cancel(false))
    println("There were {} producer threads: " + producerThreads.length)
    producerThreads.foreach(_.dataReader.close())
    getKafkaProducer.close()
    executor.shutdown()
    benchmarkEndTime = System.currentTimeMillis()
    val expectedRecordNumber = producerThreads.map(t => t.numberOfRecords).sum


    val averageMsgSize = producerThreads.map(t => t.accumulatedMsgSize).sum / producerThreads.map(t => t.messageCounter).sum
    println(s"Expected number of records: $expectedRecordNumber")
    println(s"ResultHandler: $resultHandler")

    val totalBenchTime = benchmarkEndTime - benchmarkStartTime
    val minutes = totalBenchTime / 60000
    val seconds = (totalBenchTime % 60000) / 1000

    val humanReadableBenchTime = s"${minutes}m ${seconds}s"
    resultHandler.outputResults(getKafkaProducer, getTopicOffsets, expectedRecordNumber, humanReadableBenchTime, averageMsgSize)
  }

  def execute(): Unit = {
    val initialDelay = 0
    benchmarkStartTime = System.currentTimeMillis()
    ts = producerThreads.map { thread =>
      executor.scheduleAtFixedRate(thread, initialDelay, sendingInterval, sendingIntervalTimeUnit)
    }
    val allTopics = sourceTopics.mkString(" ")
    println(s"Sending records to following topics: $allTopics")
  }

  def getKafkaProducer: KafkaProducer[String, String] = DataProducer.kafkaProducer

  def getTopicOffsets: Map[String, Long] = {
    sourceTopics.map(topic => {
      val currentOffset = OffsetManagement.getNumberOfMessages(topic, partition = 0)
      topic -> currentOffset
    }).toMap[String, Long]
  }
}
