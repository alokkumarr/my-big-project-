package sncr.metadata.engine

import org.json4s.JsonAST.{JArray, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.ui_components.UINode

/**
  * Created by srya0001 on 4/18/2017.
  */
trait UIResponse extends Response {

    override protected val m_log: Logger = LoggerFactory.getLogger(classOf[UIResponse].getName)

    import MDObjectStruct.formats
    private def remap(remappingAttr: String, data : List[Map[String, Any]]) : List[JObject] = {
      data.map(d => {
          m_log debug s"Content to re-map:   ${d.mkString("{", ", ", "}")}"
          if (d.contains("content")) {
          val content: JValue =
          d("content") match {
            case jv: JValue => jv
            case s: String => parse(s, false, false)
            case _ => val m = "Could not build content mapping, unsupported representation"
              m_log error m
              JObject(Nil)
          }
          val contentType = if (d.contains(remappingAttr)) d(remappingAttr).asInstanceOf[String] else (content \ remappingAttr).extractOrElse[String]("UNDEF_" + remappingAttr)
          val res = JObject((contentType, content))
          m_log debug s"Remapped object: ${pretty(render(res))}"
          res
        }
        else {
          val m = "Error: No content to re-map"; m_log error m
          JObject(Nil)
        }
      })
    }

    def build_ui(remappingAttr: String, data : List[Map[String, Any]] ) : JObject = {
      val lo: List[JObject] = remap(remappingAttr, data)
      val genArtList: List[String] = UINode.UIArtifacts :+ ("UNDEF_" + remappingAttr)
      JObject(genArtList.map( contentType =>
        JField(contentType , JArray(lo.map( o => ( o \ contentType ).extractOpt[JValue]).filter(e => {e.isDefined && e.nonEmpty} ).map(_.get))))
        .filter( e => {
          e._2 match{
            case JArray(a) => { m_log debug s"Check array element: ${e._1} => ${a.mkString("{", ",", "}")}, size = ${a.size}"; a.nonEmpty && a(0) != JNothing }
            case JObject(o) => { m_log debug s"Check object: ${e._1} => ${compact(render(e._2))}"; o.nonEmpty }
            case _ => false
          }
        })
      )
    }

  def build_ui(remappingAttr: String, data : Map[String, Any] ) : JObject = {
    val lo: List[JObject] = remap(remappingAttr, List(data))
    val genArtList: List[String] = UINode.UIArtifacts :+ ("UNDEF_" + remappingAttr)
    if (lo.isEmpty) return JObject(JField("result", JString("Node not found")))
    val onlyNode = lo.head
    val o = genArtList.map( contentType => {
      val onlyNodeContent = (onlyNode \ contentType).extractOpt[JValue]
      if (onlyNodeContent.isDefined)
        JField(contentType, onlyNodeContent.get)
      else
        JField(contentType, JNothing)
      }
    ).filter( _._2 != JNothing)
    JObject(o)
  }

}


