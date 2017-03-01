package sncr.metadata

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
  val elementList = Value(3, "_elements")
  val systemProperties = Value(4, "_system")
  val reportContent = Value(5, "_report")
  val dataObjects = Value(6, "_objects")
  val es_explore = Value(7, "_es_explore")
  val dataLakeLocation = Value(8, "_dl_locations")
  val applications = Value(9, "_applications")

  val columnContent = Value(100, "content")
  val columnReportParameters = Value(101, "reportParameters")


  val MDKeys = Map( sourceSection.id -> Bytes.toBytes(sourceSection.toString),
      searchSection.id -> Bytes.toBytes(searchSection.toString),
      relationsSection.id -> Bytes.toBytes(relationsSection.toString),
      elementList.id -> Bytes.toBytes(elementList.toString),
      systemProperties.id -> Bytes.toBytes(systemProperties.toString),
      reportContent.id -> Bytes.toBytes(reportContent.toString),
      dataObjects.id -> Bytes.toBytes(dataObjects.toString),
      es_explore.id -> Bytes.toBytes(es_explore.toString),
      dataLakeLocation.id -> Bytes.toBytes(dataLakeLocation.toString),
      applications.id -> Bytes.toBytes(applications.toString),
      columnContent.id -> Bytes.toBytes(columnContent.toString))
}


object tables extends Enumeration {

  val SemanticMetadata = Value(0, "semantic_metadata")
  val DatalakeMetadata = Value(1, "datalake_metadata")
  val reports = Value(2, "report_metadata")
  val reportResults = Value(3, "report_results")

}