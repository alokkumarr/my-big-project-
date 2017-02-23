package scala.sncr.metadata.semantix

import org.json4s.JsonAST.{JInt, JString, _}
import org.json4s.native.JsonMethods._
import org.json4s.{JField => _, JNothing => _, JObject => _, JValue => _, _}
import sncr.metadata.MDObjectStruct
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.semantix.SearchDictionary
import MDObjectStruct.formats
/**
  * Created by srya0001 on 2/17/2017.
  */
class SemanticMDRequestHandler(val docAsJson : JValue)  {


  def buildResponse(res: (Int, String)) : JValue = new JObject(List(JField("result", new JInt(res._1)),JField("reason", new JString(res._2))))

  def buildResponse(data : Map[String, Any]) : JValue = {
    new JObject(data.map(d => JField(d._1, d._2 match {
      case s: String => new JString(d._2.asInstanceOf[String])
      case i: Int => new JInt(d._2.asInstanceOf[Int])
      case l: Long => new JLong(d._2.asInstanceOf[Long])
      case b: Boolean => new JBool(d._2.asInstanceOf[Boolean])
      case _ => JNothing
    })).toList)
  }

  def buildResponse(data : List[Map[String, Any]]) : JValue = new JArray(data.map(d => buildResponse(d)))

  def execute :String =
  {
    val elements : JValue = docAsJson \ "contents"
    val ticket: JValue = docAsJson \ "ticket"
    val action = (docAsJson \ "action").extract[String].toLowerCase
    val responses = elements.children.map( content_element => {
      val sNode = new SemanticNode(ticket, content_element)
      action match {
        case "create" => buildResponse(sNode.storeNode)
        case "retrieve" => buildResponse(sNode.retrieveNode(extractKeys))
        case "search" => buildResponse(sNode.searchNodes(extractKeys))
        case "delete" => buildResponse(sNode.removeNode(extractKeys))
        case "update" => buildResponse(sNode.modifyNode(extractKeys))
      }
    })
    val cnt = JField("contents", new JArray(responses))
    val r_ticket = JField("ticket", ticket)
    val res = JField("performed_action", new JString(action))
    val requestResult = new JObject(List(r_ticket, res, cnt))
    compact(render(requestResult))
  }

  def extractKeys : Map[String, Any] =
  {
    val filter_values : Map[String, JValue]= (docAsJson \ "contents" \ "keys")
      .children.filter( jv => SearchDictionary.searchFields.contains( jv.asInstanceOf[JField]._1 ))
      .map( jv => jv.asInstanceOf[JField]._1 -> jv.asInstanceOf[JField]._2  ).toMap

    filter_values.map(key_values =>
      SearchDictionary.searchFields(key_values._1) match {
        case "String"  => key_values._1 -> key_values._2.extract[String]
        case "Int"     => key_values._1 -> key_values._2.extract[Int]
        case "Long"    => key_values._1 -> key_values._2.extract[Long]
        case "Boolean" => key_values._1 -> key_values._2.extract[Boolean]
      })
  }

  def validate : (Int, String ) = {

    if (docAsJson == null || docAsJson.toSome.isEmpty)
      (Error.id, "Validation fails: document is empty")

    SemanticMDRequestHandler.requiredFields.keySet.foreach {
      case k@"ticket" => SemanticMDRequestHandler.requiredFields(k).foreach(rf => {
        val fieldValue = docAsJson \ k \ rf
        if (fieldValue == null || fieldValue.extractOpt[String].isEmpty)
          return (Rejected.id, s"Required token field $k.$rf is missing or empty")
      })
      case k@"root" => {
        SemanticMDRequestHandler.requiredFields(k).foreach {
          case rf@"contents" =>
            val fieldValue = docAsJson \ rf
            if (fieldValue == null || !fieldValue.isInstanceOf[JObject] || fieldValue.toSome.isEmpty)
              return (Rejected.id, s"Required object $k.$rf is missing or empty")
          case rf@"action" => SemanticMDRequestHandler.requiredFields(k).foreach(rf => {
            val fieldValue = docAsJson \ rf
            if (fieldValue == null || fieldValue.extractOpt[String].isEmpty)
              return (Rejected.id, s"Action field $k.$rf is missing or empty")
          })
        }
      }
    }
    val action = (docAsJson \ "action").extract[String].toLowerCase()
    if (!List("create", "update", "delete", "retrieve", "search").exists(_.equalsIgnoreCase(action)))
      return (Rejected.id, s"Action is incorrect: $action")
    if (action.equalsIgnoreCase("retrieve") || action.equalsIgnoreCase("search")) {
      if ((docAsJson \ "contents" \ "keys") == null || (docAsJson \ "keys").toSome.isEmpty)
        return (Rejected.id, s"Keys list (filter) is missing or empty")

      val filteringKeys = (docAsJson \ "contents" \ "keys").children.filter( jv => SearchDictionary.searchFields.contains( jv.asInstanceOf[JField]._1 ) )
      if ( filteringKeys.isEmpty)
        return (Rejected.id, s"Keys list does not have any pre-defined keys")
    }
    (Success.id, "Success")
  }

}


object SemanticMDRequestHandler{

  val requiredFields = Map(
    "ticket" -> List("tickedId", "masterLoginId","customer_code","dataSecurityKey","userName"),
    "root" -> List("action", "contents")
  )

  def parseAndValidate(document: String) : (Int, String ) = {
    val docAsJson = parse(document, false, false)
    val rh = new SemanticMDRequestHandler(docAsJson)
    rh.validate
  }

}
