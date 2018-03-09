import controllers.{RTISControl, Stat}
import mapr.streaming.EventHandler
import org.scalatest.FlatSpec

/**
  * Created by srya0001 on 10/21/2016.
  */
class ControlIntfTest extends FlatSpec{

  Stat.getRequestStat("Charter.SmartCare", 1)
  Stat.getRequestStat("countly_crash", 1)
  Stat.getRequestStat("countly_event", 1)
  Stat.getRequestStat("generic", 1)

  Stat.getRejectedRequestStat("Charter.SmartCare", 1)
  Stat.getRejectedRequestStat("countly_crash", 1)
  Stat.getRejectedRequestStat("countly_event", 1)
  Stat.getRejectedRequestStat("generic", 1)

  EventHandler.buildEventHandlerList

  "sr interface" should "return short status report" in {

    val rtisctrl = new RTISControl
    val res =  rtisctrl.sr(Option("{\"verbose\": false}"))
    val printable = res.as("application/json;charset=utf-8")
    println(printable)

  }

  "sr interface" should "return full status report" in {

    val rtisctrl = new RTISControl
    val res =  rtisctrl.sr(Option("{\"verbose\": true}"))
    assert(res.status() == 200)

    val rtisctrl1 = new RTISControl
    val res1 =  rtisctrl1.sr(Option("{\"verbose\":true,\"AppKey\":\"Charter.SmartCare\"}"))
    assert(res1.status() == 200)

  }

  "CMD: getApps" should "return list of application keys as JSON" in
  {
    val rtisctrl = new RTISControl
    val res =  rtisctrl.executeCmd("getApps", Option(null))
    assert(res.status() == 200)
  }

  "CMD: getList" should "return stream list of all applications as JSON" in
  {
    val rtisctrl = new RTISControl
    val res =  rtisctrl.executeCmd("getList", Option(null))
    assert(res.status() == 200)
  }

  "CMD: getList" should "return stream list of Charter.SmartCare as JSON" in
  {
    val rtisctrl = new RTISControl
    val res =  rtisctrl.executeCmd("getList", Option("{\"AppKey\":\"Charter.SmartCare\"}"))
    assert(res.status() == 200)
  }

  "CMD: getActive" should "return active stream list of all applications as JSON" in
  {
    val rtisctrl = new RTISControl
    val res =  rtisctrl.executeCmd("getActive", Option(null))
    assert(res.status() == 200)
  }

  "CMD: getActive" should "return active stream list of Charter.SmartCare as JSON" in
  {
    val rtisctrl = new RTISControl
    val res =  rtisctrl.executeCmd("getActive", Option("{\"AppKey\":\"Charter.SmartCare\"}"))
    assert(res.status() == 200)
  }

  "CMD: setActive" should "set active stream in all applications, return previously active ones" in
  {
    val rtisctrl = new RTISControl
    val res =  rtisctrl.executeCmd("setActive", Option("{\"Stream\":\"streams_2\"}"))
    assert(res.status() == 200)

  }

  "CMD: setActive" should "set active stream of Charter.SmartCare, return previously active ones" in
  {

    val rtisctrl1 = new RTISControl
    val res1 =  rtisctrl1.executeCmd("setActive", Option("{\"AppKey\":\"Charter.SmartCare\",\"Stream\":\"streams-charter-2\"}"))
    assert(res1.status() == 200)

    val rtisctrl2 = new RTISControl
    val res2 =  rtisctrl2.executeCmd("setActive", Option("{\"AppKey\":\"Charter.SmartCare\",\"Stream\":\"streams-charter-1\"}"))
    assert(res2.status() == 200)
  }


}
