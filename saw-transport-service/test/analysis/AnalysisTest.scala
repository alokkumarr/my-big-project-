import java.net.InetAddress

import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatest._

/* Test analysis service operations */
class AnalysisTest extends PlaySpec with OneAppPerSuite {
  /* Add MapR classpath to test runner */
  MaprHelper.addClasspath

  "Analysis service" should {
    requireMapr
    val id = System.currentTimeMillis - 1490100000000L
    "create analysis" in {
      /* Write analysis */
      val body = actionAnalysisMessage("create", analysisJson(id))
      val response = sendRequest(body)
      val analysis = (response \ "contents" \ "analysis")(0)
      val JInt(analysisId) = analysis \ "analysisId"
      analysisId must be (id)
    }

    "update analysis" in {
      /* Update previously created analysis */
      val body = actionKeyAnalysisMessage("update", id,
        analysisJson(id, "customer-2"))
      val response = sendRequest(body)
      val analysis = (response \ "contents" \ "analysis")(0)
      val JInt(analysisId) = analysis \ "analysisId"
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

    "delete analysis" in {
      /* Delete previously created analysis */
      val body = actionKeyMessage("delete", id)
      val response = sendRequest(body)
      val JString(action) = response \ "contents" \ "action"
      action must be ("delete")
    }
}

  def actionAnalysisMessage(action: String, analysis: JValue) = {
    message(("action" -> action) ~ ("analysis" -> List(analysis)))
  }

  def actionKeyAnalysisMessage(
    action: String, key: Long, analysis: JValue) = {
    message(("action" -> action) ~ ("keys" -> List(key)) ~
      ("analysis" -> List(analysis)))
  }
  def analysisJson(id: Long, customerCode: String = "customer-1") = {
    ("analysisId" -> id) ~
    ("module" -> "analyze") ~
    ("customer_code" -> customerCode) ~
    ("name" -> s"test-$id")
  }

  def actionKeyMessage(action: String, id: Long) = {
    message(("action" -> action) ~ ("keys" -> JArray(List(id))))
  }

  def message(contents: JValue) = {
    ("ticket" -> JObject()) ~
    ("_links" -> JObject()) ~
    ("contents" -> contents)
  }

  def sendRequest(body: JValue) = {
    val headers = FakeHeaders(Seq("Content-type" -> "application/json"))
    val Some(response) = route(
      FakeApplication(), FakeRequest(
        POST, "/analysis", headers, pretty(render(body))))
    status(response) mustBe OK
    contentType(response) mustBe Some("application/json")
    parse(contentAsString(response))
  }

  def requireMapr {
    val hostname = InetAddress.getLocalHost.toString()
    /* Skip these tests that require a MapR-DB connection if not running
     * in an environment with MapR-DB available */
    if (!hostname.contains("cloud.synchronoss.net")) {
      pending
    }
  }
}
