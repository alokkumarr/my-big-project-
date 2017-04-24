package sncr.metadata.engine

import org.json4s.JsonAST.{JArray, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by srya0001 on 4/18/2017.
  */
trait UIResponse extends Response {

    override protected val m_log: Logger = LoggerFactory.getLogger(classOf[UIResponse].getName)


    import MDObjectStruct.formats
    /**
    * Internal function re-structures response:
    * from flat list of Maps, each map is a node to
      * "module_name": { "type" : {} }
    * @param data
    * @return Map - restructured response
    */
    private def remap(data : List[Map[String, Any]]) : Map[String, List[JValue]] = {
      val remappedData = new scala.collection.mutable.HashMap[String, List[JValue]]
      data.foreach(d => {
          m_log trace s"Content to re-map:   ${d.mkString("{", ", ", "}")}"
          if (d.contains("content")) {
          val content: JValue =
          d("content") match {
            case jv: JValue => jv
            case s: String => parse(s, false, false)
            case _ => val m = "Could not build content mapping, unsupported representation"
              m_log error m
              JObject(Nil)
          }
          val module = if (d.contains("module")) d("module").asInstanceOf[String] else (content \ "module").extractOrElse[String]("UNDEF_module")
          val intermRes :List[JValue] = if (remappedData.contains(module)) remappedData(module) :+ content else List(content)
          remappedData(module) = intermRes
          m_log debug s"Remapped object: ${pretty(render(content))}"
        }
        else {
          val m = "Error: No content to re-map"; m_log error m
        }
      })
      remappedData.toMap
    }

  /**
    * The method process search operation output
    * @param data - list of nodes retrieved from MDDB
    * @return - a response wrapped into a single JSON object
    */
    def build_ui(data : List[Map[String, Any]] ) : JValue = {
      val moduleMap: Map[String, List[JValue]] = remap(data)
      JObject(moduleMap.keySet.map( moduleName => {
        val moduleUIComponents : List[JValue] = moduleMap(moduleName)
          JField(moduleName, JArray(moduleUIComponents))
        }).toList
      )
    }

  /**
    * The method is designed to process output of read operation - expecting ONLY ONE node
    * @param data - read node
    * @return - response as a single JObject
    */
  def build_ui(data : Map[String, Any] ) : JValue = {
    val remappedData: Map[String, List[JValue]] = remap(List(data))
    if (remappedData.isEmpty) return JObject(JField("result", JString("Node not found")))
    if (remappedData.size > 1) return JObject(JField("result", JString("More than one node found with different module")))
    val theOnlyNode_ModuleName = remappedData.keysIterator.next()
    val nodeToResponse : List[JValue] = remappedData(theOnlyNode_ModuleName)
    if (nodeToResponse.size > 1)  return JObject(JField("result", JString(s"More than one node found in one module: $theOnlyNode_ModuleName")))
    if (nodeToResponse.isEmpty)  return JObject(JField("result", JString(s"Internal error: empty list for module: $theOnlyNode_ModuleName")))
    JObject(JField(theOnlyNode_ModuleName, nodeToResponse.head))
  }

}


