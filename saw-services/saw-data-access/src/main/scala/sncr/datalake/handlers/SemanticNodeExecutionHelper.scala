package sncr.datalake.handlers

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.fs.Path
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.exceptions.{DAException, ErrorCodes}
import sncr.datalake.{DLConfiguration, DLSession}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.semantix.SemanticNode

/**
  * Created by srya0001 on 5/18/2017.
  */
class SemanticNodeExecutionHelper(val sn : SemanticNode, cacheIt : Boolean = false ) extends DLSession with HasDataObject[SemanticNodeExecutionHelper]{

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[SemanticNodeExecutionHelper].getName)

  if (sn.getCachedData.isEmpty) throw new DAException(ErrorCodes.NodeDoesNotExist, "SemanticNode")
  if (sn.getRelatedNodes.isEmpty) throw new DAException(ErrorCodes.DataObjectNotFound, "SemanticNode")

  private val dSemanticRaw = sn.getCachedData.get(key_Definition.toString)

  dataObjects = sn.loadRelationElements.map( _.asInstanceOf[DataObject])

  if (dSemanticRaw.isEmpty)
    throw new DAException(ErrorCodes.InvalidAnalysisNode, s"Definition not found, Row ID: ${Bytes.toString(sn.getRowKey)}")

//  if (cacheIt) DLSession.pinToCache(this)
  dataObjects = sn.loadRelationElements.map( _.asInstanceOf[DataObject])
  if (dataObjects.isEmpty) throw new DAException(ErrorCodes.DataObjectNotLoaded, "SemanticNode")

  //if (cacheIt) DLSession.pinToCache(this)

  val definition: JValue =
    dSemanticRaw.get match {
      case x: JValue => x
      case s: String => parse(s, false, false)
      case _  =>
        val m = "Incorrect SemanticNode representation"; m_log error m
        throw new Exception(m)
    }

  m_log trace s"Check definition before extracting value ==> ${pretty(render(definition))}"

  val metric = (definition \ "metric").extractOrElse[String]("")
  val outputLocation = SemanticNodeExecutionHelper.getUserSpecificPath(metric) + "-" + id

  var lastSQLExecRes = -1
  var execResult : java.util.List[java.util.Map[String, (String, Object)]] = null
  var isDataLoaded = false

  var startTS : java.lang.Long = System.currentTimeMillis()
  var finishedTS: Long =  System.currentTimeMillis()

  /**
    * Specific to SemanticNode method to load data objects
    */
  def loadObjects() =
    m_log debug "Start loading objects!"
  try{
    loadData(this)
    isDataLoaded = true
  }
  catch {
    case dax : DAException => m_log error s"Could not load data for semantic node: ${Bytes.toString(sn.getRowKey)}"
    case t:Throwable =>   t.printStackTrace(); m_log error (s"Could not load data for semantic node: ${Bytes.toString(sn.getRowKey)}, unexpected exception: ", t)
  }

  var lastSQLExecMessage : String = null

  /**
    * Base method to execute SQL statement
    *
    * @param limit - number of rows to return, it should be less or equal value configured in application configuration file.
    * @return
    */
  def executeSQL(sql : String, limit : Int = DLConfiguration.rowLimit): (Integer, String) = {
    m_log debug s"Execute SQL: $sql for metric: $metric"
    val ( llastSQLExecRes, llastSQLExecMessage) = executeAndGetData(metric, sql, limit)
    lastSQLExecRes = llastSQLExecRes; lastSQLExecMessage = llastSQLExecMessage
    ( lastSQLExecRes, lastSQLExecMessage)
  }

  def getData : java.util.List[java.util.Map[String, (String, Object)]] = getData(metric)

}

object SemanticNodeExecutionHelper{

  //TODO:: The function is to be replaced with another one to construct user ( tenant ) specific path
  def getUserSpecificPath(outputLocation: String): String = {
    DLConfiguration.semanticLayerTempLocation + Path.SEPARATOR + outputLocation
  }


  def apply( rowId: String, cacheIt: Boolean = false) : SemanticNodeExecutionHelper = { val an = SemanticNode(rowId); new SemanticNodeExecutionHelper(an,cacheIt) }
}
