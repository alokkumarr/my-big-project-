package controllers

import org.json4s.JsonAST.{JField, JObject, JString}
import play.mvc.Result

/**
 * Actuator endpoints for monitoring health and other metrics
 */
class Actuator extends BaseController {
  def health: Result = {
    handle((json, ticket) => {
      JObject(List(JField("status", JString("UP"))))
    })
  }
}
