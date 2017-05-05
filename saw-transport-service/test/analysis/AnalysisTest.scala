import java.text.SimpleDateFormat

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatest.CancelAfterFailure

/* Test analysis service operations */
class AnalysisTest extends MaprTest with CancelAfterFailure {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  "Analysis service" should {
    requireMapr
    var id: String = null
    var analysis: JValue = null

    "create analysis" in {
      /* Create analysis using analysis template preloaded into MapR-DB */
      val semanticId = "c7a32609-2940-4492-afcc-5548b5e5a040"
      val typeJson: JObject = ("analysisType", "report")
      val response = sendRequest(
        actionKeyMessage("create", semanticId, typeJson))
      val JString(analysisId) = analyze(response) \ "id"
      analysisId must not be (semanticId)
      /* Save analysis ID for use by subsequent tests */
      id = analysisId
      val JString(name) = analyze(response) \ "metricName"
      name must be ("MCT Session")
      val JString(sId) = analyze(response) \ "semanticId"
      sId must be (semanticId)
      val JString(analysisType) = analyze(response) \ "type"
      analysisType must be ("report")
    }

    "read analysis" in {
      /* Read back previously created analysis */
      val body = actionKeyMessage("read", id)
      analysis = analyze(sendRequest(body))
      val JString(metricName) = analysis \ "metricName"
      metricName must be ("MCT Session")
    }

    "update analysis" in {
      /* Update previously created analysis */
      val checkedJson: JValue = ("checked", "true")
      analysis = analysis.merge(checkedJson)
      /* Set columns to checked to ensure there are at least some selected
       * columns for the query builder */
      analysis = analysis.transform {
        case column: JObject => {
          if ((column \ "aliasName").extractOrElse[String]("none") != "none")
            column ~ ("checked", true)
          else
            column
        }
      }
      val body = actionKeyAnalysisMessage("update", id, analysis)
      analysis = analyze(sendRequest(body))
      val JString(analysisId) = analysis \ "id"
      analysisId must be (id)
    }

    "execute analysis" in {
      /* Execute previously created analysis */
      val body = actionKeyMessage("execute", id)
      val analysis = analyze(sendRequest(body))
      /* Note: Currently the result is just the static mock */
      val JString(value) = (analysis \ "data")(0) \ "SESSION_ID"
      value must be ("0025D642-5ED9-49DE-96B5-6F643DFDBB1F")
    }

    "delete analysis" in {
      /* Delete previously created analysis */
      val body = actionKeyMessage("delete", id)
      val response = sendRequest(body)
      val JString(action) = response \ "contents" \ "action"
      action must be ("delete")
    }
  }

  def analyze(response: JValue): JValue = {
    val analyzes = (response \ "contents" \ "analyze") match {
      case array: JArray => array.arr
      case obj: JValue => throw new RuntimeException("Expected JArray: " + obj)
    }
    analyzes.length must be (1)
    analyzes(0)
  }
}
