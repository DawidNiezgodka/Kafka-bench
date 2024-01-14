package datasender.output.model
import datasender.output.model.ResultValues._
import commons.output.Util._

object ResultValues {

  val RECORD_SEND_RATE = "record-send-rate"
  val REQUEST_LATENCY_MAX = "request-latency-max"
  val REQUEST_LATENCY_AVG = "request-latency-avg"
  val MB_SEND_RATE = "mb-send-rate"
  val MSG_SIZE = "msg-size"
  val BYTES_PER_DURATION = "bytes-per-duration"

  val header = List(
    RECORD_SEND_RATE,
    REQUEST_LATENCY_MAX,
    REQUEST_LATENCY_AVG,
    MB_SEND_RATE,
    MSG_SIZE,
    BYTES_PER_DURATION)

}

case class ResultValues(recordSendRate: Double,
                        requestLatencyMax: Double,
                        requestLatencyAvg: Double,
                        mbSendRate: Double,
                        msgSize: Double,
                        bytesPerDuration: Double) {

  def this(m: Map[String, String]) = this(
    m(RECORD_SEND_RATE).toDouble,
    m(REQUEST_LATENCY_MAX).toDouble,
    m(REQUEST_LATENCY_AVG).toDouble,
    m(MB_SEND_RATE).toDouble,
    m(MSG_SIZE).toDouble,
    m(BYTES_PER_DURATION).toDouble)



  def roundAndFormat(value: Double): String = {
    format(round(value, precision = 2))
  }

  def toList(): List[String] = {
    val fRecordSendRate = roundAndFormat(recordSendRate)
    val fRequestLatencyMax = roundAndFormat(requestLatencyMax)
    val fRequestLatencyAvg = roundAndFormat(requestLatencyAvg)
    val fMbSendRate = roundAndFormat(mbSendRate)
    val fMsgSize = roundAndFormat(msgSize)
    val fBytesPerDuration = roundAndFormat(bytesPerDuration)

    List(fRecordSendRate,
      fRequestLatencyMax,
      fRequestLatencyAvg,
      fMbSendRate,
      fMsgSize,
      fBytesPerDuration
    )
  }
}

