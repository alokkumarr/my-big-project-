import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatest.CancelAfterFailure

/* Test analysis service operations */
class AnalysisTest extends MaprTest with CancelAfterFailure {
  "Analysis service" should {
    requireMapr
    var id: String = null

    "create analysis" in {
      /* Create analysis using analysis template preloaded into MapR-DB */
      val semanticId = "ATT::analyze::semantic::10300083164142186"
      val typeJson: JObject = ("analysisType", "report")
      val response = sendRequest(
        actionKeyMessage("create", semanticId, typeJson))
      val JString(analysisId) = analyze(response) \ "id"
      analysisId must not be (semanticId)
      /* Save analysis ID for use by subsequent tests */
      id = analysisId
      val JString(name) = analyze(response) \ "metricName"
      name must be ("ATT/MCT Session view")
      val JString(sId) = analyze(response) \ "semanticId"
      sId must be (semanticId)
      val JString(analysisType) = analyze(response) \ "type"
      analysisType must be ("report")
    }

    "update analysis" in {
      /* Update previously created analysis */
      val body = actionKeyAnalysisMessage("update", id,
        analysisJson(id, "customer-2"))
      val response = sendRequest(body)
      val JString(analysisId) = analyze(response) \ "id"
      analysisId must be (id)
    }

    "read analysis" in {
      /* Read back previously created analysis */
      val body = actionKeyMessage("read", id)
      val response = sendRequest(body)
      val analysis = analyze(response)
      val JString(name) = analysis \ "name"
      name must be (s"test-$id")
      val JString(customerCode) = analysis \ "customerCode"
      customerCode must be ("customer-2")
    }

    "execute analysis" in {
      /* Execute previously created analysis */
      val body = actionKeyMessage("execute", id)
      val analysis = analyze(sendRequest(body))
      /* Note: Currently the result is just the static mock */
      val JInt(value) = (analysis \ "data")(0) \ "foo"
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

  def analyze(response: JValue): JValue = {
    (response \ "contents" \ "analyze")(0)
  }
}
