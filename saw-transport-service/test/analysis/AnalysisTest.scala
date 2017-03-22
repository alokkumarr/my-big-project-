import java.net.InetAddress

import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatest._

class AnalysisTest extends PlaySpec with OneAppPerSuite {
  def requireMapr {
    val hostname = InetAddress.getLocalHost.toString()
    /* Skip these tests that require a MapR-DB connection if not running
     * in an environment with MapR-DB available */
    if (!hostname.contains("cloud.synchronoss.net")) {
      pending
    }
  }

  MaprHelper.addClasspath()
  "Analysis service" should {
    requireMapr
    val id = System.currentTimeMillis - 1490100000000L
    "create analysis" in {
      /* Write analysis */
      val body =
        ("ticket" -> JObject()) ~
          ("_links" -> JObject()) ~
          ("contents" ->
            ("action" -> "create") ~
            ("analysis" ->
              List(
                ("analysisId" -> id) ~
                  ("module" -> "analyze") ~
                  ("customer_code" -> "customer-1") ~
                  ("name" -> s"test-$id"))))
      val headers = FakeHeaders(Seq("Content-type" -> "application/json"))
      val Some(create) = route(
        FakeRequest(POST, "/analysis", headers, pretty(render(body))))
      status(create) mustBe OK
      contentType(create) mustBe Some("application/json")
      val responseNode = parse(contentAsString(create))
      val analysis = responseNode \ "contents" \ "analysis"
      val JInt(v) = (analysis(0) \ "analysisId")
      v must be (id)
    }

    "read analysis" in {
      /* Read back same analysis */
      val body =
        ("ticket" -> JObject()) ~
          ("_links" -> JObject()) ~
          ("contents" ->
            ("action" -> "read") ~
            ("keys" -> List(id)))
      val headers = FakeHeaders(Seq("Content-type" -> "application/json"))
      val Some(read) = route(
        FakeRequest(POST, "/analysis", headers, pretty(render(body))))
      status(read) mustBe OK
      contentType(read) mustBe Some("application/json")
      val responseJson = parse(contentAsString(read))
      val JString(v) = (responseJson \ "contents" \ "analysis")(0) \ "name"
      v must be (s"test-$id")
    }
  }
}
