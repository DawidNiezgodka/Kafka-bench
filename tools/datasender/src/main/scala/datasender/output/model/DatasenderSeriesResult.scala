package datasender.output.model

import commons.output.Util._
import commons.output.model.SeriesResult

class DatasenderSeriesResult(l: List[DatasenderResultRow]) extends SeriesResult {

  def toTable(): List[List[String]] = {

    val resultValues = l.map(_.resultValues)

    val mergedResultValues = ResultValues(
      recordSendRate = average(resultValues.map(_.recordSendRate)),
      requestLatencyMax = average(resultValues.map(_.requestLatencyMax)),
      requestLatencyAvg = average(resultValues.map(_.requestLatencyAvg)),
      mbSendRate = average(resultValues.map(_.mbSendRate)),
      msgSize = average(resultValues.map(_.msgSize)),
      bytesPerDuration = average(resultValues.map(_.bytesPerDuration))
    )

    println("mergedResultValues: " + mergedResultValues)

    val configValues = l.head.configValues
    val header: List[String] = ConfigValues.header ++ ResultValues.header
    List(header, configValues.toList ++ mergedResultValues.toList())
  }
}
