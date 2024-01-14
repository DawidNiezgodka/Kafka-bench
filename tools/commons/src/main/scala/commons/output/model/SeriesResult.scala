package commons.output.model

abstract class SeriesResult extends Result {
  override def toTable(): List[List[String]]
}
