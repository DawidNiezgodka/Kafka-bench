package datasender.output.model

import commons.config.Configs.BenchmarkConfig
import datasender.config.Config

object ConfigValues {
  val BATCH_SIZE = "batchSize"
  val BUFFER_MEMORY_SIZE = "bufferMemorySize"
  val ACKS = "acks"
  val LINGER_TIME = "lingerTime"
  val READ_IN_RAM = "readInRam"
  val BENCHMARK_DURATION = "benchmarkDuration"
  val SENDING_INTERVAL = "sendingInterval"
  val SENDING_INTERVAL_TIMEUNIT = "sendingIntervalTimeUnit"

  val header = List(
    BATCH_SIZE,
    BUFFER_MEMORY_SIZE,
    ACKS,
    LINGER_TIME,
    READ_IN_RAM,
    BENCHMARK_DURATION,
    SENDING_INTERVAL,
    SENDING_INTERVAL_TIMEUNIT)

  def get(config: Config, benchmarkConfig: BenchmarkConfig): ConfigValues = {
    ConfigValues(
      batchSize = config.kafkaProducerConfig.batchSize.get.toString,
      bufferMemorySize = config.kafkaProducerConfig.bufferMemorySize.toString,
      acks = config.kafkaProducerConfig.acks.get,
      lingerTime = config.kafkaProducerConfig.lingerTime.toString,
      readInRam = config.dataReaderConfig.readInRam.toString,
      benchmarkDuration = benchmarkConfig.duration.toString,
      sendingInterval = benchmarkConfig.sendingInterval.toString,
      sendingIntervalTimeUnit = benchmarkConfig.sendingIntervalTimeUnit)
  }
}

import ConfigValues._

case class ConfigValues(batchSize: String,
                        bufferMemorySize: String,
                        acks: String,
                        lingerTime: String,
                        readInRam: String,
                        benchmarkDuration: String,
                        sendingInterval: String,
                        sendingIntervalTimeUnit: String) {

  def this(m: Map[String, String]) = this(
    m(BATCH_SIZE),
    m(BUFFER_MEMORY_SIZE),
    m(ACKS),
    m(LINGER_TIME),
    m(READ_IN_RAM),
    m(BENCHMARK_DURATION),
    m(SENDING_INTERVAL),
    m(SENDING_INTERVAL_TIMEUNIT))

  def toList: List[String] = {
    List(
      batchSize,
      bufferMemorySize,
      acks,
      lingerTime,
      readInRam,
      benchmarkDuration,
      sendingInterval,
      sendingIntervalTimeUnit)
  }
}
