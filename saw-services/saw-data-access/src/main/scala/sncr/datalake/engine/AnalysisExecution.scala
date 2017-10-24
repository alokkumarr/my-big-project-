package sncr.datalake.engine

import files.HFileOperations;
import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.json4s.{JObject, DefaultFormats}
import org.json4s.native.JsonMethods.parse
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.engine.ExecutionStatus.ExecutionStatus
import sncr.datalake.engine.ExecutionType.ExecutionType
import sncr.datalake.handlers.AnalysisNodeExecutionHelper
import sncr.metadata.analysis.{AnalysisNode, AnalysisResult}
import org.json4s.JsonAST.JValue
import sncr.metadata.engine.ProcessingResult
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import scala.collection.JavaConverters._

import scala.concurrent.Future

/**
  * Created by srya0001 on 6/8/2017.
  */
import scala.concurrent.ExecutionContext.Implicits.global

class AnalysisExecution(val an: AnalysisNode, val execType : ExecutionType, val resultId: String = null) {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisExecution].getName)

  def getAnalysisExecutionResultNode : AnalysisResult = analysisNodeExecution.resultNode
  protected var analysisNodeExecution : AnalysisNodeExecutionHelper = null
  protected var id : String = resultId
  protected var executionMessage : String = null
  protected var executionCode : Integer = -1
  protected var status : ExecutionStatus = ExecutionStatus.INIT
  protected var startTS : java.lang.Long = null
  protected var finishTS : java.lang.Long = null

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  def startExecution(sqlRuntime: String = null): Unit =
  {
    try {
      analysisNodeExecution = new AnalysisNodeExecutionHelper(an, sqlRuntime, false, resultId)
      id = analysisNodeExecution.resId
      m_log debug s"Started execution, result ID: $id"
      status = ExecutionStatus.STARTED
      analysisNodeExecution.loadObjects()
      analysisNodeExecution.setStartTime
      startTS = analysisNodeExecution.getStartTS
      m_log debug s"Loaded objects, Started TS: $startTS "
      status = ExecutionStatus.IN_PROGRESS
      /* Execute the query and use the execution ID also as the analysis
       * result node ID (and do not load any results into the Spark
       * driver) */
      execType match {
        case ExecutionType.scheduled => {
          analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.createAnalysisResult(id, null)
        }
        case ExecutionType.onetime => {
          analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.createAnalysisResult(id, null)
        }
        case ExecutionType.preview => {
          analysisNodeExecution.executeSQL()
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
    throw new RuntimeException("getData: No longer supported to prevent out of memory issues")
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
    throw new RuntimeException("getAllData: No longer supported to prevent out of memory issues")
    analysisNodeExecution.getAllData
    analysisNodeExecution.getExecutionData
  }

  /**
    * Returns all rows of execution result
    *
    * @return List<Map<…>> data structure
    */
  def getPreview(limit:Int) : java.util.List[java.util.Map[String, (String, Object)]] = {
    throw new RuntimeException("getPreview: No longer supported to prevent out of memory issues")
    analysisNodeExecution.getPreview(limit)
    analysisNodeExecution.getExecutionData
  }

  /**
    * Returns the rows of the query execution result
    *
    * @return List<Map<…>> data structure
    */
  def loadExecution(id: String, limit: Integer = 0) : java.util.List[java.util.Map[String, (String, Object)]] = {
    /* Note: If loading of execution results is reimplemented in any other
     * service as part of a refactoring, it should be implemented
     * using Java streams to allow processing the data using a
     * streaming approach. This avoids risking out of memory errors
     * due to loading the entire execution result into memory at the
     * same time. */
    val results = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
    val resultNode = AnalysisResult(null, id)
    if (!resultNode.getObjectDescriptors.contains("dataLocation")) {
      m_log.debug("Data location property not found: {}", id)
      return results
    }
    resultNode.getObject("dataLocation") match {
      case Some(dir: String) => {
        val files = try {
          /* Get list of all files in the execution result directory */
          HFileOperations.getFilesStatus(dir)
        } catch {
          case e: Throwable => {
            m_log.debug("Exception while getting result files: {}", e)
            return results
          }
        }
        /* Filter out the JSON files which contain the result rows */
        files.filter(_.getPath.getName.endsWith(".json")).foreach(file => {
          m_log.debug("Filtered file: " + file.getPath.getName)
          val is = HFileOperations.readFileToInputStream(file.getPath.toString)
          val reader = new BufferedReader(new InputStreamReader(is))
          /* Use an iterator over the lines of the file, which map to rows of the results encoded as JSON */
          reader.lines.iterator.asScala.foreach(line => {
            val resultsRow = new java.util.HashMap[String, (String, Object)]
            parse(line) match {
              case obj: JObject => {
                /* Convert the parsed JSON to the data type expected by the
                 * loadExecution method signature */
                val rowMap = obj.extract[Map[String, Any]]
                rowMap.keys.foreach(key => {
                  rowMap.get(key).foreach(value => resultsRow.put(key, ("unknown", value.asInstanceOf[AnyRef])))
                })
              }
              case obj => throw new RuntimeException("Unknown result row type from JSON: " + obj.getClass.getName)
            }
            results.add(resultsRow)
            if (limit > 0 && results.size() >= limit) {
              return results
            }
          })
        })
      }
      case obj => {
        m_log.debug("Data location not found for results: {}", id)
        return results
      }
    }
    results
  }

  def loadESExecutionData(anres: AnalysisResult) : JValue  = AnalysisNodeExecutionHelper.loadESAnalysisResult(anres)
}


object ExecutionType extends Enumeration{

  type ExecutionType = Value
  val onetime = Value(0, "onetime")
  val scheduled = Value(1, "scheduled")
  val preview = Value(2, "preview")


}
