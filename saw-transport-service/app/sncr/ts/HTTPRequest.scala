package sncr.ts

/**
  * Created by srya0001 on 5/8/2016.
  */

import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.Result


class HTTPRequest
{

  val m_log: Logger = LoggerFactory.getLogger(classOf[HTTPRequest].getName)
  implicit val httpClient : CloseableHttpAsyncClient = HttpAsyncClients.createDefault()
  implicit val format = org.json4s.DefaultFormats

  def handleFailure(s: String, e: Exception): Result =
  {
    m_log.error(s, e)
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", s)
    res.put("details", e.getMessage)
    play.mvc.Results.internalServerError(res)
  }




}

