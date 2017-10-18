package sncr.datalake.engine

import java.util.concurrent.Callable

import com.mapr.org.apache.hadoop.hbase.util.Bytes
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.engine.ExecutionType.ExecutionType
import sncr.metadata.analysis.AnalysisNode
import sncr.metadata.engine.ProcessingResult

/**
  * Created by srya0001 on 6/19/2017.
  */
class AsynchAnalysisExecWithIterator(an: AnalysisNode, execType : ExecutionType)
  extends AnalysisExecution(an, execType)
  with Callable[java.util.Iterator[java.util.HashMap[String, (String, Object)]]] {

  override protected val m_log: Logger = LoggerFactory.getLogger(classOf[AsynchAnalysisExecWithIterator].getName)

  override def call(): java.util.Iterator[java.util.HashMap[String, (String, Object)]] =
  {
    try {
      startExecution()
      if (status == ExecutionStatus.COMPLETED){
         return analysisNodeExecution.getDataIterator
      }
      else
        null
    }
    catch {
      case t: Throwable => {
        executionMessage = s"Could not start execution: ${Bytes.toString(an.getRowKey)}"
        executionCode = ProcessingResult.Error.id
        status = ExecutionStatus.FAILED
        m_log error(s"Could not start execution: ", t)
        null
      }
    }
  }


}
