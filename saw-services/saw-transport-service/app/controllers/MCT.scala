package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.libs.Json
import play.mvc.Result

/**
  * Created by srya0001 on 3/1/2017.
  */
class MCT extends BaseServiceProvider{

  def handleTagRequest(LCID: String, query: Option[String]): Result =
  {
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "Not Implemented")
    play.mvc.Results.badRequest(res)
  }


  def extendedTagRequest(LCID: String, indexname: Option[String], objecttype: Option[String]): Result =
  {
    val res: ObjectNode = Json.newObject
    res.put("result", "failure")
    res.put("reason", "Not Implemented")
    play.mvc.Results.badRequest(res)
  }

}
