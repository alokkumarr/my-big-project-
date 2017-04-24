package sncr.metadata.engine.ihandlers

import org.json4s.JsonAST.{JNull, JObject, JString, _}
import org.json4s.native.JsonMethods._
import org.json4s.{JField => _, JNothing => _, JObject => _, JValue => _, _}
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDNodeUtil
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.responses.UIResponse
import sncr.metadata.ui_components.{UIMDRequestHandler, UINode}

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

class InteractionHandler(val docAsJson : JValue, val printPretty: Boolean = true) extends UIResponse {


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

  protected var action : String = null
  protected val moduleDesc : mutable.HashMap[String, Map[String, List[JValue]]] = new mutable.HashMap[String, Map[String, List[JValue]]]

  protected def testUIComponent(uicomp: JObject) : Boolean = ???

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
  protected def preProcessRequest() : Unit = {

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

  protected var validated = false

  /**
    * Validates requests, if a request malformed, incorrectly structured or does not have
    * required parts - rejects the requests
    *
    * @return (result as Int, Explanation as String)
    */
  def validate : (Int, String ) = ???


  /**
    * Executes the requests, return response as String in JSON format.
    * A request must be validated before calling this method.
    *
    * @return
    */
  def execute :String = ???


  /**
    *  Internal function, extracts keys for reading and searching objects and converts their values from JSON to lang types.
    *
    * @return
    */
  protected def extractKeys : Map[String, Any] =
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


