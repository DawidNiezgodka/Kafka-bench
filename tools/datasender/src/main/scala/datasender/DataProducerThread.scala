package datasender

import java.util.concurrent.TimeUnit

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import commons.util.Logging

class DataProducerThread(dataProducer: DataProducer,
                         val dataReader: DataReader,
                         topic: String,
                         duration: Long,
                         durationTimeUnit: TimeUnit)
  extends Runnable with Logging {

  var numberOfRecords: Int = 0
  val startTime: Long = currentTime
  val endTime: Long = startTime + durationTimeUnit.toMillis(duration)

  private var topicMsgIdList: Map[String, Int] = Map[String, Int]()

  def getNextMessageId(topic: String): Int = {
    var id = 0
    if (topicMsgIdList.contains(topic)) {
      id = topicMsgIdList(topic)
      id += 1
    }
    topicMsgIdList += (topic -> id)
    id
  }

  def currentTime: Long = System.currentTimeMillis()

  var messageCounter: Long = 0
  var accumulatedMsgSize: Long = 0

  def run() {

    val msg = dataReader.readRecord
    if (currentTime < endTime && msg.nonEmpty) {
      send(msg.get)
    } else {
      if (msg.isEmpty) {
        print(msg)
        println("Message was empty")
      }
      println(s"Start time: $startTime")
      println(s"End time: $endTime")
      val threadId = Thread.currentThread().getId
      println(s"Shutting down thread ID: $threadId")
      dataProducer.shutDown()
    }
  }

  def send(message: String): Unit = {
    sendToKafka(topic, message)
  }

  def sendToKafka(topic: String, message: String): Unit = {
    messageCounter += 1
    numberOfRecords +=1
    val msgWithIdAndTs = s"${getNextMessageId(topic)};$currentTime;$message"
    accumulatedMsgSize += msgWithIdAndTs.length
    val record = new ProducerRecord[String, String](topic, msgWithIdAndTs)
    dataProducer.getKafkaProducer.send(record)

  }
}
