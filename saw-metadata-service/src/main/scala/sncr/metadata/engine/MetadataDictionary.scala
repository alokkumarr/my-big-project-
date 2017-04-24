package sncr.metadata.engine

import org.apache.hadoop.hbase.util.Bytes
import org.json4s.DefaultFormats

/**
  * Created by srya0001 on 1/27/2017.
  */
object MetadataDictionary extends Enumeration {

  val user_id = Value(0, "user_id")
  val DSK = Value(1, "dsk" )
  val Token = Value(2, "token" )
  val storage_type = Value(3, "storage_type")
  val index_name = Value(10, "index_name")
  val object_type = Value(11, "object_type")
  val verb = Value(12, "verb")
  val query = Value(13, "query")

  val separator: String = "::"

}


object MDObjectStruct extends Enumeration{

  implicit val formats = DefaultFormats

  val _cf_source = Value (0, "_source")
  val _cf_search = Value (1, "_search")
  val _cf_objects = Value(3, "_objects")
  val _cf_relations = Value (2, "_relations")
  val systemProperties = Value(4, "_system")
  val _cf_es_explore = Value(7, "_es_explore")
  val _cf_datalakelocations = Value(8, "_dl_locations")
  val _cf_applications = Value(9, "_applications")

  val key_Definition = Value(100, "content")
  val key_RelationSimpleSet = Value(101, "AtomicRelation")
  val key_EnrichedContentRelation = Value(102, "BetaRelation")

  val syskey_NodeType = Value(200, "NodeType")
  val syskey_NodeCategory = Value(201, "NodeCategory")
  val syskey_RelCategory = Value(202, "RelCategory")

  val key_Searchable = Value(300, "Searchable")
  val key_Schema = Value(301, "DataObject_Schema")
  val key_DL_DataLocation = Value(302, "DL_DataLocation")
  val key_AnalysisResultObjects = Value(303, "AnalysisResultObjects")


  val MDColumnFamilies = Map(
      _cf_source.id -> Bytes.toBytes(_cf_source.toString),
      _cf_search.id -> Bytes.toBytes(_cf_search.toString),
      _cf_objects.id -> Bytes.toBytes(_cf_objects.toString),
      _cf_relations.id -> Bytes.toBytes(_cf_relations.toString),
      systemProperties.id -> Bytes.toBytes(systemProperties.toString),
      _cf_es_explore.id -> Bytes.toBytes(_cf_es_explore.toString),
      _cf_datalakelocations.id -> Bytes.toBytes(_cf_datalakelocations.toString),
      _cf_applications.id -> Bytes.toBytes(_cf_applications.toString)
  )

  val MDKeys = Map(
    key_Definition.id -> Bytes.toBytes(key_Definition.toString),
    key_RelationSimpleSet.id -> Bytes.toBytes(key_RelationSimpleSet.toString),
    syskey_NodeType.id -> Bytes.toBytes(syskey_NodeType.toString),
    syskey_NodeCategory.id -> Bytes.toBytes(syskey_NodeCategory.toString),
    syskey_RelCategory.id -> Bytes.toBytes(syskey_RelCategory.toString),
    key_Searchable.id -> Bytes.toBytes(key_Searchable.toString),
    key_Schema.id -> Bytes.toBytes(key_Schema.toString),
    key_DL_DataLocation.id -> Bytes.toBytes(key_DL_DataLocation.toString),
    key_AnalysisResultObjects.id ->  Bytes.toBytes(key_AnalysisResultObjects.toString)
  )

}


object tables extends Enumeration {

  val SemanticMetadata = Value(0, "semantic_metadata")
  val DatalakeMetadata = Value(1, "datalake_metadata")
  val AnalysisMetadata = Value(2, "analysis_metadata")
  val AnalysisResults = Value(3, "analysis_results")
  val UIMetadata = Value(4, "ui_metadata")

}

object NodeCategoryMapper extends Enumeration {

  val SemanticNode =    Value(0, "semantic_metadata")
  val UINode =          Value(1, "semantic_metadata")
  val AnalysisNode =    Value(2, "analysis_metadata")
  val DataObject =      Value(3, "datalake_metadata")
  val AnalysisResults = Value(4, "analysis_results")

  val NCM = Map(
    "UINode" -> UINode,
    "AnalysisNode" -> AnalysisNode,
    "SemanticNode" -> SemanticNode,
    "DataObject" -> DataObject,
    "AnalysisResult" -> AnalysisResults
  )
}


object RelationCategory extends Enumeration {

  val UndefinedRelation = Value(0, "_undefined_")
  val BetaRelation = Value(MDObjectStruct.key_EnrichedContentRelation.id, MDObjectStruct.key_EnrichedContentRelation.toString)
  val AtomicRelation = Value(MDObjectStruct.key_RelationSimpleSet.id, MDObjectStruct.key_RelationSimpleSet.toString)

}


object NodeType extends Enumeration {

  val ContentNode = Value(0, "ContentNode")
  var RelationNode = Value(1, "RelationNode")
  var RelationContentNode = Value(2, "RelationContentNode")

}

object Fields extends Enumeration {

  val UNDEF_VALUE = Value(0, "_undefined_")
  val NodeId = Value(1, "id")
  val NumOfLocations =  Value(2, "_number_of_locations")
  val NumOfElements =  Value(3, "_number_of_elements")
  val RelationCategory = Value(4, "_relation_category")
  val ObjectDescriptor = Value(5, "_object_descriptor")
  val ObjectMetadata = Value(6, "_object_metadata")

}




