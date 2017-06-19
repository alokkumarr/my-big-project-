package sncr.datalake.engine

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.engine.ExecutionStatus.ExecutionStatus
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

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisExecution].getName)


  var analysisNodeExecution : AnalysisNodeExecutionHelper = null
  var id : String = null
  var executionMessage : String = null
  var executionCode : Integer = -1
  var status : ExecutionStatus = ExecutionStatus.INIT
  var startTS : java.lang.Long = null

  def startExecution( persist: Boolean) : Unit =
  {
    try {
      analysisNodeExecution = new AnalysisNodeExecutionHelper(an)
      id = analysisNodeExecution.resId
      m_log debug s"Started execution, result ID: $id"
      status = ExecutionStatus.STARTED
      analysisNodeExecution.loadObjects()
      startTS = analysisNodeExecution.getStartTS
      m_log debug s"Loaded objects, Started TS: $startTS "
      status = ExecutionStatus.IN_PROGRESS
      analysisNodeExecution.executeSQL()
      if (persist){
        analysisNodeExecution.createAnalysisResult(null, null)
        status = ExecutionStatus.COMPLETED
      }

    }
    catch{
      case t: Throwable => {
        executionMessage = s"Could not start execution: ${Bytes.toString(an.getRowKey)}"
        executionCode = ProcessingResult.Error.id
        status = ExecutionStatus.FAILED
        m_log error (s"Could not start execution: ", t)
      }
    }
    executionMessage = "success";
    executionCode = ProcessingResult.Success.id
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
  def getStatus : ExecutionStatus = status

  /**
    * // return an auxiliary human-readable explanation of the status, which would be mainly used to describe why an execution resulted in an "error” status
    */
  def getExecMessage: String = executionMessage
  def getExecCode: Int = executionCode

  /**
    * Returns execution start timestamp as milliseconds since epoch
    */
  def getStartedTimestamp : java.lang.Long = analysisNodeExecution.startTS

  /**
    * Returns execution finished timestamp as milliseconds since epoch
    */
  def getFinishedTimestamp : java.lang.Long = analysisNodeExecution.finishedTS

  /**
    * Returns Futute with the query execution result
    *
    * @return List<Map<…>> data structure
    */
  def getData : Future[java.util.List[java.util.Map[String, (String, Object)]]] = {
      Future {
        analysisNodeExecution.getExecutionData
      }
  }

  /**
    * Returns the rows of the query execution result
    * @return List<Map<…>> data structure
    */
  def fetchData : java.util.List[java.util.Map[String, (String, Object)]] = {
      analysisNodeExecution.getExecutionData
  }

  /**
    * Returns the rows of the query execution result
    * @return List<Map<…>> data structure
    */
  def loadExecution(id : String) : java.util.List[java.util.Map[String, (String, Object)]] = {
    AnalysisNodeExecutionHelper.loadAnalysisResult(id)
  }


}


object ExecutionType extends Enumeration{

  type ExecutionType = Value
  val onetime = Value(0, "onetime")
  val scheduled = Value(1, "scheduled")
  val preview = Value(2, "preview")


}