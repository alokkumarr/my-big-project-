package sncr.metadata.datalake

import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.engine.MDObjectStruct.formats
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine.MDNodeUtil
import sncr.metadata.engine.responses.Response

/**
  * Created by srya0001 on 3/3/2017.
  */
class DataObjectHelper(var requests: JValue) extends Response {

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[DataObjectHelper].getName)
  def requestsParsed : Boolean = requests != JNothing
  def handleRequests(printPretty: Boolean) : String =  {
    requests match{
      case JArray(arr) => val d = render(JArray(arr.map( v => handleRequest(v))))
                          if (!printPretty) compact(d) else pretty(d)
      case _ => val msg = s"Request is not correct. reject it"; m_log error msg; msg
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
          case "write"        => val doh = new DataObject(content);  build(doh.create)
          case "read"         => val doh = new DataObject;  build(doh.read(keys))
          case "update"       => val doh = new DataObject(content);  build(doh.update())
          case "search"       => val doh = new DataObject;  build(doh.find(keys))
          case "delete"       => val doh = new DataObject;  build(doh.deleteAll(keys))
          case "scan"         => val doh = new DataObject;  build(doh.scan)
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


object DataObjectHelper{

  val m_log: Logger = LoggerFactory.getLogger("DataObjectHelper")

  def apply(source: String): DataObjectHelper = {
    try {
      val requests = parse(source, false, false)
      new DataObjectHelper( requests )
    }
    catch {
      case x: Throwable => m_log error("Could not parse request: ", x); throw x
    }
  }
}



