package controllers

import java.time.Instant
import java.util.UUID

import com.synchronoss.querybuilder.{KPIDataQueryBuilder, SAWElasticSearchQueryExecutor}
import com.synchronoss.querybuilder.model.kpi.KPIExecutionObject
import model.ClientException
import org.json4s.JsonAST.{JObject, JString, JValue}
import org.json4s.native.JsonMethods.{compact, parse, render}
import org.json4s.JsonDSL._
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
        case "create" => {
          val semanticId = extractKey(json,"semanticId")
          val instanceJson: JObject = ("semanticId", semanticId) ~
            ("createdTimestamp", Instant.now().toEpochMilli()) ~
            ("userId", userId.asInstanceOf[Number].longValue) ~
            ("userFullName", userFullName)
          val semanticNodeJson = readSemanticNode(semanticId)
          val kpiId = UUID.randomUUID.toString
         // val kPIEligible =
          val mergeJson =
            semanticNodeJson.merge(instanceJson)
          val responseJson = json merge mergeJson
          responseJson
        }
        case "execute" => {
          val jsonString: String = compact(render(json));
        val executionObject : KPIExecutionObject = new KPIDataQueryBuilder(jsonString).buildQuery();
           val data = SAWElasticSearchQueryExecutor.executeReturnDataAsString(executionObject)
          val responseJson = parse(data)
         //  val kPIEligibleFields =
       //   val mergeJson =
      //      semanticNodeJson.merge(instanceJson)
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

  private def fetchSemanticwithKPIfields(semanticNodeJson : JObject): Unit =
  {
    print(semanticNodeJson)
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
}

