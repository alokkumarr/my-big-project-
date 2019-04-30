package synchronoss.handlers.countly

import _root_.util.JsProcessor
import play.api.libs.json.{JsObject, _}


/**
  * Created by srya0001 on 5/1/2016.
  */
class CountlyCrashBridge extends CountlyHandler {

    var crash_data: JsValue = null

    var tr_crash = (__ \ 'crash)

    var crash: JsObject = null

    def addCrashData(jn: JsValue): Unit = {
        crash_data = jn
        if ( crash_data != null )
            m_log trace "Test post data => " + Json.prettyPrint(crash_data)
    }

    override def processRequest(): (Boolean, String) = {

        if (validateKey(raw_data, "device_id", (__ \ 'device_id))) {
            mHeader = JsProcessor deSequencer raw_data
            m_log trace "Crash data => " + Json.prettyPrint(crash_data)
            (true, "n/a")
        }
        else {
            m_log debug "Header is not valid"
            (false, "Mandatory key: device_id is missing")
        }
    }

    def createCrashReport(uuid: String): JsObject = {

        crash = mHeader.foldLeft(JsObject(Nil))((z,elem) => z + (elem._1 -> JsString(elem._2)))
        if (crash_data != null) {
            crash = crash ++ crash_data.as[JsObject]
        }
        val incident_id = extractKey(crash , "incident_id", ( __ \ 'incident_id ))
        if (incident_id == null)
        {
            crash += ("incident_id" -> JsString(uuid) )
        }
        crash
    }

    def sendMessages(crashID: String):Unit =
    {
        val strValue = play.api.libs.json.Json.stringify(crash)
        m_log trace "Print event before sending, size = " + strValue.length + ", Data: " + strValue
        sendMessage(strValue, crashID)
    }



}
