package sncr.request

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._
import sncr.metadata.engine.MetadataDictionary


/**
  * Created by srya0001 on 1/27/2017.
  * Class provides functions to validate request and extract request parameters,
  * save them into mutable Map, make them available for other modules (classes)
  */

class Extractor {

  val m_log: Logger = LoggerFactory.getLogger(classOf[Extractor].getName)

  var security : scala.collection.mutable.HashMap[String, Any] = new scala.collection.mutable.HashMap[String, Any]()

  def validateRequest(json: JsValue) : (Boolean, String) =
  {
    val (dsk_bRes, dsk) = testField(json, MetadataDictionary.DSK.toString)
    if (!dsk_bRes) return (false, "DSK is missing")
    security(MetadataDictionary.DSK.toString) = dsk.get
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
    stv match {
      case "ES" =>  {val (esRes, esMsg) = extractESSpecificValues(stv, json); if (!esRes) return (esRes, esMsg)}
      case "DL" =>  {val (dlRes, dlMsg) = extractDLSpecificValues(stv, json); if (!dlRes) return (dlRes, dlMsg)}
      case _ => return (false, "Unsupported storage type!")
    }

    val queryLookupRes = (__ \ 'query).json.pick
    val pickResult = json.transform( queryLookupRes )
    pickResult match {
      case JsError(_) =>  val msg = "Mandatory field query not found"; m_log debug msg; (false, msg)
      case _ => values(MetadataDictionary.query.toString) =  pickResult.get; (true, "valid")
    }
  }

  var values : scala.collection.mutable.HashMap[String, Any] = new scala.collection.mutable.HashMap[String, Any]()


  private def testField( json: JsValue, fn: String) : ( Boolean, JsResult[String]) =
  {
    val vReads: Reads[String] = (JsPath \ fn).read[String]
    val value: JsResult[String] = json.validate[String](vReads)
    if ( value.isError || value.get == null || value.get.isEmpty ) return (false, null)
    (true, value)
  }


  def extractESSpecificValues(stv: String, json: JsValue): (Boolean, String) ={

    val (in_bRes, in_value) = testField(json, "index_name")
    if (!in_bRes) return (false, "Storage type (ES) requires index name")
    val inn = in_value.get
    m_log debug "Extracted index name: " + inn
    values(MetadataDictionary.index_name.toString) = inn


    if (inn == null || inn.isEmpty) return (false, "Storage type (ES) requires correct index name")

    val (ot_bRes, ot_value) = testField(json, "object_type")
    val ot = if (ot_bRes) values(MetadataDictionary.object_type.toString) = ot_value.get
    m_log debug "Extracted object type: " + ot


    val (v_bRes, v_value) = testField(json, "verb")
    if (!v_bRes) return (false, "Storage type (ES) requires ES verb")
    val verb = v_value.get
    m_log debug "Extracted verb: " + verb
    values(MetadataDictionary.verb.toString) = verb

    if (verb == null || verb.isEmpty ) return (false, "Storage type (ES) requires correct ES verb, e.g: _search")

    (true, "success")
  }


  private def extractDLSpecificValues(stv: String, json: JsValue): (Boolean, String) ={


    val ids = List( MetadataDictionary.analysisId.toString,
                    MetadataDictionary.semanticId.toString,
                    MetadataDictionary.dataObjectId.toString,
                    MetadataDictionary.verb.toString,
                    MetadataDictionary.analysisResult.toString)

    ids.foreach( idType => {
    val (bRes, v) = testField(json, idType); if (bRes) {
        val id = v.get
        m_log debug s"Extracted $idType = $id"; values(idType) = id}})

    if (!values.keySet.exists( k => ids.foldLeft(false)( (p, s) => p || s.equals(k))))
      return (false, "Request must contain one of the following: Analysis ID or Semantic ID or DataObject ID")

    (true, "success")
  }

}
