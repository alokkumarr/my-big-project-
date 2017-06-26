import org.scalatest.FlatSpec
import play.test.Helpers
import rtcontrollers.EventTestController
import synchronoss.handlers.countly.CountlyEventHandler

/**
  * Created by srya0001 on 5/4/2016.
  */
  class EventTest extends FlatSpec {

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

  private val query = new String (
        "http://localhost:9000?app_key=CNTLY-CLIENT&timestamp=1461164600&hour=11&dow=3&events=%5B%7B%22key%22%3A%22TestEvent0%22%2C%" +
        "22count%22%3A1%2C%22timestamp%22%3A1461164591%2C%22hour%22%3A11%2C%22dow%22%3A3%2C%22sum%22%3A0%7D%2C%" +
        "7B%22key%22%3A%22TestEvent0%22%2C%22count%22%3A1%2C%22timestamp%22%3A1461164591%2C%22hour%22%3A11%2C%22" +
        "dow%22%3A3%2C%22sum%22%3A0%7D%2C%7B%22key%22%3A%22TestEvent1%22%2C%22count%22%3A10%2C%22timestamp%22%3A14" +
        "61164591%2C%22hour%22%3A11%2C%22dow%22%3A3%2C%22sum%22%3A0%7D%2C%7B%22key%22%3A%22TestEvent1%22%2C%22count%" +
        "22%3A10%2C%22timestamp%22%3A1461164591%2C%22hour%22%3A11%2C%22dow%22%3A3%2C%22sum%22%3A0%7D%2C%7B%22key%22%3" +
        "A%22TestEvent2%22%2C%22count%22%3A11%2C%22timestamp%22%3A1461164591%2C%22hour%22%3A11%2C%22dow%22%3A3%2" +
        "C%22sum%22%3A11.11%7D%2C%7B%22key%22%3A%22TestEvent3%22%2C%22count%22%3A12%2C%22timestamp%22%3A1461164591" +
        "%2C%22hour%22%3A11%2C%22dow%22%3A3%2C%22segmentation%22%3A%7B%22Segment2%22%3A%22BC%22%2C%22Segment1%22%3A%" +
        "22A%22%2C%22Segment3%22%3A%22DEF%22%7D%2C%22sum%22%3A0%7D%2C%7B%22key%22%3A%22TestEvent4%22%2C%22count%22%3A" +
        "13%2C%22timestamp%22%3A1461164591%2C%22hour%22%3A11%2C%22dow%22%3A3%2C%22segmentation%22%3A%7B%22Segment2%22" +
        "%3A%22BC%22%2C%22Segment1%22%3A%22A%22%2C%22Segment3%22%3A%22DEF%22%7D%2C%22sum%22%3A13.13%7D%5D&device_id" +
        "=a3c4c1b919c61491"
  )


}


