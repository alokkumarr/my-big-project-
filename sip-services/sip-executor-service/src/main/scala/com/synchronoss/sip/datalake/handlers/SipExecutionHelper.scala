package sncr.datalake.handlers

import java.io.OutputStream
import java.util.UUID

import com.synchronoss.saw.InternalServiceClient
import com.synchronoss.sip.datalake.{DLConfiguration, SipExecutorsConfig}
import com.synchronoss.sip.datalake.engine.DLSession
import org.json.JSONException
import org.json4s.JsonAST.{JObject, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.exceptions.DAException

import scala.reflect.io.File

class SipExecutionHelper(sqlRuntime: String, cacheIt: Boolean = false, var resId: String = null) extends DLSession with DataObject[SipExecutionHelper] {

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[SipExecutionHelper].getName)
  resId = if (resId == null || resId.isEmpty) UUID.randomUUID().toString else resId

  def setFinishTime = {
    finishedTS = java.math.BigInteger.valueOf(System.currentTimeMillis())
  }

  def setStartTime = {
    startTS = java.math.BigInteger.valueOf(System.currentTimeMillis())
  }

  var ExecutionResultId: String = null

  var resultNodeDescriptor: JObject = null

  var executionKey: String = "AN_" + System.currentTimeMillis()

  def setExecutionKey(executionKey: String): Unit = {
    this.executionKey = executionKey;
  }

  val outputType = DLConfiguration.defaultOutputType
  val initOutputLocation = DLConfiguration.commonLocation

  val outputLocation = SipExecutionHelper.getUserSpecificPath(initOutputLocation) + "-" + resId
  var lastSQLExecRes = -1
  var execResult: java.util.List[java.util.Map[String, (String, Object)]] = null
  var isDataLoaded = false
  var startTS: java.math.BigInteger = java.math.BigInteger.valueOf(System.currentTimeMillis())
  var finishedTS: java.math.BigInteger = java.math.BigInteger.valueOf(System.currentTimeMillis())

  /**
    * Specific to semanticNode method to load data objects
    */
  def loadObjects( semanticId :String): Unit = {

    m_log debug "Start loading objects!!"

    val semanticJSON = semanticNodeHelper(semanticId)
    // Getting the repository details from content
    repositories = semanticJSON \ "repository" match {
      case repository: JArray => repository.arr
      case JNothing => List()
      case obj: JValue => Nil
    }
    try { // Using overloaded method
      // This overloaded method has been introduced to the change related to SIP-4226 & SIP-4220
      loadData(repositories, this, DLConfiguration.rowLimit);
      isDataLoaded = true
      m_log debug "loading objects!! completed"
    }
    catch {
      case dax: DAException => m_log error s"Could not load data for semantic node: ${semanticId}"
      case t: Throwable => m_log error(s"Could not load data for semantic node: ${semanticId}, unexpected exception: ", t)
    }
  }
  def unexpectedElement(json: JValue, expected: String, location: String): Nothing = {
    val name = json.getClass.getSimpleName
    throw new JSONException(
      "Unexpected element: %s, expected %s, at %s".format(
        name, expected, location))
  }

  var lastSQLExecMessage: String = null

  def semanticNodeHelper(semanticId: String): JObject = {
    val semanticHost = SipExecutorsConfig.semanticService.resolve().getString("host")
    val semanticEndpoint = SipExecutorsConfig.semanticService.getString("endpoint")
    val client: InternalServiceClient = new InternalServiceClient(semanticHost + semanticEndpoint)

    m_log.trace("semantic details : {}", semanticHost + semanticEndpoint + semanticId)

    parse(client.retrieveObject(semanticId)).asInstanceOf[JObject]
  }

  def getExecutionData: java.util.List[java.util.Map[String, (String, Object)]] = getData(executionKey)

  def getDataIterator: java.util.Iterator[java.util.HashMap[String, (String, Object)]] = dataIterator(executionKey)

  def printSample(out: OutputStream): Unit = {
    if (!isDataLoaded) {
      m_log error "Could print sample, data were not loaded"
      return
    }
    val sample = getDataSampleAsString(executionKey)
    if (out != null && sample != null)
      out.write(sample.getBytes)
  }

  protected def unexpectedElement(expected: String, obj: JValue): Nothing = {
    val name = obj.getClass.getSimpleName
    throw new RuntimeException(
      "Expected %s but got: %s".format(expected, name))
  }

  /**
    * The method creates Execution Result for one time/preview execution:
    * 1. Creates result of SQL execution
    * 2. write the output with preview identifier to differentiate from actual execution history,
    * these location can be cleaned up on periodic basis, since execution results are for one time use.
    */
  def createExecutionResultForOneTime(resId: String): Unit = {
    val previewLocation = SipExecutionHelper.getUserSpecificPath(initOutputLocation) +
      File.separator + "preview-" + resId
    saveData(executionKey, previewLocation, outputType)
    finishedTS = java.math.BigInteger.valueOf(System.currentTimeMillis())
    m_log debug ("finishedTS:" + finishedTS)
  }

  def getPreDefinedResultKey: String = ExecutionResultId

  def setPreDefinedResultKey(resultId: String): Unit = ExecutionResultId = resultId

  def getStartTS: java.math.BigInteger = startTS

}

object SipExecutionHelper {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[SipExecutionHelper].getName)

  //TODO:: The function is to be replaced with another one to construct user ( tenant ) specific path
  def getUserSpecificPath(outputLocation: String): String = outputLocation


}
