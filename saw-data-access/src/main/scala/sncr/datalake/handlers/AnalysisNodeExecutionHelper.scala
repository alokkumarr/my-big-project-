package sncr.datalake.handlers

import java.io.OutputStream
import java.lang.Long
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import com.typesafe.config.Config
import org.apache.hadoop.fs.Path
import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.exceptions.{DAException, ErrorCodes}
import sncr.datalake.{DLConfiguration, DLSession}
import sncr.metadata.analysis.{AnalysisNode, AnalysisResult}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.Fields
import sncr.metadata.engine.MDObjectStruct._
import sncr.saw.common.config.SAWServiceConfig
/**
  * Created by srya0001 on 5/18/2017.
  */
class AnalysisNodeExecutionHelper(val an : AnalysisNode, cacheIt: Boolean = false, var resId : String = null ) extends DLSession with HasDataObject[AnalysisNodeExecutionHelper]{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNodeExecutionHelper].getName)
  resId = if (resId == null || resId.isEmpty) UUID.randomUUID().toString else resId

  if (an.getCachedData.isEmpty) throw new DAException(ErrorCodes.NodeDoesNotExist, "AnalysisNode")
  if (an.getRelatedNodes.isEmpty) throw new DAException(ErrorCodes.DataObjectNotFound, "AnalysisNode")

  private val dAnalysisDescRaw = an.getCachedData.get(key_Definition.toString)

  if (dAnalysisDescRaw.isEmpty)
    throw new DAException(ErrorCodes.InvalidAnalysisNode, s"Definition not found, Row ID: ${Bytes.toString(an.getRowKey)}")

  val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val conf: Config = SAWServiceConfig.spark_conf

  dataObjects = an.loadRelationElements.map( _.asInstanceOf[DataObject])

  //TODO:: Should not be here, if consistency mechanism is in place -- remove it, unnecessary
  if (dataObjects.isEmpty) throw new DAException(ErrorCodes.DataObjectNotLoaded, "AnalysisNode")

  //if (cacheIt) DLSession.pinToCache(this)

  val definition: JValue =
    dAnalysisDescRaw.get match {
      case x: JValue => x
      case s: String => parse(s, false, false)
      case _  =>
        val m = "Incorrect AnalysisNode representation"; m_log error m
        throw new Exception(m)
    }

  var analysisResultNodeID: String = null
  var resultNode: AnalysisResult = null
  var resultNodeDescriptor: JObject = null

  m_log debug s"Check definition before extracting value ==> ${pretty(render(definition))}"

  val sqlManual = (definition \ "queryManual").extractOrElse[String]("")
  val analysis = (definition \ "analysis").extractOrElse[String]("")
  val sql = if (sqlManual != "") sqlManual else (definition \ "query").extractOrElse[String]("")
  val outputType = ( definition \ "outputFile" \ "outputFormat").extractOrElse[String]("")
  val initOutputLocation = ( definition \ "outputFile" \ "outputFileName").extractOrElse[String]("")

  if (sql.isEmpty || outputType.isEmpty || initOutputLocation.isEmpty || analysis.isEmpty )
    throw new Exception("Invalid Analysis object, one of the attribute is null/empty: SQL, outputType, outputLocation")
  else
    m_log debug s"Analysis executions parameters, type : $outputType, \n output filename:  $initOutputLocation, \n sql => $sql"

  val outputLocation = AnalysisNodeExecutionHelper.getUserSpecificPath(initOutputLocation) + "-" + resId
  var lastSQLExecRes = -1
  var execResult : java.util.List[java.util.Map[String, (String, Object)]] = null
  var isDataLoaded = false
  var startTS : java.lang.Long = System.currentTimeMillis()
  var finishedTS: Long = null
  /**
    * Specific to AnalysisNode method to load data objects
    */
  def loadObjects() : Unit =
    m_log debug "Start loading objects!"
    try{
      loadData(this)
      isDataLoaded = true
    }
    catch {
      case dax : DAException => m_log error s"Could not load data for analysis node: ${Bytes.toString(an.getRowKey)}"
      case t:Throwable => m_log error (s"Could not load data for analysis node: ${Bytes.toString(an.getRowKey)}, unexpected exception: ", t)
    }


  var lastSQLExecMessage : String = null

  /**
    * Wrapper for base method to execute SQL statement
    *
    * @param limit - number of rows to return, it should be less or equal value configured in application configuration file.
    * @return
    */
  def executeSQL(limit : Int = DLConfiguration.rowLimit): (Integer, String) = {
    val (llastSQLExecRes, llastSQLExecMessage) = executeAndGetData(analysis, sql, limit)
    lastSQLExecRes = llastSQLExecRes; lastSQLExecMessage = llastSQLExecMessage
    (lastSQLExecRes, lastSQLExecMessage)
  }


  def getExecutionData :java.util.List[java.util.Map[String, (String, Object)]] = getData(analysis)

  /**
    * Wrapper around base method that combines SQL execution and new data object saving to appropriate location
    *
    * @param rl - rows limit , it should be less or equal value configured in application configuration file.
    */
  def executeAndSave(rl: Int) : Unit = _executeAndSave(null, rl)

  /**
    * Wrapper around base method that combines SQL execution and new data object saving to appropriate location
    *
    * @param rl - rows limit , it should be less or equal value configured in application configuration file.
    * @param out - Hadoop output stream is used to print AnalysisResult descriptor into it.
    *            can be used for debugging
    */
  def executeAndSave(out: OutputStream, rl: Int) : Unit = _executeAndSave(out, rl)

  /**
    * Wrapper around base method that combines SQL execution and new data object saving to appropriate location
    *
    * @param out - Hadoop output stream is used to print AnalysisResult descriptor into it.
    *            can be used for debugging
    */
  def executeAndSave(out: OutputStream ) : Unit = _executeAndSave(out, DLConfiguration.rowLimit)


  /**
    * Base method to execute SQl statement, print AnalysisResult to output stream.
    *
    * @param out
    * @param rowLim
    */
  private def _executeAndSave(out: OutputStream = null, rowLim: Int = DLConfiguration.rowLimit) : Unit =
  {
    loadObjects
    if (!isDataLoaded) {
      m_log error "Could not load data, see underlying exception"
      return
    }
    executeSQL(rowLim)
    createAnalysisResult(resId, out)
  }

  def printSample(out: OutputStream) : Unit =
  {

    if (!isDataLoaded) {
      m_log error "Could print sample, data were not loaded"
      return
    }

    val sample = getDataSampleAsString(analysis)
    if (out != null && sample != null)
      out.write(sample.getBytes)
  }


  /**
    * The method creates AnalysisResult node:
    * 1. Creates node from result of SQL execution
    * 2. if out parameter is not null - appends it this ResultNode represented in JSON format.
    *
    */
  def createAnalysisResult(resId: String = null, out: OutputStream = null): Unit = {

    createAnalysisResultHeader(resId)
    saveData(analysis, outputLocation, outputType)

    resultNode.addObject("dataLocation", outputLocation, getSchema(analysis))
    resultNode.update()
    val descriptorPrintable = new JObject(resultNodeDescriptor.obj ::: List(("dataLocation", JString(outputLocation))))

    if (out != null) {
      out.write(pretty(render(descriptorPrintable)).getBytes())
      out.flush()
    }
  }


  /**
    * The method creates AnalysisResult node:
    * 1. Creates node from result of SQL execution
    * 2. if out parameter is not null - appends it this ResultNode represented in JSON format.
    *
    */
  def completeAnalysisResult: Unit = {
    saveData(analysis, outputLocation, outputType)
    resultNode.addObject("dataLocation", outputLocation, getSchema(analysis))
    resultNode.update()
  }

  def getPreDefinedResultKey : String = analysisResultNodeID
  def setPreDefinedResultKey(resultId : String) : Unit =  analysisResultNodeID = resultId


  def getStartTS: Long = startTS


  /**
    * The method creates AnalysisResult node:
    * 1. Creates node from result of SQL execution
    * 2. if out parameter is not null - appends it this ResultNode represented in JSON format.
    *
    */
  def createAnalysisResultHeader(resId: String = null): (Int, String) =
  {

    analysisResultNodeID = if (resId != null && !resId.isEmpty) resId else analysisResultNodeID

    if (lastSQLExecRes != 0){
      val m = "Last SQL execution was not successful, cannot create AnalysisResult from it"
      m_log error m
      return (lastSQLExecRes, m)
    }

    if (getData(analysis) == null) throw new DAException( ErrorCodes.NoResultToSave, analysis)

    var nodeExists = false
    try {
      m_log debug s"Remove result: " + analysisResultNodeID
      resultNode = AnalysisResult(Bytes.toString(an.getRowKey), analysisResultNodeID)
      nodeExists = true
    }
    catch {
      case e: Exception => m_log debug("Tried to load node: ", e)
    }
    if (nodeExists) resultNode.delete

    var analysisName = (definition \ "metricName").extractOpt[String]
    if (analysisName.isEmpty) analysisName = (definition \ "analysisName").extractOpt[String]
    if (analysisName.isEmpty) analysisName = (definition \ "analysis").extractOpt[String]

    val analysisId = (definition \ "id").extractOpt[String]

//    val ldt: LocalDateTime = LocalDateTime.now()
//    val timestamp: String = ldt.format(dfrm)

    resultNodeDescriptor = new JObject(List(
      JField("name", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
      JField("id", JString(analysisId.get)),
      JField("analysisName", JString(analysisName.getOrElse(Fields.UNDEF_VALUE.toString))),
      JField("sql", JString(sql)),
      JField("execution_start_ts", JLong(startTS)),
      JField("outputType", JString(outputType)),
      JField("outputLocation", JString(outputLocation))
    ))

    m_log trace s"Create result: with content: ${compact(render(resultNodeDescriptor))}"
    resultNode = new AnalysisResult(Bytes.toString(an.getRowKey), resultNodeDescriptor, analysisResultNodeID)
    m_log trace "Result node descriptor: " + pretty(render(resultNodeDescriptor))

    val (res, msg) = resultNode.create
    m_log info s"Analysis result creation: $res ==> $msg"
    (res, msg)
  }


}

object AnalysisNodeExecutionHelper{

  //TODO:: The function is to be replaced with another one to construct user ( tenant ) specific path
  def getUserSpecificPath(outputLocation: String): String = {
     DLConfiguration.commonLocation + Path.SEPARATOR + outputLocation
  }

  def apply( rowId: String, cacheIt: Boolean = false) : AnalysisNodeExecutionHelper = { val an = AnalysisNode(rowId); new AnalysisNodeExecutionHelper(an, cacheIt)}

}
