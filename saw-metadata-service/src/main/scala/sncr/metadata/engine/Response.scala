package sncr.metadata.engine


import org.json4s.JsonAST.{JArray, JBool, JLong, JNothing, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by srya0001 on 3/3/2017.
  */
trait Response {

  protected val m_log: Logger = LoggerFactory.getLogger(classOf[Response].getName)

  def build(res: (Int, String)) : JValue = new JObject(List(JField("result", new JString(ProcessingResult(res._1).toString)),
                                                            JField("message", new JString(res._2))))

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


object ResponseConverter{

  val m_log: Logger = LoggerFactory.getLogger("ResponseObject")

  def convertToJavaMapList( response: JValue) : java.util.ArrayList[java.util.HashMap[String, Object]] =
  {
    def convertJObjectToMap(o: JObject ) : java.util.HashMap[String, Object] =
    {
      val resMap = new java.util.HashMap[String, Object]
      o.obj.foreach(kv => resMap.put(kv._1, compact(render(kv._2))))
      resMap
    }
    val resList : java.util.ArrayList[java.util.HashMap[String, Object]] = new java.util.ArrayList
    if (response == JNothing)
    {
      m_log error s"Empty response. Nothing to convert."
      return resList
    }

    response match {
      case a: JArray => a.arr.foreach { case ae: JObject => resList.add(convertJObjectToMap(ae))
      case _ => s"Inappropriate JSON structure passed to convert to Map List"}
      case o: JObject => resList.add(convertJObjectToMap(o))
      case _ => m_log error s"Inappropriate JSON structure passed to convert to Map List"
    }
    resList

  }

}