package sncr.datalake.handlers

import java.text.SimpleDateFormat

import com.synchronoss.sip.datalake.engine.DLSession
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.slf4j.{Logger, LoggerFactory}


trait DataObject[H <: DLSession] {

  protected var repositories: List[JValue] = null
  protected val m_log: Logger = LoggerFactory.getLogger(classOf[DataObject[DLSession]].getName)
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  def loadData(repositories: List[JValue], node: H, limit: Integer): Unit = {
    var name: String = null
    var location: String = null
    var format: String = null
    repositories.foreach(repo => {
      name = (repo \ "name").extract[String]
      m_log.trace("name : {}", name);
      location = (repo \ "physicalLocation").extract[String]
      m_log.trace("location : {}", location);
      format = (repo \ "format").extract[String]
      m_log.trace("format : {}", format)
      node.lastUsed = System.currentTimeMillis()
      m_log.trace("node.loadObject name: {}", name + " location :" + location + " format :" + format)
      node.loadObject(name, location, format)
    })
  }
}

