package datasender.config

import java.nio.file.{FileSystems, Path}
import commons.config.Configs
import commons.util.Logging
import pureconfig.loadConfig
import pureconfig.generic.auto._

object ConfigHandler extends Logging {
  val projectPath = System.getProperty("user.dir")
  val dataSenderPath = s"$projectPath/tools/datasender"
  val configName = "datasender.conf"
  val userConfigPath = s"$dataSenderPath/$configName"
  val resultsPath = s"$dataSenderPath/results"
  val config: Config = getConfig()

  def resultFileNameJson(currentTime: String): String = s"${Configs.benchmarkConfig.topicPrefix}_" +
    s"${Configs.benchmarkConfig.benchmarkRun}_$currentTime.json"

  private def getConfig(): Config = {
    if (!FileSystems.getDefault.getPath(userConfigPath).toFile.exists && FileSystems.getDefault.getPath(userConfigPath).toFile.isFile) {
      println(s"The config file '$userConfigPath' does not exist.")
      sys.exit(1)
    }

    val config = loadConfig[Config](FileSystems.getDefault.getPath(userConfigPath)) match {
      case Left(f) => {
        println(s"Invalid configuration for file $userConfigPath")
        println(f.toString)
        sys.exit(1)
      }
      case Right(conf) => conf
    }

    if (!config.isValid) {
      println(s"Invalid configuration:\n${config.toString}")
      sys.exit(1)
    }
    config
  }
}
