package controllers

import java.{util, _}
import java.text.SimpleDateFormat
import java.util._

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import model.PaginateDataSet
import org.json4s.JsonAST.{JArray, JObject, JString, JValue}
import org.json4s.JsonDSL._
import play.libs.Json
import play.mvc.{Http, Result, Results}
import sncr.datalake.{DLConfiguration}
import sncr.metadata.analysis.AnalysisResult
import sncr.metadata.engine.MDObjectStruct

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
      ("executions", executions) ~ ("results", executions) : JValue
    })
  }


  def getExecutionData(analysisId: String, executionId: String, page: Int,pageSize: Int, analysisType: String): Result = {
    handle(process = (json, ticket) =>
    {
      var totalRows : Int =0
      var pagingData: JValue = null
      val analysis = new sncr.datalake.engine.Analysis(analysisId)
      val execution = analysis.getExecution(executionId)


      m_log.trace("analysisType {}", analysisType)
      if (analysisType == "report") {
        if (PaginateDataSet.INSTANCE.getCache(executionId) != null && PaginateDataSet.INSTANCE.getCache(executionId).size()>0)
        {
          if (PaginateDataSet.INSTANCE.getCache(executionId).get(0).size()>0) {
            m_log.trace("when data is available in cache executionId: {}", executionId)
            m_log.trace("when data is available in cache size of pageSize {}", pageSize)
            m_log.trace("when data is available in cache size of page {}", page)
            pagingData = analysisController.processReportResult(PaginateDataSet.INSTANCE.paginate(pageSize, page, executionId))
            totalRows = PaginateDataSet.INSTANCE.sizeOfData()
            m_log.trace("totalRows {}", totalRows)
          }
        else
        {
          val data: util.List[util.Map[String, (String, Object)]] = execution.loadExecution(executionId)
          if (data != null)
          {
            m_log.trace("when data is not available in cache executionId: {}", executionId);
            m_log.trace("when data is not available in cache size of limit {}", pageSize)
            m_log.trace("when data is not available in cache size of start {}", page)
            m_log.trace("when data is not available in cache actual size of data {}", data.size())
            pagingData = analysisController.processReportResult(data)
            PaginateDataSet.INSTANCE.putCache(executionId, data)
            pagingData = analysisController.processReportResult(PaginateDataSet.INSTANCE.paginate(pageSize, page, executionId))
            totalRows = PaginateDataSet.INSTANCE.sizeOfData()
            m_log.trace("totalRows {}", totalRows)
          }
        }
      }
        else {
          val data: util.List[util.Map[String, (String, Object)]] = execution.loadExecution(executionId)
          pagingData = analysisController.processReportResult(data)
          PaginateDataSet.INSTANCE.putCache(executionId, data)
          pagingData = analysisController.processReportResult(PaginateDataSet.INSTANCE.paginate(pageSize, page, executionId))
          totalRows = PaginateDataSet.INSTANCE.sizeOfData()
          m_log.trace("totalRows {}", totalRows)
        }
        ("data", pagingData) ~ ("totalRows",totalRows)
      }
      else {
        val anares = AnalysisResult(analysisId, executionId)
        val desc = anares.getCachedData(MDObjectStruct.key_Definition.toString)
        val d_type = (desc.asInstanceOf[JValue] \ "type").extractOpt[String];
        if (d_type.isDefined) {

          if (d_type.get == "chart" || d_type.get == "pivot") {
            ("data", execution.loadESExecutionData(anares))
          }
          else throw new Exception("Unsupported data format")
        }
        else null
      } // end of chart & pivot
    })
  }
  def execute(analysisId: String): Result = {
    handle((json, ticket) => {
      analysisController.executeAnalysis(analysisId, null, null, null, null)
      JObject()
    })
  }
}
