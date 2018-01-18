package com.synchronoss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.synchronoss.ESProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SAWElasticTransportService {
  private static final Logger logger = LoggerFactory.getLogger(
      SAWElasticTransportService.class.getName());

  private static String HITS= "hits";
  private static String _SOURCE ="_source";
  private static String TotalRecords = "total";

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
    if(isReport)
      return  buildReportData(esResponse.get("data")).toString();
     JsonNode finalResponse = objectMapper.readTree(esResponse.get("data").toString());
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

  private static String buildReportData(JsonNode jsonNode)
  {
    Iterator<JsonNode> recordIterator = jsonNode.get(HITS).get(HITS).iterator();
    ArrayList<String> data = new ArrayList<>();
    while(recordIterator.hasNext())
    {
       JsonNode source = recordIterator.next();
      data.add(source.get(_SOURCE).toString());
    }
    return data.toString();
  }

}
