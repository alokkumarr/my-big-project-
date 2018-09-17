package com.synchronoss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.synchronoss.querybuilder.model.report.Column;
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
        esProxy.setProductCode("SIP");
        esProxy.setRequestBy("transportsvc@synchronoss.com");
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
        if (esResponse.get("aggregationData") == null) {
            throw new NullPointerException("Data is not available based on provided query criteria");
        }
        JsonNode finalResponse = objectMapper.readTree(esResponse.get("aggregationData").toString());
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
        esProxy.setProductCode("SIP");
        esProxy.setRequestBy("transportsvc@synchronoss.com");
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
        if (esResponse.get("aggregationData") == null) {
            throw new NullPointerException("Data is not available based on provided query criteria");
        }
        JsonNode finalResponse = objectMapper.readTree(esResponse.get("aggregationData").toString());
        return finalResponse;
    }
    public Integer getTimeOut() {
      return timeOut;
    }
    
    public static void main(String[] args) throws IOException {
      System.setProperty("schema.report", "/Users/spau0004/code/SIP/saw-services/json-schema/report_querybuilder_schema.json");
      System.setProperty("schema.pivot", "/Users/spau0004/code/SIP/saw-services/json-schema/chart_querybuilder_schema.json");
      System.setProperty("schema.chart", "/Users/spau0004/code/SIP/saw-services/json-schema/pivot_querybuilder_schema.json");
      OkHttpClient client = new OkHttpClient();
      boolean isReport =true;
      String data = "";
      String jsonString = "{\"type\":\"esReport\",\"semanticId\":\"3e5e8353-1173-4a71-a148-8a8113b0a289\",\"metricName\":\"PTT Subscr Detail Feb\",\"name\":\"Untitled Analysis\",\"description\":\"\",\"scheduled\":null,\"artifacts\":[{\"artifactName\":\"PTT_SUBSCR_MNTH_AGGR\",\"columns\":[{\"columnName\":\"MONTH_VALUE\",\"displayName\":\"Review Month\",\"filterEligible\":true,\"joinEligible\":false,\"kpiEligible\":true,\"name\":\"MONTH_VALUE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"date\"},{\"columnName\":\"SUBS_PROFILE_CREATION_TIME_LOCAL\",\"displayName\":\"Subscriber Local Profile Creation Time\",\"filterEligible\":true,\"joinEligible\":false,\"kpiEligible\":true,\"name\":\"SUBS_PROFILE_CREATION_TIME_LOCAL\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"date\"},{\"columnName\":\"DEVICE_GRP.keyword\",\"displayName\":\"Device Group\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DEVICE_GRP\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"ACCT_GROSS_ADD_TYPE.keyword\",\"displayName\":\"Account Gross Add Type\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCT_GROSS_ADD_TYPE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"aliasName\":\"\",\"columnName\":\"SUBSCR_ID.keyword\",\"displayName\":\"Subscriber ID\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBSCR_ID\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"aliasName\":\"\",\"columnName\":\"SUBSCR_MSISDN.keyword\",\"displayName\":\"Subscriber MSISDN\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBSCR_MSISDN\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"ACCT_NUM.keyword\",\"displayName\":\"Account Number\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCT_NUM\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\",\"checked\":true,\"visibleIndex\":0,\"aggregate\":\"count\"},{\"columnName\":\"ACCOUNT_NAME.keyword\",\"displayName\":\"Account Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCOUNT_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\",\"checked\":true,\"visibleIndex\":1},{\"columnName\":\"CORP_ID.keyword\",\"displayName\":\"Corp ID\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"CORP_ID\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"CORP_NAME.keyword\",\"displayName\":\"Corp Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"CORP_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"FAN_ID.keyword\",\"displayName\":\"FAN ID\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"FAN_ID\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"FAN_NAME.keyword\",\"displayName\":\"FAN Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"FAN_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"BAN_ID.keyword\",\"displayName\":\"BAN ID\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"BAN_ID\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"BAN_NAME.keyword\",\"displayName\":\"BAN Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"BAN_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"CLIENT_TYPE.keyword\",\"displayName\":\"Client Type\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"CLIENT_TYPE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"ACCT_SEGMENT.keyword\",\"displayName\":\"Account Segment\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCT_SEGMENT\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\",\"checked\":true,\"visibleIndex\":2},{\"columnName\":\"APPS_VERSION.keyword\",\"displayName\":\"Apps Version\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"APPS_VERSION\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"SUBSCR_STATE.keyword\",\"displayName\":\"Subscriber State\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBSCR_STATE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"SUBSCR_SUB_STATE.keyword\",\"displayName\":\"Subscriber Sub State\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBSCR_SUB_STATE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"SUBSCR_TENURE\",\"displayName\":\"Subscriber Tenure\",\"filterEligible\":true,\"joinEligible\":false,\"kpiEligible\":true,\"name\":\"SUBSCR_TENURE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBSCR_TENURE_SEGMENT.keyword\",\"displayName\":\"Subscriber Tenure Segment\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBSCR_TENURE_SEGMENT\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"CHURN_TENURE\",\"displayName\":\"Churn Tenure\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"CHURN_TENURE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"CHURN_TENURE_SEGMENT.keyword\",\"displayName\":\"Churn Tenure Segment\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"CHURN_TENURE_SEGMENT\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"CHURN_TYPE.keyword\",\"displayName\":\"Churn Type\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"CHURN_TYPE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"NTWK_TECH_TYPE.keyword\",\"displayName\":\"Network Tech Type\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"NTWK_TECH_TYPE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"DEVICE_NAME.keyword\",\"displayName\":\"Device Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DEVICE_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"MODEL_NAME.keyword\",\"displayName\":\"Model Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"MODEL_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"DEVICE_TYPE.keyword\",\"displayName\":\"Device Type\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DEVICE_TYPE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"DISPR_GRP_MBR.keyword\",\"displayName\":\"Dispatch Group Member\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DISPR_GRP_MBR\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"OS_VERSION.keyword\",\"displayName\":\"OS Version\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"OS_VERSION\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\"},{\"columnName\":\"TOT_SUBSCR_CNT_NEW\",\"displayName\":\"New Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"kpiEligible\":true,\"name\":\"TOT_SUBSCR_CNT_NEW\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"TOT_ACCT_CNT_NEW\",\"displayName\":\"New Account Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_ACCT_CNT_NEW\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"TOT_SUBSCR_CNT_CHURN\",\"displayName\":\"Churn Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"kpiEligible\":true,\"name\":\"TOT_SUBSCR_CNT_CHURN\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\",\"checked\":true,\"aggregate\":\"sum\"},{\"columnName\":\"TOT_SUBSCR_CNT_EARLY_CHURN\",\"displayName\":\"Early Churn Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_SUBSCR_CNT_EARLY_CHURN\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"TOT_SUBSCR_CNT_FALSE_CHURN\",\"displayName\":\"False Churn Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_SUBSCR_CNT_FALSE_CHURN\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"TOT_BILLED_UNIT_CALL\",\"displayName\":\"Call Billed Unit\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_BILLED_UNIT_CALL\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"double\",\"checked\":true,\"aggregate\":\"avg\"},{\"columnName\":\"TOT_BILLED_UNIT_MOU\",\"displayName\":\"MOU Billed Unit\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_BILLED_UNIT_MOU\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"double\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_PROVISIONED\",\"displayName\":\"Days for Activated Active to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_ACTIVATED_INACTIVE\",\"displayName\":\"Days for Activated Active to Activated\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_ACTIVATED_INACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Days for Activated Inactive to Activated\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_ACTIVE_TO_PROVISIONED\",\"displayName\":\"Subscriber Activated Active to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_ACTIVE_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_ACTIVE_TO_ACTIVATED_INACTIVE\",\"displayName\":\"Subscriber Activated Active to Activated\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_ACTIVE_TO_ACTIVATED_INACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_INACTIVE_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Subscriber Activated Inactive to Activated\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_INACTIVE_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_PROVISIONED_TO_ACTIVATED_NO_USAGE\",\"displayName\":\"Subscriber Provisioned to Activated No Usage\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_PROVISIONED_TO_ACTIVATED_NO_USAGE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_PROVISIONED_TO_ACTIVATED_NO_USAGE\",\"displayName\":\"Days for Provisioned to Activated No Usage\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_PROVISIONED_TO_ACTIVATED_NO_USAGE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_SUSPENDED_TO_ACTIVATED_NO_USAGE\",\"displayName\":\"Subscriber Suspended to Activated No Usage\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_SUSPENDED_TO_ACTIVATED_NO_USAGE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_SUSPENDED_TO_ACTIVATED_NO_USAGE\",\"displayName\":\"Days for Suspended to Activated No Usage\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_SUSPENDED_TO_ACTIVATED_NO_USAGE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_SUSPENDED_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Subscriber Suspended to Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_SUSPENDED_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_SUSPENDED_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Days for Suspended to Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_SUSPENDED_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_PROVISIONED_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Subscriber Provisioned to Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_PROVISIONED_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_PROVISIONED_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Days for Provisioned to Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_PROVISIONED_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_NO_USAGE_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Subscriber Activated No Usage to Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_NO_USAGE_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_ACTIVATED_ACTIVE\",\"displayName\":\"Days for Activated No Usage to Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_PROVISIONED\",\"displayName\":\"Days for Activated Inactive to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_INACTIVE_TO_PROVISIONED\",\"displayName\":\"Subscriber Activated Inactive to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_INACTIVE_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_SUSPENDED\",\"displayName\":\"Days for Activated Inactive to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_INACTIVE_TO_SUSPENDED\",\"displayName\":\"Subscriber Activated Inactive to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_INACTIVE_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_NO_USAGE_TO_PROVISIONED\",\"displayName\":\"Subscriber Activated No Usage to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_NO_USAGE_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_PROVISIONED\",\"displayName\":\"Days for Activated No Usage to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_NO_USAGE_TO_SUSPENDED\",\"displayName\":\"Subscriber Activated No Usage to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_NO_USAGE_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_SUSPENDED\",\"displayName\":\"Days for Activated No Usage to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_PROVISIONED_TO_SUSPENDED\",\"displayName\":\"Subscriber Provisioned to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_PROVISIONED_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_PROVISIONED_TO_SUSPENDED\",\"displayName\":\"Days for Provisioned to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_PROVISIONED_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_SUSPENDED_TO_PROVISIONED\",\"displayName\":\"Subscriber Suspended to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_SUSPENDED_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_SUSPENDED_TO_PROVISIONED\",\"displayName\":\"Days for Suspended to Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_SUSPENDED_TO_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_FROM_ACTIVATED_ACTIVE_TO_SUSPENDED\",\"displayName\":\"Subscriber Activated Active to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_FROM_ACTIVATED_ACTIVE_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_SUSPENDED\",\"displayName\":\"Days for Activated Active to Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_ACTIVATED_ACTIVE\",\"displayName\":\"Days in Activated Active\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_ACTIVATED_ACTIVE\",\"displayName\":\"Activated Active Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_ACTIVATED_ACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_PROVISIONED\",\"displayName\":\"Days in Provisioned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_PROVISIONED\",\"displayName\":\"Provisioned Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_PROVISIONED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_ACTIVATED_INACTIVE\",\"displayName\":\"Days in Activated Inactive\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_ACTIVATED_INACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_ACTIVATED_INACTIVE\",\"displayName\":\"Activated Inactive Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_ACTIVATED_INACTIVE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_SUSPENDED\",\"displayName\":\"Days in Suspended\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_SUSPENDED\",\"displayName\":\"Suspended Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_SUSPENDED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_ACTIVATED_NO_USAGE\",\"displayName\":\"Days in Activated No Usage\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_ACTIVATED_NO_USAGE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_ACTIVATED_NO_USAGE\",\"displayName\":\"Activated No Usage Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_ACTIVATED_NO_USAGE\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_ACTIVATED_ACTIVE_TO_CHURNED\",\"displayName\":\"Subscriber Activated Active to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_ACTIVATED_ACTIVE_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_CHURNED\",\"displayName\":\"Days for Activated Active to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_ACTIVE_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_ACTIVATED_INACTIVE_TO_CHURNED\",\"displayName\":\"Subscriber Activated Inactive to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_ACTIVATED_INACTIVE_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_CHURNED\",\"displayName\":\"Days for Activated Inactive to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_INACTIVE_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_ACTIVATED_NO_USAGE_TO_CHURNED\",\"displayName\":\"Subscriber Activated No Usage to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_ACTIVATED_NO_USAGE_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_CHURNED\",\"displayName\":\"Days for Activated No Usage to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_ACTIVATED_NO_USAGE_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_PROVISIONED_TO_CHURNED\",\"displayName\":\"Subscriber Provisioned to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_PROVISIONED_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_PROVISIONED_TO_CHURNED\",\"displayName\":\"Days for Provisioned to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_PROVISIONED_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_FROM_SUSPENDED_TO_CHURNED\",\"displayName\":\"Days for Suspended to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_FROM_SUSPENDED_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"SUBS_SUSPENDED_TO_CHURNED\",\"displayName\":\"Subscriber Suspended to Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"SUBS_SUSPENDED_TO_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"DAYS_CHURNED\",\"displayName\":\"Days in Churned\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"DAYS_CHURNED\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"},{\"columnName\":\"TOT_SUBSCR_CNT\",\"displayName\":\"Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_SUBSCR_CNT\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\"}],\"artifactPosition\":[20,0]}],\"checked\":\"\",\"customerCode\":\"SYNCHRONOSS\",\"dataSecurityKey\":\"\",\"disabled\":false,\"esRepository\":{\"indexName\":\"ptt_subscr_month_aggr\",\"storageType\":\"ES\",\"type\":\"month_record\"},\"id\":\"7a81af6a-d759-419e-ae1a-08a1b2ce7c46\",\"metric\":\"PTT Subscr Detail Feb\",\"module\":\"ANALYZE\",\"repository\":{\"storageType\":\"DL\",\"objects\":[{\"EnrichedDataObjectId\":\"PTT Subscr Detail ES Feb::parquet::1519109730764\",\"displayName\":\"PTT Subscr Detail Feb\",\"EnrichedDataObjectName\":\"PTT Subscr Detail ES Feb\",\"description\":\"PTT Subscr Detail Feb\",\"lastUpdatedTimestamp\":\"undefined\"}],\"_number_of_elements\":1},\"createdTimestamp\":1530556302120,\"userId\":1,\"userFullName\":\"system sncr admin\",\"sqlBuilder\":{\"booleanCriteria\":\"AND\",\"filters\":[],\"sorts\":[],\"dataFields\":[{\"columnName\":\"ACCT_NUM.keyword\",\"displayName\":\"Account Number\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCT_NUM\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\",\"checked\":true,\"visibleIndex\":0,\"aggregate\":\"count\"},{\"columnName\":\"ACCOUNT_NAME.keyword\",\"displayName\":\"Account Name\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCOUNT_NAME\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\",\"checked\":true,\"visibleIndex\":1},{\"columnName\":\"ACCT_SEGMENT.keyword\",\"displayName\":\"Account Segment\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"ACCT_SEGMENT\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"string\",\"checked\":true,\"visibleIndex\":2},{\"columnName\":\"TOT_SUBSCR_CNT_CHURN\",\"displayName\":\"Churn Subscriber Count\",\"filterEligible\":true,\"joinEligible\":false,\"kpiEligible\":true,\"name\":\"TOT_SUBSCR_CNT_CHURN\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"integer\",\"checked\":true,\"aggregate\":\"sum\"},{\"columnName\":\"TOT_BILLED_UNIT_CALL\",\"displayName\":\"Call Billed Unit\",\"filterEligible\":true,\"joinEligible\":false,\"name\":\"TOT_BILLED_UNIT_CALL\",\"table\":\"PTT_SUBSCR_MNTH_AGGR\",\"type\":\"double\",\"checked\":true,\"aggregate\":\"avg\"}]},\"edit\":false,\"executionType\":\"preview\",\"isScheduled\":\"false\"}";
      client.setConnectTimeout(10, TimeUnit.MINUTES);
      client.setReadTimeout(10, TimeUnit.MINUTES);
      MediaType JSON = MediaType.parse("application/json; charset=utf-8");
      ESProxy esProxy = new ESProxy();
      esProxy.setStorageType("ES");
      esProxy.setIndexName("ptt_subscr_month_aggr");
      esProxy.setObjectType("month_record");
      esProxy.setAction("aggregate");
      String query = "{\"size\":0,\"query\":{\"bool\":{\"disable_coord\":false,\"adjust_pure_negative\":true,\"boost\":1.0}},\"aggregations\":{\"group_by_field_2\":{\"terms\":{\"field\":\"ACCT_SEGMENT.keyword\",\"size\":10000,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]},\"aggregations\":{\"group_by_field_1\":{\"terms\":{\"field\":\"ACCOUNT_NAME.keyword\",\"size\":10000,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]},\"aggregations\":{\"ACCT_NUM\":{\"value_count\":{\"field\":\"ACCT_NUM.keyword\"}},\"TOT_SUBSCR_CNT_CHURN\":{\"sum\":{\"field\":\"TOT_SUBSCR_CNT_CHURN\"}},\"TOT_BILLED_UNIT_CALL\":{\"avg\":{\"field\":\"TOT_BILLED_UNIT_CALL\"}}}}}}}}";
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
      JsonNode finalResponse =null;
        if(isReport) {
          // For elastic search report data
          // the reason to use data because report uses search action
            if (esResponse.get("aggregationData")!=null)
            {
              finalResponse = objectMapper.readTree(esResponse.toString());
              //System.out.println("esResponse.get(\"aggregationData\").toString() :" +esResponse.get("aggregationData").toString());
              data= buildAggregatedReportData(jsonString,finalResponse.get("aggregationData")).toString();
            }
            else
            {
              //System.out.println("esResponse.get(\"data\").toString() :" +esResponse.get("data").toString());
                //return buildReportData(esResponse.get("data")).toString();
                data= esResponse.get("data").toString();
            }
        }
        // In case of Pivot and chart
    }
}
