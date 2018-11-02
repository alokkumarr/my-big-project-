package sncr.datalake.engine

import files.HFileOperations
import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.json4s.{DefaultFormats, JObject}
import org.json4s.native.JsonMethods.parse
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.engine.ExecutionStatus.ExecutionStatus
import sncr.datalake.engine.ExecutionType.ExecutionType
import sncr.datalake.handlers.AnalysisNodeExecutionHelper
import sncr.metadata.analysis.{AnalysisNode, AnalysisResult}
import org.json4s.JsonAST.{JObject, JValue}
import sncr.metadata.engine.ProcessingResult
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat

import org.apache.hadoop.fs.FileStatus
import sncr.datalake.TimeLogger.logWithTime

import scala.collection.JavaConverters._
import sncr.datalake.{DLConfiguration, DLExecutionObject}

import scala.concurrent.Future
import scala.reflect.io.File

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
  // This has been modified due to version incompatibility issue with json4s 3.3.0 version
  protected var startTS : java.math.BigInteger = null
  protected var finishTS : java.math.BigInteger = null

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  @deprecated
  def startExecution(sqlRuntime: String = null, limit: Integer= DLConfiguration.rowLimit): Unit =
  {
    try {
      analysisNodeExecution = new AnalysisNodeExecutionHelper(an, sqlRuntime, false, resultId)
      id = analysisNodeExecution.resId
      m_log trace s"Started execution, result ID: $id"
      status = ExecutionStatus.STARTED
      analysisNodeExecution.loadObjects()
      analysisNodeExecution.setStartTime
      startTS = analysisNodeExecution.getStartTS
      m_log trace s"Loaded objects, Started TS: $startTS "
      status = ExecutionStatus.IN_PROGRESS
      /* Execute the query and use the execution ID also as the analysis
       * result node ID (and do not load any results into the Spark
       * driver) */
      execType match {
        case ExecutionType.scheduled => {
          analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.createAnalysisResult(id, null,ExecutionType.scheduled.toString)
        }
        case ExecutionType.onetime => {
          analysisNodeExecution.executeSQLWithLimit(limit)
          analysisNodeExecution.createAnalysisResultForOneTime(id)
        }
        case ExecutionType.preview => {
          analysisNodeExecution.executeSQLWithLimit(DLConfiguration.rowLimit)
          analysisNodeExecution.createAnalysisResultForOneTime(id)
        }
        case ExecutionType.regularExecution => {
          if (DLConfiguration.publishRowLimit>0)
          analysisNodeExecution.executeSQLWithLimit(DLConfiguration.publishRowLimit)
          else
            analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.createAnalysisResultForOneTime(id)
        }
        case ExecutionType.publish => {
          if (DLConfiguration.publishRowLimit>0)
            analysisNodeExecution.executeSQLWithLimit(DLConfiguration.publishRowLimit)
          else
            analysisNodeExecution.executeSQLNoDataLoad()
          analysisNodeExecution.createAnalysisResult(id, null, ExecutionType.publish.toString)
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
    * This method added to re-use the spark-session across multiple query execution.
    * @param sqlRuntime
    * @param limit
    */
  def executeDLAnalysis( sqlRuntime: String = null,
                  limit: Integer = DLConfiguration.rowLimit): Unit = {
    val viewName = "AN_" + System.currentTimeMillis()
    val outputLocation = AnalysisNodeExecutionHelper.getUserSpecificPath(DLConfiguration.commonLocation) +
      File.separator + "preview-" + resultId
    logWithTime(m_log, "Execute Spark SQL query", {
      analysisNodeExecution = new AnalysisNodeExecutionHelper(an, sqlRuntime, false, resultId)
      analysisNodeExecution.loadObjects()
      analysisNodeExecution.setAnalysisKey(viewName)
      var status: ExecutionStatus = ExecutionStatus.INIT
      analysisNodeExecution.loadObjects()
      analysisNodeExecution.setStartTime
      startTS = analysisNodeExecution.getStartTS
      m_log debug s"Loaded objects, Started TS: $startTS"
      status = ExecutionStatus.IN_PROGRESS
      analysisNodeExecution.lastSQLExecMessage = "success"
      analysisNodeExecution.lastSQLExecRes = 0
      execType match {
        case ExecutionType.scheduled => {
          DLExecutionObject.dlSessions.execute(viewName, sqlRuntime, DLConfiguration.publishRowLimit)
          m_log.trace("Location : " + outputLocation)
          DLExecutionObject.dlSessions.saveData(viewName, analysisNodeExecution.outputLocation, "json")
          analysisNodeExecution.createAnalysisResult(resultId, null, ExecutionType.scheduled.toString)
        }
        case ExecutionType.onetime => {
          DLExecutionObject.dlSessions.execute(viewName, sqlRuntime, limit)
          m_log.trace("Location : " + outputLocation)
          DLExecutionObject.dlSessions.saveData(viewName, outputLocation, analysisNodeExecution.outputType)
        }
        case ExecutionType.preview => {
          DLExecutionObject.dlSessions.execute(viewName, sqlRuntime, limit)
          m_log.trace("Location : " + outputLocation)
          DLExecutionObject.dlSessions.saveData(viewName, outputLocation, analysisNodeExecution.outputType)
        }
        case ExecutionType.regularExecution => {
          if (DLConfiguration.publishRowLimit > 0)
            DLExecutionObject.dlSessions.execute(viewName, sqlRuntime, DLConfiguration.publishRowLimit)
          else
            DLExecutionObject.dlSessions.execute(viewName, sqlRuntime)
          m_log.trace("Location : " + outputLocation)
          DLExecutionObject.dlSessions.saveData(viewName, outputLocation, analysisNodeExecution.outputType)
        }
        case ExecutionType.publish => {
          if (DLConfiguration.publishRowLimit > 0)
            DLExecutionObject.dlSessions.execute(viewName, sqlRuntime, DLConfiguration.publishRowLimit)
          else
            DLExecutionObject.dlSessions.execute(viewName, sqlRuntime)
          DLExecutionObject.dlSessions.saveData(viewName, analysisNodeExecution.outputLocation, analysisNodeExecution.outputType)
          analysisNodeExecution.createAnalysisResult(resultId, null, ExecutionType.publish.toString)
        }
      }
      startTS = getStartedTimestamp
      finishTS = getFinishedTimestamp
    })
  }

  /**
    * Returns an identifier for the analysis execution
    */
  def getId: String = id


 /**
   * Returns the type tag that was provided with the analysis execution call, which would be "onetime", "scheduled", “preview" or "regularExecution"
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
  def getStartedTimestamp : java.math.BigInteger =  if (startTS == null ) java.math.BigInteger.valueOf(-1L) else startTS

  /**
    * Returns execution finished timestamp as milliseconds since epoch
    */
  def getFinishedTimestamp : java.math.BigInteger = if (finishTS == null ) java.math.BigInteger.valueOf(-1L) else finishTS

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
    * Returns the rows of the query execution result
    *
    * @return List<Map<…>> data structure
    */
  def loadExecution(id: String, limit: Integer = 10000) : java.util.stream.Stream[String] = {

    // use this as just a data type holder
    // val results = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
    val results = new java.util.ArrayList[String]
    // not resultsStream will have required data type.
    var resultsStream = results.stream()

    val resultNode = AnalysisResult(null, id)

    if (!resultNode.getObjectDescriptors.contains("dataLocation")) {
      m_log.debug("Data location property not found: {}", id)
      return resultsStream
    }

    resultNode.getObject("dataLocation") match {
      case Some(dir: String) => {
        val files = try {
          /* Get list of all files in the execution result directory */
          HFileOperations.getFilesStatus(dir)
        } catch {
          case e: Throwable => {
            m_log.debug("Exception while getting result files: {}", e)
            return resultsStream
          }
        }
        /* Filter out the JSON files which contain the result rows */
        files.filter(_.getPath.getName.endsWith(".json")).foreach(file => {
          m_log.debug("Filtered file: " + file.getPath.getName)
          val is = HFileOperations.readFileToInputStream(file.getPath.toString)
          // stream of all the "string" values for one file.
          val reader = new BufferedReader(new InputStreamReader(is))
          // merge each file to a resultsStream
          resultsStream = java.util.stream.Stream.concat(resultsStream, reader.lines)

        })
        // this can very well be an infinite stream if we don't give it a limit
        return resultsStream
      }
      case obj => {
        m_log.debug("Data location not found for results: {}", id)
        return resultsStream
      }
    }
    resultsStream
  }

  /**
    * Returns the rows count of execution result
    *
    * @return List<Map<…>> data structure
    */
  def getRowCount(id: String, outputLocation: String= null ) : Long = {

    var rowCount :Long= 0;
     if (outputLocation!=null && !outputLocation.isEmpty) {
       val files = try {
         /* Get list of all files in the execution result directory */
         HFileOperations.getFilesStatus(outputLocation)
       } catch {
         case e: Throwable => {
           m_log.debug("Exception while getting result files: {}", e)
           return rowCount
         }
       }
       getCount(files)
     }
     else {
       val resultNode = AnalysisResult(null, id)
       if (!resultNode.getObjectDescriptors.contains("dataLocation") && outputLocation == null) {
         m_log.debug("Data location property not found: {}", id)
         return rowCount
       }

       resultNode.getObject("dataLocation") match {
         case Some(dir: String) => {
           val files = try {
             /* Get list of all files in the execution result directory */
             HFileOperations.getFilesStatus(dir)
           } catch {
             case e: Throwable => {
               m_log.debug("Exception while getting result files: {}", e)
               return rowCount
             }
           }
           getCount(files)
         }
         case obj => {
           m_log.debug("Data location not found for results: {}", id)
         }
           rowCount
       }
     }
        def getCount(files :Array[FileStatus])  : Long = {
         /* Filter out the recordCount file which contain the rows count */
          files.filter(_.getPath.getName.endsWith("recordCount")).foreach(file => {
            m_log.debug("Filtered file: " + file.getPath.getName)
            val is = HFileOperations.readFileToInputStream(file.getPath.toString)
            // stream of all the "string" values for one file.
            val reader = new BufferedReader(new InputStreamReader(is))
            reader.lines.iterator.asScala.foreach({
              parse(_) match {
                case obj: JObject => {
                  /* Convert the parsed JSON to the data type expected by the*/
                  val rowMap = obj.extract[Map[String, Any]]
                  rowMap.keys.foreach(key => {
                    if (key.equalsIgnoreCase("recordCount")) {
                      rowMap.get(key) match {
                        case Some(count) =>
                          rowCount = count.asInstanceOf[BigInt].toLong
                      }
                    }
                  })
                }
              }})})
          rowCount
        }
      rowCount
    }
  /**
    * Returns the rows of the query execution result
    *
    * @return List<Map<…>> data structure
    */
  def loadOneTimeExecution(outputLocation: String, limit: Integer = DLConfiguration.rowLimit) : java.util.stream.Stream[String] = {

    // use this as just a data type holder
    // val results = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
    val results = new java.util.ArrayList[String]
    // not resultsStream will have required data type.
    var resultsStream = results.stream()

    if (outputLocation==null || outputLocation.isEmpty) {
      m_log.debug("Data location property not found: {}", id)
      return resultsStream
    }

    outputLocation match {
      case (dir: String) => {
        val files = try {
          /* Get list of all files in the execution result directory */
          HFileOperations.getFilesStatus(dir)
        } catch {
          case e: Throwable => {
            m_log.debug("Exception while getting result files: {}", e)
            return resultsStream
          }
        }
        /* Filter out the JSON files which contain the result rows */
        files.filter(_.getPath.getName.endsWith(".json")).foreach(file => {
          m_log.debug("Filtered file: " + file.getPath.getName)
          val is = HFileOperations.readFileToInputStream(file.getPath.toString)
          // stream of all the "string" values for one file.
          val reader = new BufferedReader(new InputStreamReader(is))
          // merge each file to a resultsStream
          resultsStream = java.util.stream.Stream.concat(resultsStream, reader.lines)

        })
        // this can very well be an infinite stream if we don't give it a limit
        return resultsStream
      }
      case obj => {
        m_log.debug("Data location not found for results: {}", id)
        return resultsStream
      }
    }
    resultsStream
  }

  def loadESExecutionData(anres: AnalysisResult) : JValue  = AnalysisNodeExecutionHelper.loadESAnalysisResult(anres)
}


object ExecutionType extends Enumeration {

  type ExecutionType = Value
  val onetime = Value(0, "onetime")
  val scheduled = Value(1, "scheduled")
  val preview = Value(2, "preview")
  val regularExecution= Value(3, "regularExecution")
  val publish = Value(4, "publish")

}
