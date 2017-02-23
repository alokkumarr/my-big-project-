package sncr.metadata.engine

/**
  * Created by srya0001 on 2/21/2017.
  */
object ProcessingResult extends Enumeration{

  val Success = Value (0, "success")
  val Error = Value (1, "success")
  val noDataFound = Value (100, "success")
  val NodeDoesNotExists = Value (200, "success")
  val Rejected = Value (2, "success")

}
