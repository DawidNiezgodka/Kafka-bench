package datasender

import commons.util.Logging

object Main extends Logging {

  def main(args: Array[String]): Unit = {
    println("args: " + args.mkString(" "))
    println("Starting Datasender...")
      setLogLevel(true)
      new DataDriver().run()
  }

  def setLogLevel(verbose: Boolean): Unit = {
    if (verbose) {
      Logging.setToInfo()
      println("DEBUG/VERBOSE mode switched on")
    }
  }
}
