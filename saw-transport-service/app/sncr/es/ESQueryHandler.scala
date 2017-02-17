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
import sncr.metadata.MetadataDictionary
import sncr.request.Extractor
import sncr.saw.common.config.SAWServiceConfig
import sncr.ts.HTTPRequest

/**
  * Created by srya0001 on 1/27/2017.
  */
class ESQueryHandler (ext: Extractor) extends HTTPRequest {

  override val m_log: Logger = LoggerFactory.getLogger(classOf[ESQueryHandler].getName)

//  val uname = "elastic"
//  val pswd = "xuw3dUraHapret"


  def esRequest( source : JsValue) : Result =
  {
    val es_ip = SAWServiceConfig.es_conf.getString("host")
    val es_port = SAWServiceConfig.es_conf.getInt("port")
    val timeout = SAWServiceConfig.es_conf.getInt("timeout")
    val uname = SAWServiceConfig.es_conf.getString("username")
    val pswd = SAWServiceConfig.es_conf.getString("password")


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
//      req_builder. setCharset (Charset.forName("UTF-8"))
      req_builder setPath ("/" + inxName +  "/" + (if (objType != null ) objType + "/" else "" ) + verb)
      req_builder setHost es_ip
      req_builder setPort es_port.toInt
      req_builder setScheme "http"
      req_builder.setUserInfo(uname, pswd)

      m_log.debug(s"Execute ES query: ${req_builder.build().toASCIIString}" )

      val future: Future[HttpResponse] =
        if (query != null ) {
          val request: HttpGet = new HttpGet(req_builder.build())
          httpClient.execute(request, null)
        }
        else{
          val request: HttpPost = new HttpPost(req_builder.build())
          m_log.debug(s"Add native query to request: $query" )
          request.setEntity(new StringEntity( play.api.libs.json.Json.stringify(query)))
          httpClient.execute(request, null)
        }


      val response: HttpResponse = future.get(timeout, TimeUnit.SECONDS)
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
