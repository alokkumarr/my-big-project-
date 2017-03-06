package sncr.metadata.engine

/**
  * Created by srya0001 on 2/21/2017.
  */
object ProcessingResult extends Enumeration{

  val Success = Value (0, "success")
  val Error = Value (1, "error")
  val noDataFound = Value (100, "no data found")
  val NodeDoesNotExists = Value (200, "node does not exist")
  val Rejected = Value (2, "rejected")
  val OperationDeclined = Value (3, "declined")


}

