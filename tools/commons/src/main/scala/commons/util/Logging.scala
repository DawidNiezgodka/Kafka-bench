package commons.util

import org.apache.log4j.{Level, Logger}

trait Logging {
  var logger: Logger = Logger.getLogger("kafkaBenchLogger")
}

object Logging {

  def setToInfo() {
    Logger.getRootLogger.setLevel(Level.DEBUG)
  }
}