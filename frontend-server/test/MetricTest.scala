import org.scalatest.FlatSpec
import play.test.Helpers
import rtcontrollers.EventTestController
import synchronoss.handlers.countly.CountlyEventHandler

/**
  * Created by srya0001 on 5/5/2016.
  */
class MetricTest extends FlatSpec {

  val helpers = new Helpers

  "Event processing" should "get data correctly" in {

    val cch = new CountlyEventHandler()

    val test_controller = new EventTestController(cch)

    Helpers.invokeWithContext(Helpers.fakeRequest(Helpers.GET, query), test_controller)

    val (validationResult, msg) = cch.processRequest

    assert(validationResult)

    cch.processCountlyEvents

    assert(cch.mHeader != null)

  }

  private val query = new String(
    "http://localhost:9000?app_key=CNTLY-CLIENT&timestamp=1462226813&hour=18&dow=1&sdk_version=15.08.01" +
      "&begin_session=1&metrics=%7B%22_device%22%3A%22Android+SDK+built+for+x86%22%2C%22_os%22%3A%22Android%22%2C%" +
      "22_os_version%22%3A%226.0%22%2C%22_carrier%22%3A%22Android%22%2C%22_resolution%22%3A%221080x1794%22%2C%22_" +
      "locale%22%3A%22en_US%22%2C%22_app_version%22%3A%221.0%22%7D&device_id=a3c4c1b919c61491")

}