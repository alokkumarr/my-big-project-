package com.synchronoss.querybuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.BuilderUtil;
import com.synchronoss.SAWElasticTransportService;
import com.synchronoss.querybuilder.model.ColumnField;
import com.synchronoss.querybuilder.model.DataField;
import com.synchronoss.querybuilder.model.RowField;
import com.synchronoss.querybuilder.model.SqlBuilder;

/**
 * This class will be used to get the query executed into designated<br/>
 * elastic search cluster
 * 
 * @author saurav.paul
 */
public class SAWElasticSearchQueryExecutor {
  



 /* private static SearchResponse execute(SearchSourceBuilder searchSourceBuilder, String jsonString)
      throws JsonProcessingException, IOException

  {
    String host = System.getProperty("host");
    int port = Integer.parseInt(System.getProperty("port"));
    String clusterName = System.getProperty("cluster"); // "sncr-salesdemo"
    SearchResponse response = null;
    TransportClient client = null;
    JsonNode repository = BuilderUtil.getRepositoryNodeTree(jsonString, "esRepository");
    String indexName = repository.get("indexName").asText();
    String type = repository.get("type").textValue();

    try {

      client =
          new PreBuiltTransportClient(Settings.builder()
              .put("client.transport.nodes_sampler_interval", "5s")
              .put("client.transport.sniff", false).put("transport.tcp.compress", true)
              .put("cluster.name", clusterName).put("request.headers.X-Found-Cluster", clusterName)
              .build()).addTransportAddress(new InetSocketTransportAddress(InetAddress
              .getByName(host), port));

      
       * client = new PreBuiltTransportClient( Settings.builder()
       * .put("client.transport.nodes_sampler_interval", "5s") .put("client.transport.sniff",
       * false).put("transport.tcp.compress", true) .put("cluster.name", "sncr-salesdemo")
       * .put("xpack.security.transport.ssl.enabled", false) .put("request.headers.X-Found-Cluster",
       * clusterName) .put("xpack.security.user", username+ ":"+ password).build())
       * .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
       
      response =
          client.prepareSearch(indexName).setTypes(type)
              .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setSource(searchSourceBuilder).get();

    } finally {
      if (client != null) {
        client.close();
      }
    }

    return response;
  }
*/
  /**
   * 
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnAsString(SearchSourceBuilder searchSourceBuilder, String jsonString) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    return SAWElasticTransportService.executeReturnAsString(searchSourceBuilder.toString(), jsonString, "some", "system", "analyze");
  }

  /**
   * 
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String[] executeReturnAsflateString(SearchSourceBuilder searchSourceBuilder,
      String jsonString) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    SearchResponse response = null;
        //execute(searchSourceBuilder, jsonString);
   
    JsonNode esResponse = objectMapper.readTree(response.toString());
    List<String> flattenDataList = new ArrayList<String>();
    SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTree(jsonString, "sqlBuilder");

    List<RowField> rowfield = sqlBuilderNode.getRowFields();
    List<ColumnField> columnFields = sqlBuilderNode.getColumnFields();
    List<DataField> dataFields = sqlBuilderNode.getDataFields();
    JsonNodeFactory factory = JsonNodeFactory.instance;
    // Use case I: The below block is only when column & Data Field is not empty & row field is
    // empty
    if ((rowfield.isEmpty() && rowfield.size() == 0)) {
      if ((columnFields != null && columnFields.size() <= 5)
          && (dataFields != null && dataFields.size() <= 5)) {
        
        for (int i=0; i< columnFields.size(); i++)
        {
          ArrayNode arrayNode = factory.arrayNode();
          ObjectNode node1 = factory.objectNode();
          
          node1.put(columnFields.get(0).getColumnName(), "apple");
          node1.put("TARGET_DATE", "apple");
          
          
          arrayNode.add(node1);
        
        }
      }
    }

    // Use case II: The below block is only when column & row Field
    if (((dataFields.isEmpty()) && dataFields.size() == 0)) {
      if ((rowfield != null && rowfield.size() <= 5)
          && (columnFields != null && columnFields.size() <= 5)) {
      }
    }

    // Use case III: The below block is only when row field with column field & data field
    if ((rowfield != null && rowfield.size() <= 5)
        && ((columnFields != null && columnFields.size() <= 5) && ((dataFields != null && dataFields
            .size() <= 5)))) {
    }

    // Use case IV: The below block is only when row field is not empty but column field & data
    // field are empty
    if ((columnFields.isEmpty() && columnFields.size() == 0)
        && (dataFields.isEmpty() && dataFields.size() == 0)) {
      if ((!rowfield.isEmpty()) && rowfield.size() <= 5) {
      }

    }

    // Use case V: The below block is only when row field is not empty but column field & data field
    // are empty
    if ((rowfield.isEmpty() && rowfield.size() == 0)
        && (dataFields.isEmpty() && dataFields.size() == 0)) {

      if ((!columnFields.isEmpty()) && columnFields.size() <= 5) {
      }

    }

    return (String[]) flattenDataList.toArray();
  }
  public static void main(String[] args) {
    
  }
  
}
