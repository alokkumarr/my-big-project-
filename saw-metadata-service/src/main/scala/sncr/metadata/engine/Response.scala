package sncr.metadata.engine

import org.json4s.JsonAST.{JArray, JBool, JLong, JNothing, _}

/**
  * Created by srya0001 on 3/3/2017.
  */
trait Response {


  def build(res: (Int, String)) : JValue = new JObject(List(JField("result", new JInt(res._1)),JField("reason", new JString(res._2))))

  def build(data : Map[String, Any]) : JValue = {
    new JObject(data.map(d => JField(d._1, d._2 match {
      case s: String => new JString(d._2.asInstanceOf[String])
      case i: Int => new JInt(d._2.asInstanceOf[Int])
      case l: Long => new JLong(d._2.asInstanceOf[Long])
      case b: Boolean => new JBool(d._2.asInstanceOf[Boolean])
      case j: JValue => j
      case _ => JNothing
    })).toList)
  }

  def build(data : List[Map[String, Any]]) : JValue = new JArray(data.map(d => build(d)))


}
