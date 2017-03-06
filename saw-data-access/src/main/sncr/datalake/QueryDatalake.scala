package sncr.datalake

import org.apache.log4j.Logger
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import sncr.metadata.engine.MDObjectStruct

/**
   *Created by srya0001 on 2/10/2017.
  */
class QueryDatalake(val queryDescriptor: String ) {

  private val m_log: Logger = Logger.getLogger(classOf[QueryDatalake].getName)

  var qDescJson: JValue = null
  var query : String = null
  var metadata : String = null
  import MDObjectStruct.formats

  try {
    qDescJson =  parse (queryDescriptor, false, false)
    val query = (qDescJson \ "query").extract[String]
    val metadata = (qDescJson \ "metadata").extract[String]

  }
  catch {
    case x: Exception => m_log trace s"Could not parse query"
  }



}
