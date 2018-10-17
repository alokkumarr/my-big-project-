package sncr.metadata.engine.ihandlers

import java.io.OutputStream

import org.apache.hadoop.hbase.util.Bytes
import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._
import org.json4s.{JArray, JString}
import org.slf4j.{Logger, LoggerFactory}
import sncr.analysis.execution.ExecutionTaskHandler
import sncr.metadata.analysis.{AnalysisExecutionHandler, AnalysisNode, AnalysisResult}
import sncr.metadata.datalake.DataObject
import sncr.metadata.engine.MDObjectStruct.{apply => _, _}
import sncr.metadata.engine.ProcessingResult._
import sncr.metadata.engine._
import sncr.metadata.engine.context.SelectModels
import sncr.metadata.engine.relations.CategorizedRelation
import sncr.metadata.semantix.SemanticNode
import sncr.metadata.ui_components.UINode


/*
   Created by srya0001 on 3/20/2017.

   [
   // Work item 1:
  {
  // Subject:
  "node" : "<NodeID>",

  // Node category: one of UINode, Analysis, DataObject, AnalysisResult, SemanticNode
  "node-category" : "UINode|Anlysis|DataObject|AnalysisResult"

  // Action descriptor
  "action" :
  {

 // What to do
 "verb" : "|create|update|delete|read|search|add-element|del-element|add-dl-location|del-dl-location",

 // Specific objects for node category:

 // UINode, Analysis, DataObject, AnalysisResult
			*"content" : {

  }

 // Analysis - anlysis is essencially Content-Relation Node
			*"base-relation" : [
				*{ "key": "id" , "value" : "<RowKey>" }
 ]

 //DataObject - to make the DataObject is more structured the key dl-location was introduced
			"dl-locations" : [
			"<location1>",
			"<location2>"
  ]

 //DataObject
			*"schema" : {

 }
		}
// in future implementation:
		"search-fields": [
				{ "name": "<Name>", "type" : "<Type>" },
  {}
  ]
	},
// Another work-item:
   {}

 ]

*/


import sncr.metadata.engine.MDObjectStruct.formats

import scala.language.implicitConversions
class RequestHandler(private[this] var request: String, outStream: OutputStream ){

  val m_log: Logger = LoggerFactory.getLogger(classOf[RequestHandler].getName)

  var requestJ : JValue = null
  try{
      requestJ = parse(request, false)
  }
  catch{
    case t: Throwable => { m_log.error("Native exceptions", t);   throw new Exception("Request is not parsable")}
  }
  var nodeCategory: String = null
  var id : String = null
  var action : JObject = null
  var schema : JObject = null
  var verb : String = null
  var content : JObject = null
  var dl_locations  : JArray = null
  var base_relation : JArray = null
  var keys : Map[String, JValue] = null
  var ui_module : String = null
  var ui_type : String = null
  var ui : JObject = null
  var manyWI : JArray = null
  var oneWI : JObject = null
  val respGenerator = new ResponseGenerator(outStream)

  def validate: (Integer, String) =
  {
    //General structure:
    requestJ match {
      case a:JArray => {
        var i: Int = 0
        a.arr.foreach(el => {
          el match {
            case obj: JObject => {
              var (res, msg) = validateWorkItem(obj)
              if (res != Success.id) {
                msg = s"Validation failed for work-item #$i: $msg"
                m_log error msg
                return (res, msg)
              }
            }
            case _ => return (Rejected.id, "Incorrect request structure")
          }
          i += 1
        })
        manyWI = a
        (Success.id, "All work-items are OK.")
      }
      case o:JObject => { oneWI = o; validateWorkItem( o )}
      case _ => ( Rejected.id, "Incorrect request structure")
    }
  }


  private def validateCreate: (Int, String) =
  {
    nodeCategory match {
      case "UINode" => if( content == null || content.obj.isEmpty ||
                           ui == null || ui.obj.isEmpty ||
                           ui_module == null || ui_module.isEmpty ||
                           ui_type == null || ui_type.isEmpty )
                          (Rejected.id, "Content is empty UINode creation requires content" )

      case "AnalysisNode" | "SemanticNode" => if( content == null || content.obj.isEmpty)
                          (Rejected.id, "Content is empty Analysis/Semantic node creation requires content" )
      case "DataObject"     => {
        if (content == null || content.obj.isEmpty)
          (Rejected.id, "Content and/or DataLake Locations are empty DataObject creation requires content and locations")
        try {
          val dataObject = new DataObject(content, schema)
          val (res, msg) = dataObject.validate
          if (res != ProcessingResult.Success.id)
            return (res, msg)
        }
        catch{
          case e:Throwable => {m_log.error("Native exception:", e); (Rejected.id, "Could not create DataObject from request")}
        }
      }
      case "AnalysisResult" =>  (Rejected.id, "The creation verb is not supported for this category" )
      case _ =>  (Rejected.id, s"Internal error: $nodeCategory")
    }
    (Success.id, "Verb/Action is good")
  }

  private def validateUpdate: (Int, String) =
  {
    nodeCategory match {
      case "UINode" => if (content == null || content.obj.isEmpty )
                        (Rejected.id, "Content is empty UINode update requires content" )

      case "AnalysisNode" | "SemanticNode" =>
        if( content == null || content.obj.isEmpty )
          (Rejected.id, "Content is empty Analysis/Semantic node creation requires content" )

      case "DataObject" => {
        if ((content == null || content.obj.isEmpty) &&
           (schema == null || dl_locations.arr.isEmpty))
           return (Rejected.id, "Content and/or DataObject schema are empty DataObject update requires content and/or schema ")
        try {
          DataObject(id).setDescriptor(content)
        }
        catch{
          case e:Throwable => (Rejected.id, "Update data object does not exist or cannot be loaded")
        }
      }

      case "AnalysisResult" =>  (Rejected.id, "The update verb is not supported for this category" )

      case _ =>  (Rejected.id, s"Internal error: $nodeCategory")
    }
    if( id == null || id.isEmpty )
      (Rejected.id, "Node ID is empty/not provided, the verb requires Node ID" )
    else
      (Success.id, "Verb/Action is good")
  }

  private def validateDelete: (Int, String) =
  {
    if( (id == null || id.isEmpty) && ( keys == null || keys.isEmpty) )
      (Rejected.id, "Node ID and keys are empty/ not provided, the verb requires at least on of this set" )
    else
      (Success.id, "Verb/Action is good")
  }

  private def validateRead: (Int, String) =
  {
    if( id == null || id.isEmpty )
      (Rejected.id, "Node ID is empty/not provided, the verb requires Node ID" )
    else
      (Success.id, "Verb/Action is good")
  }

  private def validateSearch: (Int, String) =
    if( keys == null || keys.isEmpty )
      (Rejected.id, "Keys are empty/not provided, the verb requires search keys" )
    else
      (Success.id, "Verb/Action is good")

  private def validateDLLoc: (Int, String) =
    if( dl_locations == null || dl_locations.arr.isEmpty || id == null || id.isEmpty)
      (Rejected.id, "DataLake Locations and/or NodeID are empty/not provided, the verb requires both." )
    else
      (Success.id, "Verb/Action is good")

  private def validateElements: (Int, String) =
    if( base_relation == null || base_relation.arr.isEmpty || id == null || id.isEmpty)
      (Rejected.id, "Relation elements and/or NodeID are empty/not provided, the verb requires both." )
    else
      (Success.id, "Verb/Action is good")

  private def validateExecute: (Int, String) =
  {
    if( id == null || id.isEmpty || !nodeCategory.equalsIgnoreCase("AnalysisNode"))
      (Rejected.id, "The verb is supported only for existing AnalysisNodes" )
    else
      (Success.id, "Verb/Action is good")
  }

  private def validateList: (Int, String) =
  {
      (Success.id, "Verb/Action is good")
  }

  def validateWorkItem(wi: JObject): (Integer, String) = {
    try {
      action = (wi \ "action").extractOrElse[JObject](JObject(Nil))
      if (action == null || action.obj.isEmpty) {
        val msg = "Action is empty, reject request."
        m_log error msg
        return (Rejected.id, msg)
      }

      verb = (action \ "verb").extractOrElse[String]("")
      if (verb == null || verb.isEmpty) {
        val msg = "Verb is empty, reject request."
        m_log error msg
        return (Rejected.id, msg)
      }

      nodeCategory = (wi \ "node-category").extractOrElse[String]("")
      nodeCategory match {
        case "UINode" | "AnalysisNode" | "DataObject" | "AnalysisResult" | "SemanticNode" =>
        case _ => {
          val msg = s"Node category is not set or incorrect."
          m_log error msg
          return (Rejected.id, msg)
        }
      }

      id = (wi \ "id").extractOrElse[String]("")
      schema = (action \ "schema").extractOrElse[JObject](JObject(Nil))
      content = (action \ "content").extractOrElse[JObject](JObject(Nil))
      dl_locations = (action \ "dl-locations").extractOrElse[JArray](JArray(Nil))
      base_relation = (action \ "base-relation").extractOrElse[JArray](JArray(Nil))
      keys = MDNodeUtil.extractKeysAsJValue(action \ "keys")

      ui = (action \ "ui").extractOrElse[JObject](JObject(Nil))
      ui_module = if (ui != null && ui.obj.nonEmpty)  ( ui \ "ui-module").extractOrElse[String]("") else ""
      ui_type = if (ui != null && ui.obj.nonEmpty)  ( ui \ "ui-type").extractOrElse[String]("") else ""

      verb match {
        case "create" => validateCreate
        case "update" => validateUpdate
        case "delete" => validateDelete
        case "read" => validateRead
        case "search" => validateSearch
        case "add-element" => validateElements
        case "del-element" => validateElements
        case "add-dl-location" => validateDLLoc
        case "del-dl-location" => validateDLLoc
        case "execute" => validateExecute
        case "list" => validateList
      }
      (Success.id, "Work item is OK")
    }
    catch {
      case x: Throwable => {
        m_log error("Validation failed, exception: ", x)
        val msg = "Validation failed, exception: " + x.getMessage
        (Rejected.id, msg)
      }
    }
  }

  def execute(): Unit =
  {
    if (manyWI != null)
      respGenerator.generate(manyWI.arr.map{ case o:JObject => executeWorkItem(o)
                                             case _ => m_log error "Incorrect request structure: work-item"; JNothing } )
    else
      respGenerator.generate(List(executeWorkItem(oneWI)))
  }



  private def executeExecute: JValue =
  {
    val er: ExecutionTaskHandler = new ExecutionTaskHandler(1)
    try {
      m_log debug s"Analysis ID: $id"
      val analysisNode = AnalysisNode(id)
      if ( analysisNode.getCachedData == null || analysisNode.getCachedData.isEmpty)
          throw new Exception("Could not find analysis node with provided search keys.")

      val aeh: AnalysisExecutionHandler = new AnalysisExecutionHandler(id, null)
      er.startSQLExecutor(aeh)
      val analysisResultId: String = er.getPredefResultRowID(id)
      m_log debug s"Predefined result ID: $analysisResultId"

      er.waitForCompletion(id, aeh.getWaitTime)
      val msg = "Execution: AnalysisID = " + id + ", Result Row ID: " + analysisResultId
      aeh.handleResult(outStream)
      respGenerator.build((Success.id, msg + ",  Execution result: " + aeh.getStatus))
    }
    catch {
      case e: Exception =>
          val msg = s"Execution exception: ${e.getMessage}"; m_log error (msg, e); respGenerator.build((Error.id, msg))
      }
  }



  private def read: JValue =
  {
    nodeCategory match {
      case "UINode"       => val uih = UINode(id); respGenerator.build(uih.getCachedData)
      case "AnalysisNode" => val ah = AnalysisNode(id); respGenerator.build(ah.getCachedData)
      case "DataObject"   => val doh = DataObject(id); respGenerator.build(doh.getCachedData)
      case "AnalysisResult"   => val arh = AnalysisResult(null, id); respGenerator.build(arh.getCachedData)
      case "SemanticNode" =>  val sh = SemanticNode(id, SelectModels.relation.id); respGenerator.build(sh.getCachedData)
      case _ => respGenerator.build(Error.id, "Not supported")
    }
  }

  private def update: JValue = {
    val keys = Map("id" -> id)
    nodeCategory match {
      case "UINode" => {
        val uih = new UINode(content, ui_module, ui_type);
        respGenerator.build(uih.update(keys))
      }
      case "AnalysisNode" => {
        val ah = AnalysisNode(id)
        if (content != null && content.obj.nonEmpty)
          ah.setDefinition(content)
        m_log debug s"Raw DO list: $base_relation"
        base_relation.arr.foreach {
          case o: JObject => {
            val lNodeID = (o \ "id").extractOrElse[String]("")
            val lNodeCategory = (o \ "node-category").extractOrElse[String]("")
            if (lNodeID.nonEmpty && lNodeCategory.nonEmpty) {
              val rels = ah.addNodeToRelation(lNodeID, lNodeCategory)
              m_log debug s"Nodes: ${compact(render(rels))}"
            }
          }
          case _ => m_log error "Incorrect request structure: base-relation entry, skip it"
        }
        respGenerator.build(ah.update(keys))
      }
      case "DataObject" => {
        val doh = DataObject(id)
        if (content != null && content.obj.nonEmpty) doh.setDescriptor(content)
        if (schema != null && schema.obj.nonEmpty) doh.setSchema(schema)
        if (dl_locations.arr.nonEmpty) {
          dl_locations.arr.foreach {
            case o: JString => doh.addLocation(o.s)
            case _ => m_log error "Incorrect location: skip it"
          }
        }
        respGenerator.build(doh.update())
      }
      case "SemanticNode" => {
        val snh: SemanticNode = SemanticNode(id, 3)
        if (content != null && content.obj.nonEmpty)
          snh.setSemanticNodeContent(content)
        m_log debug s"Raw DO list: $base_relation"
        base_relation.arr.foreach {
          case o: JObject => {
            val lNodeID = (o \ "id").extractOrElse[String]("")
            val lNodeCategory = (o \ "node-category").extractOrElse[String]("")
            if (lNodeID.nonEmpty && lNodeCategory.nonEmpty) {
              val rels = snh.addNodeToRelation(lNodeID, lNodeCategory)
              m_log debug s"Nodes: ${compact(render(rels))}"
            }
          }
          case _ => m_log error "Incorrect request structure: base-relation entry, skip it"
        }
        respGenerator.build(snh.update(keys))
      }
      case _ => respGenerator.build(Error.id, "Not supported")
    }
  }



  private def create: JValue =
  {
    nodeCategory match {
      case "UINode"       => {
        val uih = new UINode(content, ui_module, ui_type); respGenerator.build(uih.create)}
      case "AnalysisNode" => {
        val ah = new AnalysisNode(content)
        m_log debug s"Raw Rels: ${base_relation}"
        base_relation.arr.foreach {
          case o: JObject => {
            val lNodeID = (o \ "id").extractOrElse[String]("")
            val lNodeCategory = (o \ "node-category").extractOrElse[String]("")
            if (lNodeID.nonEmpty && lNodeCategory.nonEmpty) {
              val rels = ah.addNodeToRelation(lNodeID, lNodeCategory)
              m_log debug s"Nodes: ${compact(render(rels))}"
            }
          }
          case _ => m_log error "Incorrect request structure: base-relation entry, skip it"
        }
        respGenerator.build(ah.write)
      }
      case "DataObject"   => {
        val doh = new DataObject(content, if (schema == null || schema.obj.isEmpty) JNothing else schema)
        if (dl_locations.arr.nonEmpty){
            dl_locations.arr.foreach{
            case o: JString => val locs = doh.addLocation(o.s); m_log debug s"Locations: ${compact(render(locs))}"
            case _ => m_log error "Incorrect request structure: dl-location, skip it"
          }
        }
        respGenerator.build(doh.create)
      }
      case "SemanticNode" =>
        {
            val snh = new SemanticNode(content)
            m_log debug s"Raw DO list: $base_relation"
            base_relation.arr.foreach {
              case o: JObject => {
                val lNodeID = (o \ "id").extractOrElse[String]("")
                val lNodeCategory = (o \ "node-category").extractOrElse[String]("")
                if (lNodeID.nonEmpty && lNodeCategory.nonEmpty) {
                  val rels = snh.addNodeToRelation(lNodeID, lNodeCategory)
                  m_log debug s"Nodes: ${compact(render(rels))}"
                }
              }
              case _ => m_log error "Incorrect request structure: base-relation entry, skip it"
            }
            respGenerator.build(snh.create)
          }
      case _ => respGenerator.build(Error.id, "Not supported")
    }
  }

  private def search: JValue =
  {
    val keys2 : Map[String, Any] = MDNodeUtil.convertKeys(keys)
    val (nodeCategoryValue, table_name) = nodeCategory match {
      case "UINode"       => (classOf[UINode].getName, tables.UIMetadata.toString)
      case "AnalysisNode" => (classOf[AnalysisNode].getName, tables.AnalysisMetadata.toString)
      case "DataObject"   => (classOf[DataObject].getName, tables.DatalakeMetadata.toString)
      case "AnalysisResult" => (classOf[AnalysisResult].getName, tables.AnalysisResults.toString)
      case "SemanticNode" => (classOf[SemanticNode].getName, tables.SemanticMetadata.toString)
      case _ => return respGenerator.build(Error.id, "Not supported")
    }
    val systemKeys = Map( syskey_NodeCategory.toString -> nodeCategoryValue)
    val rowKeys = SearchMetadata.simpleSearch(table_name, keys2, systemKeys, "and")
    val nodes = rowKeys.map( rowKey => {
      val rowID = Bytes.toString(rowKey)
      try {
        nodeCategory match {
          case "UINode" => UINode(rowID).getCachedData
          case "AnalysisNode" => AnalysisNode(rowID).getCachedData
          case "DataObject" => DataObject(rowID).getCachedData
          case "AnalysisResult" => AnalysisNode(rowID).getCachedData
          case "SemanticNode" => SemanticNode(rowID).getCachedData
          case _ => return respGenerator.build(Error.id, "Not supported")
        }
    }
    catch{
      case e: Exception => m_log error (s"Could not load data for row key $rowID, continue", e); return JNothing
      case x: Throwable => m_log error ("Unrecoverable error occurred - cancel processing", x); return JNothing
    }
    })
    respGenerator.build(nodes)
  }


  private def list: JValue =
  {
    val table_name = nodeCategory match {
      case "UINode"       => tables.UIMetadata.toString
      case "AnalysisNode" => tables.AnalysisMetadata.toString
      case "DataObject"   => tables.DatalakeMetadata.toString
      case "AnalysisResult" => tables.AnalysisResults.toString
      case "SemanticNode" => tables.SemanticMetadata.toString
      case _ => return respGenerator.build(Error.id, "Not supported")
    }
    val rowKeyes = SearchMetadata.scanMDNodes(table_name)
    respGenerator.build(rowKeyes)
  }


  private def manageRelations(ah : CategorizedRelation) : Unit = {
    if (base_relation.arr.nonEmpty) {
      base_relation.arr.foreach {
        case o: JObject => {
          val lNodeID = (o \ "id").extractOrElse[String]("")
          val lNodeCategory = (o \ "node-category").extractOrElse[String]("")
          if (lNodeID.nonEmpty && lNodeCategory.nonEmpty) {
            verb match {
              case "add-element" =>
                val relation = ah.addNodeToRelation(lNodeID, lNodeCategory)
                m_log debug s"Nodes: ${compact(render(relation))}"
              case "del-element" =>
                val relation = ah.removeNodeFromRelation(lNodeID, lNodeCategory)
                m_log debug s"Nodes: ${compact(render(relation))}"
            }
          }
        }
        case _ => m_log error "Incorrect request structure: base-relation entry"
      }
    }
  }

  private def updateRelation(): JValue =
  {
    nodeCategory match {
      case "AnalysisNode" => {
        val ah = AnalysisNode(id)
        manageRelations(ah)
        respGenerator.build(ah.updateRelations)
      }
      case "SemanticNode" => {
        val ah = SemanticNode(id, 3)
        manageRelations(ah)
        respGenerator.build(ah.updateRelations)
      }
      case _ => respGenerator.build(Error.id, "Not supported.")
    }
  }

  private def delete: JValue =
  nodeCategory match {
    case "UINode"       => val uih = UINode(id); respGenerator.build(uih.delete)
    case "AnalysisNode" => val ah = AnalysisNode(id); respGenerator.build(ah.delete)
    case "DataObject"   => val doh = DataObject(id); respGenerator.build(doh.delete)
    case "AnalysisResult" => val arh = AnalysisResult(null, id); respGenerator.build(arh.delete)
    case "SemanticNode" => val snh = SemanticNode(id); respGenerator.build(snh.delete)
    case _ => respGenerator.build(Error.id, "Not supported.")
  }

  private def updateDLLoc(): JValue =
  {
    nodeCategory match {
      case "UINode" | "AnalysisNode" | "SemanticNode" | "AnalysisResult" => respGenerator.build(Error.id, "Not supported.")
      case "DataObject"   => {
        val doh = DataObject(id)
        m_log trace s"DL location src: ${compact(render(dl_locations))}"
        if (dl_locations.arr.nonEmpty){
          dl_locations.arr.foreach{
            case o: JString => if (verb.equals("del-dl-location")) doh.removeLocation(o.s) else doh.addLocation(o.s)
            case _ => m_log error "Incorrect request structure: dl-location, skip it"
          }
        }
        respGenerator.build(doh.updateDLLocations())
      }
    }
  }

  def executeWorkItem(value: JObject): JValue =
  {

    action = (value \ "action").extractOrElse[JObject](JObject(Nil))
    verb = (action \ "verb").extractOrElse[String]("")
    nodeCategory = (value \ "node-category").extractOrElse[String]("")

    id = (value \ "id").extractOrElse[String]("")
    schema = (action \ "schema").extractOrElse[JObject](JObject(Nil))
    content = (action \ "content").extractOrElse[JObject](JObject(Nil))
    dl_locations = (action \ "dl-locations").extractOrElse[JArray](JArray(Nil))
    base_relation = (action \ "base-relation").extractOrElse[JArray](JArray(Nil))

    keys = MDNodeUtil.extractKeysAsJValue(action \ "keys")

    ui = (action \ "ui").extractOrElse[JObject](JObject(Nil))
    ui_module = if (ui != null && ui.obj.nonEmpty)
                   ( ui \ "ui-module").extractOrElse[String](Fields.UNDEF_VALUE.toString)
                   else Fields.UNDEF_VALUE.toString
    ui_type =   if (ui != null && ui.obj.nonEmpty)
                   ( ui \ "ui-type").extractOrElse[String](Fields.UNDEF_VALUE.toString)
                   else Fields.UNDEF_VALUE.toString

    verb match{
      case "create" => create
      case "update" => update
      case "delete" => delete
      case "read"   => read
      case "search" => search
      case "add-element" | "del-element"          => updateRelation()
      case "add-dl-location" | "del-dl-location"  => updateDLLoc()
      case "execute"                              => executeExecute
      case "list"   => list
      case _ => m_log error s"Internal error, unknown verb => $verb"; JObject(Nil)
    }
  }


}












