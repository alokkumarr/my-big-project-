package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
public abstract class ResultMigration {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResultMigration.class);

  private static final String KEY = "key";
  private static final String NAME = "name";
  private static final String VALUE = "value";
  private static final String BUCKETS = "buckets";
  private static final String COLUMN_NAME = "columnName";
  private static final String DATA_FIELDS = "dataFields";
  private static final String KEY_AS_STRING = "key_as_string";

  private JsonNode queryBuilder;

  public abstract List<Object> parseData(JsonNode dataNode, JsonNode queryNode);

  /**
   * Parsed chart data to flatten structure
   *
   * @param dataNode
   * @param queryNode
   * @return
   */
  public List<Object> parseData(
      JsonNode dataNode, JsonNode queryNode, String levelField, String columnField) {
    queryBuilder = queryNode;
    Map<String, String> dataObj = new LinkedHashMap<>();
    List<Object> flatStructure = new ArrayList<>();
    flatStructure = jsonNodeParser(dataNode, dataObj, flatStructure, 1, levelField, columnField);
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
      JsonNode jsonNode,
      Map dataObj,
      List<Object> flatStructure,
      int level,
      String levelField,
      String columnField) {

    JsonNode childNode = jsonNode;
    if (childNode.get(KEY) != null) {
      String columnName = getColumnNames(level, columnField);
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

    JsonNode childNodeLevel = jsonNode.get(levelField + level);
    if (childNodeLevel != null) {
      JsonNode nodeBucket = childNodeLevel.get(BUCKETS);
      if (nodeBucket != null && !nodeBucket.isNull()) {
        Iterator<JsonNode> iterator = nodeBucket.iterator();
        while (iterator.hasNext()) {
          JsonNode nextNode = iterator.next();
          jsonNodeParser(nextNode, dataObj, flatStructure, level + 1, levelField, columnField);
        }
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
  private String getColumnNames(int level, String columnField) {
    JsonNode nodeFields = queryBuilder.get(columnField);
    String columnName = nodeFields.get(level - 2).get(COLUMN_NAME).asText();
    String[] split = columnName != null ? columnName.split("\\.") : null;
    if (split != null && split.length >= 2) return split[0];
    return columnName;
  }
}
