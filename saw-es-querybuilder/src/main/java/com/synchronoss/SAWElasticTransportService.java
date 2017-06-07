package com.synchronoss;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//import com.google.common.base.Preconditions;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.synchronoss.querybuilder.model.ESProxy;

public class SAWElasticTransportService {
  private static ESProxy execute(String query, String jsonString, String dsk, String username,
      String moduleName) throws JsonProcessingException, IOException

  {
    String url = System.getProperty("url");
    JsonNode repository = BuilderUtil.getRepositoryNodeTree(jsonString, "repository");
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
    String json = mapper.writeValueAsString(esProxy);
    //System.out.println(json);
   // json = Pattern.compile("\\\\").matcher(json).replaceAll("\\\\\\\\");
    System.out.println(json);
    RequestBody body = RequestBody.create(JSON, mapper.writeValueAsBytes(esProxy));
    Request req = new Request.Builder().post(body).url(url).build();
    
    Response response = client.newCall(req).execute();
    System.out.println(response.isSuccessful());
    /*client.newCall(req).enqueue(new Callback() {
      @Override
      public void onFailure(Request request, IOException e) {}
      @Override
      public void onResponse(Response response) throws IOException {
          System.out.println(response.body().toString());
      }
    });*/
    return esProxy;
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
 /*   Preconditions.checkArgument(query != null && !query.trim().equals(""), "query cannot be null");
    Preconditions.checkArgument(jsonString != null && !jsonString.trim().equals(""),
        "jsonString cannot be null");
    Preconditions.checkArgument(dsk != null && !dsk.trim().equals(""), "dsk cannot be null");
    Preconditions.checkArgument(userName != null && !userName.trim().equals(""),
        "userName cannot be null");
 */   ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    ESProxy response = execute(query, jsonString, dsk, userName, moduleName);
    // JsonNode esResponse = objectMapper.readTree(response.toString());
    return null;

    // esResponse.get("data").toString();

  }
}
