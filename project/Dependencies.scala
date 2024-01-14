import sbt.*
import scala.util.Try
import scala.io.Source

object Dependencies {


  def readKafkaVersionFromFile(filePath: String): String = {
    Try {
      val source = Source.fromFile(filePath)
      val versionLine = source.getLines().find(_.startsWith("kafka_version: "))
      println(versionLine)
      source.close()
      versionLine.map(_.split(": ").last.trim).getOrElse("2.3.0")
    }.getOrElse("2.3.0")
  }

  val kafkaVersionFilePath = "./versions/kafka_scala_version_file"
  val kafkaVersion: String = readKafkaVersionFromFile(kafkaVersionFilePath)

  val kafkaClients: Seq[ModuleID] = Seq(
    "org.apache.kafka" % "kafka-clients" % kafkaVersion
  )

  val kafka: Seq[ModuleID] = Seq(
    "org.apache.kafka" %% "kafka" % kafkaVersion
  )

  val testUtils: Seq[ModuleID] = Seq(
    "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
    "org.mockito" %% "mockito-scala" % "1.5.13" % Test,
    "org.scalactic" %% "scalactic" % "3.0.4" % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test
  )

  val logging: Seq[ModuleID] = Seq(
    "log4j" % "log4j" % "1.2.14"
  )

  val pureConfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.11.1"
  )

  val scopt: Seq[ModuleID] = Seq(
    "com.github.scopt" %% "scopt" % "3.5.0"
  )

  val csv: Seq[ModuleID] = Seq(
    "com.github.tototoshi" %% "scala-csv" % "1.3.4"
  )

  val json: Seq[ModuleID] = Seq(
    "net.liftweb" %% "lift-json" % "3.0.1"
  )

}
