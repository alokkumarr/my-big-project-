package controllers

import java.text.SimpleDateFormat

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import play.libs.Json
import play.mvc.{Http, Result}

import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.engine.MDNodeUtil
import sncr.metadata.engine.ProcessingResult._

class ANA extends BaseServiceProvider {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  override def process(arr: Array[Byte]): Result = {
    process(new String(arr))
  }

  override def process(txt: String): Result = {
    process(parse(txt, false, false))
  }

  def process(json: JValue): Result = {
    val action = (json \ "contents" \ "action").extract[String].toLowerCase
    val response = action match {
      case "create" => {
        val analysisListJson = json \ "contents" \ "analysis"
        val analysisJson = analysisListJson match {
          case array: JArray => {
            if (array.arr.length > 1) {
              throw new RuntimeException("Only one element supported")
            }
            if (array.arr.length == 0) {
              throw new RuntimeException("No element to write found")
            }
            array.arr(0)
          }
          case _ => throw new RuntimeException(
            "Expected array: " + analysisListJson)
        }
        val analysisNode = new AnalysisNode(analysisJson)
        val (result, message) = analysisNode.write
        if (result != Success.id) {
          throw new RuntimeException("Writing failed: " + message)
        }
        json
      }
      case "read" => {
        val analysisId = (json \ "contents" \ "keys")(0)
        val analysisNode = new AnalysisNode
        val result = analysisNode.read(Map("analysisId" -> analysisId))
        if (result == Map.empty) {
          throw new RuntimeException("Reading failed")
        }
        result("content") match {
          case content: JValue => {
            json merge(
              ("contents", ("analysis", JArray(List(content)))) ~ (".", "."))
          }
          case _ => throw new RuntimeException("no match")
        }
      }
      case _ => {
        throw new RuntimeException("Unknown action: " + action)
      }
    }
    val playJson = Json.parse(compact(render(response)))
    play.mvc.Results.ok(playJson)
  }
}
