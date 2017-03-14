package sncr.metadata.engine

import java.text.SimpleDateFormat

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


}


object MDObjectStruct extends Enumeration{

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  val _cf_source = Value (0, "_source")
  val _cf_search = Value (1, "_search")
  val _cf_objects = Value(3, "_objects")
  val _cf_relations = Value (2, "_relations")
  val systemProperties = Value(4, "_system")
  val _cf_es_explore = Value(7, "_es_explore")
  val _cf_datalakelocations = Value(8, "_dl_locations")
  val _cf_applications = Value(9, "_applications")

  val key_Definition = Value(100, "content")
  val key_RelationSimpleSet = Value(101, "RelationSimpleSet")

  val syskey_NodeType = Value(102, "NodeType")
  val syskey_NodeCategory = Value(103, "NodeCategory")
  val syskey_RelCategory = Value(104, "RelCategory")

  val key_Searchable = Value(105, "Searchable")
  val key_Schema = Value(106, "DataObject_Schema")
  val key_DL_DataLocation = Value(107, "DL_DataLocation")


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
    key_DL_DataLocation.id -> Bytes.toBytes(key_DL_DataLocation.toString)
  )

}


object tables extends Enumeration {

  val SemanticMetadata = Value(0, "semantic_metadata")
  val DatalakeMetadata = Value(1, "datalake_metadata")
  val AnalysisMetadata = Value(2, "analysis_metadata")
  val AnalysisResults = Value(3, "analysis_results")

}


object RelationCategory extends Enumeration {

  val UndefinedRelation = Value(0, "_undefined_")
  val RelationSimpleSet = Value(MDObjectStruct.key_RelationSimpleSet.id, MDObjectStruct.key_RelationSimpleSet.toString)

}


object NodeType extends Enumeration {

  val ContentNode = Value(0, "ContentNode")
  var RelationNode = Value(1, "RelationNode")
  var RelationContentNode = Value(2, "RelationContentNode")

}





