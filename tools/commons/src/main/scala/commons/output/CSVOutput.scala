package commons.output

import commons.util.Logging
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

import java.io.{File, PrintWriter}
import scala.io.Source

object CSVOutput extends Logging {

  val unitMap: Map[String, String] = Map(
    "record-send-rate" -> "ops/s",
    "request-latency-max" -> "ms",
    "request-latency-avg" -> "ms",
    "mb-send-rate" -> "MB/s",
    "msg-size" -> "bytes",
    "bytes-per-duration" -> "MB/s"
  )

  def writeResultsToJson(mergedResults: List[List[String]], resultPath: String, fileName: String,
                         totalBenchTime: String): Unit = {


    val dir = new File(s"$resultPath")

    var directoryExists = dir.exists()
    if (!directoryExists) {
      if (dir.mkdir()) {
        directoryExists = true
      } else {
        println("Dir creation error.")
      }
    }

    if (directoryExists) {
      val header = mergedResults.head
      val data = mergedResults.tail.head

      val zippedData = header.zip(data)
      zippedData.foreach {
        case (h, d) => println(s"$h: $d")
      }

      val j = createJson(zippedData, totalBenchTime)

      val f = new File(dir, fileName)
      val writer = new PrintWriter(f)
      try {
        writer.write(j.asJson.toString())
      } catch {
        case e: Exception =>
          println("Writing to JSON file failed.", e)
      } finally {
        writer.close()
      }
    }
  }

  case class Parametrization(
                              batchSize: Integer,
                              bufferMemorySize: Integer,
                              ack: Integer,
                              lingerTime: Integer,
                              readInRam: Boolean,
                              benchmarkDuration: Integer,
                              sendingInterval: Integer,
                              sendingIntervalTimeUnit: String
                            )

  case class Result(unit: String, value: Double, name: String)

  case class BenchInfo(
                        executionTime: String,
                        parametrization: Parametrization,
                        otherInfo: String
                      )

  case class Benchmark(
                        benchInfo: BenchInfo,
                        results: List[Result]
                      )


  def createJson(zippedData: List[(String, String)], totalBenchTime:String): Json = {
    val parametrizationKeys = Set(
      "batchSize",
      "bufferMemorySize",
      "acks",
      "lingerTime",
      "readInRam",
      "benchmarkDuration",
      "sendingInterval",
      "sendingIntervalTimeUnit")

    val paramMap = zippedData.filter { case (key, _) => parametrizationKeys.contains(key) }
      .map { case (key, value) => key -> value }
      .toMap

    val parametrization = Parametrization(
      paramMap("batchSize").toInt,
      paramMap("bufferMemorySize").toInt,
      paramMap("acks").toInt,
      paramMap("lingerTime").toInt,
      paramMap("readInRam").toBoolean,
      paramMap("benchmarkDuration").toInt,
      paramMap("sendingInterval").toInt,
      paramMap("sendingIntervalTimeUnit")
    )

    val results = zippedData.filterNot { case (key, _) => parametrizationKeys.contains(key) }
      .map { case (key, value) =>
        Result(
          unitMap.getOrElse(key, ""),
          value.toDouble,
          key
        )
      }

    val kafkaVersion = readVersionInfo("./versions/kafka_scala_version_file", "kafka_version")
    val scalaVersion = readVersionInfo("./versions/kafka_scala_version_file", "scala_version")
    val stringWithKafkaAndScalaVersion = s"Kafka version: $kafkaVersion, Scala version: $scalaVersion"
    val benchInfo = BenchInfo(totalBenchTime, parametrization, stringWithKafkaAndScalaVersion)

    val benchmark = Benchmark(benchInfo, results)
    benchmark.asJson
  }

  def readVersionInfo(filePath: String, key: String): String = {
    val fileContent = Source.fromFile(filePath).getLines().mkString("\n")
    fileContent.split("\n")
      .find(_.startsWith(key))
      .map(_.split(":").last.trim)
      .getOrElse("unknown")
  }
}
