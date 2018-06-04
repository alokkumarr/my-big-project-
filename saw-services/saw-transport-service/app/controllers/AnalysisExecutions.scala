package controllers

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import model.PaginateDataSet
import org.json4s.JsonAST.{JArray, JObject, JString, JValue}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.parse
import play.mvc.{Http, Result, Results}
import sncr.datalake.DLSession
import sncr.metadata.analysis.AnalysisResult
import sncr.metadata.engine.MDObjectStruct

import scala.collection.JavaConverters._

class AnalysisExecutions extends BaseController {
  val analysisController = new Analysis

  def list(analysisId: String): Result = {
    handle((json, ticket) => {
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val executions = analysis.listExecutions.map(result => {
        val content = result.getCachedData("content") match {
          case obj: JObject => obj
          case obj: JValue => unexpectedElement("object", obj)
        }
        val id = Bytes.toString(result.getRowKey)
        ("id", id) ~
          ("finished", (content \ "execution_finish_ts").extractOpt[Long]) ~
          ("status", (content \ "exec-msg").extractOpt[String])
      })
      /* Note: Keep "results" property for API backwards compatibility */
      ("executions", executions) ~ ("results", executions): JValue
    })
  }

  // input type is string because of string stream
  def isJObject(abc: Any): Boolean = {
    return parse(abc + "").getClass.getName().equals("org.json4s.JObject")
  }

  def getExecutionData(analysisId: String, executionId: String, page: Int, pageSize: Int, analysisType: String): Result = {
    handle(process = (json, ticket) => {
      // changed from Int to Long because stream returns long.
      var totalRows: Long = 0
      var pagingData: JValue = null
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val execution = analysis.getExecution(executionId)

      m_log.trace("analysisType {}", analysisType)
      if (analysisType == "report") {

        // since we are using streams, we don't have to use cache as it's exactly the same i.e. both are streams
        val dataStream: java.util.stream.Stream[String] = execution.loadExecution(executionId)
        // stream can not be reused hence calling it again. Won't be any memory impact
          totalRows = execution.getRowCount(executionId)

        /* To maintain the backward compatibility check the row count with
           execution result */
        if(totalRows==0) {
          totalRows = execution.loadExecution(executionId).count()
           if(totalRows>0) {
             log.info("recordCount"+ totalRows)
             // if count not available in node and fetched from execution result, add count for next time reuse.
             val resultNode = AnalysisResult(null, executionId)
             resultNode.getObject("dataLocation")
               DLSession.createRecordCount(String.valueOf(resultNode.getObject("dataLocation")),totalRows);
           }
        }
        // result holder
        val data = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
        // process only the required rows and not entire data set
        val skipVal = if ((page - 1) * pageSize > 0) {
          (page - 1) * pageSize
        } else {
          0
        }
        dataStream.skip(skipVal)
          .limit(pageSize)
          .iterator().asScala
          .foreach(line => {
            val resultsRow = new java.util.HashMap[String, (String, Object)]
            parse(line) match {
              case obj: JObject => {
                /* Convert the parsed JSON to the data type expected by the
                 * loadExecution method signature */
                val rowMap = obj.extract[Map[String, Any]]
                rowMap.keys.foreach(key => {
                  rowMap.get(key).foreach(value => resultsRow.put(key, ("unknown", value.asInstanceOf[AnyRef])))
                })
              }
              case obj => throw new RuntimeException("Unknown result row type from JSON: " + obj.getClass.getName)
            }
            data.add(resultsRow)
          })
        pagingData = analysisController.processReportResult(data)
        ("data", pagingData) ~ ("totalRows", totalRows)
      }
      else {
        val anares = AnalysisResult(analysisId, executionId)
        val results = new java.util.ArrayList[java.util.Map[String, (String, Object)]]
        val desc = anares.getCachedData(MDObjectStruct.key_Definition.toString)
        val d_type = (desc.asInstanceOf[JValue] \ "type").extractOpt[String];
        if (d_type.isDefined) {

          if (d_type.get == "chart" || d_type.get == "pivot") {
            ("data", execution.loadESExecutionData(anares))
          }
          else if (d_type.get == "esReport") {
            val data = execution.loadESExecutionData(anares)
              .extract[scala.List[Map[String, Any]]]
              .foreach(row => {
                val resultsRow = new java.util.HashMap[String, (String, Object)]
                row.keys.foreach(key => {
                  row.get(key).foreach(value => resultsRow.put(key, ("unknown", value.asInstanceOf[AnyRef])))
                })
                results.add(resultsRow)
              }
              )
            pagingData = analysisController.processReportResult(results)
            PaginateDataSet.INSTANCE.putCache(executionId, results)
            pagingData = analysisController.processReportResult(PaginateDataSet.INSTANCE.paginate(pageSize, page, executionId))
            totalRows = PaginateDataSet.INSTANCE.sizeOfData()
            m_log.trace("totalRows {}", totalRows)
            ("data", pagingData) ~ ("totalRows", totalRows)
          }
          else throw new Exception("Unsupported data format")
        }
        else null
      } // end of chart & pivot
    })
  }

  def execute(analysisId: String): Result = {
    handle((json, ticket) => {
      analysisController.executeAnalysis(analysisId, "scheduled", null, null, null)
    })
  }
}
