package sncr.datalake.handlers

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.DLSession
import sncr.datalake.exceptions.{DAException, ErrorCodes}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.MDObjectStruct._

/**
  * Created by srya0001 on 5/21/2017.
  */
trait HasDataObject[H<:DLSession] {

  protected var dataObjects : List[DataObject] = null
  protected val m_log: Logger = LoggerFactory.getLogger("sncr.datalake.handlers.HasDataObject")

  def loadData(node:H): Unit =
  {
    dataObjects.foreach( dataObject => {
      val (nameDO, formatDO) = HasDataObject.loadDODescriptor(dataObject)
      node.lastUsed = System.currentTimeMillis()
      node.loadObject(nameDO.get, dataObject.getDLLocations(0), formatDO.get)
    })
  }

}

object HasDataObject {

  protected val m_log: Logger = LoggerFactory.getLogger("sncr.datalake.handlers.HasDataObject")

  def loadDODescriptor(dataObject: DataObject) : (Option[String], Option[String]) =
  {
    val dRaw = dataObject.getCachedData.get(key_Definition.toString)
    m_log debug pretty(render(dRaw.get.asInstanceOf[JValue]))
    if (dRaw.isEmpty)
      throw new DAException(ErrorCodes.InvalidDataObject, s"Definition not found, Row ID: ${Bytes.toString(dataObject.getRowKey)}")
    val ldesc: JValue = dRaw.get match {
      case x: JValue => x
      case s: String => parse(s, false, false)
    }
    val formatDO = (ldesc \ "type").extractOpt[String]
    val nameDO = (ldesc \ "name").extractOpt[String]
    if (formatDO.isEmpty || nameDO.isEmpty)
      throw new DAException(ErrorCodes.InvalidDataObject, s"Data Object Name and/or format attributes not found, Row ID: ${Bytes.toString(dataObject.getRowKey)}")
    (nameDO, formatDO)
  }


}
