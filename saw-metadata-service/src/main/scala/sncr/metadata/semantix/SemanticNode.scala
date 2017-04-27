package sncr.metadata.semantix

import java.util.UUID

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Result, _}
import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct.{apply => _, _}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.metadata.engine.context.{MDContentBuilder, SelectModels}
import sncr.metadata.engine.relations.CategorizedRelation
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 2/19/2017.
  */
class SemanticNode(private var metricDescriptor: JValue = JNothing,
                   private val select: Int = SelectModels.node.id,
                   private val predefRowKey : String = null)
      extends ContentNode
      with CategorizedRelation
      with MDContentBuilder {

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[SemanticNode].getName)

  def setSemanticNodeContent() : Unit = {
    if (metricDescriptor != null && metricDescriptor != JNothing) {
      val (result, msg) = validate
      if (result != Success.id)
        throw new Exception(s"Could not create Semantic Node with provided descriptor, reason: $result - $msg")
      else {
        metricDescriptor = metricDescriptor.replace(List("id"), JString(new String(rowKey)))
        setContent(compact(render(metricDescriptor)))
      }
    }
  }


  override protected def getSourceData(res:Result): (JValue, Array[Byte]) = super[MDContentBuilder].getSourceData(res)

  override protected def compileRead(g : Get) =
    select match {
      case 2 | 4 => includeContent(includeSearch(g))
      case 1 | 3 => includeRelation(includeContent(includeSearch(g)))
      case _ => includeContent(g)
    }

  override protected def header(g : Get) = includeSearch(g)

  override protected def getData(res:Result): Option[Map[String, Any]] = {
    val (dataAsJVal, dataAsByteArray) = getSourceData(res)
    setContent(dataAsByteArray)
    m_log debug s"Select model: ${SelectModels(select)}"
    select match{
      case 2|4 => Option(getSearchFields(res) + (key_Definition.toString -> dataAsJVal))
      case 1|3 => getRelationData(res); Option(getSearchFields(res) +
                (key_Definition.toString -> (dataAsJVal ++ JField("repository", collectRelationData(res)))))
      case 0 => Option(Map(key_Definition.toString -> headerAsJVal(dataAsJVal)))
    }
  }

  protected val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.SemanticMetadata
  protected val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc =  SemanticNode.searchFields

  override protected def initRow : String = {
    if (predefRowKey == null || predefRowKey.isEmpty) {
      val rowkey = UUID.randomUUID().toString
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

      setSemanticNodeContent()
      val searchValues : Map[String, Any] = SemanticNode.extractSearchData(metricDescriptor) + (Fields.NodeId.toString -> new String(rowKey))
      searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})

      if (commit(saveRelation(saveContent(saveSearchData(put_op, searchValues)))))
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
      setSemanticNodeContent()
      val searchValues : Map[String, Any] = SemanticNode.extractSearchData(metricDescriptor) + (Fields.NodeId.toString -> new String(rowKey))
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

  def updateRelations(): (Int, String) = {
    try {
      if (rowKey != null  && !rowKey.isEmpty) {
        if (commit(saveRelation(update)))
          (Success.id, s"The Semantic Node relations [ ${Bytes.toString(rowKey)} ] has been updated")
        else
          (Error.id, "Could not update Semantic Node")
      }
      else
      {
        (Error.id, "Semantic Node should be loaded/identified first")
      }
    }
    catch {
      case x: Exception => {
        val msg = s"Could not update Semantic node [ ID = ${Bytes.toString(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def validate: (Int, String) = {
    metricDescriptor match {
      case null | JNothing => (Rejected.id, "Empty node, does not apply for requested operation")
      case _: JValue => {
        if (SemanticNode.requiredFields.exists( p = field =>
          metricDescriptor \ field match {
            case a: JArray => a.arr.isEmpty
            case o: JObject => o.obj.isEmpty
            case s: JString => s.s.isEmpty
            case _ => true
          }
          ))
          {
            val msg = s"Required field is missing or empty"
            m_log debug Rejected.id + " ==> " + msg
            return (Rejected.id, msg)
          }
        }
    }
    (Success.id, "Request is correct")
  }


}


object SemanticNode
{
  val searchFields: Map[String, String] = Map(
    "customerCode" -> "String",
    "metricName" -> "String",
    "type" -> "String",
    "metric" -> "String",
    "module" -> "String",
    "dataSecurityKey" -> "String",
    "id" -> "String"
  )

  val requiredFields = List("id", "supports", "artifacts")


  protected val m_log: Logger = LoggerFactory.getLogger("SemanticNodeObject")

  def apply(rowId: String, selectId : Int = 2) :SemanticNode =
  {
    val semNode = new SemanticNode(JNothing, selectId)
    semNode.setRowKey(Bytes.toBytes(rowId))
    semNode.load
    semNode
  }

  def  extractSearchData( content_element: JValue) : Map[String, Any] = {
    List((content_element, "metricName"),
         (content_element, "metric"),
         (content_element, "type"),
         (content_element, "dataSecurityKey"),
         (content_element, "customerCode"),
         (content_element, "module"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }




}



