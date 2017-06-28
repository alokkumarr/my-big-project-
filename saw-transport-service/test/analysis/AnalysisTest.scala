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
    var executionId: String = null
    var analysis: JValue = null
    val categoryId = UUID.randomUUID.toString

    "create analysis" in {
      /* Create analysis using analysis template preloaded into MapR-DB */
      val semanticId = "340df99d-16a6-45cb-af49-5699e3337e50"
      val typeJson: JObject = ("analysisType", "report")
      val response = sendRequest(
        actionKeyMessage("create", semanticId, typeJson))
      val JString(analysisId) = analyze(response) \ "id"
      analysisId must not be (semanticId)
      /* Save analysis ID for use by subsequent tests */
      id = analysisId
      val JString(name) = analyze(response) \ "metricName"
      name must be ("MCT Events aggregated by session (view)")
      val JString(sId) = analyze(response) \ "semanticId"
      sId must be (semanticId)
      val createdTimestamp = (analyze(response) \ "createdTimestamp")
        .extract[Long]
      createdTimestamp must be > 1490000000000L;
      val JString(analysisType) = analyze(response) \ "type"
      analysisType must be ("report")
      val JInt(userId) = analyze(response) \ "userId"
      userId must be (1)
      val JString(userFullName) = analyze(response) \ "userFullName"
      userFullName must be ("SAW Admin")
    }

    "read analysis" in {
      /* Read back previously created analysis */
      val body = actionKeyMessage("read", id)
      analysis = analyze(sendRequest(body))
      val JString(metricName) = analysis \ "metricName"
      metricName must be ("MCT Events aggregated by session (view)")
    }

    "update analysis" in {
      /* Update previously created analysis */
      analysis = analysis.merge(
        ("saved", "true") ~
          ("categoryId", categoryId) ~
          ("schedule", ("repeatUnit", "daily") ~
            ("repeatInterval", 1)))
      /* Add a runtime filter for use in subsequent executions */
      analysis = analysis.merge(("sqlBuilder", ("filters", List(
        ("type", "string") ~
          ("tableName", "MCT_SESSION") ~
          ("columnName", "TRANSFER_END_TS") ~
          /* Because the query normally returns rows, intentionally use filter
           * that doesn't match any rows to ensure filter is in
           * effect */
          ("model", ("modelValues", List("nonexistent"))) ~
          ("isRuntimeFilter", true)))): JObject)
      /* Set columns to checked to ensure there are at least some selected
       * columns for the query builder */
      analysis = checkColumns(analysis, "TRANSFER_END_TS")
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
      metricName must be ("MCT Events aggregated by session (view)")
    }

    "search multiple analyses by category" in {
      /* Create a second analysis */
      val semanticId = "340df99d-16a6-45cb-af49-5699e3337e50"
      val typeJson: JObject = ("analysisType", "report")
      val secondAnalysis = analyze(sendRequest(
        actionKeyMessage("create", semanticId, typeJson)))
      val JString(secondAnalysisId) = secondAnalysis \ "id"
      /* Update analysis with category */
      val updatedAnalysis = checkColumns(secondAnalysis.merge(
        ("saved", "true") ~ ("categoryId", categoryId)), "TRANSFER_END_TS")
      analyze(sendRequest(actionKeyAnalysisMessage(
        "update", secondAnalysisId, updatedAnalysis)))
      /* Search for both analyses */
      val body = actionKeysMessage(
        "search", ("categoryId", categoryId): JObject)
      val analyses = extractArray(sendRequest(body) \ "contents", "analyze")
      analyses.length must be (2)
    }

    "list analysis schedules" in {
      /* List results of previously updated analysis */
      val response = sendGetRequest("/analysis?view=schedule")
      val results = extractArray(response, "analyses")
      /* Ensure the previously updated analysis with a schedule exists among
       * the analyses with a schedule set */
      results.exists(analysis => {
        (analysis \ "id").extract[String] == id
      }) must be (true)
    }

    "execute non-interactive analysis" in {
      /* Execute previously updated analysis */
      val body = actionKeyAnalysisMessage("execute", id,
        analysis.merge(("executionType", "scheduled"): JObject))
      val response = analyze(sendRequest(body))
      val JString(value) = (response \ "data")(0) \ "TRANSFER_END_TS"
      value must be ("21-DEC-16 11.14.05.000000 AM")
    }

    "execute interactive analysis with runtime filters" in {
      /* Execute existing analysis with runtime filters*/
      val body = actionKeyAnalysisMessage("execute", id, analysis)
      val response = analyze(sendRequest(body))
      /* Expect zero rows in result because runtime filter is designed to
       * rejects all rows */
      extractArray(response, "data").length must be (0)
    }

    "execute analysis with manual query" in {
      /* Update previously executed analysis */
      val manualJson: JValue = ("queryManual", "SELECT 1 AS a")
      analysis = analyze(sendRequest(
        actionKeyAnalysisMessage("update", id, analysis.merge(manualJson))))
      /* Execute updated analysis */
      val body = actionKeyAnalysisMessage("execute", id, analysis)
      val response = analyze(sendRequest(body))
      val JInt(value) = (response \ "data")(0) \ "a"
      value must be (1)
    }

    "list analysis executions" in {
      /* List results of previously executed analysis */
      val response = sendGetRequest("/analysis/%s/executions".format(id))
      val results = extractArray(response, "execution")
      /* There are three executions in the preceding tests, so the results
       * list should contain three elements */
      results.length must be (3)
      executionId = (results(0) \ "id").extract[String]
    }

    "get analysis execution data" in {
      /* Get execution data of previously executed analysis */
      val response = sendGetRequest("/analysis/%s/executions/%s/data"
        .format(id, executionId))
      val data = extractArray(response, "data")
      data.length must be (10)
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

  private def checkColumns(analysis: JValue, columName: String,
    value: Boolean = true): JValue = {
    /* Set columns to checked to ensure there are at least some selected
     * columns for the query builder */
    analysis.transform {
      case column: JObject => {
        if ((column \ "aliasName").extractOrElse[String]("none") != "none" &&
          (column \ "columnName").extract[String] == "TRANSFER_END_TS")
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
