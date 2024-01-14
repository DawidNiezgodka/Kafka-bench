package datasender.output.writers

import org.apache.kafka.clients.producer.KafkaProducer
import commons.config.Configs
import commons.output.{CSVOutput, Tabulator}
import datasender.config._
import datasender.metrics.MetricHandler
import datasender.output.model.{ConfigValues, DatasenderResultRow, ResultValues}

import java.text.SimpleDateFormat
import java.util.Date

class DatasenderRunResultWriter() {

  val currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())

  def outputResults(kafkaProducer: KafkaProducer[String, String],
                    topicOffsets: Map[String, Long], expectedRecordNumber: Int,
                    totalBenchTime: String, averageMsgSize: Double): Unit = {

    val metricHandler = new MetricHandler(kafkaProducer, topicOffsets, expectedRecordNumber)
    var metrics = metricHandler.fetchMetrics()

    val recordSendRate = metrics("record-send-rate").toDouble
    val byteSendRate = recordSendRate * averageMsgSize
    val byteSendRateMB = byteSendRate / 1000000
    metrics += ("mb-send-rate" -> byteSendRateMB.toString)
    metrics += ("msg-size" -> averageMsgSize.toString)

    val configValues = ConfigValues.get(ConfigHandler.config, Configs.benchmarkConfig)
    val benchmarkDuration = configValues.benchmarkDuration
    val bytesPerDuration = calculateBytesPerDuration(metrics,benchmarkDuration) / 1000000

    metrics += ("bytes-per-duration" -> bytesPerDuration.toString)
    metrics.foreach { case (key, value) => println(s"Key: $key, Value: $value") }

    val resultValues = new ResultValues(metrics)
    val dataSenderResultRow = DatasenderResultRow(configValues, resultValues)
    val table = dataSenderResultRow.toTable()

    CSVOutput.writeResultsToJson(table, ConfigHandler.resultsPath, ConfigHandler.resultFileNameJson(currentTime),
      totalBenchTime)
    println(Tabulator.format(table))
  }

  def calculateBytesPerDuration(metrics: Map[String, String], benchmarkDurationStr: String): Double = {
    val benchmarkDuration = benchmarkDurationStr.toDouble
    val byteMetrics = metrics
      .filterKeys(_.startsWith("byte-total-"))
      .values
      .map(_.toDouble)
      .sum
    byteMetrics / benchmarkDuration
  }
}
