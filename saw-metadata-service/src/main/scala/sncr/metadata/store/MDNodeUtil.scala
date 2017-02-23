package sncr.metadata.store

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.MDObjectStruct

/**
  * Created by srya0001 on 2/20/2017.
  */
object MDNodeUtil {

  val m_log: Logger = LoggerFactory.getLogger("MDNodeUtil")
  import MDObjectStruct.formats


  def extractValues( json: JValue, fd: (String,String) ) : ( Boolean, Any) =
  {
    try {
      fd._2 match
      {
        case "String" =>  val r = (json \ fd._1).extract[String]; (true, r)
        case "Long" => val r = (json \ fd._1).extract[Long]; (true, r)
        case "Boolean" => val r = (json \ fd._1).extract[Boolean]; (true, r)
        case "Int" => val r = (json \ fd._1).extract[Int]; (true, r)
        case "Time" => {
          val r = (json \ fd._1).extract[String]
          val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
          val ldt: LocalDateTime = LocalDateTime.parse(r, dfrm)
          (true, ldt)
        }
        case _ =>  m_log error "Unsupported search type"; (false, null)
      }

    }
    catch {
      case x: Exception => m_log trace s"Could not extract value for field ${fd._1}, declared type: ${fd._2}"; (false, -1L )
    }
  }


  def extractSearchData( src: String, searchDictionary: Map[String, String]) : Map[String, Any] =
  {
    val json: JValue = parse(src, false, false)
    extractSearchData(json, searchDictionary)
  }

  def extractSearchData( src: JValue, searchDictionary: Map[String, String]) : Map[String, Any] =
  {
    searchDictionary.map( sf => {
    val (r, s) = extractValues(src, sf)
    m_log debug s"Search data: ${sf._1} = $s"
    if (r)  sf._1 -> Option(s) else  sf._1 -> None  }).filter( _._2.isDefined ).map(  kv => kv._1 -> kv._2.get)
  }

}
