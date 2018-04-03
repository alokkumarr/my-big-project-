package controllers

import com.synchronoss.querybuilder.{KPIDataQueryBuilder, SAWElasticSearchQueryExecutor}
import com.synchronoss.querybuilder.model.kpi.KPIExecutionObject
import model.ClientException
import org.json4s.{JArray, JNothing}
import org.json4s.JsonAST.{JObject, JString, JValue}
import org.json4s.native.JsonMethods.{compact, parse, render}
import play.mvc.Result
import sncr.metadata.engine.context.SelectModels
import sncr.metadata.semantix.SemanticNode

class KPIBuilder extends BaseController {

  def process: Result = {
    handle(doProcess)
  }

  private def doProcess(json: JValue, ticket: Option[Ticket]): JValue = {
    m_log trace("Validate and process request:  " + compact(render(json)))
    val (userId: Integer, userFullName: String) = ticket match {
      case None => throw new ClientException(
        "Valid JWT not found in Authorization header")
      case Some(ticket) =>
        (ticket.userId, ticket.userFullName)
    }
    val action = (json \ "action").extract[String].toLowerCase;

    action match {
        case "fetch" => {
          val semanticId = extractKey(json,"semanticId")
          val semanticNodeJson = readSemanticNode(semanticId)
          val responseJson = json merge semanticNodeJson
          responseJson
        }
        case "execute" => {
          val jsonString: String = compact(render(json));
          val executionObject : KPIExecutionObject = new KPIDataQueryBuilder(jsonString).buildQuery();
          val data = SAWElasticSearchQueryExecutor.executeReturnDataAsString(executionObject)
          val responseJson = parse(data)
          responseJson
        }
      }
  }

    private def readSemanticNode(semanticId: String): JObject = {
      val semanticNode = SemanticNode(semanticId, SelectModels.relation.id)
      semanticNode.getCachedData("content") match {
        case content: JObject => content
        case _ => throw new ClientException("no match")
      }
    }

  private def checkSemanticwithKPIfieldsPresent(semanticNodeJson : JObject) =
  {
    val artifacts = semanticNodeJson \ "artifacts" match {
      case artifacts: JArray => artifacts.arr
      case JNothing => List()
      case obj: JValue => unexpectedElement(obj, "array", "artifacts")
    }
   checkKpiEligibleColumns(artifacts)
  }

  private def checkKpiEligibleColumns(artifacts: List[JValue]) = {
    if(artifacts.size>1)
      throw ClientException("Multiple artifacts is not supported for the KPI Builder")
    val columnElements = artifacts.flatMap((artifact: JValue) => {
      val columns = extractArray(artifact, "columns")
      if (columns.size < 1)
        throw new ClientException("No column present in artifact ")
      columns.filter(kpiEligible(_))
    })

    if (columnElements.isEmpty)
      throw ClientException("No kpiEligible column present in artifacts")
    columnElements
  }

  private def extractKey(json: JValue, property: String) = {
    try {
      val JString(value) = (json \ "keys")(0) \ property
      value
    } catch {
      case e: Exception =>
        throw new ClientException("ID not found in keys property")
    }
  }
  private def kpiEligible(column: JValue) = {
    (column \ "kpiEligible").extractOrElse[Boolean](false) == true
  }

  def extractArray(json: JValue, name: String): List[JValue] = {
    json \ name match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json, "array", name)
    }
  }
  def unexpectedElement(json: JValue, expected: String, location: String): Nothing = {
    val name = json.getClass.getSimpleName
    throw new ClientException(
      "Unexpected element: %s, expected %s, at %s".format(
        name, expected, location))
  }
}

