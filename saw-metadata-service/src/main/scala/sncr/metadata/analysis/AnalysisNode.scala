package sncr.metadata.analysis

import com.typesafe.config.Config
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Get, Result}
import org.apache.hadoop.hbase.util.Bytes
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
class AnalysisNode(private[this] var analysisNode: JValue = JNothing, c: Config = null) extends ContentNode(c)
  with SourceAsJson
  with Relation{

  def setDefinition(newDefinition: String) : Unit =
  {
    analysisNode = parse(newDefinition, false, false)
    setDefinition
  }

  def setDefinition: Unit = {
    val (result, msg) = validate
    if (result != Success.id)
      throw new Exception(s"Could not create Analysis Node with provided content element, reason: $result - $msg")
    else
      setContent(compact(render(analysisNode)))
  }

  override protected def getSourceData(res: Result): (JValue, Array[Byte]) = super[SourceAsJson].getSourceData(res)

  override protected def compileRead(g: Get) = {
    includeRelation(
    includeContent(g))
  }

  override protected def header(g : Get) = includeSearch(g)

  override protected def getData(res: Result): Option[Map[String, Any]] = {
    val (dataAsJValue, dataAsByteArray) = getSourceData(res)
    setContent(dataAsByteArray)
    Option(getSearchFields(res) ++
           getSystemData(res) +
          (key_Definition.toString -> dataAsJValue) +
          (key_RelationSimpleSet.toString -> getRelationDataAsJson(res) )
    )
  }

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[AnalysisNode].getName)

  import MDObjectStruct.formats
  val table = SAWServiceConfig.metadataConfig.getString("path") + "/" + tables.AnalysisMetadata
  val tn: TableName = TableName.valueOf(table)
  mdNodeStoreTable = connection.getTable(tn)
  headerDesc =  SearchDictionary.searchFields


  override protected def initRow: String = {
    val rowkey =
      (analysisNode \ "analysisId").extract[String] + MetadataDictionary.separator +
      System.nanoTime()
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
                m_log debug Rejected.id.toString + " ==> " + msg
                return (Rejected.id, msg)
              }
          }
          case k@"root" => {
            AnalysisNode.requiredFields(k).foreach {
              rf =>
              if (!rf.equalsIgnoreCase("outputFile")) {
                val fieldValue = analysisNode \ rf
                if (fieldValue == null || fieldValue.extractOpt[String].isEmpty) {
                  val msg = s"Required root field $rf is missing or empty"
                  m_log debug Rejected.id.toString + " ==> " + msg
                  return (Rejected.id, msg)
                }
              }
              else{
                val o = analysisNode \ rf
                if (o == null || o.extractOpt[JObject].isEmpty) {
                  val msg = s"Required root object outputFile is missing or empty"
                  m_log debug Rejected.id.toString + " ==> " + msg
                  return (Rejected.id, msg)
                }
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
      val put_op = createNode(NodeType.RelationContentNode.id, classOf[AnalysisNode].getName)
      setDefinition
      val searchValues: Map[String, Any] = AnalysisNode.extractSearchData(analysisNode) + ("NodeId" -> Bytes.toString(rowKey))
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })
      if (commit(saveRelation(saveContent(saveSearchData(put_op,searchValues)))))
        (NodeCreated.id, s"${Bytes.toString(rowKey)}")
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
      val (res, msg) = selectRowKey(filter)
      if (res != Success.id) return (res, msg)
      load
      setDefinition
      val searchValues: Map[String, Any] = AnalysisNode.extractSearchData(analysisNode) + ("NodeId" -> new String(rowKey))
      searchValues.keySet.foreach(k => {
        m_log debug s"Add search field $k with value: ${searchValues(k).toString}"
      })

      if (commit(saveRelation(saveContent(saveSearchData(update,searchValues)))))
        (Success.id, s"The Analysis Node [ ${new String(rowKey)} ] has been updated")
      else
        (Error.id, "Could not update Analysis Node")
    }
    catch {
      case x: Exception => {
        val msg = s"Could not update Analysis node [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }

  def updateRelations: (Int, String) = {
    try {
      if (rowKey != null  && !rowKey.isEmpty) {
        if (commit(saveRelation(update)))
          (Success.id, s"The Analysis Node relations [ ${new String(rowKey)} ] has been updated")
        else
          (Error.id, "Could not update Analysis Node")
      }
      else
        {
          (Error.id, "Analysis Node should be loaded/identified first")
        }
    }
    catch {
      case x: Exception => {
        val msg = s"Could not update Analysis node [ ID = ${new String(rowKey)} ]: "; m_log error(msg, x); (Error.id, msg)
      }
    }
  }


}

object AnalysisNode{


  protected val m_log: Logger = LoggerFactory.getLogger("AnalysisNodeObject")

  def apply( src : String, rowID : String, c: Config) : AnalysisNode =
  {
    try {
      val jv = parse(src, false, false)
      val anNode = new AnalysisNode(jv, c)
      if ( rowID != null && rowID.nonEmpty) anNode.setRowKey(Bytes.toBytes(rowID))
      anNode
    }
    catch{
      case x: Exception => m_log error s"Could not parse Analysis JSON representation"; new AnalysisNode
    }
  }

  def apply(rowId: String) :AnalysisNode =
  {
    val an = new AnalysisNode(JNothing, null)
    an.setRowKey(Bytes.toBytes(rowId))
    an.load
    an
  }


  protected val requiredFields = Map(
    "root" -> List("analysisId", "module", "customer_code")
  )

  def  extractSearchData(analysisNode: JValue) : Map[String, Any] = {

    List(
      (analysisNode, "analysisId"),
      (analysisNode, "module"),
      (analysisNode, "customer_code"))
      .map(jv => {
        val (result, searchValue) = MDNodeUtil.extractValues(jv._1, (jv._2, SearchDictionary.searchFields(jv._2)) )
        m_log trace s"Field: ${jv._2}, \nSource JSON: ${compact(render(jv._1))},\n Search field type: ${SearchDictionary.searchFields(jv._2)}\n, Value: $searchValue"
        if (result) jv._2 -> Option(searchValue) else jv._2 -> None
      }).filter(_._2.isDefined).map(kv => kv._1 -> kv._2.get).toMap
  }

}
