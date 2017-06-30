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


  protected var analysisNodeExecution : AnalysisNodeExecutionHelper = null
  protected var id : String = null
  protected var executionMessage : String = null
  protected var executionCode : Integer = -1
  protected var status : ExecutionStatus = ExecutionStatus.INIT
  protected var startTS : java.lang.Long = null
  protected var finishTS : java.lang.Long = null

  def startExecution(sqlRuntime: String = null): Unit =
  {
    try {
      analysisNodeExecution = new AnalysisNodeExecutionHelper(an, sqlRuntime)
      id = analysisNodeExecution.resId
      m_log debug s"Started execution, result ID: $id"
      status = ExecutionStatus.STARTED
      analysisNodeExecution.loadObjects()
      analysisNodeExecution.setStartTime
      startTS = analysisNodeExecution.getStartTS
      m_log debug s"Loaded objects, Started TS: $startTS "
      status = ExecutionStatus.IN_PROGRESS
      execType match {
        case ExecutionType.scheduled => {
          analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.createAnalysisResult(null, null)
        }
        case ExecutionType.onetime => {
          analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.getAllData
        }
        case ExecutionType.preview => {
          analysisNodeExecution.executeSQL()
          analysisNodeExecution.getPreview()
        }
      }
      analysisNodeExecution.setFinishTime
      finishTS = analysisNodeExecution.finishedTS
      status = ExecutionStatus.COMPLETED
    }
    catch{
      case t: Throwable => {
        executionMessage = s"Could not start execution: ${Bytes.toString(an.getRowKey)}"
        executionCode = ProcessingResult.Error.id
        status = ExecutionStatus.FAILED
        m_log error (s"Could not start execution: ", t)
      }
    }
    executionMessage = "success"
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
  def getStartedTimestamp : java.lang.Long =  if (startTS == null ) -1L else startTS

  /**
    * Returns execution finished timestamp as milliseconds since epoch
    */
  def getFinishedTimestamp : java.lang.Long = if (finishTS == null ) -1L else finishTS

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
    * Returns all rows of execution result
    *
    * @return List<Map<…>> data structure
    */
  def getAllData : java.util.List[java.util.Map[String, (String, Object)]] = {
    analysisNodeExecution.getAllData
    analysisNodeExecution.getExecutionData
  }

  /**
    * Returns all rows of execution result
    *
    * @return List<Map<…>> data structure
    */
  def getPreview(limit:Int) : java.util.List[java.util.Map[String, (String, Object)]] = {
    analysisNodeExecution.getPreview(limit)
    analysisNodeExecution.getExecutionData
  }

  /**
    * Returns the rows of the query execution result
    *
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
