package sncr.metadata.datalake

import org.json4s.JsonAST.{JArray, JField, JObject, JString, JValue}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.{MDNodeUtil, ProcessingResult}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.ihandlers.RequestProcessor
import sncr.metadata.ui_components.UINode
import sncr.metadata.engine.MDObjectStruct.formats
import org.json4s.native.JsonMethods._

/**
  * The semantic interaction handler is a layer that provides request translations from request to
  * execution plan to:
  * - read/show semantic data
  * - save/update semantic nodes
  * - manage semantic relations.
  * Then executed it and packs operation result into JSON response.
  */

class DataObjectRequestProcessor (a_docAsJson : JValue, a_printPretty: Boolean = true)
  extends RequestProcessor(a_docAsJson : JValue, a_printPretty) {

  protected override val m_log: Logger = LoggerFactory.getLogger(classOf[DataObjectRequestProcessor].getName)


  /**
    * Validates requests, if a request malformed, incorrectly structured or does not have
    * required parts - rejects the requests
    *
    * @return (result as Int, Explanation as String)
    */
  override def validate : (Int, String ) = {
    if (docAsJson == null || docAsJson.toSome.isEmpty)
      (Error.id, "Validation fails: document is empty")

    preProcessRequest()

    if (!DataObjectRequestProcessor.verbs.exists(_.equalsIgnoreCase(action))){
      val msg = s"Action is incorrect: $action"
      m_log error Rejected.id + " ==> " + msg
      return (Rejected.id, msg)
    }
    m_log trace "Validate action and content section"
    action match {
      case "read" =>
      {
        if ( keys == null || keys.isEmpty) {
          val msg = s"Keys list (filter) is missing or empty"; m_log debug Rejected.id + " ==> " + msg; return (Rejected.id, msg)
        }
        if (!keys.keySet.forall(k => UINode.searchFields.contains(k))){
          val msg = s"Keys list has keys that are not defined as searchable! "; m_log debug Rejected.id + " ==> " + msg; return (Rejected.id, msg)
        }
        (Success.id, "Success")
      }
    }
    validated = true
    (Success.id, "Success")
  }

  /**
    * Executes the requests, return response as String in JSON format.
    * A request must be validated before calling this method.
    *
    * @return
    */
  override def execute :String =
  {
    if (!validated) return "Internal error: Request has not been validated!"
    var responses : List[JValue] = Nil

    action match {
      case "read" | "delete" => responses = responses :+ actionHandler(action)
    }
    m_log.trace(" responses in DataObjectRequestProcessor : {}", responses)
    var cnt : JField = null
    if (responses.nonEmpty &&
      responses.head.extractOpt[JObject].isDefined &&
      responses.head.extract[JObject].obj.nonEmpty)
      cnt = new JField ("contents", new JArray(responses))
    else
      cnt = new JField ("result", JString(ProcessingResult.noDataFound.toString))
    if (!printPretty) compact(render(JObject(List(cnt)))) else pretty(render(JObject(List(cnt)))) + "\n"
  }

  /**
    * Internal function re-structures response:
    * from flat list of Maps, each map is a node to
    * "module_name": { "type" : {} }
    * @param data
    * @return Map - restructured response
    */
  private def remapDataObject(data : List[Map[String, Any]]) : Map[String, List[JValue]] = {
    val remappedData = new scala.collection.mutable.HashMap[String, List[JValue]]
    data.foreach(d => {
      m_log trace s"Content to re-map:   ${d.mkString("{", ", ", "}")}"
      if (d.contains("DL_DataLocation")) {
        val content: JValue =
          d("DL_DataLocation") match {
            case jv: JValue => jv
            case s: String => parse(s, false)
            case _ => val m = "Could not build content mapping, unsupported representation"
              m_log error m
              JObject(Nil)
          }

        val module = "dataLocation"
        val locationJson = parse(compact(render(content)))
        val location = (locationJson) \ "0"
          //if (d.contains("id")) d("id").asInstanceOf[String] else (content \ "id").extractOrElse[String]("UNDEF_id")
        val intermRes :List[JValue] = if (remappedData.contains(module)) remappedData(module) :+ location else List(location)
        remappedData(module) = intermRes
        m_log debug s"Remapped object: ${pretty(render(content))}"
      }
      else {
        val m = "Error: No content to re-map"; m_log error m
      }
    })
    remappedData.toMap
  }

  override def build_ui(data : Map[String, Any] ): JValue = {
    val remappedData: Map[String, List[JValue]] = remapDataObject(List(data))
    if (remappedData.isEmpty) return JObject(JField("result", JString("Node not found")))
    if (remappedData.size > 1) return JObject(JField("result", JString("More than one node found with different module")))
    val theOnlyNode_ModuleName = remappedData.keysIterator.next()
    val nodeToResponse : List[JValue] = remappedData(theOnlyNode_ModuleName)
    if (nodeToResponse.size > 1)  return JObject(JField("result", JString(s"More than one node found in one module: $theOnlyNode_ModuleName")))
    if (nodeToResponse.isEmpty)  return JObject(JField("result", JString(s"Internal error: empty list for module: $theOnlyNode_ModuleName")))
    JObject(JField(theOnlyNode_ModuleName, nodeToResponse.head))
  }

  /**
    * Internal function, execute a piece of request
    * @param action
    * @return
    */
  private def actionHandler(action: String) : JValue =
  {
    val sNode = new DataObject;
    val response = action match {
      case "read" =>   build_ui(sNode.read(keys))
      case "delete" => build(sNode.deleteAll(keys))
    }
    m_log trace s"Response: ${pretty(render(response))}\n"
    response
  }

  /**
    *  Internal function, extracts keys for reading and searching objects and converts their values from JSON to lang types.
    *
    * @return
    */
  override protected def extractKeys : Map[String, Any] =
  {
    val keysJValue : JValue = elements \ "keys"
    if (keysJValue == null || keysJValue == JNothing) return Map.empty

    m_log trace s"Keys:$keysJValue ==> ${compact(render(keysJValue))}"
    val lkeys = MDNodeUtil.extractKeysAsJValue(keysJValue)

    m_log trace s"Extracted keys: ${lkeys.mkString("{", ",", "}")}"
    lkeys.map(key_values => {
      DataObject.searchFields(key_values._1) match {
        case "String"  => key_values._1 -> key_values._2.extract[String]
        case "Int"     => key_values._1 -> key_values._2.extract[Int]
        case "Long"    => key_values._1 -> key_values._2.extract[Long]
        case "Boolean" => key_values._1 -> key_values._2.extract[Boolean]
      }})

  }



}
  object DataObjectRequestProcessor
  {
    val verbs = List("read", "delete")
  }
