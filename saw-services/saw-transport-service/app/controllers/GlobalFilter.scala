package controllers

import java.util

import com.synchronoss.querybuilder.{SAWElasticSearchQueryBuilder, SAWElasticSearchQueryExecutor}
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject
import model.ClientException
import org.json4s.JNothing
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods.compact
import org.json4s.native.JsonMethods.render
import org.json4s.native.JsonMethods.parse
import play.mvc.Result

class GlobalFilter extends BaseController {

  def process: Result = {
    handle(doProcess)
  }

  private def doProcess(json: JValue, ticket: Option[Ticket]): JValue = {
    if (ticket== None) throw new ClientException(
        "Valid JWT not found in Authorization header")
    val jsonString: String = compact(render(json));
    m_log info("fetch global filter result")
    val executionList :util.List[GlobalFilterExecutionObject] =
      new SAWElasticSearchQueryBuilder().getsearchSourceBuilder(jsonString)
    var data : String= null;
    var result: JValue = JNothing
    val iterator = executionList.iterator();
    while (iterator.hasNext)
      {
        val obj: GlobalFilterExecutionObject = iterator.next()
        data= SAWElasticSearchQueryExecutor.executeReturnDataAsString(obj)
        m_log debug (data)
        val myArray = parse(data);
        if(result==JNothing)
          result=myArray
        else
        result.merge(myArray)
      }
    return result;
  }
}