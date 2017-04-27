package sncr.metadata.engine.context

import org.json4s.JField
import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._
import sncr.metadata.engine.SourceAsJson

/**
  * Created by srya0001 on 4/26/2017.
  */
trait MDContentBuilder extends SourceAsJson{

  import sncr.metadata.engine.MDObjectStruct.formats
  def headerAsJVal(content:JValue) : JValue = {
    val attributes = SemanticNodeModel.models(SelectModels.headers.id)
    val builtCnt  = attributes.map(attr => {
      val attributeVal = attr._2 match {
        case "JString" => { val v = (content \ attr._1).extractOpt[String]; if (v.isDefined && v.nonEmpty) JString(v.get) else JNothing}
        case "JArray" => val v = (content \ attr._1).extractOpt[JArray]; if (v.isDefined && v.nonEmpty) v.get else JNothing
        case _ => JNothing
      }
      (attr._1, attributeVal)
    }).filter(entry => entry._2 != JNothing)
    val r = JObject(builtCnt.map(entry => JField(entry._1, entry._2)))
    m_log trace s"Header result: ${pretty(render(r))}"
    r
  }


}

object SelectModels extends Enumeration {

  val headers     = Value(0, "headers")
  val everything  = Value(1, "everything")
  val node        = Value(2, "node")
  val relation    = Value(3, "relation")
  val content     = Value(4, "content")

}

object SemanticNodeModel{

  // Supported sSelect models
  val models : Map[Int, List[(String, String)]] =
    Map(0 -> List(
      ("id","JString"),
      ("dataSecurityKey","JString"),
      ("type","JString"),
      ("metric","JString"),
      ("metricName","JString"),
      ("customerCode","JString"),
      ("disabled","JString"),
      ("checked","JString"),
      ("supports","JArray"),
      ("module","JString")
    ))



}

