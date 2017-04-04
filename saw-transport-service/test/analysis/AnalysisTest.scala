import org.json4s._

/* Test analysis service operations */
class AnalysisTest extends MaprTest {
  "Analysis service" should {
    requireMapr
    val id = (System.currentTimeMillis - 1490100000000L).toString

    "create analysis" in {
      /* Write analysis */
      val body = actionAnalysisMessage("create", analysisJson(id))
      val response = sendRequest(body)
      val analysis = (response \ "contents" \ "analysis")(0)
      val JString(analysisId) = analysis \ "analysisId"
      analysisId must be (id)
    }

    "update analysis" in {
      /* Update previously created analysis */
      val body = actionKeyAnalysisMessage("update", id,
        analysisJson(id, "customer-2"))
      val response = sendRequest(body)
      val analysis = (response \ "contents" \ "analysis")(0)
      val JString(analysisId) = analysis \ "analysisId"
      analysisId must be (id)
    }

    "read analysis" in {
      /* Read back previously created analysis */
      val body = actionKeyMessage("read", id)
      val response = sendRequest(body)
      val analysis = (response \ "contents" \ "analysis")(0)
      val JString(name) = analysis \ "name"
      name must be (s"test-$id")
      val JString(customerCode) = analysis \ "customer_code"
      customerCode must be ("customer-2")
    }

    "execute analysis" in {
      /* Execute previously created analysis */
      val body = actionKeyMessage("read", id)
      val response = sendRequest(body)
      /* TODO */
    }

    "delete analysis" in {
      /* Delete previously created analysis */
      val body = actionKeyMessage("delete", id)
      val response = sendRequest(body)
      val JString(action) = response \ "contents" \ "action"
      action must be ("delete")
    }
  }
}
