package datasender

import commons.config.Configs
import commons.util.Logging
import datasender.config._
import datasender.output.writers.DatasenderRunResultWriter

import scala.io.Source

class DataDriver extends Logging {


  private val config = ConfigHandler.config
  private val resultHandler = new DatasenderRunResultWriter()
  private val dataProducer = createDataProducer


  def run(): Unit = {
    dataProducer.execute()
  }

  def createDataProducer: DataProducer = {
    val sendingInterval = Configs.benchmarkConfig.sendingInterval
    val sendingIntervalTimeUnit = Configs.benchmarkConfig.getSendingIntervalTimeUnit
    val duration = Configs.benchmarkConfig.duration
    val durationTimeUnit = Configs.benchmarkConfig.getDurationTimeUnit
    new DataProducer(resultHandler, config.dataReaderConfig, Configs.benchmarkConfig.sourceTopics,
      sendingInterval, sendingIntervalTimeUnit, duration, durationTimeUnit)
  }
}
