package synchronoss.ts

/**
  * Created by srya0001 on 5/8/2016.
  */

import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.{ExecutionException, Future, TimeUnit}

import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.http.HttpResponse
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.Result


class HTTPRequest(ip:String, port:Int, timeout:Int)
{

  val m_log: Logger = LoggerFactory.getLogger(classOf[HTTPRequest].getName)
  implicit val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.createDefault()
  implicit val format = org.json4s.DefaultFormats
  val the_ip = ip
  val the_port = port

  def handleFailure(s: String, e: Exception): Result =
  {
    m_log.error(s, e)
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", s)
    res.put("details", e.getMessage)
    play.mvc.Results.internalServerError(res)
  }

  def sendESRequest(verb: String, inxName: String, objType: String, query : String) : Result =
  {
    try {
      httpClient.start()
      val req_builder: URIBuilder = new URIBuilder
      req_builder setCharset (Charset.forName("UTF-8"))
      req_builder setPath ("/" + inxName + "/" + objType + "/" + verb)
      req_builder setHost (the_ip)
      req_builder setPort (the_port.toInt)
      req_builder setScheme ("http")

      m_log.debug(s"Execute ES query: ${req_builder.build().toASCIIString}" )

      val future: Future[HttpResponse] =
      if (query != null &&  !query.isEmpty ) {
        val request: HttpGet = new HttpGet(req_builder.build())
        httpClient.execute(request, null)
      }
      else{
        val request: HttpPost = new HttpPost(req_builder.build())
        m_log.debug(s"Add native query to request: ${query}" )
        request.setEntity(new StringEntity(query))
        httpClient.execute(request, null)
      }

      val response: HttpResponse = future.get(timeout, TimeUnit.SECONDS)
      System.out.println("Response: " + response.getStatusLine())
      val respHandler = new BasicResponseHandler
      return play.mvc.Results.ok(respHandler.handleResponse(response))
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
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "unknown")
    play.mvc.Results.internalServerError(res)
  }



}

