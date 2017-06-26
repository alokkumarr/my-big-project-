package synchronoss.handlers.countly

import _root_.util.JsProcessor
import play.api.libs.json.{JsObject, _}
import synchronoss.data.countly.model.Crash


/**
  * Created by srya0001 on 5/1/2016.
  */
class CountlyCrashHandler extends CountlyHandler {

    var crash_data: JsValue = null

    var tr_crash = (__ \ 'crash)

    var crash: Crash = null

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

    def createCrashReport(uuid: String): Crash = {

        crash = mHeader.foldLeft[Crash](new Crash(uuid))(mapBaseEventData(_, _))
        if (crash_data != null) {
            crash = crash_data.as[JsObject].fieldSet.foldLeft[Crash](crash)(mapCrashData(_, _))
        }
        m_log debug "Crash object ID:  " + crash.incident_id
        crash
    }

    def sendMessages(uuid : String):Unit =
    {
       sendMessage(crash.getBytes, uuid)
       m_log trace "Message has been sent, size: " + crash.getBytes.length
    }



}
