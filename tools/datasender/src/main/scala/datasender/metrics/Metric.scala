package datasender.metrics

abstract class Metric {
  def getMetrics(): Map[String, String]
}
