import java.net.InetAddress

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

class MaprTest extends PlaySpec with OneAppPerSuite {
  /* Add MapR classpath to test runner */
  MaprHelper.addClasspath

  def requireMapr {
    val hostname = InetAddress.getLocalHost.getHostName()
    /* Skip these tests that require a MapR-DB connection if not running
     * in an environment with MapR-DB available */
    if (!hostname.contains("cloud.synchronoss.net")) {
      pending
    }
  }

  def actionAnalysisMessage(action: String, analysis: JValue) = {
    message(("action" -> action) ~ ("analysis" -> List(analysis)))
  }

  def actionKeyAnalysisMessage(
    action: String, key: String, analysis: JValue) = {
    message(("action" -> action) ~ ("keys" -> List(key)) ~
      ("analysis" -> List(analysis)))
  }
  def analysisJson(id: String, customerCode: String = "customer-1") = {
    ("analysisId" -> id) ~
    ("module" -> "analyze") ~
    ("customer_code" -> customerCode) ~
    ("name" -> s"test-$id")
  }

  def actionKeyMessage(action: String, id: String) = {
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
}
