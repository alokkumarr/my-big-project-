package sncr.metadata.analysis

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Get, Result}
import org.json4s.JsonAST.{JNothing, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.engine.MDObjectStruct._
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.metadata.engine.relations.Relation
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 3/1/2017.
  */
class AnalysisNode(private[this] var analysisNode: JValue = JNothing) extends ContentNode
  with SourceAsJson
  with Relation{

  override def getSourceData(res: Result): JValue = super[SourceAsJson].getSourceData(res)

  override def compileRead(g: Get) = {
    includeRelation(
    includeContent(g))
  }

  override def header(g : Get) = includeSearch(g)

  override def getData(res: Result): Option[Map[String, Any]] = {
    Option(getSearchFields(res) +
          (key_Definition.toString -> getSourceData(res)) +
          (key_RelationSimpleSet.toString -> getRelationData(res) )
    )
  }

  override val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNode].getName)

  import MDObjectStruct.formats

  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.AnalysisMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc =  SearchDictionary.searchFields


  override protected def initRow: String = {
    val rowkey = (analysisNode \ "name").extract[String] + AnalysisNode.separator +
      (analysisNode \ "analysis" \ "analysisId").extract[String] + AnalysisNode.separator +
      (analysisNode \ "analysis" \ "analysisCategoryId").extract[String] + AnalysisNode.separator +
      System.currentTimeMillis()
    m_log debug s"Generated RowKey = $rowkey"
    rowkey
  }

  protected def validate: (Int, String) = {
    analysisNode match {
      case null | JNothing => (Rejected.id, "Empty node, does not apply for requested operation")
      case _: JValue => {
        AnalysisNode.requiredFields.keySet.foreach {
          case k@"analysis" => AnalysisNode.requiredFields(k).foreach {
            case rf@"columns" =>
              analysisNode \ k \ rf match {
                case JArray(ja) => if (ja.isEmpty) (Rejected.id, "Analysis column section is empty")
                case _ => return (Rejected.id, "Analysis column section is missing")
              }
            case x: String =>
              val fieldValue = analysisNode \ k \ x
              if (fieldValue == null || fieldValue.extractOpt[String].isEmpty) {
                val msg = s"Required field $k.$x is missing or empty"
                m_log debug Rejected.id + " ==> " + msg
                return (Rejected.id, msg)
              }
          }
          case k@"root" => {
            AnalysisNode.requiredFields(k).foreach {
              rf =>
                val fieldValue = analysisNode \ rf
                if (fieldValue == null || fieldValue.extractOpt[String].isEmpty) {
                  val msg = s"Required root field $rf is missing or empty"
                  m_log debug Rejected.id + " ==> " + msg
                  return (Rejected.id, msg)
                }
            }
          }
        }
      }
    }
    (Success.id, "Request is correct")
  }


  def write: (Int, String) = {
    try {
      val (result, msg) = validate
      if (result != Success.id) return (result, msg)
      val put_op = createNode(NodeType.RelationContentNode.id, classOf[AnalysisNode].getName)
      val searchValues: Map[String, Any] = AnalysisNode.extractSearchData(analysisNode) + ("NodeId" -> new String(rowKey))
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })
      if (commit(
          saveRelation(
          saveContent(put_op, compact(render(analysisNode)), searchValues))))
          (Success.id, s"The Analysis Node [ ${new String(rowKey)} ] has been created")
      else
        (Error.id, "Could not create Analysis Node")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def update(filter: Map[String, Any]): (Int, String) = {
    try {
      val (result, validate_msg) = validate
      if (result != Success.id) return (result, validate_msg)

      val (res, msg) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)
      readCompiled(prepareRead).getOrElse(Map.empty)
      setRowKey(rowKey)

      val searchValues: Map[String, Any] = AnalysisNode.extractSearchData(analysisNode) + ("NodeId" -> new String(rowKey))
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })
      if (commit(
          saveRelation(
          saveContent(
          update, compact(render(analysisNode)), searchValues))))
        (Success.id, s"The Analysis Node [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update Analysis Node")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

}

object AnalysisNode{


  val m_log: Logger = LoggerFactory.getLogger("AnalysisNodeObject")
  val separator: String = "::"

  def parseAnalysisJSON( src : String) : JValue =
  {
    try {
      parse(src, false, false)
    }
    catch{
      case x: Exception => m_log error s"Could not parse Analysis JSON representation"; JNothing
    }
  }

  val requiredFields = Map(
    "root" -> List("name", "tenantId", "productId"),
    "analysis" -> List( "analysisId", "productId", "analysisId", "analysisName", "columns")
  )

  def  extractSearchData(analysisNode: JValue) : Map[String, Any] = {

    val analysis = analysisNode \ "analysis"
    List(
      (analysisNode, "name"),
      (analysisNode, "tenantId"),
      (analysisNode, "productId"),
      (analysis, "analysisId"),
      (analysis, "analysisCategoryId"),
      (analysis, "analysisCategoryName"),
      (analysis, "tenantId"),
      (analysis, "productId"),
      (analysis, "analysisName"),
      (analysis, "displayStatus"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, SearchDictionary.searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${SearchDictionary.searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}
