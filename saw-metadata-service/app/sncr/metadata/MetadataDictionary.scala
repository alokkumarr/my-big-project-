package sncr.metadata

import java.text.SimpleDateFormat

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

object SearchDictionary{

  val searchFields = List ("customer_Prod_module_feature_sys_id")


}


object MetadataObjectStructure extends Enumeration{

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  val sourceSection = Value (0, "_source")
  val searchSection = Value (1, "_search")
  val relationsSection = Value (2, "_relations")
  val columnContent = Value(10, "content")

}