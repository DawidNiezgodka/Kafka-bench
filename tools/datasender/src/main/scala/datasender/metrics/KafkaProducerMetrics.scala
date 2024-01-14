package datasender.metrics

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.MetricName
import org.apache.kafka.common.metrics.KafkaMetric
import commons.output.Util._

import scala.collection.JavaConverters._

class KafkaProducerMetrics(kafkaProducer: KafkaProducer[String, String]) extends Metric {
  val desiredMetrics = List("batch-size-avg", "record-send-rate", "records-per-request-avg",
    "request-latency-max", "request-latency-avg")

  override def getMetrics(): Map[String, String] = filterMetric(desiredMetrics)

  def filterMetric(desiredMetrics: List[String]): Map[String, String] = {
    kafkaProducer.metrics().asScala.foldLeft(Map.empty[String, String]) {
      case (acc, (metricName, metric)) => {
        val metricGroup = metricName.group()
        if (metricName.name() == "byte-total" && metricGroup == "producer-topic-metrics") {
          val topic = metricName.tags().getOrDefault("topic", "unknown")
          val metricValue = round(metric.value(), precision = 2).toString
          acc + (s"byte-total-$topic" -> metricValue)
        }
        else if (desiredMetrics.contains(metricName.name()) && metricGroup == "producer-metrics") {
          val key = metricName.name()
          val value = round(metric.value(), precision = 2).toString
          acc + (key -> value)
        } else {
          acc
        }
      }
    }
  }
}
