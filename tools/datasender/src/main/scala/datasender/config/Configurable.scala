package datasender.config

import commons.util.Logging

import java.io.File

trait Configurable extends Logging {
  def opToStr(v: Option[_], default: String = ""): String = v match {
    case Some(x) => x.toString
    case None if !default.isEmpty => s"DEFAULT($default)"
    case _ => "NO VALUE"
  }

  def isValidDataInputPath(dataInputPath: Option[String]): Boolean = {
    dataInputPath match {
      case Some(path) =>
        if (existsFilePath(path)) {
          true
        } else {
          println(s"The provided file path (path:${dataInputPath.get}) does not exist.")
          false
        }
      case _ =>
        println("Please provide a correct path to the data input file.")
        false
    }
  }

  def existsFilePath(path: String): Boolean = {
    val file = new File(path)
    file.exists && file.isFile
  }

  def checkGreaterOrEqual(attributeName: String, attributeValue: Long, valueMinimum: Int): Boolean = {
    val invalid = attributeValue < valueMinimum
    if (invalid) {
      println(s"Config invalid: $attributeName but must be >= $valueMinimum.")
    }
    !invalid
  }

  def checkAttributeOptionHasValue(attributeName: String, attributeValue: Option[String], conditionText: String = ""): Boolean = {
    val invalid = attributeValue.isEmpty || attributeValue.get.trim.isEmpty
    if (invalid) {
      println(s"Config invalid: $attributeName must be defined and not empty $conditionText")
    }
    !invalid
  }

}


