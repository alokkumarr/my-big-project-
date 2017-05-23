import java.net.InetAddress

import akka.util.Timeout
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatestplus.play._
import org.slf4j.{Logger, LoggerFactory}
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.duration._

class MaprTest extends PlaySpec with OneAppPerSuite with DefaultAwaitTimeout {
  /* Add MapR classpath to test runner */
  MaprHelper.addClasspath

  /* Increase the default timeout to allow Spark SQL execution requests
   * to complete.  The default of 20 seconds is too little.  */
  override implicit def defaultAwaitTimeout: Timeout = 300.seconds

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
    actionKeysMessage(action, key, ("analyze" -> List(analysis)) : JObject)
  }

  def actionKeyMessage(action: String, id: String,
    keyAdditionalJson: JObject = JObject()) = {
    val keys: JObject = ("id", id)
    actionKeysMessage(action, keys ~ keyAdditionalJson)
  }

  def actionKeysMessage(action: String, keys: JObject,
    more: JObject = JObject()) = {
    message(("action" -> action) ~ ("keys" -> JArray(List(keys))) ~ more)
  }

  def message(contents: JValue): JObject = {
    ("contents" -> contents)
  }

  def sendRequest(body: JValue) = {
    log.trace("Request: " + shortMessage(pretty(render(body))))
    val headers = FakeHeaders(Seq("Content-type" -> "application/json"))
    val Some(response) = route(
      FakeApplication(), FakeRequest(
        POST, "/analysis", headers, pretty(render(body))))
    val responseString = contentAsString(response)
    val responseLog = pretty(render(parse(responseString)))
    log.trace("Response: " + shortMessage(responseLog))
    withClue("Response: " + responseString) {
      status(response) mustBe OK
    }
    contentType(response) mustBe Some("application/json")
    parse(contentAsString(response))
  }

  private def shortMessage(message: String) = {
    message.substring(0, Math.min(message.length(), 500))
  }
}
