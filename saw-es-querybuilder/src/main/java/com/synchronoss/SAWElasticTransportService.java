package com.synchronoss;

import java.io.IOException;

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
import com.synchronoss.querybuilder.model.ESProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SAWElasticTransportService {
  private static final Logger logger = LoggerFactory.getLogger(
      SAWElasticTransportService.class.getName());
 
  private static String execute(String query, String jsonString, String dsk, String username,
    String moduleName) throws JsonProcessingException, IOException{
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
    esProxy.setQuery(query.replaceAll("\\s+",""));
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
    JsonNode esResponse = objectMapper.readTree(responseString);
    JsonNode finalResponse = objectMapper.readTree(esResponse.get("data").toString());
    return finalResponse.get("aggregations").toString();
  }

  /**
   * 
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnAsString(String query, String jsonString, String dsk,
      String userName, String moduleName) throws JsonProcessingException, IOException

  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String response = execute(query, jsonString, dsk, userName, moduleName);
   // String arr[] = new String[1];
    String arr = response;
    return arr;
  }
}
