import java.text.SimpleDateFormat
import java.util.UUID

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
    val categoryId = UUID.randomUUID.toString

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
      analysis = analysis.merge(
        ("saved", "true") ~ ("categoryId", categoryId))
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

    "search analysis by category" in {
      /* Search for previously created analysis */
      val body = actionKeysMessage(
        "search", ("categoryId", categoryId): JObject)
      analysis = analyze(sendRequest(body))
      val JString(metricName) = analysis \ "metricName"
      metricName must be ("MCT Session")
    }

    "search multiple analyses by category" in {
      /* Create a second analysis */
      val semanticId = "c7a32609-2940-4492-afcc-5548b5e5a040"
      val typeJson: JObject = ("analysisType", "report")
      val secondAnalysis = analyze(sendRequest(
        actionKeyMessage("create", semanticId, typeJson)))
      val JString(secondAnalysisId) = secondAnalysis \ "id"
      /* Update analysis with category */
      val updatedAnalysis = checkColumns(secondAnalysis.merge(
        ("saved", "true") ~ ("categoryId", categoryId)))
      analyze(sendRequest(actionKeyAnalysisMessage(
        "update", secondAnalysisId, updatedAnalysis)))
      /* Search for both analyses */
      val body = actionKeysMessage(
        "search", ("categoryId", categoryId): JObject)
      val analyses = extractArray(sendRequest(body) \ "contents", "analyze")
      analyses.length must be (2)
    }

    "execute analysis" in {
      /* Execute previously updated analysis */
      val body = actionKeyMessage("execute", id)
      val response = analyze(sendRequest(body))
      val JString(value) = (response \ "data")(0) \ "SESSION_ID"
      value must be ("0025D642-5ED9-49DE-96B5-6F643DFDBB1F")
    }

    "execute analysis with manual query" in {
      /* Update previously executed analysis */
      val manualJson: JValue = ("queryManual", "SELECT 1 AS a")
      analysis = analyze(sendRequest(
        actionKeyAnalysisMessage("update", id,
          checkColumns(analysis.merge(manualJson), false))))
      /* Execute updated analysis */
      val body = actionKeyMessage("execute", id)
      val response = analyze(sendRequest(body))
      val JInt(value) = (response \ "data")(0) \ "a"
      value must be (1)
    }

    "delete analysis" in {
      /* Delete previously created analysis */
      val body = actionKeyMessage("delete", id)
      val response = sendRequest(body)
      val JString(action) = response \ "contents" \ "action"
      action must be ("delete")
    }
  }

  private def analyze(response: JValue): JValue = {
    val analyzes = (response \ "contents" \ "analyze") match {
      case array: JArray => array.arr
      case obj: JValue => throw new RuntimeException("Expected JArray: " + obj)
    }
    analyzes.length must be (1)
    analyzes(0)
  }

  private def extractArray(json: JValue, name: String): List[JValue] = {
    json \ name match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json)
    }
  }

  private def checkColumns(analysis: JValue, value: Boolean = true): JValue = {
    /* Set columns to checked to ensure there are at least some selected
     * columns for the query builder */
    analysis.transform {
      case column: JObject => {
        if ((column \ "aliasName").extractOrElse[String]("none") != "none")
          column ~ ("checked", value)
        else
          column
      }
    }
  }

  private def unexpectedElement(json: JValue): Nothing = {
    val name = json.getClass.getSimpleName
    fail("Unexpected element: %s".format(name))
  }
}
