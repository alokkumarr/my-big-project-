import java.util.UUID

import org.scalatest.FlatSpec
import play.api.libs.json.Json
import play.test.Helpers
import rtcontrollers.CrashTestController
import synchronoss.handlers.countly.CountlyCrashHandler

/**
  * Created by srya0001 on 5/4/2016.
  */
class CrashReportTest extends FlatSpec {



  val helpers = new Helpers

  "Crash Report processing" should "get data correctly" in {

    val cch = new CountlyCrashHandler()
    val test_controller = new CrashTestController(cch)

    Helpers.invokeWithContext(
      Helpers.fakeRequest(Helpers.GET, query ),test_controller)
    val ( validationResult, msg) = cch.processRequest
    assert (validationResult)
    assert ( cch.mHeader != null)
    val uuid = UUID.randomUUID().toString
    cch.createCrashReport(uuid)
    assert(cch.crash != null)

//    println(cch.crash.toString)
    println(Json.prettyPrint(cch.crash_data))

  }


  private val query: String = new String(
    "http://localhost:9000?app_key=dbdf6b2c119fb0f54208c810283b377343c4ad34&device_id=" +
    "24407F43-E8E8-44FE-8E13-77B8DF0C2072&timestamp=1461503890&hour=9&dow=0&sdk_version=" +
    "16.02&crash=%7B%22_opengl%22%3A3%2C%22_error%22%3A%220%20%20CoreFoundation%20%200x000" +
    "00001815dee50%20%3Credacted%3E%20%2B%20148%5Cn1%20%20libobjc.A.dylib%20%200x0000000" +
    "180c43f80%20objc_exception_throw%20%2B%2056%5Cn2%20%20CoreFoundation%20%200x00000001815" +
    "e5ccc%20%3Credacted%3E%20%2B%200%5Cn3%20%20CoreFoundation%20%200x00000001815e2c74%20%3" +
    "Credacted%3E%20%2B%20872%5Cn4%20%20CoreFoundation%20%200x00000001814e0d1c%20_CF_forward" +
    "ing_prep_0%20%2B%2092%5Cn5%20%20Orbit%20Mail%20%200x00000001000b4960%20-%5BCountlyCrash" +
    "Reporter%20crashTest%5D%20%2B%2052%5Cn6%20%20Orbit%20Mail%20%200x0000000100043e4c%20__" +
    "33%2B%5BAnalyicsManager%20startAnalytics%5D_block_invoke%20%2B%2088%5Cn7%20%20libdispatch.dy" +
    "lib%20%200x00000001810294bc%20%3Credacted%3E%20%2B%2024%5Cn8%20%20libdispatch.dylib%20%200x0" +
    "00000018102947c%20%3Credacted%3E%20%2B%2016%5Cn9%20%20libdispatch.dylib%20%200x0000000181034d0" +
    "8%20%3Credacted%3E%20%2B%2092%5Cn10%20%20libdispatch.dylib%20%200x000000018102947c%20%3Credact" +
    "ed%3E%20%2B%2016%5Cn11%20%20libdispatch.dylib%20%200x0000000181040090%20%3Credacted%3E%20%2B%20" +
    "2556%5Cn12%20%20libdispatch.dylib%20%200x000000018102b970%20%3Credacted%3E%20%2B%20808%5Cn13" +
    "%20%20libdispatch.dylib%20%200x000000018102e63c%20_dispatch_main_queue_callback_4CF%20%2B%20492%" +
    "5Cn14%20%20CoreFoundation%20%200x0000000181594dd8%20%3Credacted%3E%20%2B%2012%5Cn15%20%20Co" +
    "reFoundation%20%200x0000000181592c40%20%3Credacted%3E%20%2B%201628%5Cn16%20%20CoreFoundatio" +
    "n%20%200x00000001814bcd10%20CFRunLoopRunSpecific%20%2B%20384%5Cn17%20%20GraphicsServices%20" +
    "%200x0000000182da4088%20GSEventRunModal%20%2B%20180%5Cn18%20%20UIKit%20%200x0000000186791f7" +
    "0%20UIApplicationMain%20%2B%20204%5Cn19%20%20Orbit%20Mail%20%200x00000001000181dc%20main%20%2" +
    "B%20136%5Cn20%20%20libdyld.dylib%20%200x000000018105a8b8%20%3Credacted%3E%20%2B%204%5Cn%22%2C%22" +
    "_device%22%3A%22iPhone8%2C1%22%2C%22_orientation%22%3A%22FaceUp%22%2C%22_online%22%3A1%2C%22_ram" +
    "_total%22%3A2005%2C%22_app_version%22%3A%22INT%22%2C%22_root%22%3Afalse%2C%22_ram_current%22%3A1" +
    "974%2C%22_background%22%3Afalse%2C%22_nonfatal%22%3Afalse%2C%22_os_version%22%3A%229.3.1%22%2C%2" +
    "2_disk_total%22%3A116741%2C%22_os%22%3A%22iOS%22%2C%22_name%22%3A%22-%5BCountlyCrashReporter%20th" +
    "isIsTheUnrecognizedSelectorCausingTheCrash%5D%3A%20unrecognized%20selector%20sent%20to%20instance" +
    "%200x157800fa0%22%2C%22_bat%22%3A90%2C%22_resolution%22%3A%22640x1136%22%2C%22_disk_current%22%" +
    "3A29211%2C%22_run%22%3A19%7D"
  )



}
