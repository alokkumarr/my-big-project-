package com.synchronoss.saw.storage.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
public class ChartResultMigration {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChartResultMigration.class);

  private static final String KEY = "key";
  private static final String NAME = "name";
  private static final String DATA = "data";
  private static final String VALUE = "value";
  private static final String BUCKETS = "buckets";
  private static final String NODE_FIELD = "node_field_";
  private static final String COLUMN_NAME = "columnName";
  private static final String DATA_FIELDS = "dataFields";
  private static final String NODE_FIELDS = "nodeFields";
  private static final String QUERY_BUILDER = "queryBuilder";

  private JsonNode queryBuilder;

  /**
   * Parsed chart data to flatten structure
   *
   * @param jsonNode
   * @return
   */
  public List<Object> parseData(JsonNode jsonNode) {
    queryBuilder = jsonNode.get(QUERY_BUILDER);
    Map<String, String> dataObj = new LinkedHashMap<>();
    List<Object> flatStructure = new ArrayList<>();
    flatStructure = jsonNodeParser(jsonNode.get(DATA), dataObj, flatStructure, 1);
    return flatStructure;
  }

  /**
   * Parsed the chart related data
   *
   * @param jsonNode
   * @param dataObj
   * @param flatStructure
   * @param level
   * @return flatten response
   */
  public List<Object> jsonNodeParser(
      JsonNode jsonNode, Map dataObj, List<Object> flatStructure, int level) {

    LOGGER.trace("jsonNodeParser starts here :" + jsonNode);
    JsonNode childNode = jsonNode;
    if (childNode.get(KEY) != null) {
      String columnName = getColumnNames(level);
      if (childNode.get(KEY).isNumber()) {
        switch (childNode.get(KEY).numberType()) {
          case LONG:
            dataObj.put(columnName, childNode.get(KEY).longValue());
            break;
          case BIG_INTEGER:
            dataObj.put(columnName, childNode.get(KEY).bigIntegerValue());
            break;
          case FLOAT:
            dataObj.put(columnName, childNode.get(KEY).floatValue());
            break;
          case DOUBLE:
            dataObj.put(columnName, childNode.get(KEY).doubleValue());
            break;
          case BIG_DECIMAL:
            dataObj.put(columnName, childNode.get(KEY).doubleValue());
            break;
          case INT:
            dataObj.put(columnName, childNode.get(KEY).intValue());
            break;
          default:
            dataObj.put(columnName, childNode.get(KEY).textValue());
        }
      } else {
        dataObj.put(columnName, childNode.get(KEY).textValue());
      }
    }

    JsonNode childNodeLevel = jsonNode.get(NODE_FIELD + level);
    if (childNodeLevel != null) {
      JsonNode nodeBucket = childNodeLevel.get(BUCKETS);
      Iterator<JsonNode> iterator = nodeBucket.iterator();
      while (iterator.hasNext()) {
        JsonNode nextNode = iterator.next();
        jsonNodeParser(nextNode, dataObj, flatStructure, level + 1);
      }
    } else {
      Map<String, String> records = new LinkedHashMap<>();
      records.putAll(dataObj);
      Iterator<JsonNode> iterator = queryBuilder.get(DATA_FIELDS).iterator();

      while (iterator.hasNext()) {
        JsonNode dataField = iterator.next();
        String columnName = dataField.get(NAME).asText();
        if (jsonNode.has(columnName)) {
          records.put(columnName, String.valueOf(jsonNode.get(columnName).get(VALUE)));
        }
      }
      flatStructure.add(records);
      LOGGER.trace("jsonNodeParser ends here :" + jsonNode);
      LOGGER.trace("Flat Structure Build :" + flatStructure);
    }
    return flatStructure;
  }

  /**
   * Fetch the column name from the node level
   *
   * @param level
   * @return column name
   */
  private String getColumnNames(int level) {
    JsonNode nodeFields = queryBuilder.get(NODE_FIELDS);
    String columnName = nodeFields.get(level - 2).get(COLUMN_NAME).asText();
    String[] split = columnName != null ? columnName.split("\\.") : null;
    if (split != null && split.length >= 2) return split[0];
    return columnName;
  }
}
