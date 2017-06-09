package sncr.datalake.engine

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import sncr.datalake.engine.ExecutionType.ExecutionType
import sncr.datalake.handlers.AnalysisNodeExecutionHelper
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.engine.ProcessingResult

import scala.concurrent.Future

/**
  * Created by srya0001 on 6/8/2017.
  */
import scala.concurrent.ExecutionContext.Implicits.global

class AnalysisExecution(val an: AnalysisNode, val execType : ExecutionType) {


  var analysisNodeExecution : AnalysisNodeExecutionHelper = null
  var id : String = null
  var executionMessage : String = null
  var executionCode : Integer = -1

  var startTS : java.lang.Long = null

  def startExecution() : Unit =
  {
    try {
      analysisNodeExecution = new AnalysisNodeExecutionHelper(an)
      id = analysisNodeExecution.resId

      //TODO:: Maybe we need to move it some other place.
      analysisNodeExecution.loadObjects()
      startTS = analysisNodeExecution.getStartTS
      analysisNodeExecution.createAnalysisResultHeader()
    }
    catch{
      case t: Throwable => executionMessage = s"Could not start execution: ${Bytes.toString(an.getRowKey)}"; executionCode = ProcessingResult.Error.id
    }
    executionMessage = "success"; executionCode = ProcessingResult.Success.id
  }




  /**
    * Returns an identifier for the analysis execution
    */
  def getId: String = id


 /**
   * Returns the type tag that was provided with the analysis execution call, which would be "onetime", "scheduled" or “preview"
  */
  def getType : String = execType.toString

  /**
    * Returns the execution status, an enumeration of possible String values that includes at least "success" and “error"
    */
  def getStatus : String = ProcessingResult(executionCode).toString

  /**
    * // return an auxiliary human-readable explanation of the status, which would be mainly used to describe why an execution resulted in an "error” status
    */
  def getStatusMessage: String = executionMessage

  /**
    * Returns execution start timestamp as milliseconds since epoch
    */
  def getStartedTimestamp : java.lang.Long = analysisNodeExecution.startTS

  /**
    * Returns execution finished timestamp as milliseconds since epoch
    */
  def getFinishedTimestamp : java.lang.Long = analysisNodeExecution.finishedTS

  /**
    * Returns the rows of the query execution result, where V is the List<Map<…>> data structure suggested earlier
    *
    * @return
    */
  def getData : Future[java.util.List[java.util.Map[String, (String, Object)]]] = {
      Future {
        analysisNodeExecution.executeSQL()
        analysisNodeExecution.getExecutionData
      }
  }


}



object ExecutionType extends Enumeration{

  type ExecutionType = Value
  val onetime = Value(0, "onetime")
  val scheduled = Value(1, "scheduled")
  val preview = Value(2, "preview")


}