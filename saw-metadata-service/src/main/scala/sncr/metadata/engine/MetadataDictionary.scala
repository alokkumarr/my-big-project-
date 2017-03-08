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

  val sourceSection = Value (0, "_source")
  val searchSection = Value (1, "_search")
  val relationsSection = Value (2, "_relations")
  val relationAttributesSection = Value(3, "_r_attributes")
  val systemProperties = Value(4, "_system")
  val analysisContent = Value(5, "_analysis")
  val dataObjects = Value(6, "_objects")
  val es_explore = Value(7, "_es_explore")
  val dataLakeLocation = Value(8, "_dl_locations")
  val applications = Value(9, "_applications")

  val key_Definition = Value(100, "content")
  val key_RelationSimpleSet = Value(101, "RelationSimpleSet")
  val syskey_NodeType = Value(102, "NodeType")
  val syskey_NodeCategory = Value(103, "NodeCategory")
  val syskey_RelCategory = Value(104, "RelCategory")
  val key_Searchable = Value(105, "Searchable")
  val key_SearchableRelation = Value(106, "SearchableRelation")


  val MDSections = Map(
      sourceSection.id -> Bytes.toBytes(sourceSection.toString),
      searchSection.id -> Bytes.toBytes(searchSection.toString),
      relationsSection.id -> Bytes.toBytes(relationsSection.toString),
      relationAttributesSection.id -> Bytes.toBytes(relationAttributesSection.toString),
      systemProperties.id -> Bytes.toBytes(systemProperties.toString),
      analysisContent.id -> Bytes.toBytes(analysisContent.toString),
      dataObjects.id -> Bytes.toBytes(dataObjects.toString),
      es_explore.id -> Bytes.toBytes(es_explore.toString),
      dataLakeLocation.id -> Bytes.toBytes(dataLakeLocation.toString),
      applications.id -> Bytes.toBytes(applications.toString)
  )

  val MDKeys = Map(
    key_Definition.id -> Bytes.toBytes(key_Definition.toString),
    key_RelationSimpleSet.id -> Bytes.toBytes(key_RelationSimpleSet.toString),
    syskey_NodeType.id -> Bytes.toBytes(syskey_NodeType.toString),
    syskey_NodeCategory.id -> Bytes.toBytes(syskey_NodeCategory.toString),
    syskey_RelCategory.id -> Bytes.toBytes(syskey_RelCategory.toString),
    key_Searchable.id -> Bytes.toBytes(key_Searchable.toString),
    key_SearchableRelation.id -> Bytes.toBytes(key_SearchableRelation.toString)
  )

}


object tables extends Enumeration {

  val SemanticMetadata = Value(0, "semantic_metadata")
  val DatalakeMetadata = Value(1, "datalake_metadata")
  val AnalysisMetadata = Value(2, "analysis_metadata")
  val AnalysisResults = Value(3, "analysis_results")

}

object ContentNodeCategory extends Enumeration {

  val  UINode         = Value(0, "UINode")
  val  SematicNode    = Value(1, "SemanticNode")
  val  DataObjectNode = Value(2, "DataObjectNode")
  val  AnalysisNode   = Value(3, "AnalysisNode")

}

object RelationCategory extends Enumeration {

  val RelationSimpleSet = Value(MDObjectStruct.key_RelationSimpleSet.id, MDObjectStruct.key_RelationSimpleSet.toString)

}


object NodeType extends Enumeration {

  val ContentNode = Value(0, "ContentNode")
  var RelationNode = Value(1, "RelationNode")
  var RelationContentNode = Value(2, "RelationContentNode")

}





