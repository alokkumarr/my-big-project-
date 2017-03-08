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
class AnalysisProvHelper(val source: String) extends Response {

  val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisProvHelper].getName)

  var requests : JValue = JNothing
  def requestsParsed : Boolean = requests != JNothing


  try {
    requests = parse(source, false, false)
    m_log trace pretty(render(requests))
  }
  catch{
    case x: Throwable => m_log error ("Could not parse request: ", x)
  }



  def handleRequests(printPretty: Boolean) : String =  {
    requests match{
      case JArray(arr) => val d = render(JArray(arr.map( v => handleRequest(v))))
                          if (!printPretty) compact(d) else pretty(d)
      case _ => val msg = s"Request is not correct. reject it"; m_log error msg; msg
    }
  }

  def handleRequest(a_request : JValue) : JValue =  {
    a_request match {
      case o: JObject => {
        val verb : String = (o \ "verb").extractOpt[String].getOrElse("none")
        val content = o \ "content"
        val keys : Map[String, Any] = MDNodeUtil.keyExtractor(o \ "keys")
        verb match {
          case "write"  => val anh = new AnalysisNode(content);  build(anh.write)
          case "read"   => val anh = new AnalysisNode;  build(anh.read(keys))
          case "update"   => val anh = new AnalysisNode(content);  build(anh.update(keys))
          case "search" => val anh = new AnalysisNode;  build(anh.find(keys))
          case "delete" => val anh = new AnalysisNode;  build(anh.delete(keys))
          case "scan" => val anh = new AnalysisNode;  build(anh.scan)
          case "list-headers" =>  val anh = new AnalysisNode;  build(anh.listHeaders(keys))
          case "none"   => JObject(  JField("result", JInt(Rejected.id)), JField("reason", JString("Incorrect verb")))
        }
      }
      case _ => val msg = s"Request is not correct. reject it"
                    m_log error msg
                    JObject(  JField("result", JInt(Rejected.id)), JField("reason", JString(msg)))
    }
  }




}


