package controllers

import java.util

import com.synchronoss.querybuilder.{SAWElasticSearchQueryBuilder, SAWElasticSearchQueryExecutor}
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject
import model.ClientException
import org.apache.http.client.HttpClient
import org.json4s.JNothing
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods.compact
import org.json4s.native.JsonMethods.render
import org.json4s.native.JsonMethods.parse
import play.mvc.Result
import sncr.saw.common.config.SAWServiceConfig
import sncr.service.InternalServiceClient

class GlobalFilter extends BaseController {

  def process: Result = {
    handle(doProcess)
  }

  private def doProcess(json: JValue, ticket: Option[Ticket]): JValue = {
    if (ticket== None) throw new ClientException(
        "Valid JWT not found in Authorization header")
    val jsonString: String = compact(render(json));
    val  client: InternalServiceClient = new InternalServiceClient()
    client.setParameters()
    val trustStore : String = client.getTrustStore()
    val trustPswd : String = client.getTrustPassWord()
    val keyStore : String = client.getKeyStore()
    val keyPassword : String = client.getKeyPassword()
    val sslEnabled : Boolean = client.isSslEnabled()


    m_log info("fetch global filter result")
    val executionList :util.List[GlobalFilterExecutionObject] =
      new SAWElasticSearchQueryBuilder(trustStore, trustPswd, keyStore, keyPassword, sslEnabled).getsearchSourceBuilder(jsonString)
    var data : String= null;
    var result: JValue = JNothing
    val iterator = executionList.iterator();
    val timeOut :java.lang.Integer =if (SAWServiceConfig.es_conf.hasPath("timeout"))
      new Integer(SAWServiceConfig.es_conf.getInt("timeout")) else new java.lang.Integer(3)

    while (iterator.hasNext)
      {
        val obj: GlobalFilterExecutionObject = iterator.next()
        data= SAWElasticSearchQueryExecutor.executeReturnDataAsString(obj,timeOut,trustStore, trustPswd, keyStore, keyPassword, sslEnabled )
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
