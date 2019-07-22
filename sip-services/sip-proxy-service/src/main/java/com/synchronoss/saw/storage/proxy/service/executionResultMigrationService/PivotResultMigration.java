package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
public class PivotResultMigration {

  private static final Logger LOGGER = LoggerFactory.getLogger(PivotResultMigration.class);

  private static final String KEY = "key";
  private static final String NAME = "name";
  private static final String VALUE = "value";
  private static final String BUCKETS = "buckets";
  private static final String ROW_FIELDS = "rowFields";
  private static final String COLUMN_FIELDS = "columnFields";
  private static final String COLUMN_NAME = "columnName";
  private static final String DATA_FIELDS = "dataFields";
  private static final String COLUMN_LEVEL = "column_level_";
  private static final String ROW_FIELD_LEVEL = "row_level_";
  private static final String KEY_AS_STRING = "key_as_string";

  private JsonNode queryBuilder;

  /**
   * Parsed chart data to flatten structure
   *
   * @param dataNode
   * @param queryNode
   * @return
   */
  public List<Object> parseData(JsonNode dataNode, JsonNode queryNode) {
    queryBuilder = queryNode;
    Map<String, String> dataObj = new LinkedHashMap<>();
    List<Object> flatStructure = new ArrayList<>();
    flatStructure = jsonNodeParser(dataNode, dataObj, flatStructure, 1);

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

    JsonNode childNode = jsonNode;
    if (childNode.get(KEY) != null) {
      String columnName = getColumnNames(level);
      if (childNode.get(KEY_AS_STRING) != null) {
        dataObj.put(columnName, childNode.get(KEY_AS_STRING).textValue());
      } else if (childNode.get(KEY).isNumber()) {
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
            dataObj.put(columnName, childNode.get(KEY).decimalValue());
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

    String childNodeName = childNodeName(jsonNode);
    JsonNode childNodeLevel = jsonNode.get(childNodeName);

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
          JsonNode childJSNode = jsonNode.get(columnName).get(VALUE);
          if (childJSNode.isNumber()) {
            switch (childJSNode.numberType()) {
              case LONG:
                dataObj.put(columnName, childJSNode.longValue());
                break;
              case BIG_INTEGER:
                dataObj.put(columnName, childJSNode.bigIntegerValue());
                break;
              case FLOAT:
                dataObj.put(columnName, childJSNode.floatValue());
                break;
              case DOUBLE:
                dataObj.put(columnName, childJSNode.doubleValue());
                break;
              case BIG_DECIMAL:
                dataObj.put(columnName, childJSNode.decimalValue());
                break;
              case INT:
                dataObj.put(columnName, childJSNode.intValue());
                break;
              default:
                dataObj.put(columnName, childJSNode.textValue());
            }
          } else {
            dataObj.put(columnName, childJSNode.textValue());
          }
          records.putAll(dataObj);
        }
      }
      flatStructure.add(records);
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
    JsonNode rowFields = queryBuilder.get(ROW_FIELDS);
    JsonNode columnFields = queryBuilder.get(COLUMN_FIELDS);

    JsonNode jsonNode = rowFields.get(level - 2);
    if (jsonNode == null) {
      // reset column level
      level = 2;
      jsonNode = columnFields.get(level - 2);
    }
    String columnName = jsonNode != null ? jsonNode.get(COLUMN_NAME).asText() : null;
    String[] split = columnName != null ? columnName.split("\\.") : null;
    if (split != null && split.length >= 2) return split[0];
    return columnName;
  }

  private String childNodeName(JsonNode jsonNode) {
    Iterator<String> keys = jsonNode.fieldNames();
    while (keys.hasNext()) {
      String key = keys.next();
      if (key.contains(ROW_FIELD_LEVEL) || key.contains(COLUMN_LEVEL)) {
        return key;
      }
    }
    return null;
  }
}
