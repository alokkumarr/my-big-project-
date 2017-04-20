import org.json4s._
import org.scalatest.CancelAfterFailure
import org.json4s.native.JsonMethods._

/* Test analysis service operations */
class AnalysisTest extends MaprTest with CancelAfterFailure {
  "Analysis service" should {
    requireMapr
    var id: String = null

    "create analysis" in {
      /* Create semantic analysis template using the built-in "_static"
       * ID */
      val templateResponse = sendRequest(actionKeyMessage("create", "_static"))
      val JString(semanticId: String) = analyze(templateResponse) \ "id"

      /* Write analysis */
      val response = sendRequest(actionKeyMessage("create", semanticId))
      val JString(analysisId) = analyze(response) \ "id"
      analysisId must not be ("_static")
      id = analysisId

      val JString(name) = analyze(response) \ "name"
      name must be ("static")
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
