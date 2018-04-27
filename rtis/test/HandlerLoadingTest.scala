import mapr.streaming.EventHandler
import org.scalatest.FlatSpec
import synchronoss.handlers.GenericEventHandler
import synchronoss.handlers.charter.smartcare.CharterEventHandler
import synchronoss.handlers.countly.{CountlyCrashBridge, CountlyGenericBridge}

/**
  * Created by srya0001 on 5/2/2016.
  */
class HandlerLoadingTest extends FlatSpec{

  "Load configuration" should "create handler classes correctly" in {
    EventHandler.buildEventHandlerList

    val countlyEventHandler = "countly_event"
    val countlyCrashHandler = "countly_crash"

    val countly_event_handler: CountlyGenericBridge =
      EventHandler.getEventHandler(countlyEventHandler)
    assert(countly_event_handler != null)

    val countly_crash_handler: CountlyCrashBridge =
      EventHandler.getEventHandler(countlyCrashHandler)
    assert(countly_crash_handler != null)

  }

  "Load configuration with multiple streams" should "correctly process array of streams" in {
    EventHandler.buildEventHandlerList

    val genericEventHandlerAppKey = "generic"

    val o: GenericEventHandler = EventHandler.getEventHandler(genericEventHandlerAppKey)
    assert ( o.isInstanceOf[GenericEventHandler] )

    val o1: CharterEventHandler = EventHandler.getEventHandler(CharterEventHandler.CHARTER_APP_KEY)
    assert ( o1.isInstanceOf[CharterEventHandler] )

  }


}
