package synchronoss.handlers.countly

import mapr.streaming.EventHandler
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

class CountlyHandler extends EventHandler {

  var mHeader : Seq[(String, String)] = new ArrayBuffer[(String, String)]()

  def processRequest(): (Boolean, String) = ???


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


}