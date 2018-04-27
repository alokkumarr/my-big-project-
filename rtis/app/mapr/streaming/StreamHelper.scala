package mapr.streaming

import java.io.{File, IOException, PrintWriter}

import exceptions.ErrorCodes
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.{JsArray, JsObject, JsString, JsValue}

import scala.collection.mutable
import scala.io.Source

/**
  * Created by srya0001 on 10/21/2016.
  */
object StreamHelper {

  def getStreams(appKey: String): JsObject =
  {
    if (appKey.equalsIgnoreCase("ALL"))
      EventHandler.eventHandlerStreams.foldLeft(JsObject(Seq("result" -> JsString("ok"))))(
        (o:JsObject, evs:(String,
            (Array[mutable.HashMap[String, String]],
             Array[mutable.HashMap[String, String]])
          )) =>
          o ++ JsObject(  Seq(evs._1 -> JsArray(List[JsObject](
               JsObject(  Seq(StreamHelper.primaryStreamsKey -> JsString(evs._2._1(0).get(streamKey).get))),
               JsObject(  Seq(StreamHelper.secondaryStreamsKey -> JsString(evs._2._2(0).get(streamKey).get)))))
          ))
      )
    else
    if (EventHandler.streamPointer.contains(appKey))
        JsObject(  Seq("result" -> JsString("ok"),
                       appKey -> JsArray(List[JsString](
        JsString(EventHandler.eventHandlerStreams(appKey)._1(0).get(streamKey).get),
        JsString(EventHandler.eventHandlerStreams(appKey)._2(0).get(streamKey).get)))))
    else
      JsObject( Seq("error" -> JsString(s"Application ${appKey} not found")))
  }

  private def decodeStreamName(appKey: String, streamKeyRef: String): String =
  {
      streamKeyRef match
      {
        case StreamHelper.primaryStreamsKey =>
          EventHandler.eventHandlerStreams(appKey)._1(0).get(streamKey).get
        case StreamHelper.secondaryStreamsKey =>
          EventHandler.eventHandlerStreams(appKey)._2(0).get(streamKey).get
        case _ => "none"
      }

  }

  def getActiveStreams(appKey: String): JsObject =
  {
    if (appKey.equalsIgnoreCase("ALL"))
      EventHandler.streamPointer.foldLeft(JsObject(Seq("result" -> JsString("ok"))))( (o:JsObject, spe:(String, String)) =>
       o + (spe._1 -> JsString(decodeStreamName(spe._1, spe._2))))
    else
      if (EventHandler.streamPointer.contains(appKey))
        JsObject( Seq("result" -> JsString("ok"), appKey -> JsString(decodeStreamName(appKey, EventHandler.streamPointer(appKey) ))) )
      else
        JsObject( Seq("error" -> JsString(s"Application ${appKey} not found")))
  }


  private val m_log: Logger = LoggerFactory.getLogger(classOf[EventHandler].getName)

  protected[streaming] val primaryStreamsKey = "streams_1"
  protected[streaming] val secondaryStreamsKey = "streams_2"
  protected[streaming] val streamKey = "queue"
  protected[streaming] val topicKey = "topic"
  protected[streaming] val keyConcat = "::"

  def loadActiveStreams: Unit =
  {
    try {
      val src = Source.fromFile(new File(EventHandler.getStreamFile))
      val jsonSrcStr = src.mkString
      src.close()
      val json = play.api.libs.json.Json.parse(jsonSrcStr)

      EventHandler.streamPointer = json.as[JsObject].fieldSet.
        foldLeft(new mutable.HashMap[String, String])((t:mutable.HashMap[String, String], pair:(String, JsValue)) =>
          t += (pair._1 ->  pair._2.as[JsString].value))
    }
    catch{
      case e:IOException => {
         m_log.warn(String.format(ErrorCodes.getDescription(ErrorCodes.NoCheckPoint), EventHandler.getStreamFile))
        EventHandler.streamPointer = EventHandler.getAppKeys.
          foldLeft(new mutable.HashMap[String, String])((t:mutable.HashMap[String, String],  s:String) => t += (s ->primaryStreamsKey))
      }
    }
    logStreamMap
  }

  def dumpActiveStreams : Unit = {
    val dmpFile = new PrintWriter(new File(EventHandler.getStreamFile))
    dmpFile.write(play.api.libs.json.Json.prettyPrint (EventHandler.streamPointer.foldLeft(JsObject(Nil))( (o:JsObject, spe:(String, String )) =>
          o + (spe._1 -> JsString(spe._2)))))
    dmpFile.close()
  }
  def printStreamMap : Unit = { println ("----------------"); EventHandler.streamPointer.foreach( x => println ( s"appkey = ${x._1}, Stream = ${x._2}" )); println ("----------------") }
  def logStreamMap : Unit = { m_log debug ("----------------"); EventHandler.streamPointer.foreach( x => m_log debug ( s"appkey = ${x._1}, Stream = ${x._2}" )); m_log debug ("----------------") }


  def swapStream(appKey: String, a_streamKey : String) : JsObject = synchronized
  {
    val result :JsObject = if (appKey.equals("ALL")) {
      val oldStream = EventHandler.streamPointer.clone()
      if (a_streamKey != StreamHelper.primaryStreamsKey &&
          a_streamKey != StreamHelper.secondaryStreamsKey)
      {
        val resp = "Could not find target stream: " + a_streamKey
        m_log.error(resp)
        return JsObject (Seq( "error" + appKey -> JsString(resp)))
      }
      val keys = EventHandler.streamPointer.keySet
      keys.foreach( k => EventHandler.streamPointer(k) = a_streamKey)
      oldStream.foldLeft(JsObject(Seq("result" -> JsString("ok")) ))( (o:JsObject, se:(String, String)) => o + (se._1 -> JsString(decodeStreamName(se._1, se._2))))
    }
    else {
      m_log debug ("Switch to " + a_streamKey)
      if (EventHandler.streamPointer.contains(appKey)) {
        val oldValue : String = decodeStreamName(appKey, EventHandler.streamPointer(appKey))
        val stream_1 = EventHandler.eventHandlerStreams(appKey)._1(0).get (streamKey).get
        val stream_2 = EventHandler.eventHandlerStreams(appKey)._2(0).get (streamKey).get

        m_log debug (s"Stream 1: ${stream_1}, stream 2: ${stream_2}")
        EventHandler.streamPointer(appKey) = if (a_streamKey == stream_1) primaryStreamsKey else
                                             if (a_streamKey == stream_2) secondaryStreamsKey
                                             else primaryStreamsKey

        JsObject (Seq("result" -> JsString("ok"),  appKey -> JsString(oldValue)))
      }
      else {
        val resp = "Could not find Application key: " + appKey
        m_log.error(resp)
        return JsObject (Seq( "error" + appKey -> JsString(resp)))
      }
    }
    dumpActiveStreams
    logStreamMap
    result
  }


}
