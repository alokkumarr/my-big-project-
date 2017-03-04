package sncr.metadata.analysis

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Result
import org.json4s.JsonAST.{JNothing, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.metadata.ProcessingResult._
import sncr.metadata.ui_components.SearchDictionary._
import sncr.metadata.engine.{MDNodeUtil, MetadataNode, SearchMetadata, SourceAsJson}
import sncr.metadata.{MDObjectStruct, tables}
import sncr.saw.common.config.SAWServiceConfig

/**
  * Created by srya0001 on 3/1/2017.
  */
class AnalysisNode(val analysisNode: JValue = JNothing) extends MetadataNode
  with SearchMetadata
  with SourceAsJson{

  override def getSourceData(res:Result): JValue = super[SourceAsJson].getSourceData(res)

  override val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNode].getName)

  def this() =
  {
    this(JNothing)
  }


  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.AnalysisMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  this.searchFields = SearchDictionary.searchFields

  override protected def createRowKey : String =
  {
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
            case k@"analysis" => AnalysisNode.requiredFields(k).foreach{
              case rf@"columns" =>
                analysisNode \ k \ rf match {
                  case JArray(ja) => if (ja.isEmpty) (Rejected.id, "Analysis column section is empty")
                  case _ => return (Rejected.id, "Analysis column section is missing")
                }
              case x:String =>
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
    (Success.id, "Node is Correct")
  }

  def write: ( Int, String ) =
  {
    try {
      val (result, msg) = validate
      if (result != Success.id) return (result, msg)

      createNode
      completeNode("has been created")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }

  def update(filter: Map[String, Any]) : (Int, String) =
  {
    try {
      val (result, validate_msg) = validate
      if (result != Success.id) return (result, validate_msg)

      val (res, msg ) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)
      retrieve.getOrElse(Map.empty)
      setRowKey(rowKey)
      super.update
      completeNode("has been updated")
    }
    catch{
      case x: Exception => { val msg = s"Could not store node [ ID = ${new String(rowKey)} ]: "; m_log error (msg, x); ( Error.id, msg)}
    }
  }



  private def completeNode(operName : String): (Int, String) =
  {
    val searchValues : Map[String, Any] = AnalysisNode.extractSearchData(analysisNode) + ("NodeId" -> new String(rowKey))
    searchValues.keySet.foreach(k => {m_log debug s"Add search field $k with value: ${searchValues(k).asInstanceOf[String]}"})
    addSearchSection (searchValues)
    addSource(compact(render(analysisNode)))
    if (saveNode)
      (Success.id, s"The Analysis Node [ ${new String(rowKey)} ] $operName")
    else
      (Error.id, "Could not create/update Semantic Node")
  }


  def read(filter: Map[String, Any]) : Map[String, Any] =
  {
    val (res, msg ) = selectRowKey(filter)
    if (res != Success.id) return Map.empty
    retrieve.getOrElse(Map.empty)
  }


  def delete(keys: Map[String, Any]) : (Int, String) =
  {
    val (res, msg ) = selectRowKey(keys)
    if (res != Success.id) return (res, msg)
    super.delete
  }


  def find(searchFilter: Map[String, Any]) : List[Map[String, Any]] = loadNodes(simpleMetadataSearch(searchFilter, "and"))
  def scan : List[Map[String, Any]] = loadNodes( scanMDNodes )


}


object AnalysisNode
{
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
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, searchFields(jv._2)) )
        m_log debug s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}


