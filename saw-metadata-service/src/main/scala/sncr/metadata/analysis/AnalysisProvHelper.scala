package sncr.metadata.analysis

import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.{MDNodeUtil, MDObjectStruct, Response}
import sncr.metadata.engine.ProcessingResult._
import MDObjectStruct.formats

/**
  * Created by srya0001 on 3/3/2017.
  */
class AnalysisProvHelper(var requests: JValue) extends Response {

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisProvHelper].getName)
  def requestsParsed : Boolean = requests != JNothing
  def handleRequests(printPretty: Boolean) : String =  {
    requests match{
      case JArray(arr) => val d = render(JArray(arr.map( v => handleRequest(v))))
                          if (!printPretty) compact(d) else pretty(d)
      case _ => val msg = s"Request is not correct. reject it"; m_log error msg; msg
    }
  }

  def handleExecuteRequest: JValue =  {
    requests match{
      case JArray(arr) => JArray(arr.map {
        case o: JObject => {
          val keys: Map[String, Any] = MDNodeUtil.keyExtractor(o \ "keys")
          val anh = new AnalysisNode
          build(anh.listHeaders(keys))
        }
        case _ => {
          m_log error s"Request is not correct. reject it"; JNothing
        }
      })
      case _ => m_log error s"Request is not correct. reject it"; JNothing
    }
  }

  var verb: String = null


  def handleRequest(a_request : JValue) : JValue =  {
    a_request match {
      case o: JObject => {
        verb = (o \ "verb").extractOpt[String].getOrElse("none")
        val content = o \ "content"
        val keys : Map[String, Any] = MDNodeUtil.keyExtractor(o \ "keys")
        verb match {
          case "write"        => val anh = new AnalysisNode(content);  build(anh.write)
          case "read"         => val anh = new AnalysisNode;  build(anh.read(keys))
          case "update"       => val anh = new AnalysisNode(content);  build(anh.update(keys))
          case "search"       => val anh = new AnalysisNode;  build(anh.find(keys))
          case "delete"       => val anh = new AnalysisNode;  build(anh.deleteAll(keys))
          case "scan"         => val anh = new AnalysisNode;  build(anh.scan)
          case "list-headers" | "execute" =>  val anh = new AnalysisNode;  build(anh.listHeaders(keys))
          case "none"         => JObject(  JField("result", JInt(Rejected.id)), JField("reason", JString("Incorrect verb")))
        }
      }
      case _ => val msg = s"Request is not correct. reject it"
                    m_log error msg
                    JObject(  JField("result", JInt(Rejected.id)), JField("reason", JString(msg)))
    }
  }


}


object AnalysisProvHelper{

  val m_log: Logger = LoggerFactory.getLogger("AnalysisProvHelper")

  def apply(source: String): AnalysisProvHelper = {
    try {
      val requests = parse(source, false, false)
      new AnalysisProvHelper( requests )
    }
    catch {
      case x: Throwable => m_log error("Could not parse request: ", x); throw x
    }
  }

}



