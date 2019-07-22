package com.synchronoss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.synchronoss.querybuilder.SAWReportTypeElasticSearchQueryBuilder;
import com.synchronoss.querybuilder.model.report.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.gson.Gson;
import com.synchronoss.querybuilder.ReportAggregationBuilder;
import com.synchronoss.querybuilder.model.kpi.KPIExecutionObject;
import com.synchronoss.querybuilder.model.report.SqlBuilder;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilter;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject;



public class SAWElasticTransportService {
  private static final Logger logger = LoggerFactory.getLogger(
      SAWElasticTransportService.class.getName());

  private Integer timeOut = 3;
  private static String endpoint = "internal/proxy/storage/";


  private static String execute(String query, String jsonString, String dsk, String username,
    String moduleName,boolean isReport, Integer timeOut, HttpClient httpClient) throws JsonProcessingException, IOException, NullPointerException{
    String url = System.getProperty("url");
    JsonNode repository = BuilderUtil.getRepositoryNodeTree(jsonString, "esRepository");
    String indexName = repository.get("indexName").asText();
    String type = repository.get("type").textValue();

    ESProxy esProxy = new ESProxy();
    esProxy.setStorageType("ES");
    esProxy.setIndexName(indexName);
    esProxy.setObjectType(type);
    esProxy.setAction("aggregate");
    esProxy.setQuery(query);
    esProxy.setModuleName(moduleName);
    List<Object> dataSecurityKey = new ArrayList<>();
    esProxy.setDsk(dataSecurityKey);
    esProxy.setRequestBy(username);
    esProxy.setProductCode("SIP");
    esProxy.setResultFormat("json");
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    mapper.disable(SerializationFeature.INDENT_OUTPUT);
    logger.trace("Request Body in es-querybuilder "+ mapper.writeValueAsString(esProxy));

    HttpPost httpPost = new HttpPost(url + endpoint);
    httpPost.setConfig(setRequestConfig(timeOut));
    StringEntity entity = new StringEntity(mapper.writeValueAsString(esProxy));
    httpPost.setEntity(entity);
    httpPost.addHeader("Accept", "application/json");
    httpPost.addHeader("Content-type", "application/json");

    HttpResponse response = httpClient.execute(httpPost);

    logger.trace("Elasticsearch response: {}", response);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String responseString = EntityUtils.toString(response.getEntity());
    logger.trace("responseStringdfd" + responseString);
    JsonNode esResponse = objectMapper.readTree(responseString);
    if (esResponse.get("data") == null && esResponse.get("aggregationData")==null)
    {
      throw new NullPointerException("Data is not available based on provided query criteria");
    }
    JsonNode finalResponse =null;
      if(isReport) {
        // For elastic search report data
        // the reason to use data because report uses search action
          if (esResponse.get("aggregationData")!=null)
          {
            finalResponse = objectMapper.readTree(esResponse.toString());
            logger.trace("esResponse.get(\"aggregationData\").toString() :" +esResponse.get("aggregationData").toString());
            return buildAggregatedReportData(jsonString,finalResponse.get("aggregationData")).toString();
          }
          else
          {
              logger.trace("esResponse.get(\"data\").toString() :" +esResponse.get("data").toString());
              //return buildReportData(esResponse.get("data")).toString();
              return esResponse.get("data").toString();
          }
      }
      // In case of Pivot and chart
   // the reason to use aggregationData because pivot/charts uses search action
      logger.trace("esResponse.get(\"aggregationData\").toString() :" +esResponse.get("aggregationData").toString());
      return esResponse.get("aggregationData").toString();
  }


  /**
   *
   * @param query
   * @param jsonString
   * @param dsk
   * @param userName
   * @param moduleName
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   * @throws NullPointerException
   */
  public static String executeReturnAsString(String query, String jsonString, String dsk,
      String userName, String moduleName, Integer timeOut, HttpClient client) throws JsonProcessingException, IOException, NullPointerException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName,false,timeOut, client);
    String arr = response;
    return arr;
  }

  /**
   *
   * @param query
   * @param jsonString
   * @param dsk
   * @param userName
   * @param moduleName
   * @return
   * @throws IOException
   * @throws NullPointerException
   */
  public static String executeReturnDataAsString(String query, String jsonString, String dsk,
                                             String userName, String moduleName, Integer timeOut, HttpClient client) throws IOException, NullPointerException
  {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName,true, timeOut, client);
    String arr = response;
    return arr;
  }

    /**
     * Parse the report aggregated data.
     * @param reportDefinition
     * @param jsonNode
     * @return
     */
  private static String buildAggregatedReportData(String reportDefinition, JsonNode jsonNode)
  {
      JsonNode node =null;
      try {
          SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTreeReport(reportDefinition, "sqlBuilder");
          if (sqlBuilderNode.getDataFields().get(0).getColumns()==null)
              SAWReportTypeElasticSearchQueryBuilder.changeOldEsReportStructureintoNewStructure(sqlBuilderNode);
         List<Column> aggregationField = ReportAggregationBuilder.getAggregationField(
             sqlBuilderNode.getDataFields().get(0).getColumns());
          ESReportAggregationParser esReportAggregationParser = new ESReportAggregationParser(
                  sqlBuilderNode.getDataFields().get(0).getColumns(),aggregationField);
          List<Object> data = esReportAggregationParser.parseData(jsonNode);
          ObjectMapper mapper = new ObjectMapper();
           node = mapper.valueToTree(data);
      }
      catch (ProcessingException e) {
        logger.error("Exception occurred while building aggregation report data." + e.getMessage());
      }
      catch (IOException e) {
         logger.error("Exception occurred while building aggregation report data." + e.getMessage());
      }
      return node.toString();
  }
    public static String executeReturnDataAsString(GlobalFilterExecutionObject executionObject, Integer timeOut, HttpClient httpClient)
    throws IOException, NullPointerException{

        String url = System.getProperty("url");
        ESProxy esProxy = new ESProxy();
        esProxy.setStorageType("ES");
        esProxy.setIndexName(executionObject.getEsRepository().getIndexName());
        esProxy.setObjectType(executionObject.getEsRepository().getType());
        esProxy.setAction("aggregate");
        esProxy.setQuery(executionObject.getSearchSourceBuilder().toString());
        esProxy.setModuleName("OBSERVE");
        esProxy.setProductCode("SIP");
        esProxy.setRequestBy("transportsvc@synchronoss.com");
        esProxy.setResultFormat("json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        logger.trace("Request Body in es-querybuilder "+ mapper.writeValueAsString(esProxy));

        HttpPost httpPost = new HttpPost(url + endpoint);
        httpPost.setConfig(setRequestConfig(timeOut));
        httpPost.setConfig(setRequestConfig(timeOut));
        StringEntity entity = new StringEntity(mapper.writeValueAsString(esProxy));
        httpPost.setEntity(entity);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json");

        logger.trace("Elasticsearch request: {}", httpPost);
        HttpResponse response = httpClient.execute(httpPost);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);

        String responseString = EntityUtils.toString(response.getEntity());
        logger.trace("responseStringdfd" + responseString);

        JsonNode esResponse = objectMapper.readTree(responseString);
        if (esResponse.get("aggregationData") == null) {
            throw new NullPointerException("Data is not available based on provided query criteria");
        }
        JsonNode finalResponse = objectMapper.readTree(esResponse.get("aggregationData").toString());
        return buildGlobalFilterData(finalResponse,executionObject.getGlobalFilter());
    }
    @SuppressWarnings("unchecked")
    private static String buildGlobalFilterData(JsonNode jsonNode, GlobalFilter globalFilter)
    {
        GlobalFilterResultParser globalFilterResultParser = new GlobalFilterResultParser(globalFilter);
        JsonNode jsonNode1 = jsonNode.get("global_filter_values");
       Map<String , Object> result = globalFilterResultParser.jsonNodeParser(jsonNode1);
       result.put("esRepository",globalFilter.getEsRepository());
        Gson gson = new Gson();
        return gson.toJson(result);
    }
    @SuppressWarnings("unchecked")
     public static String executeReturnDataAsString(KPIExecutionObject executionObject, Integer timeOut,HttpClient client) throws IOException {
         ObjectMapper mapper = new ObjectMapper();
         ObjectNode data = mapper.createObjectNode();
         KPIResultParser kpiResultParser = new KPIResultParser(executionObject.getDataFields());
        Map< String , Object> current = kpiResultParser.jsonNodeParser(
             executeReturnDataAsString(executionObject,
                 executionObject.getCurrentSearchSourceBuilder(),timeOut, client));
         Map< String , Object> prior = kpiResultParser.jsonNodeParser(
             executeReturnDataAsString(executionObject,
                 executionObject.getPriorSearchSourceBuilder(),timeOut, client));
         Gson gson = new Gson();
         data.putPOJO("current",gson.toJson(current));
         data.putPOJO("prior", gson.toJson(prior));
         ObjectNode result = mapper.createObjectNode();
         result.putPOJO("data",data);
         return result.toString();
     }

    private static JsonNode executeReturnDataAsString(KPIExecutionObject executionObject
        , SearchSourceBuilder searchSourceBuilder, Integer timeOut, HttpClient httpClient)
        throws IOException, NullPointerException{
        String url = System.getProperty("url");
        ESProxy esProxy = new ESProxy();
        esProxy.setStorageType("ES");
        esProxy.setIndexName(executionObject.getEsRepository().getIndexName());
        esProxy.setObjectType(executionObject.getEsRepository().getType());
        esProxy.setAction("aggregate");
        esProxy.setQuery(searchSourceBuilder.toString());
        esProxy.setModuleName("OBSERVE");
        esProxy.setProductCode("SIP");
        esProxy.setRequestBy("transportsvc@synchronoss.com");
        esProxy.setResultFormat("json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        logger.trace("Request Body in es-querybuilder "+ mapper.writeValueAsString(esProxy));

        HttpPost httpPost = new HttpPost(url + endpoint);
        httpPost.setConfig(setRequestConfig(timeOut));
        StringEntity entity = new StringEntity(mapper.writeValueAsString(esProxy));
        httpPost.setEntity(entity);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);

        logger.trace("Elasticsearch request: {}", httpPost);
        HttpResponse response = httpClient.execute(httpPost);

        logger.trace("Elasticsearch response: {}", response);

        String responseString = EntityUtils.toString(response.getEntity());
        logger.info("responseStringdfd" + responseString);

        JsonNode esResponse = objectMapper.readTree(responseString);
        if (esResponse.get("aggregationData") == null) {
            throw new NullPointerException("Data is not available based on provided query criteria");
        }
        JsonNode finalResponse = objectMapper.readTree(esResponse.get("aggregationData").toString());
        return finalResponse;
    }
    public Integer getTimeOut() {
      return timeOut;
    }

    private static RequestConfig setRequestConfig(int timeOut) {
      RequestConfig config = RequestConfig.custom().
          setConnectTimeout(timeOut * 10000).build();
          //setConnectionRequestTimeout(timeOut * 10000).build();
          //setSocketTimeout(timeOut * 1000).build();
      return config;
    }
}
