package controllers


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.{Logger, LoggerFactory}
import play.libs.Json
import play.mvc.{Controller, Http, Result}
import synchronoss.config.TSConfig
import synchronoss.ts.HTTPRequest

/**
  * Created by srya0001 on 6/28/2016.
  */
class TS extends Controller {

  val m_log: Logger = LoggerFactory.getLogger(classOf[TS].getName)



  def query: Result = {

    val ctx: Http.Context = Http.Context.current.get
    val header = ctx._requestHeader()

    if (header.contentType.get.equals("application/json") )
    {
       return process(ctx.request.body.asJson())
    }

    if (header.contentType.get.equals("text/plain")){
       return process(ctx.request.body.asText())
    }

    if (header.contentType.get.equals("octet/stream")){
      return process(ctx.request.body.asBytes().toArray)
    }


    val res: ObjectNode = Json.newObject
    res.put("result", "success")

    return play.mvc.Results.ok(res)

  }

  def process(arr: Array[Byte]): Result =
  {
     process(play.libs.Json.parse(arr))
  }

  def process(txt: String): Result =
  {
    process(play.libs.Json.parse(txt))
  }


  def process(json: JsonNode): Result = {
    val res: ObjectNode = Json.newObject
    val (isValid, msg) = validate(json)
    if (!isValid) {
      res.put("result", msg)
      return play.mvc.Results.badRequest(res)
    }

    val eshost = TSConfig.es_conf.getString("host")
    val esport = TSConfig.es_conf.getInt("port")
    val timeout = TSConfig.es_conf.getInt("timeout")

    val req = new HTTPRequest(eshost, esport, timeout)
    stvalue match
    {
      case "ES" =>  return req.sendESRequest(verb.asText(), inn.asText(), ot.asText())
      case "DL" =>  return  req.sendESRequest(verb.asText(), inn.asText(), ot.asText())

    }
    return play.mvc.Results.ok(res)
  }

  private var stvalue : String = null
  private var inn : JsonNode = null
  private var ot : JsonNode  = null
  private var q : JsonNode = null
  private var verb : JsonNode = null

  private def validate(json: JsonNode) : (Boolean, String) =
  {
    val stn = json.get("StorageType")
    stvalue = if ( stn != null ) stn.asText() else "none"
    if (!stvalue.equalsIgnoreCase("DL") && !stvalue.equalsIgnoreCase("ES"))
        return (false, "Storage type is missing or value is not acceptable")

    inn = json.findValue("IndexName")
    if (stvalue.equalsIgnoreCase("ES") && (inn == null || inn.asText().isEmpty))
      return (false, "Storage type (ES) requires index name")

    ot = json.findValue("ObjectType")
    if (stvalue.equalsIgnoreCase("ES") && (ot == null || ot.asText().isEmpty ))
      return (false, "Storage type (ES) requires object type")

    verb = json.findValue("Verb")
    if (stvalue.equalsIgnoreCase("ES") && (verb == null || verb.asText().isEmpty ))
      return (false, "Storage type (ES) requires object type")

    q = json.findValue("Query")
    if (q == null || q.isObject)
      return (false, "Incorrect query")

    (true, "valid")
  }



}

