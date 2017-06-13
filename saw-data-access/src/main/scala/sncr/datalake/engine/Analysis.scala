package sncr.datalake.engine

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import sncr.datalake.engine.ExecutionType.ExecutionType
import sncr.metadata.analysis.{AnalysisNode, AnalysisResult}

/**
  * Created by srya0001 on 6/8/2017.
  */
class Analysis(val analysisId : String) {

  val an = AnalysisNode(analysisId)
  /**
    * start execution of an analysis and return an execution object immediately; store the "type" parameter in
    * the execution object, but no need for the Spark SQL executor to do anything with it
    */

  def execute(execType: ExecutionType) : AnalysisExecution =
  {
    val analysisExecution = new AnalysisExecution(an, execType)
    analysisExecution.startExecution
    analysisExecution
  }

  /**
    * Returns the list of executions for that analysis
    */
  def listExecutions : List[AnalysisResult] = {
    val keys = Map("analysisId" -> analysisId)
    val analysisResult = new AnalysisResult(analysisId)
    val rowIds : List[Array[Byte]] = analysisResult.simpleMetadataSearch(keys, "and")
    rowIds.map( rowId => { val rowIdStr = Bytes.toString( rowId ); AnalysisResult(analysisId, rowIdStr)} )
  }

  def delete : Unit = an.deleteAnalysisResults()
}
