package sncr.request

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.Result

/**
  * Created by srya0001 on 6/9/2017.
  */
trait TSResponse {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[TSResponse].getName)

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
