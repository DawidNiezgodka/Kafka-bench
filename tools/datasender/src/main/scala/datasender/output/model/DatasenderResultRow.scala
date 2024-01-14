package datasender.output.model

import commons.output.model.ResultRow

case class DatasenderResultRow(configValues: ConfigValues, resultValues: ResultValues)
  extends ResultRow {

  override val header: List[String] = ConfigValues.header ++ ResultValues.header

  def this(m: Map[String, String]) = this(new ConfigValues(m), new ResultValues(m))

  override def toList(): List[String] = {
    configValues.toList ++ resultValues.toList()
  }

  override def toTable(): List[List[String]] = {
    List(header, configValues.toList ++ resultValues.toList())
  }
}

object DatasenderResultRow {
  val header: List[String] = ConfigValues.header ++ ResultValues.header
}
