package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.libs.Json
import play.mvc.{Controller, Http, Result}
import synchronoss.config.TSConfig
import synchronoss.ts.HTTPRequest

/**
  * Created by srya0001 on 6/28/2016.
  */
class TS extends Controller {

  val m_log: Logger = LoggerFactory.getLogger(classOf[TS].getName)

  def handleEmptyRequest( msg: String ) : Result = {
    val ctx: Http.Context = Http.Context.current.get
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "empty request")
    m_log.debug(s"Empty request with $msg content type came from: ${ctx.request().host()}/${ctx.request().username()}")
    play.mvc.Results.badRequest(res)
  }

  def query: Result = {

    val ctx: Http.Context = Http.Context.current.get

    val header = ctx._requestHeader()

    val res: ObjectNode = Json.newObject

    header.contentType match
    {
      case None  => if (ctx.request.body.asText == null) handleEmptyRequest("text/plain")
                           else process(ctx.request.body.asText())
      case _ =>  header.contentType.get match {
        case "text/plain" => if (ctx.request.body.asText == null) handleEmptyRequest("text/plain")
                                    else process(ctx.request.body.asText())
        case "application/x-www-form-urlencoded" => if (ctx.request.body.asFormUrlEncoded == null) handleEmptyRequest("application/x-www-form-urlencoded")
                                    else {
                                        import scala.collection.JavaConversions._
                                        val requestBody =  ctx.request.body.asFormUrlEncoded().mkString
                                        m_log debug s"URL encoded: $requestBody"
                                        process(requestBody)
                                    }
        case "application/json" => if (ctx.request.body.asJson == null) handleEmptyRequest("application/json")
                                          else { val body = ctx.request.body.asJson
//                                                      m_log debug ( "App/JSON: " + body.toString)
                                                     process(body.toString)
                                               }
        case "octet/stream" =>  if (ctx.request.body.asBytes.toArray == null) handleEmptyRequest("octet/stream")
                                       else process(ctx.request.body.asBytes.toArray)
        case _ =>
              res.put("result", "failure")
              res.put("reason", s"Unsupported content type: ${header.contentType}")
              m_log.debug(s"Unprocessed request: ${ctx._requestHeader.rawQueryString}")
              play.mvc.Results.badRequest(res)

      }
    }
  }

  def process(arr: Array[Byte]): Result =
  {
     process(play.api.libs.json.Json.parse(arr))
  }

  def process(txt: String): Result =
  {
    process(play.api.libs.json.Json.parse(txt))
  }


  def process(json: JsValue): Result = {
    val res: ObjectNode = Json.newObject
    m_log.debug("Validate and process request:  " + play.api.libs.json.Json.prettyPrint(json))

    val (isValid, msg) = validate(json)
    if (!isValid) {
      m_log debug msg
      res.put("result", msg)
      return play.mvc.Results.badRequest(res)
    }

    val eshost = TSConfig.es_conf.getString("host")
    val esport = TSConfig.es_conf.getInt("port")
    val timeout = TSConfig.es_conf.getInt("timeout")

    val req = new HTTPRequest(eshost, esport, timeout)
    stvalue match
    {
      case "ES" =>  return req.sendESRequest(verb, inn, ot, q, json)
      case "DL" =>  return req.sendESRequest(verb, inn, ot, q, json)
    }
    res.put("result", "failure")
    res.put("result", "Unsupported storage type")
    play.mvc.Results.badRequest(res)
  }

  private var stvalue : String = null
  private var inn : String = null
  private var ot : Option[String]  = null
  private var q : JsValue = null
  private var verb : String = null

  private def testField( json: JsValue, fn: String) : ( Boolean, JsResult[String]) =
  {
    val vReads: Reads[String] = (JsPath \ fn).read[String]
    val value: JsResult[String] = json.validate[String](vReads)
    if ( value.isError || value.get == null || value.get.isEmpty ) return (false, null)
    (true, value)
  }


  private def validate(json: JsValue) : (Boolean, String) =
  {

    val (st_bRes , st_value) = testField(json, "storage_type")
    if ( !st_bRes) return (false, "Storage type is missing")
    stvalue = st_value.get

    if (!stvalue.equalsIgnoreCase("DL") && !stvalue.equalsIgnoreCase("ES"))
        return (false, "Storage type value is not acceptable")
    m_log debug "Extracted storage type: " + inn

    val (in_bRes, in_value) = testField(json, "index_name")
    if (!in_bRes) return (false, "Storage type (ES) requires index name")
    inn = in_value.get
    m_log debug "Extracted index name: " + inn

    if (stvalue.equalsIgnoreCase("ES") && (inn == null || inn.isEmpty))
      return (false, "Storage type (ES) requires correct index name")

    //TODO:: Strictly speaking the object type is optional in ES requests. Think about???
    val (ot_bRes, ot_value) = testField(json, "object_type")
    ot = if (ot_bRes) Option(ot_value.get) else None
    m_log debug "Extracted object type: " + ot

    val (v_bRes, v_value) = testField(json, "verb")
    if (!v_bRes) return (false, "Storage type (ES) requires ES verb")
    verb = v_value.get
    m_log debug "Extracted verb: " + verb

    if (stvalue.equalsIgnoreCase("ES") && (verb == null || verb.isEmpty ))
      return (false, "Storage type (ES) requires correct ES verb, e.g: _search")

    val queryLookupRes = (__ \ 'query).json.pick
    val pickResult = json.transform( queryLookupRes )
    pickResult match {
      case JsError(_) =>  val msg = "Mandatory field query not found"; m_log debug msg; (false, msg)
      case _ => q =  pickResult.get; (true, "valid")
    }
  }

}

