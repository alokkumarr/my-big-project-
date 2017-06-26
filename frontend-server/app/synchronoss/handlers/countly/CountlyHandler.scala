package synchronoss.handlers.countly

import java.util.Base64

import mapr.streaming.EventHandler
import util._
import play.api.libs.json._
import synchronoss.data.countly.model._

import scala.collection.mutable.ArrayBuffer

class CountlyHandler extends EventHandler {

  var mHeader : Seq[(String, String)] = new ArrayBuffer[(String, String)]()

  def processRequest(): (Boolean, String) = ???

  def mapMetricData(m : Metrics , m_param: (String, JsValue)): Metrics =
  {
//      m_log.debug( "Process metric data " + m_param.toString())
      m_param._1 match
      {
        case "_os"         => m._os = m_param._2.as[JsString].value
        case "_os_version" => m._os_version = m_param._2.as[JsString].value
        case "_device"     => m._device = m_param._2.as[JsString].value
        case "_resolution" => m._resolution = m_param._2.as[JsString].value
        case "_carrier"    => m._carrier = m_param._2.as[JsString].value
        case "_app_version" =>  m._app_version  = m_param._2.as[JsString].value
        case "_density"     =>  m._density = m_param._2.as[JsString].value
        case "_locale"      =>  m._locale = m_param._2.as[JsString].value
        case "_store"       =>  m._store = m_param._2.as[JsString].value
        case _ => m_log debug "Metrics - unsupported key: " + m_param._1
      }
      m
  }

  def mapEventData(ed: EventData, pair:(String, JsValue) ): EventData =
  {
//    m_log.debug( "Process event data " + pair.toString())
    pair._1 match {
      case "key" => ed.key = pair._2.as[JsString].value
      case "count" => ed.count = RTUtils.toString(pair._2).getOrElse("-1")
      case "hour" => ed.hour = RTUtils.toString(pair._2).getOrElse("-1")
      case "dow" => ed.dow = RTUtils.toString(pair._2).getOrElse("-1")
      case "timestamp" => ed.timestamp =  RTUtils.toString(pair._2).getOrElse("-1")
      case "dur" => ed.dur = RTUtils.toString(pair._2).getOrElse("-1")
      case "sum" => ed.sum = RTUtils.toString(pair._2).getOrElse("-1")
      case _ => m_log debug "Unsupported key: " + pair._1
    }
    ed
  }

  def mapBaseEventData[T <: BaseEvent](ev: T, tuple: (String, String)): T  =
  {
//   m_log debug "Process header data: " + tuple.toString()
    tuple._1 match
    {
      case "app_key" => ev.app_key = tuple._2
      case "device_id" => ev.device_id = tuple._2
        //TODO: default values for integer types
      case "begin_session" => ev.begin_session = tuple._2
      case "session_duration" => ev.session_duration = tuple._2
      case "end_session" => ev.end_session = tuple._2
      case "dow" => ev.dow = tuple._2
      case "dur" => ev.hour = tuple._2
      case "ip_address" => ev.ip_address = tuple._2
      case "country_code" => ev.country_code = tuple._2
      case "city" => ev.city = tuple._2
      case "location" => ev.location = tuple._2
      case "old_device_id" => ev.old_device_id = tuple._2
      case "timestamp" => ev.timestamp = tuple._2
      case "hour" => ev.hour = tuple._2
      case "sdk_version" => ev.sdk_version= tuple._2
      case _ => m_log debug "Unsupported key: " + tuple._1
    }
    ev
  }

  def mapUserData(ud: UserDetails, tuple: (String, JsValue)): UserDetails =
  {
    m_log debug "Process user details: " + tuple.toString()
    tuple._1 match {
      case "name" => ud.name = tuple._2.as[JsString].value
      case "username" => ud.username = tuple._2.as[JsString].value
      case "email" => ud.email = tuple._2.as[JsString].value
      case "organization" => ud.organization = tuple._2.as[JsString].value
      case "phone" => ud.phone = tuple._2.as[JsString].value
      case "picture" => ud.picture = tuple._2.as[JsString].value
      case "gender" => ud.gender = tuple._2.as[JsString].value
      case "byear"     => ud.byear = RTUtils.toString(tuple._2).getOrElse("")
      case _ => m_log debug "Unsupported key " + tuple._1
    }
    ud
  }

  def validateKey(data: JsObject, k: String, path: JsPath): Boolean =
  {
//    m_log debug "Validate header"
    val lk_app_key = path.json.pick
    val pickResult = data.transform( lk_app_key )
    val result: Boolean = pickResult match {
      case JsError(_) =>  m_log debug "Mandatory key " + k + " not found "; false
      case _ => true
    }
    result
  }

  def extractKey(data: JsObject, k: String, path: JsPath): JsValue =
  {
    val lk_app_key = path.json.pick
    val pickResult = data.transform( lk_app_key )
    pickResult match {
      case JsError(_) =>  m_log debug "Mandatory key " + k + " not found "; null
      case _ => pickResult.get
    }
  }

  def mapCrashData(crash :Crash, pair: (String, JsValue) ): Crash  = {

    pair._1 match {
      case "_os" => crash._os = pair._2.as[JsString].value
      case "_os_version" => crash._os_version = pair._2.as[JsString].value
      case "_manufacture" => crash._manufacture = pair._2.as[JsString].value
      case "_device" => crash._device = pair._2.as[JsString].value
      case "_resolution" => crash._resolution = pair._2.as[JsString].value
      case "_app_version" => crash._app_version = pair._2.as[JsString].value
      case "_cpu" => crash._cpu = pair._2.as[JsString].value
      case "_opengl" => crash._opengl = RTUtils.toString(pair._2).getOrElse("n/a")
      case "_ram_current" => crash._ram_current = RTUtils.toString(pair._2).getOrElse("0")
      case "_ram_total" => crash._ram_total = RTUtils.toString(pair._2).getOrElse("0")
      case "_disk_current" => crash._disk_current = RTUtils.toString(pair._2).getOrElse("0")
      case "_disk_total" => crash._disk_total = RTUtils.toString(pair._2).getOrElse("0")
      case "_bat" => crash._bat = RTUtils.toString(pair._2).getOrElse("0")
      case "_bat_current" => crash._bat_current = RTUtils.toString(pair._2).getOrElse("0")
      case "_bat_total" => crash._bat_total = RTUtils.toString(pair._2).getOrElse("0")
      case "_orientation" => crash._orientation = pair._2.as[JsString].value
      case "_root" => crash._root = RTUtils.toString(pair._2).getOrElse("false")
      case "_online" => crash._online = RTUtils.toString(pair._2).getOrElse("false")
      case "_muted" => crash._muted = RTUtils.toString(pair._2).getOrElse("false")
      case "_background" => crash._background = RTUtils.toString(pair._2).getOrElse("false")
      case "_name" => crash._name = pair._2.as[JsString].value
      case "_error" => crash._error = pair._2.as[JsString].value
      case "_run" => crash._run = RTUtils.toString(pair._2).getOrElse("0")
      case "_nonfatal" => crash._nonfatal = RTUtils.toString(pair._2).getOrElse("false")
      case "_logs" => crash._logs = RTUtils.toString(pair._2).getOrElse("false")
      case "_error_details" => crash._error_details = Base64.getDecoder.decode(pair._2.as[JsString].value)
      case _ => m_log debug "Not supported key: " + pair._1
    }
    crash
  }


  def mapSegmentationData(target: scala.collection.mutable.HashMap[String, String], src: (String, JsValue)): scala.collection.mutable.HashMap[String, String] =
  {
    target += (src._1 -> (src._2 match {
      case JsNumber(n) => n.intValue().toString
      case JsString(s) => s
      case JsBoolean(b) => b.toString
      case JsObject(o) => o.mkString
      case JsNull => ""
      case JsArray(a) => a.mkString(",")
    }))
    target
  }

}