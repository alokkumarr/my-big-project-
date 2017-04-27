import java.net.InetAddress

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatestplus.play._
import org.slf4j.{Logger, LoggerFactory}
import play.api.test._
import play.api.test.Helpers._

class MaprTest extends PlaySpec with OneAppPerSuite {
  /* Add MapR classpath to test runner */
  MaprHelper.addClasspath

  val log: Logger = LoggerFactory.getLogger("test")

  def requireMapr {
    val hostname = InetAddress.getLocalHost.getHostName()
    /* Skip these tests that require a MapR-DB connection if not running
     * in an environment with MapR-DB available */
    if (!hostname.contains("vacum-np.sncrcorp.net")) {
      pending
    }
  }

  def actionAnalysisMessage(action: String, analysis: JValue) = {
    message(("action" -> action) ~ ("analyze" -> List(analysis)))
  }

  def actionKeyAnalysisMessage(
    action: String, id: String, analysis: JValue) = {
    val key: JObject = ("id", id)
    message(("action" -> action) ~ ("keys" -> List(key)) ~
      ("analyze" -> List(analysis)))
  }

  def analysisJson(id: String = null, customerCode: String = "customer-1") = {
    val idJson: JValue = if (id != null) ("id" -> id) else JNothing
    (("module" -> "analyze") ~
      ("customerCode" -> customerCode) ~
      ("name" -> s"test-$id")).merge(idJson)
  }

  def actionKeyMessage(action: String, id: String,
    keyAdditionalJson: JObject = JObject()) = {
    val idJson: JObject = ("id", id)
    val keyJson: JObject = idJson.merge(keyAdditionalJson)
    message(("action" -> action) ~ ("keys" -> JArray(List(keyJson))))
  }

  def message(contents: JValue): JObject = {
    ("contents" -> contents)
  }

  def sendRequest(body: JValue) = {
    log.trace("Request: " + pretty(render(body)))
    val headers = FakeHeaders(Seq("Content-type" -> "application/json"))
    val Some(response) = route(
      FakeApplication(), FakeRequest(
        POST, "/analysis", headers, pretty(render(body))))
    val responseLog = pretty(render(parse(contentAsString(response))))
    val responseLogShort = responseLog.substring(
      0, Math.min(responseLog.length(), 500))
    log.trace("Response: " + responseLogShort)
    status(response) mustBe OK
    contentType(response) mustBe Some("application/json")
    parse(contentAsString(response))
  }
}
