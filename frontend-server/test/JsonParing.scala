import java.io.FileInputStream

import synchronoss.data.countly.model.Event
import org.scalatest.FlatSpec
import play.api.libs.json.JsObject
import synchronoss.handlers.countly.CountlyEventHandler

/**
  * Created by srya0001 on 5/1/2016.
  */
class JsonParing extends FlatSpec {

  "JSON parse module" should "parse JSON correctly" in {
    val js = play.api.libs.json.Json.parse(new FileInputStream("C:\\projects\\play\\RTService\\test\\resources\\TestDataRaw1.json"))
    val ceh = new CountlyEventHandler()
    ceh.raw_data = js.as[JsObject]
    val (validationResult, msg) = ceh.processRequest
    assert(validationResult)
    assert(ceh.getEvents!= null)
    //    ceh.mEvents.foreach ( assert(!_.[Event].app_key.equals("hewrweiuwpoi") )
    assert(ceh.getEvents(0).app_key.equals("hewrweiuwpoi"))
    assert(ceh.getEvents(1).app_key.equals("hewrweiuwpoi"))
    assert(ceh.getEvents(2).app_key.equals("hewrweiuwpoi"))
    assert(ceh.getEvents(3).app_key.equals("hewrweiuwpoi"))
    assert(ceh.getEvents(4).app_key.equals("hewrweiuwpoi"))
    assert(ceh.getEvents(5).app_key.equals("hewrweiuwpoi"))
    assert(ceh.getEvents(6).app_key.equals("hewrweiuwpoi"))

    assert(ceh.getEvents(0).eventData.key.equals("TestEvent0"))
    assert(ceh.getEvents(1).eventData.key.equals("TestEvent0"))
    assert(ceh.getEvents(2).eventData.key.equals("TestEvent1"))
    assert(ceh.getEvents(3).eventData.key.equals("TestEvent1"))
    assert(ceh.getEvents(4).eventData.key.equals("TestEvent2"))
    assert(ceh.getEvents(4).eventData.sum == 11.11)
    assert(ceh.getEvents(5).eventData.key.equals("TestEvent3"))
    assert(ceh.getEvents(6).eventData.key.equals("TestEvent4"))

    assert(ceh.getEvents(6).eventData.sum == 13.13)
  }

  "JSON" should "Be valid: mandatory key must be presented" in {

  }


}
