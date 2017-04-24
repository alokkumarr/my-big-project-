package sncr.metadata.semantix

import org.json4s.JsonAST.{JArray, JField, JObject, JValue}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.{MDObjectStruct, ProcessingResult}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.ihandlers.InteractionHandler
import sncr.metadata.ui_components.UINode


/**
  * The semantic interaction handler is a layer that provides request translations from request to
  * execution plan to:
  * - read/show semantic data
  * - save/update semantic nodes
  * - manage semantic relations.
  * Then executed it and packs operation result into JSON response.
  */

import MDObjectStruct.formats
class SemanticInteractionHandler (a_docAsJson : JValue, a_printPretty: Boolean = true)
  extends InteractionHandler(a_docAsJson : JValue, a_printPretty) {
  protected override val m_log: Logger = LoggerFactory.getLogger(classOf[SemanticInteractionHandler].getName)

  override def testUIComponent(uicomp: JObject) : Boolean = SemanticInteractionHandler.mandatoryAttributes.forall(attr => (uicomp \ attr ).extractOpt[String].nonEmpty)

  protected var scope : String = SemanticNode.defScope

  private def nextStepPreProcessor() : Unit =
  {
    scope = (docAsJson \ "contents" \ "scope").extractOrElse(scope)
  }

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
    nextStepPreProcessor()

    if (!SemanticInteractionHandler.verbs.exists(_.equalsIgnoreCase(action))){
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
              content_elements.map(element => actionHandler(action, element, moduleName))
            }
          )
        })
      case "read" | "search" | "delete" => responses = responses :+ actionHandler(action, null, null)
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
    * @param semantic_definition
    * @param module_name
    * @return
    */
  private def actionHandler(action: String, semantic_definition : JValue, module_name: String ) : JValue =
  {
    m_log debug "Execute action: " + action
    val sNode = new SemanticNode(semantic_definition, module_name, scope)
    val response = action match {
      case "create" => build(sNode.create)
      case "read" =>   build_ui(sNode.read(keys))
      case "search" => build_ui(sNode.find(keys))
      case "delete" => build(sNode.deleteAll(keys))
      case "update" => build(sNode.update(keys))
    }
    m_log debug s"Response: ${pretty(render(response))}\n"
    response
  }


}


object SemanticInteractionHandler
{

  val verbs = List("create", "update", "delete", "read", "search", "scan")
  val mandatoryAttributes =  List("type", "repository", "module")
}
