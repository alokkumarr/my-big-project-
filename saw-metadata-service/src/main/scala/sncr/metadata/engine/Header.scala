package sncr.metadata.engine

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.{Get, Result}
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._


/**
  * Created by srya0001 on 3/7/2017.
  */
trait Header {

  var headerDesc: Map[String, String] = null
  val m_log: Logger = LoggerFactory.getLogger(classOf[Header].getName)

  protected def compileSearchCell(getCNode: Get): Get = getCNode.addFamily(MDSections(searchSection.id))

  protected def header(g : Get) = compileSearchCell(g)

  def getHeaderData(res:Result): Option[Map[String, Any]] = Option(getSearchFields(res) )

  import scala.collection.JavaConversions._
  protected def getSearchFields(res:Result): Map[String, Any] =
  {
    val sfKeyValues = res.getFamilyMap(MDSections(searchSection.id))
    m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " => " + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"
    val sf : Map[String, Any] = sfKeyValues.keySet().map( k =>
    { val k_s = new String(k)
      val sf_type = headerDesc(k_s)
      val v_s = sf_type match {
        case "String" => Bytes.toString(sfKeyValues.get(k))
        case "Long" => Bytes.toLong(sfKeyValues.get(k))
        case "Boolean" => Bytes.toBoolean(sfKeyValues.get(k))
        case "Int" => Bytes.toInt(sfKeyValues.get(k))
        case "Time" => {
          val r = Bytes.toString(sfKeyValues.get(k))
          val dfrm: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
          val ldt: LocalDateTime = LocalDateTime.parse(r, dfrm)
          (true, ldt)
        }

      }
      m_log trace s"Search field: $k_s, value: $v_s"
      k_s -> v_s
    }).toMap
    sf
  }

}
