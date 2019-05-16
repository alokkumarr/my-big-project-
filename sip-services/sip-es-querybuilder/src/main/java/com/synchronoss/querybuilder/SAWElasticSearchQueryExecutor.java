package com.synchronoss.querybuilder;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.SAWElasticTransportService;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject;
import com.synchronoss.querybuilder.model.kpi.KPIExecutionObject;

/**
 * This class will be used to get the query executed into designated<br/>
 * elastic search cluster
 * 
 * @author saurav.paul
 */
public class SAWElasticSearchQueryExecutor {
  
  Integer timeOut = 3; // in minutes
  public SAWElasticSearchQueryExecutor(){}
  public SAWElasticSearchQueryExecutor(Integer timeOut) {
    super();
    this.timeOut = timeOut;
  }

  /**
   * 
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnAsString(SearchSourceBuilder searchSourceBuilder, String jsonString, Integer timeOut, HttpClient client) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    return SAWElasticTransportService.executeReturnAsString(searchSourceBuilder.toString(), jsonString, "some", "system", "analyze", timeOut, client);
  }

  /**
   *
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnDataAsString(SearchSourceBuilder searchSourceBuilder, String jsonString, Integer timeOut, HttpClient client) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    return SAWElasticTransportService.executeReturnDataAsString(searchSourceBuilder.toString(), jsonString, "some", "system", "analyze",timeOut, client);
  }

  /**
   *
   * @param executionObjectList
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnDataAsString(GlobalFilterExecutionObject executionObjectList, Integer timeOut, HttpClient client) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    return SAWElasticTransportService.executeReturnDataAsString(executionObjectList,timeOut, client);
  }

    /**
     *
     * @param kpiExecutionObject
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static String executeReturnDataAsString(KPIExecutionObject kpiExecutionObject, Integer timeOut, HttpClient client) throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        return SAWElasticTransportService.executeReturnDataAsString(kpiExecutionObject, timeOut, client);
    }

}
