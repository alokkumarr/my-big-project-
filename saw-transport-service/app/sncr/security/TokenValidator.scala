package sncr.security

import java.io.IOException
import java.util.concurrent.{ExecutionException, Future, TimeUnit}

import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.http.HttpResponse
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.BasicResponseHandler
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._
import sncr.saw.common.config.SAWServiceConfig
import sncr.ts.HTTPRequest

/**
  * Created by srya0001 on 1/27/2017.
  */
class TokenValidator(token: String) extends HTTPRequest{

  override val m_log: Logger = LoggerFactory.getLogger(classOf[TokenValidator].getName)


  /**
    *  Sends POST request to SAW security to validate token:
    *  Requires HEADER:
    *  Authorization : "Bearer eyJ....FerSH_ec"
    *  Expected response:
    *  {
    *   "valid": true,
    *   "validityMessage": "Token is valid"
    *  }
 *
    * @return true if token is valid
    */
  def validate : Boolean =
  {
    val bypass =  SAWServiceConfig.security_settings.getBoolean("bypass")
    if (bypass) return true

    val sec_host = SAWServiceConfig.security_settings.getString("host")
    val sec_port = SAWServiceConfig.security_settings.getInt("port")
    val sec_path = SAWServiceConfig.security_settings.getString("path")
    val sec_timeout = SAWServiceConfig.security_settings.getInt("timeout")
    var result = false

    try {
      httpClient.start()
      val req_builder: URIBuilder = new URIBuilder
//      req_builder setCharset (Charset.forName("UTF-8"))
      //TODO:: Set path to validate token
      req_builder setPath (sec_path)
      req_builder setHost (sec_host)
      req_builder setPort (sec_port.toInt)
      req_builder setScheme ("http")

      m_log.debug(s"Execute token validation request: ${req_builder.build().toASCIIString}" )

      val request: HttpPost = new HttpPost(req_builder.build())
      request.addHeader("Authorization", "Bearer " + token)

      val future: Future[HttpResponse] = httpClient.execute(request, null)
      val response: HttpResponse = future.get(sec_timeout, TimeUnit.SECONDS)
      m_log debug s"Response code: ${response.getStatusLine().getStatusCode}"

      if (response.getStatusLine.getStatusCode != 200) return false

      val respHandler = new BasicResponseHandler
      val payload =  play.api.libs.json.Json.parse(respHandler.handleResponse(response))

      m_log.debug( "Response: " + Json.prettyPrint(payload))
      val validLookupRes = (__ \ 'valid).json.pick
      val validPickResult = payload.transform( validLookupRes )

      val validityMessageLookupRes = (__ \ 'validityMessage).json.pick
      val validMessagePickResult = payload.transform( validityMessageLookupRes )

      result = validPickResult.isSuccess && validMessagePickResult.isSuccess &&
               validPickResult.get.as[Boolean] &&
               validMessagePickResult.get.as[String].equalsIgnoreCase("Token is valid")

    }
    catch{
      case e:HttpResponseException => m_log.error("Could not process HTTP response",  e); return false
      case e:IOException => m_log.error("Network exception",  e); return false
      case e:TimeoutException => m_log.error("Request to target service timed out",  e); return false
      case e:InterruptedException => m_log.error("Request execution interrupted",  e); return false
      case e:ExecutionException => m_log.error("Request execution failed",  e); return false
      case e:Exception => m_log.error("Internal error",  e); return false
    }
    finally
    {
      httpClient.close()
    }
    result
  }


}
