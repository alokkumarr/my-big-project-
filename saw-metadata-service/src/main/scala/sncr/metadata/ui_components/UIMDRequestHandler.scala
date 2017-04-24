package sncr.metadata.ui_components

import java.util

import org.json4s.JsonAST.{JObject, JString, _}
import org.json4s.ParserUtil.ParseException
import org.json4s.native.JsonMethods._
import org.json4s.{JField => _, JNothing => _, JObject => _, JValue => _, _}
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.ProcessingResult
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.ihandlers.InteractionHandler



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
class UIMDRequestHandler(a_docAsJson : JValue, a_printPretty: Boolean = true)
  extends InteractionHandler(a_docAsJson, a_printPretty) {

  protected override val m_log: Logger = LoggerFactory.getLogger(classOf[UIMDRequestHandler].getName)



  override def testUIComponent(uicomp: JObject) : Boolean = UINode.mandatoryAttributes.forall(attr => (uicomp \ attr ).extractOpt[String].nonEmpty)


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
  override def execute :String =
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

}


/**
  * UI2MD Utility object
  */
object UIMDRequestHandler{

  val m_log: Logger = LoggerFactory.getLogger("sncr.metadata.ui_components.UIMDRequestHandler")

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
