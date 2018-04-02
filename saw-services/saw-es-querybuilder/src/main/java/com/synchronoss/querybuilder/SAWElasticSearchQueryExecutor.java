package com.synchronoss.querybuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject;
import com.synchronoss.querybuilder.model.kpi.KPIExecutionObject;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.SAWElasticTransportService;

/**
 * This class will be used to get the query executed into designated<br/>
 * elastic search cluster
 * 
 * @author saurav.paul
 */
public class SAWElasticSearchQueryExecutor {
  


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
  public static String executeReturnDataAsString(SearchSourceBuilder searchSourceBuilder, String jsonString) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    return SAWElasticTransportService.executeReturnDataAsString(searchSourceBuilder.toString(), jsonString, "some", "system", "analyze");
  }

  /**
   *
   * @param executionObjectList
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   */
  public static String executeReturnDataAsString(GlobalFilterExecutionObject executionObjectList) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    return SAWElasticTransportService.executeReturnDataAsString(executionObjectList);
  }

    /**
     *
     * @param executionObjectList
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static String executeReturnDataAsString(KPIExecutionObject kpiExecutionObject) throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        return SAWElasticTransportService.executeReturnDataAsString(kpiExecutionObject);
    }
  /**
   * 
   * @param searchSourceBuilder
   * @param jsonString
   * @return
   * @throws JsonProcessingException
   * @throws IOException
   * @throws ProcessingException 
   */
  public static String[] executeReturnAsflateString(SearchSourceBuilder searchSourceBuilder,
      String jsonString) throws JsonProcessingException, IOException, ProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    //SearchResponse response = null;
    //JsonNode esResponse = objectMapper.readTree(response.toString());
    List<String> flattenDataList = new ArrayList<String>();
    com.synchronoss.querybuilder.model.pivot.SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTree(jsonString, "sqlBuilder");

    List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield = sqlBuilderNode.getRowFields();
    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields = sqlBuilderNode.getColumnFields();
    List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields = sqlBuilderNode.getDataFields();
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
