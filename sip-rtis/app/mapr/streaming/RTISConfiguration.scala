package mapr.streaming

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * @author alok.kumarr
  * @since 3.4.0
  */
object RTISConfiguration {

  private val logger: Logger = LoggerFactory.getLogger(classOf[RTISConfiguration].getName)

  case class Streams_1(topic: String, queue: String)

  case class Streams_2(topic: String, queue: String)

  case class Mapping(app_key: String, streams_1: List[Streams_1], streams_2: List[Streams_2], `class`: String, `bootstrap.servers`: String, `batch.size`: Long, `key.serializer`: String, `value.serializer`: String, `block.on.buffer.full`: Boolean, `timeout.ms`: Long)

  implicit val stream_1: Reads[Streams_1] = (
    (JsPath \ "topic").read[String] and
      (JsPath \ "queue").read[String]
    ) (Streams_1.apply _)

  implicit val stream_2: Reads[Streams_2] = (
    (JsPath \ "topic").read[String] and
      (JsPath \ "queue").read[String]
    ) (Streams_2.apply _)

  implicit val mapping: Reads[Mapping] = (
    (JsPath \ "app_key").read[String] and
      (JsPath \ "streams_1").read[List[Streams_1]] and
      (JsPath \ "streams_2").read[List[Streams_2]] and
      (JsPath \ "class").read[String] and
      (JsPath \ "bootstrap.servers").read[String] and
      (JsPath \ "batch.size").read[Long] and
      (JsPath \ "key.serializer").read[String] and
      (JsPath \ "value.serializer").read[String] and
      (JsPath \ "block.on.buffer.full").read[Boolean] and
      (JsPath \ "batch.size").read[Long]
    ) (Mapping.apply _)


  def getConfig(result: String, streamLocation: String): List[mutable.HashMap[String, Any]] = {
    val tempList = new ListBuffer[mutable.HashMap[String, Any]]()
    try {
      val jsonList: List[JsObject] = Json.parse(result).as[List[JsObject]]
      // break if api has empty response
      if (jsonList.isEmpty || result.size == 0){
        tempList
      }

      for (config <- jsonList) {
        val mapping: JsResult[Mapping] = config.validate[Mapping]

        mapping.map(conf => {
          logger debug conf.app_key

          val map: mutable.HashMap[String, Any] = new mutable.HashMap[String, Any]()
          map.put("app_key", conf.app_key)

          val pStreamList = new ListBuffer[mutable.HashMap[String, String]]()
          conf.streams_1.foreach({ p => {
            val pStream = new mutable.HashMap[String, String]
            pStream.put("topic", p.topic)
            pStream.put("queue", streamLocation.concat("/").concat(p.queue))
            pStreamList += pStream
          }
          })
          map.put("streams_1", pStreamList.toList)

          val sStreamList = new ListBuffer[mutable.HashMap[String, String]]()
          conf.streams_2.foreach({ p => {
            val stream = new mutable.HashMap[String, String]
            stream.put("topic", p.topic)
            stream.put("queue", streamLocation.concat("/").concat(p.queue))
            sStreamList += stream
          }
          })
          map.put("streams_2", sStreamList.toList)
          map.put("class", conf.`class`)
          map.put("bootstrap.servers", conf.`bootstrap.servers`)
          map.put("batch.size", conf.`batch.size`.toString)
          map.put("key.serializer", conf.`key.serializer`)
          map.put("value.serializer", conf.`value.serializer`)
          map.put("block.on.buffer.full", conf.`block.on.buffer.full`)
          map.put("timeout.ms", conf.`timeout.ms`.toString)

          tempList += map
        })
      }
    } catch {
      case ex: Exception => logger error ex.getMessage
    }
    tempList.toList
  }
}

abstract class RTISConfiguration
