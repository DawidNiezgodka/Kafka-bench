package datasender.metrics

import org.apache.kafka.clients.producer.KafkaProducer
import commons.util.Logging

class MetricHandler(kafkaProducer: KafkaProducer[String, String], topicStartOffsets: Map[String, Long],
                    expectedRecordNumber: Long) extends Logging {


  def fetchMetrics(): Map[String, String] = {
    val kafkaProducerMetrics = new KafkaProducerMetrics(kafkaProducer)
    val kafkaProducerMetricsValues = kafkaProducerMetrics.getMetrics()

    val sendMetrics = new SendMetrics(topicStartOffsets, expectedRecordNumber)
    val sendMetricsValues = sendMetrics.getMetrics()

    kafkaProducerMetricsValues ++ sendMetricsValues
  }
}
