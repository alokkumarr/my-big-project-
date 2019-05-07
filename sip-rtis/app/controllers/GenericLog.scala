package controllers

import java.util.NoSuchElementException

import com.fasterxml.jackson.databind.node.ObjectNode
import mapr.streaming.LogMessageHandler
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.{Controller, Http, Result}

import scala.collection.Seq

/**
  * Created by srya0001 on 12/4/2017.
  */
class GenericLog extends Controller{

  val m_log: Logger = LoggerFactory.getLogger(classOf[GenericLog].getName)

  LogMessageHandler.loadLogHandlerConf

  private def success(): Result = {
    play.mvc.Results.ok("success")
  }

  private def failure( freason : String ): Result = {
    play.mvc.Results.internalServerError("failed with: " + freason)
  }

  private def failedNoParameters(): Result = {
    play.mvc.Results.badRequest("Failed: no parameters have been passed.")
  }

  def doPost(CID : String, LOG_TYPE : String) : Result = {
    val ctx: Http.Context = Http.Context.current.get
    //val query: Map[String, Seq[String]] = ctx._requestHeader().queryString
    m_log.info("URI: " + ctx._requestHeader().uri)
    m_log.info("Raw query string: " + ctx._requestHeader.rawQueryString)
    m_log.info(s"Headers:\n${ctx._requestHeader().headers.toMap.mkString("\n")}")
    val body = new String (ctx.request.body.asBytes().toArray)
    m_log.info("BODY: " + body)

    try {
      val handler = new LogMessageHandler(CID, LOG_TYPE)
      handler.sendMessage(body, "none");
    }catch {
      case noSuchElement: NoSuchElementException => return failure( s"Configuration does not contain pair: ${CID + LogMessageHandler.delimiter + LOG_TYPE}, please fix configuration" )
      case e: Throwable => return failure( e.getMessage )
    }
    success
  }

}
