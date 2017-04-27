package sncr.metadata.ui_components

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Result, _}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct.{apply => _, _}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 2/19/2017.
  */
class UINode(private var content: JValue,
             private var module_name : String = Fields.UNDEF_VALUE.toString,
             private var ui_comp_type: String = Fields.UNDEF_VALUE.toString)
      extends ContentNode
      with SourceAsJson {

  private var fetchMode : Int = UINodeFetchMode.Everything.id

  def setFetchMode(m: Int) = { fetchMode = m }

  def setUINodeContent : Unit = {
    if (content != JNothing) {
      content = content.replace(List("id"), JString(Bytes.toString(rowKey)))
      setContent(compact(render(content)))
    }
  }

  def buildSearchData : Map[String, Any] = {
    var searchValues : Map[String, Any] = UINode.extractSearchData(content) + (Fields.NodeId.toString -> new String(rowKey))
    searchValues = searchValues + ( "request_type" -> ui_comp_type ) + ( "request_module" -> module_name )
    searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})
    searchValues
  }

  override protected def getSourceData(res:Result): (JValue, Array[Byte]) = super[SourceAsJson].getSourceData(res)

  override protected def compileRead(g : Get) = includeContent(g)

  override protected def header(g : Get) = includeSearch(g)

  override protected def getData(res:Result): Option[Map[String, Any]] = {
    val (dataAsJVal, dataAsByteArray) = getSourceData(res)
    setContent(dataAsByteArray)
    if (fetchMode == UINodeFetchMode.Everything.id)
      Option(getSearchFields(res) + (key_Definition.toString -> dataAsJVal))
    else
      Option(Map(key_Definition.toString -> dataAsJVal))
  }

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[UINode].getName)

  import MDObjectStruct.formats
  protected val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.UIMetadata
  protected val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc =  UINode.searchFields

  override protected def initRow : String =
  {
    val rowkey = (content \ "customerCode").extractOrElse[String]("UNDEF") +
            MetadataDictionary.separator + module_name +
            MetadataDictionary.separator + ui_comp_type +
            MetadataDictionary.separator + System.nanoTime()
    m_log debug s"Generated RowKey = $rowkey"
    rowkey
  }


  def create: ( Int, String )=
  {
    try {

      val put_op = createNode(NodeType.ContentNode.id, classOf[UINode].getName)
      setUINodeContent
      if (commit(saveContent(saveSearchData(put_op, buildSearchData))))
        (NodeCreated.id, s"${Bytes.toString(rowKey)}")
      else
        (Error.id, "Could not create UI Node")

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
      setUINodeContent
      if (commit(saveContent(saveSearchData(update, buildSearchData))))
        (Success.id, s"The UI Node [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update UI Node")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

}


object UINode
{
  protected val m_log: Logger = LoggerFactory.getLogger("sncr.metadata.ui_components.UINodeObject")

  val searchFields =
    Map(
      "customer_Prod_module_feature_sys_id" -> "String",
      "module" -> "String",
      "userName" -> "String",
      "dataSecurityKey" -> "String",
      "type" -> "String",
      "number_of_records" -> "Int",
      "from_record" -> "Int",
      "id" -> "String",
      "roleType" -> "String",
      "metric_name" -> "String",
      "customerCode" -> "String",
      "request_module" -> "String",
      "request_type" -> "String")

  val mandatoryAttributes =  List("module", "type", "customerCode")


  def apply(rowId: String) :UINode =
  {
    val uiNode = new UINode(JNothing, null, null)
    uiNode.setRowKey(Bytes.toBytes(rowId))
    uiNode.load
    uiNode
  }

  def  extractSearchData( content_element: JValue) : Map[String, Any] = {
    List((content_element, "customer_Prod_module_feature_sys_id"),
      (content_element, "userName"),
      (content_element, "dataSecurityKey"),
      (content_element, "type"),
      (content_element, "metric_name"),
      (content_element, "customerCode"),
      (content_element, "roleType"),
      (content_element, "module"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}


object UINodeFetchMode extends Enumeration{

  val Everything = Value(0, "Everything")
  val DefinitionAndKeys = Value(1, "Definition&Keys")
  val DefinitionOnly = Value(2, "Definition")

}


