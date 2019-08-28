package com.synchronoss.sip.datalake.engine

import com.synchronoss.sip.datalake.DLConfiguration
import com.synchronoss.sip.datalake.engine.ExecutionType.ExecutionType
import org.json4s.DefaultFormats
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.handlers.{ExecutionStatus, SipExecutionHelper}
import java.io.{BufferedReader, InputStreamReader}
import java.text.SimpleDateFormat

import sncr.bda.core.file.HFileOperations
import sncr.datalake.TimeLogger.logWithTime
import sncr.datalake.handlers.ExecutionStatus.ExecutionStatus

import scala.reflect.io.File

class DataLakeExecution(val execType: ExecutionType, val semanticId :String = null, val resultId: String = null) {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[DataLakeExecution].getName)

  protected var sipExecutionHelper: SipExecutionHelper = null
  protected var id: String = resultId
  protected var executionMessage: String = null
  protected var executionCode: Integer = -1
  protected var status: ExecutionStatus = ExecutionStatus.INIT
  protected var startTS: java.math.BigInteger = null
  protected var finishTS: java.math.BigInteger = null

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  /**
    * This method allows to re-use the spark-session across multiple query execution.
    *
    * @param sqlRuntime
    * @param limit
    */
  def executeDLQuery(sqlRuntime: String ,
                     limit: Integer = DLConfiguration.rowLimit ): Unit = {
    val viewName = "AN_" + System.currentTimeMillis()
    val outputLocation = SipExecutionHelper.getUserSpecificPath(DLConfiguration.commonLocation) +
      File.separator + "preview-" + resultId
    val dlSession = DLExecutionObject.dlSessions
    logWithTime(m_log, "Execute Spark SQL query", {
      sipExecutionHelper = new SipExecutionHelper(sqlRuntime, false, resultId)
      sipExecutionHelper.setExecutionKey(viewName)
      var status: ExecutionStatus = ExecutionStatus.INIT
      sipExecutionHelper.loadObjects(semanticId)
      sipExecutionHelper.setStartTime
      startTS = sipExecutionHelper.getStartTS
      m_log debug s"Loaded objects, Started TS: $startTS"
      status = ExecutionStatus.IN_PROGRESS
      sipExecutionHelper.lastSQLExecMessage = "success"
      sipExecutionHelper.lastSQLExecRes = 0
      execType match {
        case ExecutionType.scheduled => {
          dlSession.execute(viewName, sqlRuntime, limit)
          m_log.trace("Location : " + outputLocation)
          dlSession.saveData(viewName, sipExecutionHelper.outputLocation, sipExecutionHelper.outputType)
        }
        case ExecutionType.onetime => {
          dlSession.execute(viewName, sqlRuntime, limit)
          m_log.trace("Location : " + outputLocation)
          dlSession.saveData(viewName, outputLocation, sipExecutionHelper.outputType)
        }
        case ExecutionType.preview => {
          dlSession.execute(viewName, sqlRuntime, limit)
          m_log.trace("Location : " + outputLocation)
          dlSession.saveData(viewName, outputLocation, sipExecutionHelper.outputType)
        }
        case ExecutionType.regularExecution => {
          if (limit > 0)
            dlSession.execute(viewName, sqlRuntime, limit)
          else
            dlSession.execute(viewName, sqlRuntime)
          m_log.trace("Location : " + outputLocation)
          dlSession.saveData(viewName, outputLocation, sipExecutionHelper.outputType)
        }
        case ExecutionType.publish => {
          if (limit > 0)
            dlSession.execute(viewName, sqlRuntime, limit)
          else
            dlSession.execute(viewName, sqlRuntime)
          dlSession.saveData(viewName, sipExecutionHelper.outputLocation, sipExecutionHelper.outputType)
        }
      }
      startTS = getStartedTimestamp
      finishTS = getFinishedTimestamp
    })
  }

  /**
    * Returns an identifier for the execution
    */
  def getId: String = id


  /**
    * Returns the type tag that was provided with the execution call, which would be "onetime", "scheduled", “preview" or "regularExecution"
    */
  def getType: String = execType.toString

  /**
    * Returns the execution status, an enumeration of possible String values that includes at least "success" and “error"
    */
  def getStatus: ExecutionStatus = status

  /**
    * // return an auxiliary human-readable explanation of the status, which would be mainly used to describe why an execution resulted in an "error” status
    */
  def getExecMessage: String = executionMessage

  def getExecCode: Int = executionCode

  /**
    * Returns execution start timestamp as milliseconds since epoch
    */
  def getStartedTimestamp: java.math.BigInteger = if (startTS == null) java.math.BigInteger.valueOf(-1L) else startTS

  /**
    * Returns execution finished timestamp as milliseconds since epoch
    */
  def getFinishedTimestamp: java.math.BigInteger = if (finishTS == null) java.math.BigInteger.valueOf(-1L) else finishTS

  /**
    * Returns the rows of the query execution result
    *
    * @return List<Map<…>> data structure
    */
  def loadOneTimeExecution(outputLocation: String, limit: Integer = DLConfiguration.rowLimit): java.util.stream.Stream[String] = {

    // use this as just a data type holder
    // val results = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
    val results = new java.util.ArrayList[String]
    // not resultsStream will have required data type.
    var resultsStream = results.stream()

    if (outputLocation == null || outputLocation.isEmpty) {
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

}

object ExecutionType extends Enumeration {

  type ExecutionType = Value
  val onetime = Value(0, "onetime")
  val scheduled = Value(1, "scheduled")
  val preview = Value(2, "preview")
  val regularExecution = Value(3, "regularExecution")
  val publish = Value(4, "publish")

}
