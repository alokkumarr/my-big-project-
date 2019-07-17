package com.synchronoss.saw.export.pivot;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.model.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/** This class used to parse the Elastic search aggregation json response for pivot analysis. */
public class ElasticSearchAggregationParser {

  private static final Logger logger =
      LoggerFactory.getLogger(ElasticSearchAggregationParser.class);

  private List<Field> fields;
  private List<String> dataType = new LinkedList<>();

  public ElasticSearchAggregationParser(List<Field> fields) {
    this.fields = fields;
  }

  /**
   * Parsed flatten data
   *
   * @param jsonNode
   * @return
   */
  public List<Object> parsePivotData(JsonNode jsonNode) {
    List<Object> parsedData = new ArrayList<>();
    if (jsonNode != null) {
      Iterator<JsonNode> itr = jsonNode.elements();
      while (itr.hasNext()) {
        JsonNode childNode = itr.next();
        Map<Object, Object> dataObj = new LinkedHashMap<>();
        for (String type : dataType) {
          if (childNode.get(type) != null) {
            if (childNode.get(type).isNumber()) {
              dataObj.put(type, childNode.get(type));
            } else {
              dataObj.put(type, childNode.get(type).textValue());
            }
          }
        }
        parsedData.add(dataObj);
      }
    }
    return parsedData;
  }

  /**
   * Set column for parsing data
   *
   * @param columnName
   * @return
   */
  private String getColumnName(String columnName) {
    String[] split = columnName.split("\\.");
    if (split.length >= 2) {
      return split[0];
    }
    return columnName;
  }

  /**
   * This is new implementation for the new pivot table
   *
   * @param exportBean
   */
  public void setColumnDataType(ExportBean exportBean) {
    logger.debug(this.getClass().getName() + " setColumnDataType starts");
    if (fields != null && !fields.isEmpty()) {

      Field.Type[] columnDataType = new Field.Type[fields.size()];
      int count = 0;
      for (Field field : fields) {
        if (field != null && field.getArea() != null) {
          // row field
          if (field.getArea().matches("row")) {
            count = addColumnType(columnDataType, count, field);
          }

          // column field
          if (field.getArea().matches("column")) {
            count = addColumnType(columnDataType, count, field);
          }

          // data field
          if (field != null && field.getArea().equalsIgnoreCase("data")) {
            columnDataType[count++] = field.getType();
            dataType.add(getColumnName(field.getColumnName()));
          }
        }
      }
      exportBean.setColumnFieldDataType(columnDataType);
      logger.debug(this.getClass().getName() + " setColumnDataType ends");
    }
  }

  /**
   * Add column data type for export.
   *
   * @param columnDataType
   * @param count
   * @param field
   * @return
   */
  private int addColumnType(Field.Type[] columnDataType, int count, Field field) {
    switch (field.getType()) {
      case DATE:
        columnDataType[count++] = Field.Type.DATE;
        dataType.add(getColumnName(field.getColumnName()));
        break;
      case TIMESTAMP:
        columnDataType[count++] = Field.Type.TIMESTAMP;
        dataType.add(getColumnName(field.getColumnName()));
        break;
      case LONG:
        columnDataType[count++] = Field.Type.LONG;
        dataType.add(getColumnName(field.getColumnName()));
        break;
      case DOUBLE:
        columnDataType[count++] = Field.Type.DOUBLE;
        dataType.add(getColumnName(field.getColumnName()));
        break;
      case INTEGER:
        columnDataType[count++] = Field.Type.INTEGER;
        dataType.add(getColumnName(field.getColumnName()));
        break;
      case FLOAT:
        columnDataType[count++] = Field.Type.FLOAT;
        dataType.add(getColumnName(field.getColumnName()));
        break;
      case STRING:
        columnDataType[count++] = Field.Type.STRING;
        dataType.add(getColumnName(field.getColumnName()));
        break;
    }
    return count;
  }
}
