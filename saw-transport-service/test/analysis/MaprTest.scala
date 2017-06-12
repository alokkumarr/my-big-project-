import java.net.InetAddress
import scala.concurrent.Future
import scala.concurrent.duration._

import akka.util.Timeout
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatestplus.play._
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.Result
import play.api.test._
import play.api.test.Helpers._

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
    if (!(hostname.contains("vacum-np.sncrcorp.net")
      || hostname.contains("velocity-va.sncrcorp.net"))) {
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
    sendPostRequest(body)
  }

  def sendGetRequest(uri: String) = {
    val request = FakeRequest(GET, uri)
    log.trace("Request: {} {}", request.method, request.uri: Any)
    handleResponse(route(FakeApplication(), request))
  }

  def sendPostRequest(body: JValue) = {
    val headers = FakeHeaders(Seq(
      "Content-type" -> "application/json",
      "Authorization" -> jwtHeader))
    val bodyString = pretty(render(body))
    log.trace("Request body: {}", shortMessage(bodyString))
    val request = FakeRequest(POST, "/analysis", headers, bodyString)
    log.trace("Request: {} {}", request.method, request.uri: Any)
    handleResponse(route(FakeApplication(), request))
  }

  def jwtHeader = {
    "Bearer " +
    "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXdhZG1pbkBzeW5jaHJvbm9zcy" +
    "5jb20iLCJ0aWNrZXQiOnsidGlja2V0SWQiOiIyNl8xNDk3MDQyNzM0MjE0X" +
    "zI0OTU0OTY4NyIsIndpbmRvd0lkIjoiMjZfMTQ5NzA0MjczNDIxNF8xNTYx" +
    "MDk1NjQ5IiwibWFzdGVyTG9naW5JZCI6InNhd2FkbWluQHN5bmNocm9ub3N" +
    "zLmNvbSIsInVzZXJGdWxsTmFtZSI6IlNBVyBBZG1pbiIsImRlZmF1bHRQcm" +
    "9kSUQiOiI0Iiwicm9sZUNvZGUiOiJTTkNSX0FETUlOX0FETUlOIiwicm9sZ" +
    "VR5cGUiOiJBRE1JTiIsImNyZWF0ZWRUaW1lIjoxNDk3MDQyNzM0MjE0LCJk" +
    "YXRhU2VjdXJpdHlLZXkiOiJBVFRfQURNSU5fRFNLIiwiZXJyb3IiOm51bGw" +
    "sImN1c3RJRCI6IjEiLCJjdXN0Q29kZSI6IlNZTkNIUk9OT1NTIiwidXNlck" +
    "lkIjoxLCJwcm9kdWN0cyI6W3sicHJvZHVjdE5hbWUiOiJTQVcgRGVtbyIsI" +
    "nByb2R1Y3REZXNjIjoiU0FXIERlbW8iLCJwcm9kdWN0Q29kZSI6IlNBV0Qw" +
    "MDAwMDEiLCJwcm9kdWN0SUQiOiI0IiwicHJpdmlsZWdlQ29kZSI6MzI3Njg" +
    "sInByb2R1Y3RNb2R1bGVzIjpbeyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLC" +
    "Jwcm9kdWN0TW9kTmFtZSI6IkFOQUxZWkUiLCJwcm9kdWN0TW9kRGVzYyI6I" +
    "kFuYWx5emUgTW9kdWtsZSIsInByb2R1Y3RNb2RDb2RlIjoiQU5MWVMwMDAw" +
    "MSIsInByb2R1Y3RNb2RJRCI6IjEiLCJtb2R1bGVVUkwiOiIvIiwiZGVmYXV" +
    "sdE1vZCI6IjEiLCJwcml2aWxlZ2VDb2RlIjozMjc2OCwicHJvZE1vZEZlYX" +
    "R1cmUiOlt7InByb2RDb2RlIjoiU0FXRDAwMDAwMSIsInByb2RNb2RDb2RlI" +
    "joiQU5MWVMwMDAwMSIsInByb2RNb2RGZWF0dXJlTmFtZSI6IkNBTk5FRCBB" +
    "TkFMWVNJUyIsInByb2RNb2RGZWF0dXJlRGVzYyI6IkNhbm5lZCBBbmFseXN" +
    "pcyIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ29kZSI6NTA0MzIsIn" +
    "Byb2RNb2RGZWF0dXJlSUQiOjIsInByb2RNb2RGZWF0dXJlVHlwZSI6IlBBU" +
    "kVOVF9GMDAwMDAwMDAwMSIsImRlZmF1bHRGZWF0dXJlIjoiMSIsInByb2RN" +
    "b2RGZWF0ckNvZGUiOiJGMDAwMDAwMDAwMSIsInByb2R1Y3RNb2R1bGVTdWJ" +
    "GZWF0dXJlcyI6W3sicHJvZENvZGUiOiJTQVdEMDAwMDAxIiwicHJvZE1vZE" +
    "NvZGUiOiJBTkxZUzAwMDAxIiwicHJvZE1vZEZlYXR1cmVOYW1lIjoiT1BUS" +
    "U1JWkFUSU9OIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiT3B0aW1pemF0aW9u" +
    "IiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjo1MDQzMiwicHJ" +
    "vZE1vZEZlYXR1cmVJRCI6NCwicHJvZE1vZEZlYXR1cmVUeXBlIjoiQ0hJTE" +
    "RfRjAwMDAwMDAwMDEiLCJkZWZhdWx0RmVhdHVyZSI6IjAiLCJwcm9kTW9kR" +
    "mVhdHJDb2RlIjoiRjAwMDAwMDAwMDMiLCJwcm9kdWN0TW9kdWxlU3ViRmVh" +
    "dHVyZXMiOm51bGx9XX0seyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9" +
    "kTW9kQ29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOi" +
    "JNWSBBTkFMWVNJUyIsInByb2RNb2RGZWF0dXJlRGVzYyI6Ik15IEFuYWx5c" +
    "2lzIiwiZGVmYXVsdFVSTCI6Ii8iLCJwcml2aWxlZ2VDb2RlIjo1MDQzMiwi" +
    "cHJvZE1vZEZlYXR1cmVJRCI6MywicHJvZE1vZEZlYXR1cmVUeXBlIjoiUEF" +
    "SRU5UX0YwMDAwMDAwMDAyIiwiZGVmYXVsdEZlYXR1cmUiOiIwIiwicHJvZE" +
    "1vZEZlYXRyQ29kZSI6IkYwMDAwMDAwMDAyIiwicHJvZHVjdE1vZHVsZVN1Y" +
    "kZlYXR1cmVzIjpbeyJwcm9kQ29kZSI6IlNBV0QwMDAwMDEiLCJwcm9kTW9k" +
    "Q29kZSI6IkFOTFlTMDAwMDEiLCJwcm9kTW9kRmVhdHVyZU5hbWUiOiJQVUJ" +
    "MSUMgT1BUSU1JWkFUSU9OIiwicHJvZE1vZEZlYXR1cmVEZXNjIjoiUHVibG" +
    "ljIE9wdGltaXphdGlvbiIsImRlZmF1bHRVUkwiOiIvIiwicHJpdmlsZWdlQ" +
    "29kZSI6NTA0MzIsInByb2RNb2RGZWF0dXJlSUQiOjUsInByb2RNb2RGZWF0" +
    "dXJlVHlwZSI6IkNISUxEX0YwMDAwMDAwMDAyIiwiZGVmYXVsdEZlYXR1cmU" +
    "iOiIwIiwicHJvZE1vZEZlYXRyQ29kZSI6IkYwMDAwMDAwMDA0IiwicHJvZH" +
    "VjdE1vZHVsZVN1YkZlYXR1cmVzIjpudWxsfV19XX1dfV0sInZhbGlkVXB0b" +
    "yI6MTQ5NzA0OTkzNDIxNCwidmFsaWQiOnRydWUsInZhbGlkaXR5UmVhc29u" +
    "IjoiVXNlciBBdXRoZW50aWNhdGVkIFN1Y2Nlc3NmdWxseSIsInZhbGlkTWl" +
    "ucyI6bnVsbH0sImlhdCI6MTQ5NzA0MjczNH0.8fNsTEHwjrn6p2HovnsMpn" +
    "jBTHnVmuTBvjzy8xpR0aw"
  }

  def handleResponse(responseOption: Option[Future[Result]]) = {
    val Some(response) = responseOption
    val responseString = contentAsString(response)
    val responseLog = pretty(render(parse(responseString)))
    log.trace("Response body: " + shortMessage(responseLog))
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
