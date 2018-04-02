package com.synchronoss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;



import com.synchronoss.querybuilder.ReportAggregationBuilder;
import com.synchronoss.querybuilder.model.kpi.KPIExecutionObject;
import com.synchronoss.querybuilder.model.report.DataField;
import com.synchronoss.querybuilder.model.report.SqlBuilder;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilter;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SAWElasticTransportService {
  private static final Logger logger = LoggerFactory.getLogger(
      SAWElasticTransportService.class.getName());

  private static String HITS= "hits";
  private static String _SOURCE ="_source";


  private static String execute(String query, String jsonString, String dsk, String username,
    String moduleName,boolean isReport) throws JsonProcessingException, IOException, NullPointerException{
    String url = System.getProperty("url");
    JsonNode repository = BuilderUtil.getRepositoryNodeTree(jsonString, "esRepository");
    String indexName = repository.get("indexName").asText();
    String type = repository.get("type").textValue();
    OkHttpClient client = new OkHttpClient();
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    ESProxy esProxy = new ESProxy();
    esProxy.setStorageType("ES");
    esProxy.setIndexName(indexName);
    esProxy.setObjectType(type);
    esProxy.setVerb("_search");
    esProxy.setQuery(query);
    esProxy.setModuleName(moduleName);
    esProxy.setDsk(dsk);
    esProxy.setUsername(username);
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    mapper.disable(SerializationFeature.INDENT_OUTPUT);
    RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
    Request req = new Request.Builder().post(body).url(url).build();
    logger.trace("Elasticsearch request: {}", req);
    Response response = client.newCall(req).execute();
    logger.trace("Elasticsearch response: {}", response);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String responseString = response.body().string();
    logger.trace("responseStringdfd" +responseString);
    JsonNode esResponse = objectMapper.readTree(responseString);
    if (esResponse.get("data") == null)
    {
      throw new NullPointerException("Data is not available based on provided query criteria");
    }
      JsonNode finalResponse = objectMapper.readTree(esResponse.get("data").toString());
      // For elastic search report data
      if(isReport) {
          if (finalResponse.get("aggregations")!=null)
          {
              return buildAggregatedReportData(jsonString,
                      finalResponse.get("aggregations")).toString();
          }
          else
          {
              return buildReportData(esResponse.get("data")).toString();
          }
      }
      // In case of Pivot and chart
      return finalResponse.get("aggregations").toString();
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
      String userName, String moduleName) throws JsonProcessingException, IOException, NullPointerException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName,false);
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
                                             String userName, String moduleName) throws IOException, NullPointerException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName,true);
    String arr = response;
    return arr;
  }

  private static List<String> buildReportData(JsonNode jsonNode)
  {
    Iterator<JsonNode> recordIterator = jsonNode.get(HITS).get(HITS).iterator();
    List<String> data = new ArrayList<>();
    while(recordIterator.hasNext())
    {
       JsonNode source = recordIterator.next();
      data.add(source.get(_SOURCE).toString());
    }
    return data;
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
         List<DataField> aggregationField = ReportAggregationBuilder.getAggregationField(sqlBuilderNode.getDataFields());
          ESReportAggregationParser esReportAggregationParser = new ESReportAggregationParser(
                  sqlBuilderNode.getDataFields(),aggregationField);
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
    public static String executeReturnDataAsString(GlobalFilterExecutionObject executionObject)
    throws IOException, NullPointerException{

        String url = System.getProperty("url");
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        ESProxy esProxy = new ESProxy();
        esProxy.setStorageType("ES");
        esProxy.setIndexName(executionObject.getEsRepository().getIndexName());
        esProxy.setObjectType(executionObject.getEsRepository().getType());
        esProxy.setVerb("_search");
        esProxy.setQuery(executionObject.getSearchSourceBuilder().toString());
        esProxy.setModuleName("observe");
        esProxy.setDsk("dsk");
        esProxy.setUsername("system");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
        Request req = new Request.Builder().post(body).url(url).build();
        logger.trace("Elasticsearch request: {}", req);
        Response response = null;
            response = client.newCall(req).execute();
        logger.trace("Elasticsearch response: {}", response);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        String responseString = response.body().string();
        logger.trace("responseStringdfd" + responseString);
        JsonNode esResponse = objectMapper.readTree(responseString);
        if (esResponse.get("data") == null) {
            throw new NullPointerException("Data is not available based on provided query criteria");
        }
        JsonNode finalResponse = objectMapper.readTree(esResponse.get("data").toString());
        return buildGlobalFilterData(finalResponse.get("aggregations"),executionObject.getGlobalFilter());
    }
    private static String buildGlobalFilterData(JsonNode jsonNode, GlobalFilter globalFilter)
    {
        GlobalFilterResultParser globalFilterResultParser = new GlobalFilterResultParser(globalFilter);
        JsonNode jsonNode1 = jsonNode.get("global_filter_values");
       Map<String , Object> result = globalFilterResultParser.jsonNodeParser(jsonNode1);
       result.put("esRepository",globalFilter.getEsRepository());
        Gson gson = new Gson();
        return gson.toJson(result);
    }

    public static String executeReturnDataAsString(KPIExecutionObject executionObject)
        throws IOException, NullPointerException{

        String url = System.getProperty("url");
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        ESProxy esProxy = new ESProxy();
        esProxy.setStorageType("ES");
        esProxy.setIndexName(executionObject.getEsRepository().getIndexName());
        esProxy.setObjectType(executionObject.getEsRepository().getType());
        esProxy.setVerb("_search");
        esProxy.setQuery(executionObject.getCurrentSearchSourceBuilder().toString());
        esProxy.setModuleName("observe");
        esProxy.setDsk("dsk");
        esProxy.setUsername("system");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
        Request req = new Request.Builder().post(body).url(url).build();
        logger.trace("Elasticsearch request: {}", req);
        Response response = null;
        response = client.newCall(req).execute();
        logger.trace("Elasticsearch response: {}", response);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        String responseString = response.body().string();
        logger.trace("responseStringdfd" + responseString);
        JsonNode esResponse = objectMapper.readTree(responseString);
        if (esResponse.get("data") == null) {
            throw new NullPointerException("Data is not available based on provided query criteria");
        }
        JsonNode finalResponse = objectMapper.readTree(esResponse.get("data").toString());
        return finalResponse.get("aggregations").toString();
       // return buildGlobalFilterData(finalResponse.get("aggregations"),executionObject.getGlobalFilter());
    }
}
