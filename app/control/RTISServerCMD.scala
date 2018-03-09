package control

import java.time.{Duration, LocalDateTime, Period}

import controllers.Stat
import mapr.streaming.{EventHandler, StreamHelper}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._

/**
  * Created by srya0001 on 10/13/2016.
  */
class RTISServerCMD(CMD: String, params: JsValue ) {

//  if (params == null)
//    throw new RTException(ErrorCodes.MandatoryParameterIsNull, "PARAMETERS")

  var SwitchStreams_parameters : JsObject = if (params != null ) params.as[JsObject] else null
  val param_value = params
  var streamKey: String = null
  var appKey: String = null
  var verbose : Boolean = false

  def valid: Boolean = {

    CMD match {
      case "setActive" => if (!extractStreamKey) return false else true
      case "sr" => true
      case "getList" => true
      case "getActive" => true
      case "getApps" => true
      case _ => false
    }
  }

//          case EventHandler.primaryStreamsKey => SwitchStreams_parameter = EventHandler.primaryStreamsKey; true
//  case EventHandler.secondaryStreamsKey => SwitchStreams_parameter = EventHandler.secondaryStreamsKey; true


  def execute : JsObject = {

    extractAppKey
    CMD match {
      case "setActive" =>
          StreamHelper.swapStream(appKey, streamKey)
      case "sr" =>
        extractVerbose
        if (verbose) {
          JsObject( Seq("result" -> JsString("ok") ,"uptime" -> JsString(getUptime))) ++ (if (!appKey.equalsIgnoreCase("ALL"))
                    JsObject( List(
                      "appkey" -> JsString(appKey),
                      "requests" -> JsNumber(Stat.getRequestStat(appKey, 0)),
                      "rejectedrequests" -> JsNumber(Stat.getRejectedRequestStat(appKey, 0))).toMap)
                    else
                    JsObject(Nil))
          }
        else
          JsObject( Nil )
      case "getActive" =>
          StreamHelper.getActiveStreams(appKey)

      case "getList" =>
          StreamHelper.getStreams(appKey)

      case "getApps" =>
         JsObject( Seq("result" -> JsString("ok"), "appkeylist" -> JsString(EventHandler.getAppKeys.mkString(","))))

      case _ => JsObject( Seq("error" -> JsString("Unsupported command")) )

    }
  }

  private def extractStreamKey() : Boolean =
  {
    if ( SwitchStreams_parameters == null ) return false
    val stream_path: JsPath = (__ \ 'Stream)
    val stream_key = stream_path.json.pick
    val result = SwitchStreams_parameters.transform (stream_key)
    if (result == null || ! result.isSuccess) return false
    streamKey = result.get.as[JsString].value
    true
  }

  private def extractAppKey : String = {
    if (SwitchStreams_parameters != null) {
      val appkey_path: JsPath = (__ \ 'AppKey)
      val appkey_j = appkey_path.json.pick
      val result = SwitchStreams_parameters.transform(appkey_j)
      appKey = if (result == null || !result.isSuccess) "ALL"
      else result.get.as[JsString].value
    }
    else {
      appKey = "ALL"
    }
    appKey
  }

  private def extractVerbose : Boolean =
  {
    if (SwitchStreams_parameters != null) {
      val verb_path: JsPath = (__ \ 'verbose)
      val verb_j = verb_path.json.pick
      val result = SwitchStreams_parameters.transform(verb_j)
      verbose = if (result == null || !result.isSuccess || !result.get.as[JsBoolean].value) false else true
    }
    else{
      verbose = false
    }
    verbose
  }

  private def getUptime : String =
  {
    val t_now = LocalDateTime.now()
    val per = Period.between(Stat.getStartTime.toLocalDate, t_now.toLocalDate)
    val dur = Duration.between(Stat.getStartTime, t_now)
    val inOneDay = dur.getSeconds % (24 * 60 * 60)
    val uptimeStr = s"uptime: ${per.getMonths}m ${per.getDays}d " +
      s" ${Math.abs(Math.round(inOneDay / 3600))}:${
        Math.abs(Math.round((inOneDay % 3600) / 60))
      }:${Math.abs(dur.getSeconds % 60)}"
    uptimeStr
  }


}

object RTISServerCMD {

  val m_log: Logger = LoggerFactory.getLogger(classOf[RTISServerCMD].getName)
  val CMDs = List("setActive", "getList", "getActive", "sr", "getApps")

}

