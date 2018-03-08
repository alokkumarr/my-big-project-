package sncr.datalake.engine

import java.io.FileNotFoundException
import java.util
import java.util.UUID
import java.util.concurrent.{ExecutorService, Executors, Future}

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import files.HFileOperations
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.DLConfiguration
import sncr.datalake.TimeLogger._
import sncr.datalake.engine.ExecutionType.ExecutionType
import sncr.metadata.analysis.{AnalysisNode, AnalysisResult}
import sncr.metadata.engine.ProcessingResult
import sncr.saw.common.config.SAWServiceConfig

import scala.reflect.io.File

/**
  * Created by srya0001 on 6/8/2017.
  */
class Analysis(val analysisId : String) {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[Analysis].getName)
  val an = AnalysisNode(analysisId)

  private val executor : ExecutorService = Executors.newFixedThreadPool(1)
  private var task     : Future[util.List[util.Map[String, (String, Object)]]] = null
  private var status : String = ExecutionStatus.STARTED.toString
  private var startTS : Long = 0
  private var finishedTS : Long = 0

  def getStatus = status
  def getStartTS = startTS
  def getFinishedTS = finishedTS



  /**
    * Start execution of analysis through MapR Streams queue, instead
    * of in the Transport Service Play application, for concurrency
    */
  def executeAndWaitQueue(execType: ExecutionType, sqlRuntime: String = null,
    queue: (String, String, String) => Unit):
      AnalysisExecution = {
    val resultId = UUID.randomUUID().toString
    /* Send execution request to queue */
    queue(analysisId, resultId, sqlRuntime)
    /* Wait for analysis result node to appear */
    waitForResult(resultId,DLConfiguration.waitTime)
    /* Return analysis execution instance for compatibility with existing
     * interface */
    new AnalysisExecution(an, execType, resultId)
  }

  private def waitForResult(resultId: String, retries: Int = 60) {
    if (!executionCompleted(resultId)) {
      waitForResultRetry(resultId, retries)
    }
  }

  private def executionCompleted(resultId: String): Boolean = {
    val mainPath = if (SAWServiceConfig.executorConfig.hasPath("Path"))
      SAWServiceConfig.executorConfig.getString("Path") else "/main"
    val path = mainPath+File.separator+"saw-transport-executor-result-" + resultId
    try {
      HFileOperations.readFile(path)
    } catch {
      case e: FileNotFoundException => return false
    }
    HFileOperations.deleteFile(path)
    true
  }

  private def waitForResultRetry(resultId: String, retries: Int) {
    if (retries == 0) {
      throw new RuntimeException("Timed out waiting for result: " + resultId)
    }
    m_log.debug("Waiting for result: {}", resultId)
    Thread.sleep(1000)
    waitForResult(resultId, retries - 1)
  }

  /**
    * start execution of an analysis and return an execution object immediately; store the "type" parameter in
    * the execution object, but no need for the Spark SQL executor to do anything with it
    */

  def executeAndWait(execType: ExecutionType, sqlRuntime: String = null, resultId: String = null) : AnalysisExecution =
  {
    m_log debug s"Execute analysis as ${execType.toString}"
    val analysisExecution = new AnalysisExecution(an, execType, resultId)
    logWithTime(m_log, "Execute Spark SQL query", {
      analysisExecution.startExecution(sqlRuntime)
    })
    startTS = analysisExecution.getStartedTimestamp
    finishedTS = analysisExecution.getFinishedTimestamp
    analysisExecution
  }

  var exec :AsynchAnalysisExecWithList = null
  def execute(execType: ExecutionType) : Future[util.List[util.Map[String, (String, Object)]]] =
  {
    m_log debug s"Asynchronously execute analysis as ${execType.toString}"
    exec = new AsynchAnalysisExecWithList(an, execType)
    task = executor.submit(exec)
    startTS = exec.getStartedTimestamp
    status = exec.getStatus.toString
    task
  }

  /**
    * Returns the list of executions for that analysis
    */
  def listExecutions : List[AnalysisResult] = {
    val keys = Map("analysisId" -> analysisId)
    val analysisResult = new AnalysisResult(analysisId)
    val rowIds : List[Array[Byte]] = analysisResult.simpleMetadataSearch(keys, "and")
    rowIds.map( rowId => { val rowIdStr = Bytes.toString( rowId ); AnalysisResult(analysisId, rowIdStr)} )
  }

  def delete : Unit = an.deleteAnalysisResults()

  def getExecution(id: String ) : AnalysisExecution =
  {
      val analysisExecution = new AnalysisExecution(an, ExecutionType.onetime)
      analysisExecution
  }

  override def finalize: Unit = executor.shutdown()


}

object ExecutionStatus extends Enumeration{

  type ExecutionStatus = Value
  val INIT = Value(10,"Init")
  val STARTED = Value(1,"Execution started")
  val IN_PROGRESS = Value(2,"Execution is in progress")
  val FAILED = Value(-1,"Execution failed")
  val COMPLETED = Value(0,"Execution successfully completed")

}
