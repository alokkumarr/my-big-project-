package sncr.metadata.engine

import org.slf4j.LoggerFactory

/**
  * Created by srya0001 on 2/15/2017.
  */
object Loader extends App {

  val m_log = LoggerFactory.getLogger("Loader")

  override def main(args: Array[String]):Unit = {


/*
    cmd_data(0) match {
      case "md_create" => {
        val md_res = ns.createMDNode(cmd_data(1), cnt); result.put("MDOperationResult", md_res)
      }
      case "md_update" => {
        val md_res = ns.updateMDNode(cmd_data(1), cnt); result.put("MDOperationResult", md_res)
      }
      case "md_retrieve" => {
        val resMap = ns.readMDNode(cmd_data(1), true)
          .getOrElse("Node does not exists: " + cmd_data(1)).asInstanceOf[Map[String, Any]]
        result.put("MDOperationResult", resMap.mkString("{", ",", "}"))
      }
      case "md_delete" => {
        val md_res = ns.deleteMDNode(cmd_data(1)); result.put("MDOperationResult", md_res)
      }
      case "md_search" => {
        if (jn == null) {
          result.put("result", "failure")
          result.put("reason", s"Incorrect request")
          return play.mvc.Results.badRequest(result)
        }
        import scala.collection.JavaConversions._
        var c = 0
        val sc: Map[String, String] = (for (e <- jn.fields()) yield (e.getKey -> e.getValue.asText())).toMap
        val rowKeyList = ns.searchMetadata(sc, "and")
        rowKeyList.foreach(n => {
          result.put(s"Node ID-${c}", new String(n)); c += 1
        })
        val nodes = ns.loadMDNodes(rowKeyList, true)
        result.put("MDOperationResult", if (nodes != null && !nodes.isEmpty) nodes.mkString else "No data found/incorrect request")
      }
      case "md_scan" => {
        val rowKeyList = ns.scanMDNodes
        val nodes = ns.loadMDNodes(rowKeyList, true)
        result.put("MDOperationResult", if (nodes != null && !nodes.isEmpty) nodes.mkString else "No data found/incorrect request")
      }
    }
    */
  }
}
