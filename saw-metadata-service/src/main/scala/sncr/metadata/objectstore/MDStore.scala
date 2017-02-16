package sncr.metadata.objectstore

import java.io.IOException

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.{FilterList, SingleColumnValueFilter}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.security.UserGroupInformation
import org.json4s.native.JsonMethods._
import org.json4s.{JValue, _}
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.{MetadataObjectStructure, SearchDictionary}
import sncr.saw.common.config.SAWServiceConfig
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
  val zQuorum = SAWServiceConfig.metadataConfig.getString("zookeeper-quorum")
  val user = if (SAWServiceConfig.metadataConfig.getString("user") != null &&
    !SAWServiceConfig.metadataConfig.getString("user").isEmpty)
    SAWServiceConfig.metadataConfig.getString("user")
  else default_user
  val realUser: UserGroupInformation = UserGroupInformation.createRemoteUser(user)
  UserGroupInformation.setLoginUser(realUser)
  m_log debug "Create connection and admin object"
  hbaseConf.set("hbase.zookeeper.quorum", zQuorum)
  val connection = ConnectionFactory.createConnection(hbaseConf)
  val admin = connection.getAdmin

  import MetadataObjectStructure.formats

  protected def extractStringStoreValues( json: JValue, fn: String) : ( Boolean, String) =
  {
    try {
      val result = (json \ fn).extract[String]
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
      val r = (json \ fn).extract[Long]
      (true, r)
    }
    catch {
      case x: Exception => m_log trace (s"Could not extract long value"); (false, -1L )
    }
  }

  protected def extractSearchData( src: String ) : Map[String, Option[Any]] =
  {
    val json: JValue = parse(src, false, false)
    SearchDictionary.searchFields.map( sf => {
      val (r, s) = extractStringStoreValues(json, sf)
      m_log debug s"Search data: $sf = $s"
      if (r)  sf -> Option(s) else  sf -> None  }).toMap[String, Option[Any]].filter( _._2.isDefined )
  }
}


case class NodeStore( val nodeType: String) extends MDStore {

  val table = SAWServiceConfig.metadataConfig.getString("path")

  val tn: TableName = TableName.valueOf(table)
  val mdNodeStoreTable = connection.getTable(tn)
  //    mdtd.getFamilies.toArray().foreach( cdesc  => m_log.debug( cdesc.toString ))


  def createMDNode(compositeKey: String, content: String): Boolean = {
    try {
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      m_log trace s"Save the document as content CF: ${content}"
      putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString),
        Bytes.toBytes(MetadataObjectStructure.columnContent.toString),Bytes.toBytes(content))

      val sval = extractSearchData(content)  + ("NodeId" -> Option(compositeKey))
      sval.keySet.filter(k => sval(k).isDefined).foreach(k => {
        m_log debug s"Add search field ${k} with value: ${sval(k).get.asInstanceOf[String]}"
        putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.searchSection.toString),
          Bytes.toBytes(k), Bytes.toBytes(sval(k).get.asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
      true
    }
      catch{
        case x: IOException => m_log error ( s"Could not create/update node: ${compositeKey}, Reason: ", x); false
      }
  }


  def readMDNode(compositeKey: String, includeSearchFields: Boolean): Option[Map[String, Any]] = {
    readMDNode(Bytes.toBytes(compositeKey), includeSearchFields: Boolean)
  }

  import scala.collection.JavaConversions._
  def readMDNode(compositeKey: Array[Byte], includeSearchFields: Boolean): Option[Map[String, Any]] = {
    try {
      m_log debug s"Load row: ${new String(compositeKey)}"

      val getOp: Get = new Get(compositeKey)
      getOp.addFamily(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString))
      if (includeSearchFields)
        getOp.addFamily(Bytes.toBytes(MetadataObjectStructure.searchSection.toString))
      val res = mdNodeStoreTable.get(getOp)
      if (res.isEmpty) return None
      val content = res.getValue(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString),
                                 Bytes.toBytes(MetadataObjectStructure.columnContent.toString))
      m_log debug s"Read node: ${new String(content)}"
      if (includeSearchFields) {
         val sfKeyValues = res.getFamilyMap(Bytes.toBytes(MetadataObjectStructure.searchSection.toString))
         m_log debug s"Include list of search fields into result: ${sfKeyValues.keySet.toList.map(k => new String(k) + " =>" + new String(sfKeyValues(k))  ).mkString("{", ",", "}")}"
         val sf : Map[String, String] = sfKeyValues.keySet().map( k =>
         { val k_s = new String(k)
           val v_s = new String(sfKeyValues.get(k))
           m_log debug s"Search field: $k_s, value: $v_s"
           k_s -> v_s
         }).toMap

         Option(sf + (MetadataObjectStructure.columnContent.toString -> new String(content)))
      }
      else {
        m_log debug s"Do not Include list of search fields into result"
        Option(Map(MetadataObjectStructure.columnContent.toString -> new String(content)))
      }
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
    }
    catch{
      case x: IOException => m_log error ( s"Could not delete node: ${compositeKey}, Reason: ", x); false
    }
    true
  }

  def updateMDNode(compositeKey: String, content: String): Boolean = {
    try {
      val query = new Get(Bytes.toBytes(compositeKey))
      query.addFamily(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString))
      val res = mdNodeStoreTable.get(query)
      if (res == null || res.isEmpty){m_log debug s"Row does not exist: ${compositeKey}"; return false}
      val data = query.getFamilyMap
      if (res.isEmpty || data == null || data.size == 0){
        m_log debug s"Row is empty: $compositeKey"
      }
      var putOp: Put = new Put(Bytes.toBytes(compositeKey))
      putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.sourceSection.toString),
        Bytes.toBytes(MetadataObjectStructure.columnContent.toString),
        Bytes.toBytes(content))
      val sval = extractSearchData(content) + ("NodeId" -> Option(compositeKey))
      sval.keySet.filter(k => sval(k).isDefined).foreach(k => {
        putOp = putOp.addColumn(Bytes.toBytes(MetadataObjectStructure.searchSection.toString),
          Bytes.toBytes(k), Bytes.toBytes(sval(k).get.asInstanceOf[String]))
      })
      mdNodeStoreTable.put(putOp)
    }
    catch{
      case x: IOException => m_log error ( s"Could not retrieve  node: ${compositeKey}, Reason: ", x); false
    }
    true
  }


  def loadMDNodes(rowKeys: List[Array[Byte]], includeSearchFields : Boolean ): List[Map[String, Any]] =
  {
     m_log debug s"Load ${rowKeys.size} rows"
     rowKeys.map( k => readMDNode(k, includeSearchFields)).filter(  _.isDefined ).map(v => v.get)
  }

  import scala.collection.JavaConversions._
  def searchMetadata( keys: Map[String, String], condition: String) : List[Array[Byte]] =
  {
    val filterList : FilterList = new FilterList( if (condition.equalsIgnoreCase("or")) FilterList.Operator.MUST_PASS_ONE else FilterList.Operator.MUST_PASS_ALL )
    val filteringValues = keys.keySet.filter( SearchDictionary.searchFields.contains( _ ) )

    m_log debug s"Create filter list with the following fields: ${filteringValues.mkString("{", ",", "}")}"

    if (filteringValues.isEmpty) {
      m_log error s"Filter is empty - the method should not be used"
      return null
    }

    filteringValues.foreach( f => {
      m_log debug s"Field $f = ${keys(f)}"
      val filter1 : SingleColumnValueFilter = new SingleColumnValueFilter(
            Bytes.toBytes(MetadataObjectStructure.searchSection.toString),
            Bytes.toBytes(f),
            CompareOp.EQUAL,
            Bytes.toBytes(keys(f))
        )
      filter1.setFilterIfMissing(true)
      filterList.addFilter(filter1)
    })

    val q = new Scan
    q.setFilter(filterList)
    val sr : ResultScanner = mdNodeStoreTable.getScanner(q)

    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log trace s"Found: ${sr.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    result
  }

  def close: Unit = mdNodeStoreTable.close()

  import scala.collection.JavaConversions._
  def scanMDNodes: List[Array[Byte]] =
  {
    val sr : ResultScanner = mdNodeStoreTable.getScanner(new Scan)
    val result = (for( r: Result <- sr) yield r.getRow.clone()).toList
    m_log trace s"Full scan MD Nodes: ${sr.size} rows satisfied to filter: ${result.map ( new String( _ )).mkString("[", ",", "]")}"
    sr.close
    result
  }



}

