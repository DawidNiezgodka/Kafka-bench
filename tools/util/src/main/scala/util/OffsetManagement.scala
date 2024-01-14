package util

import commons.config.Configs
import commons.util.Logging
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import java.util
import java.util.Properties

object OffsetManagement extends Logging {

  def getNumberOfMessages(topic: String, partition: Int): Long = {

    val properties = new Properties()
    properties.put("bootstrap.servers", Configs.benchmarkConfig.kafkaBootstrapServers)
    properties.put("group.id", java.util.UUID.randomUUID().toString)
    properties.put("key.deserializer", classOf[StringDeserializer])
    properties.put("value.deserializer", classOf[StringDeserializer])

    val consumer = new KafkaConsumer[String, String](properties)
    val topicAndPartition = new TopicPartition(topic, partition)
    val topicCollection = util.Arrays.asList(topicAndPartition)
    consumer.assign(topicCollection)
    consumer.seekToEnd(topicCollection)
    consumer.position(topicCollection.get(0))
  }

}
