package sncr.datalake.handlers

import java.io.OutputStream
import java.lang.Long
import java.time.format.DateTimeFormatter
import java.util
import java.util.UUID

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import com.typesafe.config.Config
import org.json.JSONException
import org.json4s.JsonAST.{JObject, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.exceptions.{DAException, ErrorCodes}
import sncr.datalake.{DLConfiguration, DLSession}
import sncr.metadata.analysis.{AnalysisNode, AnalysisResult}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.{Fields, MDObjectStruct}
import sncr.metadata.engine.MDObjectStruct._
import sncr.saw.common.config.SAWServiceConfig

import scala.reflect.io.File
/**
  * Created by srya0001 on 5/18/2017.
  */
class AnalysisNodeExecutionHelper(val an : AnalysisNode, sqlRuntime: String, cacheIt: Boolean = false, var resId : String = null ) extends DLSession with HasDataObject[AnalysisNodeExecutionHelper]{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNodeExecutionHelper].getName)
  resId = if (resId == null || resId.isEmpty) UUID.randomUUID().toString else resId
  // This has been modified due to version incompatibility issue with json4s 3.3.0 version
  def setFinishTime = { finishedTS =  java.math.BigInteger.valueOf(System.currentTimeMillis())   }
  def setStartTime = { startTS =  java.math.BigInteger.valueOf(System.currentTimeMillis())   }

  if (an.getCachedData.isEmpty) throw new DAException(ErrorCodes.NodeDoesNotExist, "AnalysisNode")
  // The below has been commented due to the change related to SIP-4226 & SIP-4220
  //if (an.getRelatedNodes.isEmpty) throw new DAException(ErrorCodes.DataObjectNotFound, "AnalysisNode")

  private val dAnalysisDescRaw = an.getCachedData.get(key_Definition.toString)

  if (dAnalysisDescRaw.isEmpty)
    throw new DAException(ErrorCodes.InvalidAnalysisNode, s"Definition not found, Row ID: ${Bytes.toString(an.getRowKey)}")

  val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val conf: Config = SAWServiceConfig.spark_conf

  // The below has been commented due to the change related to SIP-4226 & SIP-4220
  //dataObjects = an.loadRelationElements.map( _.asInstanceOf[DataObject])
  // if (dataObjects.isEmpty) throw new DAException(ErrorCodes.DataObjectNotLoaded, "AnalysisNode")

  //if (cacheIt) DLSession.pinToCache(this)

  val definition: JValue =
    dAnalysisDescRaw.get match {
      case x: JValue => x
      case s: String => parse(s, false)
      case _  =>
        val m = "Incorrect AnalysisNode representation"; m_log error m
        throw new Exception(m)
    }

  var analysisResultNodeID: String = null
  var resultNode: AnalysisResult = null
  var resultNodeDescriptor: JObject = null

  m_log trace s"Check definition before extracting value ==> ${pretty(render(definition))}"

  val sqlDefinition = (definition \ "query").extractOrElse[String]("")
  val sqlManual = (definition \ "queryManual").extractOrElse[String]("")
  val metricName = (definition \ "metricName").extractOrElse[String]("")
  var analysisKey :String ="AN_" + System.currentTimeMillis()
   def setAnalysisKey (analysisKey : String): Unit =
  {
    this.analysisKey = analysisKey;
  }

  val sql = if (sqlRuntime != null && !sqlRuntime.isEmpty) sqlRuntime  else if (sqlManual != "") sqlManual else sqlDefinition

// ----------- SAW-880 -------------------------------
//TODO:: Modify it, see SAW-880, item 11
//  val outputType = ( definition \ "outputFile" \ "outputFormat").extractOrElse[String](DLConfiguration.defaultOutputType)
//  val initOutputLocation = ( definition \ "outputFile" \ "outputFileName").extractOrElse[String](DLConfiguration.commonLocation)
  val outputType = DLConfiguration.defaultOutputType
  val initOutputLocation = DLConfiguration.commonLocation
// ----------- SAW-880 -------------------------------

  m_log debug s" Analysis: $metricName, \n Output location: $initOutputLocation \n Output type: $outputType, \n SQL: $sql"

  if (sql.isEmpty || metricName.isEmpty || outputType.isEmpty || initOutputLocation.isEmpty)
    throw new Exception("Invalid Analysis object, one of the attribute is null/empty: SQL query, analysis attribute")

  val outputLocation = AnalysisNodeExecutionHelper.getUserSpecificPath(initOutputLocation) + "-" + resId
  var lastSQLExecRes = -1
  var execResult : java.util.List[java.util.Map[String, (String, Object)]] = null
  var isDataLoaded = false
  var startTS : java.math.BigInteger = java.math.BigInteger.valueOf(System.currentTimeMillis())
  var finishedTS: java.math.BigInteger = java.math.BigInteger.valueOf(System.currentTimeMillis())
  /**
    * Specific to AnalysisNode method to load data objects
    */
  def loadObjects() : Unit =

    m_log debug "Start loading objects!!"
    val analysisJSON = an.getCachedData("content") match {
        case content: JObject => content
        case _ => throw new NullPointerException("no content found!!")
    }
   // Getting the repository details from content
   repositories = analysisJSON \ "repository" match {
        case repository: JArray => repository.arr
        case JNothing => List()
        case obj: JValue => Nil
    }
    try
    { // Using overloaded method
      // This overloaded method has been introduced to the change related to SIP-4226 & SIP-4220
      loadData(repositories, this, DLConfiguration.rowLimit);
      isDataLoaded = true
    }
    catch {
      case dax : DAException => m_log error s"Could not load data for analysis node: ${Bytes.toString(an.getRowKey)}"
      case t:Throwable => m_log error (s"Could not load data for analysis node: ${Bytes.toString(an.getRowKey)}, unexpected exception: ", t)
    }

  def unexpectedElement(json: JValue, expected: String, location: String): Nothing = {
    val name = json.getClass.getSimpleName
    throw new JSONException(
      "Unexpected element: %s, expected %s, at %s".format(
        name, expected, location))
  }
  var lastSQLExecMessage : String = null



  /**
    * Wrapper for base method to execute SQL statement, the method does not materialized data
    *
    * @param limit - number of rows to return, it should be less or equal value configured in application configuration file.
    * @return
    */
  def executeSQLNoDataLoad(limit : Int = DLConfiguration.rowLimit): (Integer, String) = {
    val (llastSQLExecRes, llastSQLExecMessage) = execute(analysisKey, sql)
    lastSQLExecRes = llastSQLExecRes; lastSQLExecMessage = llastSQLExecMessage
    (lastSQLExecRes, lastSQLExecMessage)
  }

  /**
    * Wrapper for base method to execute SQL statement with row limit , the method does not materialized data
    *
    * @param limit - number of rows to return, it should be less or equal value configured in application configuration file.
    * @return
    */
  def executeSQLWithLimit(limit : Int = DLConfiguration.rowLimit): (Integer, String) = {
    val (llastSQLExecRes, llastSQLExecMessage) = execute(analysisKey, sql,limit)
    lastSQLExecRes = llastSQLExecRes; lastSQLExecMessage = llastSQLExecMessage
    (lastSQLExecRes, lastSQLExecMessage)
  }

  def getExecutionData :java.util.List[java.util.Map[String, (String, Object)]] = getData(analysisKey)

  def getDataIterator : java.util.Iterator[java.util.HashMap[String, (String, Object)]] = dataIterator(analysisKey)


  def printSample(out: OutputStream) : Unit =
  {
    if (!isDataLoaded) {
      m_log error "Could print sample, data were not loaded"
      return
    }
    val sample = getDataSampleAsString(analysisKey)
    if (out != null && sample != null)
      out.write(sample.getBytes)
  }

  /**
    * The method creates AnalysisResult node:
    * 1. Creates node from result of SQL execution
    * 2. if out parameter is not null - appends it this ResultNode represented in JSON format.
    *
    */
  def createAnalysisResult(resId: String = null, out: OutputStream = null, executionType :String): Unit = {

    createAnalysisResultHeader(resId)
   // saveData(analysisKey, outputLocation, outputType)
    finishedTS = java.math.BigInteger.valueOf(System.currentTimeMillis())
    val newDescriptor = JObject (resultNodeDescriptor.obj ++ List(
      JField("execution_finish_ts", JInt(finishedTS)),
      JField("executionType", JString(executionType)),
      JField("exec-code", JInt(lastSQLExecRes)),
      JField("exec-msg", JString(lastSQLExecMessage))
    ))
    resultNode.setDescriptor(compact(render(newDescriptor)))

    resultNode.addObject("dataLocation", outputLocation, "")
    resultNode.update()

    if (out != null) {
      val descriptorPrintable = new JObject(resultNodeDescriptor.obj ::: List(("dataLocation", JString(outputLocation))))
      out.write(pretty(render(descriptorPrintable)).getBytes())
      out.flush()
    }
  }

  protected def unexpectedElement(expected: String, obj: JValue): Nothing = {
    val name = obj.getClass.getSimpleName
    throw new RuntimeException(
      "Expected %s but got: %s".format(expected, name))
  }

  /**
    * The method creates AnalysisResult for one time/preview execution:
    * 1. Creates result of SQL execution
    * 2. write the output with preview identifier to differentiate from actual execution history,
    *  these location can be cleaned up on periodic basis, since execution results are for one time use.
    */
  def createAnalysisResultForOneTime(resId: String): Unit = {
    val previewLocation = AnalysisNodeExecutionHelper.getUserSpecificPath(initOutputLocation)+
      File.separator+ "preview-"+resId
    saveData(analysisKey, previewLocation, outputType)
    finishedTS = java.math.BigInteger.valueOf(System.currentTimeMillis())
    m_log debug("finishedTS:"+ finishedTS)
  }

  /**
    * The method creates AnalysisResult node:
    * 1. Creates node from result of SQL execution
    * 2. if out parameter is not null - appends it this ResultNode represented in JSON format.
    *
    */
  def completeAnalysisResult: Unit = {
    finishedTS = java.math.BigInteger.valueOf(System.currentTimeMillis())
    val newDescriptor = JObject (resultNodeDescriptor.obj ++ List(
      JField("execution_finish_ts", JInt(finishedTS)),
      JField("exec-code", JInt(lastSQLExecRes)),
      JField("exec-msg", JString(lastSQLExecMessage))
    ))
    resultNode.setDescriptor(compact(render(newDescriptor)))

    resultNode.addObject("dataLocation", outputLocation, getSchema(analysisKey))
    resultNode.update()
  }

  def getPreDefinedResultKey : String = analysisResultNodeID
  def setPreDefinedResultKey(resultId : String) : Unit =  analysisResultNodeID = resultId


  def getStartTS: java.math.BigInteger   = startTS


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

   // if (getDataset(analysisKey) == null) throw new DAException( ErrorCodes.NoResultToSave, analysisKey)

    var nodeExists = false
    try {
      m_log debug s"Remove result: " + analysisResultNodeID
      resultNode = AnalysisResult(Bytes.toString(an.getRowKey), analysisResultNodeID)
      nodeExists = true
    }
    catch {
      case e: Exception => m_log debug("Tried to load node: {}", e.toString)
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
      JField("execution_start_ts", JInt(startTS)),
      JField("outputType", JString(outputType)),
      JField("outputLocation", JString(outputLocation))
    ))

    m_log trace s"Create result: with content: ${compact(render(resultNodeDescriptor))}"
    resultNode = new AnalysisResult(Bytes.toString(an.getRowKey), resultNodeDescriptor, analysisResultNodeID)
    m_log trace "Result node descriptor: " + pretty(render(resultNodeDescriptor))

    val (res, msg) = resultNode.create
    m_log debug s"Analysis result creation: $res ==> $msg"
    (res, msg)
  }
}

object AnalysisNodeExecutionHelper{

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNodeExecutionHelper].getName)

  //TODO:: The function is to be replaced with another one to construct user ( tenant ) specific path
  def getUserSpecificPath(outputLocation: String): String = outputLocation

  def apply( rowId: String, cacheIt: Boolean = false) : AnalysisNodeExecutionHelper = { val an = AnalysisNode(rowId); new AnalysisNodeExecutionHelper(an, null, cacheIt)}
  def convertJsonToList(value: JValue): util.List[util.Map[String, (String, Object)]] =
  {
    value match {
      case a:JArray =>
      case o:JObject => //convertJsonToList(o.obj.)
      case _ => { val l = List(Map("data-conversion-error" -> ("result", new Object))) }
    }
   null
  }

  def loadAnalysisResult(id: String): util.List[util.Map[String, (String, Object)]] =
  {
    DLConfiguration.initSpark()
    val dlsession = new DLSession("SAW-TS-Show-AnalysisResult")
    val resultNode = AnalysisResult(null, id)
    //    resultNodeDescriptor = resultNode.getCachedData(MDObjectStruct.key_Definition.toString)
    val od = resultNode.getObjectDescriptors
    if (od.isEmpty)
    {
      m_log debug s"Nothing to load, return null"
      return null
    }
    val onlyKey = od.keysIterator.next()
    val rawdata = resultNode.getObject(onlyKey)
    if (rawdata.isDefined) {
      od(onlyKey) match {
        case "json" => convertJsonToList(rawdata.get.asInstanceOf[JValue])
        case "location" => {
          dlsession.loadObject(onlyKey, rawdata.get.asInstanceOf[String], "parquet", DLConfiguration.rowLimit)
          dlsession.getData(onlyKey)
        }
        case "binary" => throw new Exception("Loading binary data not supported yet")
      }
    }
    else null
  }
  def loadESAnalysisResult(anres: AnalysisResult): JValue =
  {
    val resultNodeDescriptor = anres.getCachedData(MDObjectStruct.key_Definition.toString)
    val d_type = (resultNodeDescriptor.asInstanceOf[JValue] \ "type").extractOpt[String];
    val od = anres.getObjectDescriptors
    if (od.isEmpty)
    {
      m_log debug s"Nothing to load, return null"
      return null
    }
    val onlyKey = od.keysIterator.next()
    if (d_type.get == "chart" || d_type.get == "pivot" || d_type.get == "esReport") {
        val onlyKey = od.keysIterator.next()
        val rawdata = anres.getObject(onlyKey)

        if (rawdata.isDefined) {
          od(onlyKey) match {
            case "json" => rawdata.get.asInstanceOf[JValue]
          }
        }
        else {
          throw  new Exception("No data found!!")
        }
 }
    else
      throw  new Exception("Incorrect call the method should be called only for ES data")
  }
}
