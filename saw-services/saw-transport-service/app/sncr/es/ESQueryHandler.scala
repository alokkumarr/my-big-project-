package sncr.es

import java.io.IOException
import java.util.concurrent.{ExecutionException, Future, TimeUnit}

import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.http.HttpResponse
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicResponseHandler
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsObject, JsValue}
import play.libs.Json
import play.mvc.Result
import sncr.metadata.engine.MetadataDictionary
import sncr.request.Extractor
import sncr.saw.common.config.SAWServiceConfig
import sncr.ts.HTTPRequest


/**
  * Created by srya0001 on 1/27/2017.
  */
class ESQueryHandler (ext: Extractor) extends HTTPRequest {

  override val m_log: Logger = LoggerFactory.getLogger(classOf[ESQueryHandler].getName)

  def handleRequest(source : JsValue) : Result =
  {
    val es_protocol = if (SAWServiceConfig.es_conf.hasPath("protocol")) SAWServiceConfig.es_conf.getString("protocol") else "http"

    val es_ip = SAWServiceConfig.es_conf.getString("host")

    val es_port = if (SAWServiceConfig.es_conf.hasPath("port")) Option(SAWServiceConfig.es_conf.getInt("port")) else None

    val timeout = SAWServiceConfig.es_conf.getInt("timeout")

    val uname = if (SAWServiceConfig.es_conf.hasPath("username")) Option(SAWServiceConfig.es_conf.getString("username")) else None
    val pswd = if (SAWServiceConfig.es_conf.hasPath("password")) Option(SAWServiceConfig.es_conf.getString("password")) else None

    val res: ObjectNode = Json.newObject
    res.put("result", "failure")

    val inxName : String = ext.values.get(MetadataDictionary.index_name.toString).get.asInstanceOf [String]
    val objType : String = if (ext.values.contains(MetadataDictionary.object_type.toString))
                               ext.values.get(MetadataDictionary.object_type.toString).get.asInstanceOf[String]
                          else null
    val query : JsValue = ext.values.get(MetadataDictionary.query.toString).get.asInstanceOf[JsValue]
    val verb : String = ext.values.get(MetadataDictionary.verb.toString).get.asInstanceOf [String]

    try {
      httpClient.start()
      val req_builder: URIBuilder = new URIBuilder
      req_builder setPath ("/" + inxName +  "/" + (if (objType != null ) objType + "/" else "" ) + verb)
      req_builder setHost es_ip

      if (es_port.isDefined ) req_builder setPort es_port.get
      req_builder setScheme es_protocol

      if (uname.isDefined && uname.nonEmpty && pswd.isDefined && pswd.nonEmpty)
        req_builder.setUserInfo(uname.get, pswd.get)
      else
        m_log.debug( "Username and/or password is not set - skip authentication settings" )

      m_log.debug(s"Execute ES query: ${req_builder.build().toASCIIString} \n Query: ${if(query == null)"n/a" else query}" )

      val future: Future[HttpResponse] =
        if (query == null ) {
          val request: HttpGet = new HttpGet(req_builder.build())
          httpClient.execute(request, null)
        }
        else{

          var query2 = play.api.libs.json.Json.stringify(query).replace("\\n", "").replace("\\\"", "\"")
          query2 = if (query2.startsWith("\"")) query2.substring(1) else query2
          query2 = if (query2.endsWith("\"")) query2.substring(0,query2.length-1) else query2

          m_log.debug(s"Native query to request: $query" )
          m_log debug s"Add clean query to request: $query2"
          val request: HttpPost = new HttpPost(req_builder.build())
          request.setEntity(new StringEntity(query2))
          httpClient.execute(request, null)
        }


      val response: HttpResponse = future.get(timeout, TimeUnit.MINUTES)
      val respHandler = new BasicResponseHandler
      val msg = s"Response: ${response.getStatusLine.getStatusCode} - ${response.getStatusLine.getReasonPhrase}"

      if (response.getStatusLine.getStatusCode <= 400)
      {
        val payload = play.api.libs.json.Json.parse(respHandler.handleResponse(response))
        val res2 = source.as[JsObject] + ("data" -> payload.as[JsObject])
        m_log debug msg
        return play.mvc.Results.ok(play.api.libs.json.Json.stringify(res2))
      }
      else
        m_log error msg
        res.put("reason", s"Response: ${response.getStatusLine.getStatusCode} - ${response.getStatusLine.getReasonPhrase}")
    }
    catch{
      case e:HttpResponseException => return handleFailure("Could not process HTTP response",  e)
      case e:IOException => return handleFailure("Network exception",  e)
      case e:TimeoutException => return handleFailure("Request to target service timed out",  e)
      case e:InterruptedException => return handleFailure("Request execution interrupted",  e)
      case e:ExecutionException => return handleFailure("Request execution failed",  e)
      case e:Exception => return handleFailure("Internal error",  e)
    }
    finally
    {
      httpClient.close()
    }
    play.mvc.Results.internalServerError(res)
  }

}
