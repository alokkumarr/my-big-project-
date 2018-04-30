package util

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json._
import play.api.libs.json._


/**
  * Created by srya0001 on 4/27/2016.
  */
object JsProcessor {

  private val m_log: Logger = LoggerFactory.getLogger("JsProcessor")
  //predefined transformers


  def flatten(js: JsValue): JsValue = flattenProc(js).foldLeft(JsObject(Nil))(_ ++ _.as[JsObject])


  def flattenProc(js: JsValue, prefix: String = ""): Seq[JsValue] = {
    js.as[JsObject].fieldSet.toSeq.flatMap { case (key, values) =>
      values match {
        case JsBoolean(x) => Seq(obj(concat(prefix, key) -> x))
        case JsNumber(x) => Seq(obj(concat(prefix, key) -> x))
        case JsString(x) => Seq(obj(concat(prefix, key) -> x))
        case JsArray(seq) => seq.zipWithIndex.flatMap { case (x, i) => flattenProc(x, concat(prefix, key + s"[$i]")) }
        case x: JsObject => flattenProc(x, concat(prefix, key))
        case _ => Seq(obj(concat(prefix, key) -> JsNull))
      }
    }
  }

  def concat(prefix: String, key: String): String = if (prefix.nonEmpty) s"$prefix.$key" else key

  def splitter(req: JsObject, sname: String, path: JsPath): (JsValue, JsObject) = {
    m_log debug "Get requested branch for: " + sname
    val tr_pick: Reads[JsValue] = path.json.pick
    val pickResult = req.transform(tr_pick)
    val v1: JsValue = pickResult match {
      case JsError(_) => {
        m_log debug sname + " section not found"; null
      }
      case _ => pickResult.get
    }

    val tr_prune: Reads[JsObject] = path.json.prune
    val pruneResult = req.transform(tr_prune)
    val v2: JsObject = pruneResult match {
      case JsError(_) => {
        m_log debug sname + " return document untouched: "; req
      }
      case _ => pruneResult.get
    }
    (v1, v2)
  }

  def deSequencer(j: JsValue): Seq[(String, String)] = deSequencer(j.as[JsObject])

  def deSequencer(j: JsObject): Seq[(String, String)] = {
    val dseq: Seq[(String, String)] =
      j.as[JsObject].fieldSet.toSeq.flatMap { case (key, values) =>
        values match {
          case JsBoolean(x) => Seq(key -> x.toString)
          case JsNumber(x) => Seq(key -> x.toString())
          case JsString(x) => Seq(key -> x)
          case JsArray(seq) => Seq(key -> seq.mkString)
          case x: JsObject => m_log warn "Unsupported data structure"; Seq(key -> "")
          case _ => Seq(key -> "")
        }
      }
    dseq
  }

  def reparseValue(j: JsValue ): JsValue = {
    if (j != null) {
      val events_as_string: String = j.as[JsArray].head.get.as[JsString].value
      if (events_as_string != null && !events_as_string.isEmpty) {
        Json.parse(events_as_string)
//        m_log.debug("Print re-parsing result => " + Json.prettyPrint(jj))
      }
      else null
    }
    else null
  }
}

