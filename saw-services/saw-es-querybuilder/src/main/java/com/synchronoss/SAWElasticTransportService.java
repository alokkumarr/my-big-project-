package com.synchronoss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
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



public class SAWElasticTransportService {
  private static final Logger logger = LoggerFactory.getLogger(
      SAWElasticTransportService.class.getName());

  private static String HITS= "hits";
  private static String _SOURCE ="_source";
  private Integer timeOut = 3;
  private static String endpoint = "internal/proxy/storage/";

  private static String execute(String query, String jsonString, String dsk, String username,
    String moduleName,boolean isReport, Integer timeOut) throws JsonProcessingException, IOException, NullPointerException{
    String url = System.getProperty("url");
    JsonNode repository = BuilderUtil.getRepositoryNodeTree(jsonString, "esRepository");
    String indexName = repository.get("indexName").asText();
    String type = repository.get("type").textValue();
    OkHttpClient client = new OkHttpClient();
    client.setConnectTimeout(timeOut, TimeUnit.MINUTES);
    client.setReadTimeout(timeOut, TimeUnit.MINUTES);
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    ESProxy esProxy = new ESProxy();
    esProxy.setStorageType("ES");
    esProxy.setIndexName(indexName);
    esProxy.setObjectType(type);
    if (isReport) {
    esProxy.setAction("search");} 
    else {esProxy.setAction("aggregate");}
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
    RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
    Request req = new Request.Builder().post(body).url(url + endpoint).build();
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
          if (finalResponse!=null)
          {
              return buildAggregatedReportData(jsonString,
                      finalResponse).toString();
          }
          else
          {
              return buildReportData(esResponse.get("data")).toString();
          }
      }
      // In case of Pivot and chart
      return finalResponse.toString();
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
      String userName, String moduleName, Integer timeOut) throws JsonProcessingException, IOException, NullPointerException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName,false,timeOut);
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
                                             String userName, String moduleName, Integer timeOut) throws IOException, NullPointerException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName,true, timeOut);
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
    public static String executeReturnDataAsString(GlobalFilterExecutionObject executionObject, Integer timeOut)
    throws IOException, NullPointerException{

        String url = System.getProperty("url");
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeOut, TimeUnit.MINUTES);
        client.setReadTimeout(timeOut, TimeUnit.MINUTES);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        ESProxy esProxy = new ESProxy();
        esProxy.setStorageType("ES");
        esProxy.setIndexName(executionObject.getEsRepository().getIndexName());
        esProxy.setObjectType(executionObject.getEsRepository().getType());
        esProxy.setAction("aggregate");
        esProxy.setQuery(executionObject.getSearchSourceBuilder().toString());
        esProxy.setModuleName("OBSERVE");
        esProxy.setRequestBy("transportsvc@synchronoss.com");
        esProxy.setProductCode("SIP");
        esProxy.setResultFormat("json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        logger.trace("Request Body in es-querybuilder "+ mapper.writeValueAsString(esProxy));
        RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
        Request req = new Request.Builder().post(body).url(url + endpoint).build();
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
        return buildGlobalFilterData(finalResponse,executionObject.getGlobalFilter());
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

     public static String executeReturnDataAsString(KPIExecutionObject executionObject, Integer timeOut) throws IOException {
         ObjectMapper mapper = new ObjectMapper();
         ObjectNode data = mapper.createObjectNode();
         KPIResultParser kpiResultParser = new KPIResultParser(executionObject.getDataFields());
         Map< String , Object> current = kpiResultParser.jsonNodeParser(
             executeReturnDataAsString(executionObject,
                 executionObject.getCurrentSearchSourceBuilder(),timeOut));
         Map< String , Object> prior = kpiResultParser.jsonNodeParser(
             executeReturnDataAsString(executionObject,
                 executionObject.getPriorSearchSourceBuilder(),timeOut));
         Gson gson = new Gson();
         data.putPOJO("current",gson.toJson(current));
         data.putPOJO("prior", gson.toJson(prior));
         ObjectNode result = mapper.createObjectNode();
         result.putPOJO("data",data);
         return result.toString();
     }

    private static JsonNode executeReturnDataAsString(KPIExecutionObject executionObject
        , SearchSourceBuilder searchSourceBuilder, Integer timeOut)
        throws IOException, NullPointerException{
        String url = System.getProperty("url");
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeOut, TimeUnit.MINUTES);
        client.setReadTimeout(timeOut, TimeUnit.MINUTES);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        ESProxy esProxy = new ESProxy();
        esProxy.setStorageType("ES");
        esProxy.setIndexName(executionObject.getEsRepository().getIndexName());
        esProxy.setObjectType(executionObject.getEsRepository().getType());
        esProxy.setAction("aggregate");
        esProxy.setQuery(searchSourceBuilder.toString());
        esProxy.setModuleName("OBSERVE");
        esProxy.setRequestBy("transportsvc@synchronoss.com");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        logger.trace("Request Body in es-querybuilder "+ mapper.writeValueAsString(esProxy));
        RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
        Request req = new Request.Builder().post(body).url(url + endpoint).build();
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
        return finalResponse;
    }
    public Integer getTimeOut() {
      return timeOut;
    }
    
    public static void main(String[] args) throws IOException {
      OkHttpClient client = new OkHttpClient();
      client.setConnectTimeout(10, TimeUnit.MINUTES);
      client.setReadTimeout(10, TimeUnit.MINUTES);
      MediaType JSON = MediaType.parse("application/json; charset=utf-8");
      ESProxy esProxy = new ESProxy();
      esProxy.setStorageType("ES");
      esProxy.setIndexName("mct_tmo_session");
      esProxy.setObjectType("session");
      esProxy.setAction("aggregate");
      String query = "{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"SOURCE_OS.keyword\":{\"query\":\"android\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}},{\"match\":{\"TARGET_MANUFACTURER.keyword\":{\"query\":\"motorola\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}}],\"disable_coord\":false,\"adjust_pure_negative\":true,\"boost\":1.0}},\"sort\":[{\"TRANSFER_DATE\":{\"order\":\"asc\"}}],\"aggregations\":{\"node_field_1\":{\"date_histogram\":{\"field\":\"TRANSFER_DATE\",\"format\":\"MMM YYYY\",\"interval\":\"1M\",\"offset\":0,\"order\":{\"_key\":\"desc\"},\"keyed\":false,\"min_doc_count\":0},\"aggregations\":{\"AVAILABLE_ITEMS\":{\"sum\":{\"field\":\"AVAILABLE_ITEMS\"}}}}}}";
      esProxy.setQuery(query);
      esProxy.setModuleName("ANALYZE");
      esProxy.setRequestBy("saurav");
      esProxy.setProductCode("SIP");
      esProxy.setRequestBy("saurav");
      esProxy.setResultFormat("json");
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      mapper.disable(SerializationFeature.INDENT_OUTPUT);
      RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
      Request req = new Request.Builder().post(body).url("http://localhost:9800/internal/proxy/storage/").build();
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
        System.out.println(finalResponse.toString());
    }
}
