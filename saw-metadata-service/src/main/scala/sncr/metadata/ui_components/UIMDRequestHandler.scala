package sncr.metadata.ui_components

import java.util

import org.json4s.JsonAST.{JNull, JObject, JString, _}
import org.json4s.ParserUtil.ParseException
import org.json4s.native.JsonMethods._
import org.json4s.{JField => _, JNothing => _, JObject => _, JValue => _, _}
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.{MDNodeUtil, ProcessingResult, UIResponse}

import scala.collection.mutable



/**
  * The class designed to process UI2MD Rest requests.
  * The class knows the structure of request, how to validate part of
  * the request and execute provided action.
  * The usage id following:
  *  - create UIMDRequestHandler
  *   - if (validate._1 == Success.id) execute
  *  or
  *   - validateAndExecute
  *
  * If validate was not called before execute - the request won't be processed.
  * The execute method rejects a requests indicating that request was not validated.
  * Execute and validateAndExecute returns response to be sent to client as String
  * in JSON format.
  * Created by srya0001 on 2/17/2017.
  */
import sncr.metadata.engine.MDObjectStruct.formats
class UIMDRequestHandler(val docAsJson : JValue, val printPretty: Boolean = true) extends UIResponse {


  protected override val m_log: Logger = LoggerFactory.getLogger(classOf[UIMDRequestHandler].getName)

  var elements : JValue= null
  var keys :  Map[String, Any] = Map.empty

  /**
    * Create response for incorrect request
    *
    * @return
    */
  private def handleRequestIncorrect: List[JValue] =
    List(new JObject(List(JField("result", JString(Rejected.toString)), JField("message", JString("Request structure is not correct")))))


  /**
    * The main method of the class does:
    * - pre-processing
    * - validation
    * if a request is valid - execute the request
    *
    * @return
    */
  def validateAndExecute: String = {

    validate match
    {
      case (0, _) => execute
      case (res_id: Int, r: String) => {
        val msg = render(JObject(List(JField("result", JString(Rejected.toString)),JField("message", JString(r)))))
        val response = if (!printPretty) compact(msg) else pretty(msg)
        m_log debug "Request is not valid: " + response
        response
      }
    }
  }

  private var action : String = null
  private val moduleDesc : mutable.HashMap[String, Map[String, List[JValue]]] = new mutable.HashMap[String, Map[String, List[JValue]]]

  def testUIComponent(uicomp: JObject) : Boolean = UINode.mandatoryAttributes.forall(attr => (uicomp \ attr ).extractOpt[String].nonEmpty)

  /**
    * Required to build Hash map of processed objects
    *
    * @param moduleName
    * @param o
    * @return
    */
  private def extractUIComponents(moduleName: String, o: JObject): Map[String, List[JValue]] =
  {
    //Assume one-level structure
    val node_type = (o \ "type").extractOpt[String]
    val test_module = (o \ "module").extractOpt[String]

    if (node_type.isEmpty) {
      //Try two-level structure:
      o.obj.map(content_element => {
        m_log trace s"Raw UI Component ==> ${content_element}"
        m_log trace s"UI Component:  ${content_element._1} => " + compact(render(content_element._2))
        val compType = content_element._1
        content_element._2 match {
          case ce: JObject =>
            if (!testUIComponent(ce)) throw new Exception ("Mandatory attributes are missing")
            m_log trace s"UI Item from object: ${ce.obj.mkString("{", ",", "}")}"
            (compType, List(ce))
          case ce: JArray =>
            m_log trace s"UI Item, array element: ${ce.arr.mkString("[", ",", "]")}"
            if (ce.arr.forall{ case uicomp:JObject => testUIComponent(uicomp); case _ => false })
                throw new Exception ("Mandatory attributes are missing in one of UI component in the request, reject whole request")
            (compType, ce.arr)
          case _ => throw new Exception(s"UI Component cannot be processed: ${compact(render(content_element._2))} or request structure is not corect")
        }
      }
      ).toMap[String, List[JValue]]
    }
    else if(node_type.nonEmpty && test_module.nonEmpty) Map((node_type.get, List(o)))
    else throw new Exception(s"Incorrect request structure: could not determine UI component type")
  }

  private def mergeUIComponents(headKey : String,
                                mainMap : mutable.HashMap[String, Map[String, List[JValue]]],
                                tail: Map[String, List[JValue]]) : Map[String, List[JValue]] = {
    if (mainMap.contains(headKey)) {
      val temp: Map[String, List[JValue]] = mainMap(headKey)
      return (temp.toSeq ++ tail.toSeq).groupBy { case (uiComp, uiCompContent) => uiComp
      }.mapValues(uic => uic.flatMap { case (uiComp, uiCompContent) => uiCompContent }.toList)
    }
    tail
  }


  /**
    * Pre-processing a request:
    * - extracts content and builds 2 level map:
    *    * module
    *      ** ui type
    * - extracts keys and converts key values
    * - extracts action
    */
  private def preProcessRequest() : Unit = {

    if (docAsJson == null || docAsJson.toSome.isEmpty)
      throw new Exception("Source doc is null or empty")

    if (elements != null ) return
    elements = docAsJson \ "contents"
    m_log trace s"All Content Elements => " + compact(render(elements))

    action = (docAsJson \ "contents" \ "action").extractOpt[String].getOrElse("invalid").toLowerCase()
    m_log debug s"action = $action"
    elements = elements.removeField( pair => pair._1.equalsIgnoreCase("action") )
    keys = extractKeys
    elements = elements.removeField( pair => pair._1.equalsIgnoreCase("keys") )

    UINode.UIModules.foreach( moduleName => elements \ moduleName match {
      case o: JObject  =>  moduleDesc(moduleName) =  mergeUIComponents(moduleName, moduleDesc, extractUIComponents(moduleName, o))
      case a: JArray   => a.arr.foreach {
        case ao: JObject => moduleDesc(moduleName) = mergeUIComponents(moduleName, moduleDesc, extractUIComponents(moduleName, ao))

        case _ => m_log error "Unknown request array-element found"
        }
      case JNothing | JNull  =>
      case _  => m_log error s"Unknown request element found ${elements \ moduleName }"
    })

    m_log debug s"Pre-processing result: ${moduleDesc.mkString}"

  }

  var validated = false

  /**
    * Validates requests, if a request malformed, incorrectly structured or does not have
    * required parts - rejects the requests
    *
    * @return (result as Int, Explanation as String)
    */
  def validate : (Int, String ) = {
    if (docAsJson == null || docAsJson.toSome.isEmpty)
      (Error.id, "Validation fails: document is empty")

    preProcessRequest()

    if (!List("create", "update", "delete", "read", "search", "scan").exists(_.equalsIgnoreCase(action))){
      val msg = s"Action is incorrect: $action"
      m_log error Rejected.id + " ==> " + msg
      return (Rejected.id, msg)
    }
    m_log debug "Validate action and content section"
    action match {
      case "read" | "search" | "delete" =>
      {
        if ( keys == null || keys.isEmpty) {
          val msg = s"Keys list (filter) is missing or empty"; m_log debug Rejected.id + " ==> " + msg; return (Rejected.id, msg)
        }
        if (!keys.keySet.forall(k => UINode.searchFields.contains(k))){
          val msg = s"Keys list has keys that are not defined as searchable! "; m_log debug Rejected.id + " ==> " + msg; return (Rejected.id, msg)
        }
        (Success.id, "Success")
      }
      case "update" | "create" =>
      {
        if (!UINode.UIModules.exists( uic => ( elements \ uic ).extractOpt[JObject].isDefined) &&
          !UINode.UIModules.exists( uic => ( elements \ uic ).extractOpt[JArray].isDefined)) {
          val msg = s"At least one content element is required"
          m_log debug Rejected.id + " ==> " + msg
          return (Rejected.id, msg)
        }

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
  def execute :String =
  {
    if (!validated) return "Internal error: Request has not been validated!"
    var responses : List[JValue] = Nil

    action match {
      case "create" | "update" =>
        moduleDesc.keySet.foreach( moduleName => {
          val UIComponents: Map[String, List[JValue]] = moduleDesc(moduleName)
          responses = responses ++ UIComponents.keySet.flatMap(
            UIComponent => {
              val content_elements = UIComponents(UIComponent)
              content_elements.map(element => actionHandler(action, element, moduleName, UIComponent))
            }
          )
        })
      case "read" | "search" | "delete" => responses = responses :+ actionHandler(action, null, null, null)
    }

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
    * Internal function, execute a piece of request
    *
    * @param action
    * @param content_element
    * @param module_name
    * @param ui_comp_type
    * @return
    */
  private def actionHandler(action: String, content_element : JValue, module_name: String, ui_comp_type: String) : JValue =
  {
    m_log debug "Execute action: " + action
    val sNode = new UINode(content_element, module_name, ui_comp_type)
    val response = action match {
      case "create" => build(sNode.create)
      case "read" => sNode.setFetchMode(UINodeFetchMode.Everything.id); build_ui(sNode.read(keys))
      case "search" => sNode.setFetchMode(UINodeFetchMode.Everything.id); build_ui(sNode.find(keys))
      case "delete" => build(sNode.deleteAll(keys))
      case "update" => build(sNode.update(keys))
    }
    m_log debug s"Response: ${pretty(render(response))}\n"
    response
  }


  /**
    *  Internal function, extracts keys for reading and searching objects and converts their values from JSON to lang types.
    *
    * @return
    */
  private def extractKeys : Map[String, Any] =
  {
    val keysJValue : JValue = docAsJson \ "contents" \ "keys"
    if (keysJValue == null || keysJValue == JNothing) return Map.empty

    m_log trace s"Keys:$keysJValue ==> ${compact(render(keysJValue))}"
    val lkeys = MDNodeUtil.extractKeysAsJValue(keysJValue)

    m_log trace s"Extracted keys: ${lkeys.mkString("{", ",", "}")}"
    lkeys.map(key_values => {
      UINode.searchFields(key_values._1) match {
        case "String"  => key_values._1 -> key_values._2.extract[String]
        case "Int"     => key_values._1 -> key_values._2.extract[Int]
        case "Long"    => key_values._1 -> key_values._2.extract[Long]
        case "Boolean" => key_values._1 -> key_values._2.extract[Boolean]
      }})

  }


}


/**
  * UI2MD Utility object
  */
object UIMDRequestHandler{

  val m_log: Logger = LoggerFactory.getLogger("SemanticMDRequestHandler")

  def getHandlerForRequest(document: String, printPretty: Boolean) : List[UIMDRequestHandler] = {
    try {
      val docAsJson = parse(document, false, false)
      docAsJson.children.flatMap(reqSegment => {
        m_log trace "Process JSON: " + compact(render(reqSegment))
        reqSegment match {
          case rs: JObject => List(new UIMDRequestHandler(rs, printPretty))
          case rs: JArray => rs.arr.map(jv => new UIMDRequestHandler(jv, printPretty))
          case _ => List.empty
        }
      })
    }
    catch{
      case pe:ParseException => m_log error( s"Could not parse the document, error: ", pe ); List.empty
      case x : Throwable => m_log error( s"Exception while parsing the document, error: ", x ); List.empty
    }
  }

  import scala.collection.JavaConversions._
  def getHandlerForRequest4Java(document: String, printPretty: Boolean) : util.List[UIMDRequestHandler] = {
    getHandlerForRequest(document, printPretty)
  }

  def scanSemanticTable(printPretty: Boolean) : String = {
    val srh = new UIMDRequestHandler( null )
    val sNode = new UINode(null, null)
    val result = if (!printPretty) compact(render(srh.build(sNode.scan)))
                 else pretty(render(srh.build(sNode.scan)))
    m_log debug result
    result
  }

}
