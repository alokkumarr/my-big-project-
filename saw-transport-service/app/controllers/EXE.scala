package controllers

import java.text.SimpleDateFormat

import com.fasterxml.jackson.databind.node.ObjectNode
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import play.libs.Json
import play.mvc.Result

/**
  * Created by srya0001 on 3/1/2017.
  */
class EXE extends BaseServiceProvider {

  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  override def process(arr: Array[Byte]): Result =
  {
    process(new String(arr))
  }

  override def process(txt: String): Result =
  {
    process( parse(txt, false, false))
  }

  def process(json: JValue): Result = {
    m_log trace("Validate and process request:  " + compact(render(json)))
    val res: ObjectNode = Json.newObject
    play.mvc.Results.ok(res)
  }



}
