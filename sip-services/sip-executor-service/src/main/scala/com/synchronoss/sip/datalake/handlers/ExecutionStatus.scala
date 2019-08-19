package sncr.datalake.handlers

object ExecutionStatus extends Enumeration{

  type ExecutionStatus = Value
  val INIT = Value(10,"Init")
  val STARTED = Value(1,"Execution started")
  val IN_PROGRESS = Value(2,"Execution is in progress")
  val FAILED = Value(-1,"Execution failed")
  val COMPLETED = Value(0,"Execution successfully completed")

}
