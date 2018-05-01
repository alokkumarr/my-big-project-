
package util

import play.api.libs.json._

/**
  * Created by srya0001 on 5/4/2016.
  */
object RTUtils
  {

  def toBoolean(v: JsValue): Boolean =
  {
    v match {
      case m : JsNumber => if (m.value.intValue() == 0) false else true
      case s : JsString => if (s.value.equalsIgnoreCase("false")) false else true
      case b : JsBoolean => b.value
      case a : JsArray =>  false
      case _ => false
    }
  }




  def toInt(v: JsValue): Option[Int] = {
    val nv = v match {
      case m : JsNumber => m.value.intValue()
      case s : JsString => Integer.parseInt(s.value)
      case b : JsBoolean => if ( b.value ) 1 else 0
      case a : JsArray =>  0
      case _ => 0
    }
    Some(nv)
  }

  def toLong(s: String): Option[Long] = {
    try {
      Some(java.lang.Long.getLong(s.trim))
    } catch {
      case e: Exception => None
    }
  }

  def toString(v: JsValue): Option[String] = {
      val nv = v match {
        case m : JsNumber => String.valueOf(m.as[JsNumber].value)
        case s : JsString => s.value
        case b : JsBoolean => b.value.toString()
        case a : JsArray =>  a.as[JsArray].value.mkString(",")
        case _ => String.valueOf(v)
      }
      Some(nv)
  }


}
