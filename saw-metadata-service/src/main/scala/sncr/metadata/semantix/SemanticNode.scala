package sncr.metadata.semantix

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Result, _}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST.{JNothing, JString, JValue}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct.{apply => _, _}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.metadata.engine.relations.Relation
import sncr.metadata.ui_components.UINodeFetchMode
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 2/19/2017.
  */
class SemanticNode(private var content_element: JValue,
                   private val predefRowKey : String = null)
      extends ContentNode
      with Relation
      with SourceAsJson {

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[SemanticNode].getName)

  private var fetchMode : Int = UINodeFetchMode.Everything.id

  def setFetchMode(m: Int) = { fetchMode = m }

  def setSemanticNodeContent : Unit = {
    if (content_element != JNothing) {
      content_element.replace(List("_id"), JString(new String(rowKey)))
      setContent(compact(render(content_element)))
    }
  }


  override protected def getSourceData(res:Result): (JValue, Array[Byte]) = super[SourceAsJson].getSourceData(res)

  override protected def compileRead(g : Get) = includeRelation(includeContent(includeSearch(g)))

  override protected def header(g : Get) = includeRelation(includeSearch(g))

  override protected def getData(res:Result): Option[Map[String, Any]] = {
    val (dataAsJVal, dataAsByteArray) = getSourceData(res)
    setContent(dataAsByteArray)
    if (fetchMode == UINodeFetchMode.Everything.id)
      Option(getSearchFields(res) + (key_Definition.toString -> dataAsJVal))
    else
      Option(Map(key_Definition.toString -> dataAsJVal))
  }

  import MDObjectStruct.formats
  protected val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.SemanticMetadata
  protected val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc =  SemanticNode.searchFields

  override protected def initRow : String = {
    if (predefRowKey == null || predefRowKey.isEmpty) {
      val rowkey = (content_element \ "customer_code").extractOpt[String] +
        MetadataDictionary.separator + System.nanoTime()
      m_log debug s"Generated RowKey = $rowkey"
      rowkey
    }
    else{
      predefRowKey
    }
  }


  def create: ( Int, String )=
  {
    try {
      val put_op = createNode(NodeType.ContentNode.id, classOf[SemanticNode].getName)

      setSemanticNodeContent
      val searchValues : Map[String, Any] = SemanticNode.extractSearchData(content_element) + (Fields.NodeId.toString -> new String(rowKey))
      searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})

      if (commit(saveContent(saveSearchData(put_op, searchValues))))
        (NodeCreated.id, s"${Bytes.toString(rowKey)}")
      else
        (Error.id, "Could not create Semantic Node")

    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

  def update(keys: Map[String, Any]) : (Int, String) =
  {
    try {
      val (res, msg ) = selectRowKey(keys)
      if (res != Success.id) return (res, msg)
      setSemanticNodeContent
      val searchValues : Map[String, Any] = SemanticNode.extractSearchData(content_element) + (Fields.NodeId.toString -> new String(rowKey))
      searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})

      if (commit(saveRelation(saveContent(saveSearchData(update, searchValues)))))
        (Success.id, s"The Semantic Node [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update Semantic Node")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

}


object SemanticNode
{
  val searchFields: Map[String, String] = Map(
    "customer_code" -> "String",
    "metric_name" -> "String"
  )



  protected val m_log: Logger = LoggerFactory.getLogger("SemanticNodeObject")

  def apply(rowId: String) :SemanticNode =
  {
    val semNode = new SemanticNode(JNothing, null)
    semNode.setRowKey(Bytes.toBytes(rowId))
    semNode.load
    semNode
  }

  def  extractSearchData( content_element: JValue) : Map[String, Any] = {
    List((content_element, "metric_name"),
        (content_element, "customer_code"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}



