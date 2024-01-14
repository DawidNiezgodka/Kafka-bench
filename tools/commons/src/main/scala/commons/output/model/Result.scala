package commons.output.model

abstract class Result {
  def toTable(): List[List[String]]
}
