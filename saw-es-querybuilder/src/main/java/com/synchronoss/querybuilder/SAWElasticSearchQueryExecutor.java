package com.synchronoss.querybuilder;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class will be used to get the query executed into designated<br/>
 * elastic search cluster
 * 
 * @author saurav.paul
 */
public class SAWElasticSearchQueryExecutor {
  public static Logger logger = ESLoggerFactory.getLogger(SAWElasticSearchQueryExecutor.class);



  /*private static SearchResponse execute(SearchSourceBuilder searchSourceBuilder, String jsonString)
      throws JsonProcessingException, IOException

  {
    String host = System.getProperty("host");
    int port = Integer.parseInt(System.getProperty("port"));
    SearchResponse response = null;
    TransportClient client = null;
    JsonNode repository = BuilderUtil.getRepositoryNodeTree(jsonString, "repository");
    String indexName = repository.get("indexName").asText();
    String type = repository.get("type").textValue();

    try {

      client =
          new PreBuiltXPackTransportClient(Settings.builder()
              .put("client.transport.nodes_sampler_interval", "5s")
              .put("client.transport.sniff", false).put("transport.tcp.compress", true)
              .put("cluster.name", "sncr-salesdemo")
              .put("xpack.security.transport.ssl.enabled", false)
              .put("request.headers.X-Found-Cluster", "sncr-salesdemo")
              .put("xpack.security.user", "elastic:xuw3dUraHapret").build())
              .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));

      response =
          client.prepareSearch(indexName).setTypes(type)
              .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setSource(searchSourceBuilder).get();

    } finally {
      if (client != null) {
        client.close();
      }
    }

    return response;
  }*/

  /**
   * 
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnAsString(SearchSourceBuilder searchSourceBuilder,
      String jsonString) throws JsonProcessingException, IOException

  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
   // SearchResponse response = execute(searchSourceBuilder, jsonString);
   // JsonNode esResponse = objectMapper.readTree(response.toString());
    return null; 
        //esResponse.get("aggregations").toString();

  }

}
