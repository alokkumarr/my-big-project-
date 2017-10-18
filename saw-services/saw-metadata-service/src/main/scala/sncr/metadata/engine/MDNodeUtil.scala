package sncr.metadata.engine

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.{JBool, JInt, JLong, JValue}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 2/20/2017.
  */
object MDNodeUtil {

  val m_log: Logger = LoggerFactory.getLogger("sncr.metadata.MDNodeUtil")
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

  def convertValue( v: Any) : Array[Byte]=
  {
    try {
      v match
      {
        case s: String =>  Bytes.toBytes(s)
        case i: Int => Bytes.toBytes(i)
        case b: Boolean => Bytes.toBytes(b)
        case l: Long => Bytes.toBytes(l)
        case ii: Integer => Bytes.toBytes(ii)
        case d: Double => Bytes.toBytes(d)

        case s: JString =>  Bytes.toBytes(s.extract[String])
        case i: JInt => Bytes.toBytes(i.extract[Int])
        case b: JBool => Bytes.toBytes(b.extract[Boolean])
        case l: JLong => Bytes.toBytes(l.extract[Long])
        case d: JDouble => Bytes.toBytes(d.extract[Double])

        case _ =>  m_log error s"Unsupported search field type: ${v.toString}"; Array.empty
      }
    }
    catch {
      case x: Exception => m_log trace s"Could not extract value for field ${v.toString}, declared type: ${v.toString}"; Array.empty
    }
  }

  def keyExtractor(elem: JValue): Map[String, Any]  = {
    elem match{
      case JObject(x) => x.toMap
      case JArray(a) => a.flatMap( array_elem => MDNodeUtil.keyExtractor(array_elem)).toMap
      case _ => Map.empty
    }
  }

  def extractKeysAsJValue(elem: JValue): Map[String, JValue]  = {
    elem match{
      case x: JObject => x.obj.toMap
      case a: JArray => a.arr.flatMap( array_elem => MDNodeUtil.extractKeysAsJValue(array_elem)).toMap
      case _ => Map.empty
    }
  }

  def convertKeys(keys : Map[String, JValue]) : Map[String, Any] =
    keys.map( e =>
    { e._1 -> (e._2 match{
      case s:JString => s.s
      case i:JInt    => i.num.intValue()
      case b:JBool   => b.value
      case l:JLong   =>  l.num
      case _ => null
    })})

}
