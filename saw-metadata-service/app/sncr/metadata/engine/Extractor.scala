package sncr.metadata.engine

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsError, _}
import play.api.libs.json.Reads._
import sncr.metadata.MetadataDictionary

import scala.collection.mutable





/**
  * Created by srya0001 on 1/27/2017.
  * Class provides functions to validate request and extract request parameters,
  * save them into mutable Map, make them available for other modules (classes)
  */

class Extractor {

  val m_log: Logger = LoggerFactory.getLogger(classOf[Extractor].getName)

  var security : mutable.HashMap[String, Any] = new mutable.HashMap[String, Any]()

  def validateRequest(json: JsValue) : (Boolean, String) =
  {
    val (usid_bRes , uid) = testField(json, MetadataDictionary.user_id.toString )
    if ( !usid_bRes) return (false, "User info is missing")
    security(MetadataDictionary.user_id.toString) = uid.get

    val (dsk_bRes , dsk) = testField(json,MetadataDictionary.DSK.toString)
    if ( !dsk_bRes) return (false, "DSK is missing")
    security(MetadataDictionary.DSK.toString) = dsk.get

    val (tkn_bRes , token) = testField(json,MetadataDictionary.Token.toString)
    if ( !tkn_bRes) return (false, "Token is missing")
    security(MetadataDictionary.Token.toString) = token.get

    (true, "SUCCESS")
  }




  def getDataDescriptorHeader(json: JsValue) : (Boolean, String) =
  {

    val (st_bRes , st_value) = testField(json, "storage_type")
    if ( !st_bRes) return (false, "Storage type is missing")
    val stv = st_value.get

    if ( stv.equalsIgnoreCase("RDBMS") || stv.equalsIgnoreCase("DL") || stv.equalsIgnoreCase("ES"))
      values(MetadataDictionary.storage_type.toString) = stv
    else
      return (false, "Storage type value is not acceptable")

    m_log debug "Extracted storage type: " + stv

    val (in_bRes, in_value) = testField(json, "index_name")
    if (!in_bRes) return (false, "Storage type (ES) requires index name")
    val inn = in_value.get
    m_log debug "Extracted index name: " + inn
    values(MetadataDictionary.index_name.toString) = inn


    if (stv.equalsIgnoreCase("ES") && (inn == null || inn.isEmpty))
      return (false, "Storage type (ES) requires correct index name")

    //TODO:: Strictly speaking the object type is optional in ES requests. Think about???
    val (ot_bRes, ot_value) = testField(json, "object_type")
    val ot = if (ot_bRes) Option(ot_value.get) else None
    m_log debug "Extracted object type: " + ot
    values(MetadataDictionary.object_type.toString) = ot

    val (v_bRes, v_value) = testField(json, "verb")
    if (!v_bRes) return (false, "Storage type (ES) requires ES verb")
    val verb = v_value.get
    m_log debug "Extracted verb: " + verb
    values(MetadataDictionary.verb.toString) = verb

    if (stv.equalsIgnoreCase("ES") && (verb == null || verb.isEmpty ))
      return (false, "Storage type (ES) requires correct ES verb, e.g: _search")

    val queryLookupRes = (__ \ 'query).json.pick
    val pickResult = json.transform( queryLookupRes )
    pickResult match {
      case JsError(_) =>  val msg = "Mandatory field query not found"; m_log debug msg; (false, msg)
      case _ => values(MetadataDictionary.query.toString) =  pickResult.get; (true, "valid")
    }
  }

  var values : mutable.HashMap[String, Any] = new mutable.HashMap[String, Any]()


  private def testField( json: JsValue, fn: String) : ( Boolean, JsResult[String]) =
  {
    val vReads: Reads[String] = (JsPath \ fn).read[String]
    val value: JsResult[String] = json.validate[String](vReads)
    if ( value.isError || value.get == null || value.get.isEmpty ) return (false, null)
    (true, value)
  }


}
