package sncr.metadata.objectstore

import java.io.IOException

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.security.UserGroupInformation
import org.json4s.JValue
import org.slf4j.{Logger, LoggerFactory}
import sncr.config.TSConfig
import sncr.metadata.{MetadataObjectStructure, SearchDictionary}
import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by srya0001 on 1/27/2017.
  *
  * The class is written for MapR-DB
  * To make it compatible with HBase the following should be changed:
  * hbaseConf.addResource(new Path("//conf/hbase-site.xml"))
  * hbaseConf.addResource(new Path("//etc/hadoop/core-site.xml"))
  * hbaseConf.set("hbase.master", master-host + ":16010")
  *
  *
  */
abstract class MDStore {

  val m_log: Logger = LoggerFactory.getLogger(classOf[MDStore].getName)
  private val default_user = "mapr"
  val hbaseConf = HBaseConfiguration.create
  val zQuorum = TSConfig.metadataConfig.getString("zookeeper-quorum")
  val user = if (TSConfig.metadataConfig.getString("user") != null &&
    !TSConfig.metadataConfig.getString("user").isEmpty)
    TSConfig.metadataConfig.getString("user")
  else default_user
  val realUser: UserGroupInformation = UserGroupInformation.createRemoteUser(user)
  UserGroupInformation.setLoginUser(realUser)
  m_log debug "Create connection and admin object"
  hbaseConf.set("hbase.zookeeper.quorum", zQuorum)
  val connection = ConnectionFactory.createConnection(hbaseConf)
  val admin = connection.getAdmin

  import org.json4s.DefaultFormats
  import MetadataObjectStructure.formats

  protected def extractStringStoreValues( json: JValue, fn: String) : ( Boolean, String) =
  {
    try {
      val result = (json \ "result").extract[String]
      if (result != null && !result.isEmpty)
        (true, result)
      else
        (false,  null)
    }
    catch {
      case x: Exception => m_log trace (s"Could not extract string value"); (false, null)

    }
  }


  protected def extractLongStoreValues( json: JValue, fn: String) : ( Boolean, Long) =
  {
    try {
      val r = (json \ "result").extract[Long]
      (true, r)
    }
    catch {
      case x: Exception => m_log trace (s"Could not extract long value"); (false, -1L )

    }
  }

  protected def extractSearchData( src: String ) : Map[String, Option[Any]] =
  {
    val json: JValue = parse(src)
    SearchDictionary.searchFields.map( sf => {
      val (r, s) = extractStringStoreValues(json, sf)
      if (r)  sf -> Option(s) else  sf -> None }).toMap[String, Option[Any]]
  }


}



case class NodeStore( val nodeType: String) extends MDStore {

  val table = TSConfig.metadataConfig.getString("path")

  val tn: TableName = TableName.valueOf(table)
  val mdNodeStoreTable = connection.getTable(tn)
  //    mdtd.getFamilies.toArray().foreach( cdesc  => m_log.debug( cdesc.toString ))


  def createMDNode(compositeKey: String, content: String): Boolean = {
    try {
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString),
        Bytes.toBytes(MetadataObjectStructure.columnContent.toString),
        Bytes.toBytes(content))

      val sval = extractSearchData(content)  + ("NodeID" -> Option(compositeKey))
      sval.keySet.filter(k => sval(k) != None).foreach(k => {
        putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.searchSection.toString),
          Bytes.toBytes(k), Bytes.toBytes(sval(k).get.asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
      true
    }
      catch{
        case x: IOException => m_log error ( s"Could not create node: ${compositeKey}, Reason: ", x); false
      }
  }

  def readMDNode(compositeKey: String): Option[String] = {
    try {
      val getOp: Get = new Get(Bytes.toBytes(compositeKey))
      getOp.addFamily(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString))
      val res = mdNodeStoreTable.get(getOp)
      if (res.isEmpty) return None
      val content = res.getValue(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString),
        Bytes.toBytes(MetadataObjectStructure.columnContent.toString))
      Option(new String(content))
    }
    catch{
      case x: IOException => m_log error ( s"Could not read node: ${compositeKey}, Reason: ", x); None
    }
  }


  def deleteMDNode(compositeKey: String) : Boolean =
  {
    try {
    val delOp: Delete = new Delete(Bytes.toBytes(compositeKey))
    mdNodeStoreTable.delete(delOp)
    true
    }
    catch{
      case x: IOException => m_log error ( s"Could not delete node: ${compositeKey}, Reason: ", x); false
    }

  }

  def updateMDNode(compositeKey: String, content: String): Boolean = {
    try {

//      val rowFilter = RowFilter.parseFrom( Bytes.toBytes(compositeKey))
      val query = new Get(Bytes.toBytes(compositeKey))
      query.addFamily(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString))
      val res = mdNodeStoreTable.get(query)
      if (res == null || res.isEmpty){m_log debug s"Row does not exist: ${compositeKey}"; return false}

      val data = query.getFamilyMap
      if (res.isEmpty || data == null || data.size == 0){
        m_log debug s"Row is empty: ${compositeKey}"
        return false
      }
//    if (data.containsKey(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString)))
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString),
        Bytes.toBytes(MetadataObjectStructure.columnContent.toString),
        Bytes.toBytes(content))
      val sval = extractSearchData(content) + ("NodeID" -> Option(compositeKey))
      sval.keySet.filter(k => sval(k) != None).foreach(k => {
        putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.searchSection.toString),
          Bytes.toBytes(k), Bytes.toBytes(sval(k).get.asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
      true
    }
    catch{
      case x: IOException => m_log error ( s"Could not update node: ${compositeKey}, Reason: ", x); false
    }
  }



  def loadMDNodes( rowKeys: List[String] ): List[String] =
  {
     rowKeys.map( k => readMDNode(k)).withFilter( n=> n.nonEmpty ).map(v => v.get)
  }



}

