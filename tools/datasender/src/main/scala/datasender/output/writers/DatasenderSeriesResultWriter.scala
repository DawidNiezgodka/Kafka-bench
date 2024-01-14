package datasender.output.writers

import commons.config.Configs
import commons.output.model.SeriesResult
import commons.output.writers.ResultWriter
import commons.util.Logging
import datasender.config.ConfigHandler
import datasender.output.model.{DatasenderResultRow, DatasenderSeriesResult}


class DatasenderSeriesResultWriter(inputFilesPrefix: String, resultsPath: String,
                                   outputFileName: String)
  extends ResultWriter(inputFilesPrefix, resultsPath, outputFileName) {

  override def getFinalResult(runResultMaps: List[Map[String, String]]): SeriesResult = {
    val rows: List[DatasenderResultRow] = runResultMaps.map(new DatasenderResultRow(_))
    new DatasenderSeriesResult(rows)
  }
}

object DatasenderSeriesResultWriter extends Logging {
  def main(args: Array[String]): Unit = {
    val inputFilesPrefix = Configs.benchmarkConfig.topicPrefix
    val resultsPath: String = ConfigHandler.resultsPath
    val outputFileName = s"Series_Result_$inputFilesPrefix.csv"

    val seriesResultWriter = new DatasenderSeriesResultWriter(
      inputFilesPrefix = inputFilesPrefix,
      resultsPath = resultsPath,
      outputFileName = outputFileName)

    seriesResultWriter.execute()
  }
}
