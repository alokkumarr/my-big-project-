package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.GroupInterval;
import com.synchronoss.saw.model.Field.LimitType;
import com.synchronoss.saw.model.Join;
import com.synchronoss.saw.model.Join.JoinType;
import com.synchronoss.saw.model.JoinCondition;
import com.synchronoss.saw.model.Left;
import com.synchronoss.saw.model.Right;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.model.geomap.GeoRegion;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QueryDefinitionConverter implements FieldsSipDslConverter {

  @Override
  public SipQuery convert(JsonObject queryBuilderObject) {
    SipQuery sipQuery;
    String artifactName = getTableNameFromCols(queryBuilderObject);
    if (artifactName == null) {
      artifactName = "";
      // In few cases - For old execution results, we are not storing tableName in QueryBuilder. FE
      // will
      // take care of it.
    }

    sipQuery = generateSipQuery(artifactName, queryBuilderObject);

    // Unlike Analysis definition, we don't have type information in Execution Results,
    // So prepare data with custom logic by identifying based on their structure.
    // Below code is for preparing Artifacts for ES and DL Reports.
    if (sipQuery.getArtifacts().get(0).getFields().size() == 1
        || sipQuery.getArtifacts().get(0).getFields().get(0).getColumnName() == null) {

      sipQuery.setArtifacts(buildArtifactsList(queryBuilderObject));

      // If DL Report modify Sort Object and build Query and Join Object
      if (queryBuilderObject.has(FieldNames.ORDER_BY_COLUMNS)) {
        sipQuery.setSorts(buildOrderbyCols(queryBuilderObject));
        if (queryBuilderObject.has(FieldNames.QUERY)) {
          String query = queryBuilderObject.get(FieldNames.QUERY).getAsString();
          sipQuery.setQuery(query);
        }
        sipQuery.setJoins(buildJoins(queryBuilderObject));
      }
    }

    return sipQuery;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder) {
    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has(FieldNames.DATAFIELDS)) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray(FieldNames.DATAFIELDS);

      for (JsonElement dataField : dataFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has(FieldNames.NODEFIELDS)) {
      JsonArray nodeFields = sqlBuilder.getAsJsonArray(FieldNames.NODEFIELDS);

      for (JsonElement dataField : nodeFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has(FieldNames.COLUMN_FIELDS)) {
      JsonArray nodeFields = sqlBuilder.getAsJsonArray(FieldNames.COLUMN_FIELDS);

      for (JsonElement dataField : nodeFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has(FieldNames.ROW_FILEDS)) {
      JsonArray nodeFields = sqlBuilder.getAsJsonArray(FieldNames.ROW_FILEDS);

      for (JsonElement dataField : nodeFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject) {
    Field field = new Field();
    field = setCommonFieldProperties(field, fieldObject);

    // Set area
    if (fieldObject.has(FieldNames.AREA)) {
      field.setArea(fieldObject.get(FieldNames.AREA).getAsString());
    }

    // Set groupInterval/dateInterval
    if (fieldObject.has(FieldNames.DATE_INTERVAL)) {
      JsonElement dateInterval = fieldObject.get(FieldNames.DATE_INTERVAL);

      if (!dateInterval.isJsonNull() && dateInterval != null) {
        field.setGroupInterval(GroupInterval.fromValue(dateInterval.getAsString()));
      }
    }

    if (fieldObject.has(FieldNames.COMBO_TYPE)) {
      field.setDisplayType(fieldObject.get(FieldNames.COMBO_TYPE).getAsString());
    }

    if (fieldObject.has(FieldNames.CHECKED)) {
      String checkedVal = fieldObject.get(FieldNames.CHECKED).getAsString();

      field.setArea(checkedVal);
    }

    if (fieldObject.has(FieldNames.AREA_INDEX) || fieldObject.has(FieldNames.VISIBLE_INDEX)) {
      int index = 0;
      if ((fieldObject.has(FieldNames.AREA_INDEX)
          && (!fieldObject.get(FieldNames.AREA_INDEX).isJsonNull()))) {
        index = fieldObject.get(FieldNames.AREA_INDEX).getAsInt();
      } else if (!fieldObject.get(FieldNames.VISIBLE_INDEX).isJsonNull()) {
        index = fieldObject.get(FieldNames.VISIBLE_INDEX).getAsInt();
      }

      field.setAreaIndex(index);
    }

    if (fieldObject.has(FieldNames.LIMIT_TYPE) && fieldObject.has(FieldNames.LIMIT_VALUE)) {
      LimitType limitType =
          LimitType.fromValue(fieldObject.get(FieldNames.LIMIT_TYPE).getAsString());
      field.setLimitType(limitType);

      int limitValue = fieldObject.get(FieldNames.LIMIT_VALUE).getAsInt();
      field.setLimitValue(limitValue);
    }

    if (fieldObject.has(FieldNames.CHECKED)) {
      String checkedVal = fieldObject.get(FieldNames.CHECKED).getAsString();

      field.setArea(checkedVal);
    }

    if (fieldObject.has(FieldNames.REGION)) {
      JsonObject region = fieldObject.getAsJsonObject(FieldNames.REGION);
      field.setGeoRegion(new Gson().fromJson(region, GeoRegion.class));
    }

    return field;
  }

  public String getTableNameFromCols(JsonObject queryBuilder) {
    // Check if tableName is set in dataFields first, if not go and try with nodeFields,
    // columnFields, rowFields.
    String[] columnsToBeChecked = new String[5];
    columnsToBeChecked[0] = FieldNames.DATAFIELDS;
    columnsToBeChecked[1] = FieldNames.ROW_FILEDS;
    columnsToBeChecked[2] = FieldNames.COLUMN_FIELDS;
    columnsToBeChecked[3] = FieldNames.NODEFIELDS;
    columnsToBeChecked[4] = FieldNames.COLUMNS;

    for (String column : columnsToBeChecked) {
      String tableName = iterateToFindTableName(column, queryBuilder);
      return tableName != null ? tableName : null;
    }

    return null;
  }

  public String iterateToFindTableName(String field, JsonObject queryBuilder) {
    if (queryBuilder.has(field)) {
      JsonArray dataFields = queryBuilder.getAsJsonArray(field);

      for (JsonElement dataField : dataFields) {
        if (dataField.getAsJsonObject().has(FieldNames.TABLE_NAME)) {
          String tableName = dataField.getAsJsonObject().get(FieldNames.TABLE_NAME).getAsString();
          if (tableName != null && !tableName.trim().isEmpty()) {
            return tableName;
          }
        }
      }
    }
    return null;
  }

  /**
   * Generates List of Dsl Artifact.
   *
   * @param sqlBuilder oldAnalysis sqlBuilder
   * @return {@link List} of {@link Artifact}
   */
  public List<Artifact> buildArtifactsList(JsonObject sqlBuilder) {
    List<Artifact> artifacts = new LinkedList<>();
    Artifact artifact;

    if (sqlBuilder.has(FieldNames.DATAFIELDS)) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray(FieldNames.DATAFIELDS);
      for (Object projectObj : dataFields) {
        JsonObject proj = (JsonObject) projectObj;
        artifact = buildArtifact(proj);
        artifacts.add(artifact);
      }
    }
    return artifacts;
  }

  /**
   * Generates DSL Artifact Object.
   *
   * @param sqlBuilder oldAnalysis sqlBuilder
   * @return Artifact Object
   */
  public Artifact buildArtifact(JsonObject sqlBuilder) {
    Artifact artifact = new Artifact();
    if (sqlBuilder.has(FieldNames.TABLE_NAME)) {
      artifact.setArtifactsName(sqlBuilder.get(FieldNames.TABLE_NAME).getAsString());
    }
    artifact.setFields(generateColumnFields(sqlBuilder));

    return artifact;
  }

  public List<Field> generateColumnFields(JsonObject sqlBuilder) {

    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has(FieldNames.COLUMNS)) {
      JsonArray columnsList = (JsonArray) sqlBuilder.get(FieldNames.COLUMNS);
      for (JsonElement col : columnsList) {
        fields.add(buildArtifactField(col.getAsJsonObject()));
      }
    }
    return fields;
  }

  /**
   * Generates Order By columns.
   *
   * @param orderByObj oldAnalysis orderBy Object
   * @return {@link List} of {@link Sort}
   */
  public List<Sort> buildOrderbyCols(JsonObject orderByObj) {
    List<Sort> sorts = new LinkedList<>();

    if (orderByObj.has(FieldNames.ORDER_BY_COLUMNS)) {
      JsonArray sortsArray = orderByObj.getAsJsonArray(FieldNames.ORDER_BY_COLUMNS);

      for (JsonElement sortsElement : sortsArray) {
        sorts.add(buildSortObject(sortsElement.getAsJsonObject()));
      }
    }
    return sorts;
  }

  /**
   * Prepare Sort Object as DSL structure.
   *
   * @param sortObject Old analysis Order-by Object
   * @return Sort Object
   */
  public Sort buildSortObject(JsonObject sortObject) {
    Sort sort = new Sort();

    if (sortObject.has(FieldNames.ORDER)) {
      String orderVal = sortObject.get(FieldNames.ORDER).getAsString();

      sort.setOrder(Sort.Order.fromValue(orderVal));
    }

    if (sortObject.has(FieldNames.COLUMN_NAME)) {
      String columnName = sortObject.get(FieldNames.COLUMN_NAME).getAsString();

      sort.setColumnName(columnName);
    }

    if (sortObject.has(FieldNames.TYPE)) {
      String typeVal = sortObject.get(FieldNames.TYPE).getAsString();

      sort.setType(Sort.Type.fromValue(typeVal));
    }

    if (sortObject.has(FieldNames.TABLE_NAME)) {
      String tableName = sortObject.get(FieldNames.TABLE_NAME).getAsString();

      sort.setArtifacts(tableName);
    }

    return sort;
  }

  /**
   * Generates List of DSL Joins.
   *
   * @param sqlBuilder oldAnalysis sqlBuilder
   * @return {@link List} of {@link Join}
   */
  public List<Join> buildJoins(JsonObject sqlBuilder) {
    List<Join> joinsList = new ArrayList<>();
    if (sqlBuilder.has(FieldNames.JOINS)) {
      JsonArray joinList = sqlBuilder.getAsJsonArray(FieldNames.JOINS);
      for (JsonElement joinElement : joinList) {
        joinsList.add(buildJoin(joinElement.getAsJsonObject()));
      }
    }
    return joinsList;
  }

  /**
   * Generates new DSL Join Object.
   *
   * @param joinObj oldAnalysis Join object
   * @return Join Object
   */
  public Join buildJoin(JsonObject joinObj) {
    Join join = new Join();
    List<Criteria> criList = new ArrayList<>();
    if (joinObj.has(FieldNames.TYPE)) {
      String joinType = joinObj.get(FieldNames.TYPE).getAsString();
      join.setJoinType(JoinType.fromValue(joinType));
    }
    if (joinObj.has(FieldNames.CRITERIA)) {
      JsonArray criteriaList = joinObj.getAsJsonArray(FieldNames.CRITERIA);
      JoinCondition joinCondition = new JoinCondition();
      Criteria criteria = new Criteria();
      joinCondition.setOperator(FieldNames.EQ);
      for (JsonElement criteriaElement : criteriaList) {
        JsonObject criteiaObj = criteriaElement.getAsJsonObject();

        if (criteiaObj.get(FieldNames.SIDE).getAsString().equalsIgnoreCase(FieldNames.LEFT)) {
          joinCondition.setLeft(buildLeft(criteiaObj));
        }
        if (criteiaObj.get(FieldNames.SIDE).getAsString().equalsIgnoreCase(FieldNames.RIGHT)) {
          joinCondition.setRight(buildRight(criteiaObj));
        }
      }
      criteria.setJoinCondition(joinCondition);
      criList.add(criteria);
      join.setCriteria(criList);
    }
    return join;
  }

  /**
   * Generates Join Criteria.
   *
   * @param criteriaObj oldAnalysis Criteria Object.
   * @return Left Object
   */
  public Left buildLeft(JsonObject criteriaObj) {
    Left left = new Left();
    if (criteriaObj.has(FieldNames.TABLE_NAME)) {
      left.setArtifactsName(criteriaObj.get(FieldNames.TABLE_NAME).getAsString());
    }

    if (criteriaObj.has(FieldNames.COLUMN_NAME)) {
      left.setColumnName(criteriaObj.get(FieldNames.COLUMN_NAME).getAsString());
    }

    return left;
  }

  /**
   * Generates Join Criteria.
   *
   * @param criteriaObj oldAnalysis Criteria Object.
   * @return Right Object
   */
  public Right buildRight(JsonObject criteriaObj) {
    Right right = new Right();
    if (criteriaObj.has(FieldNames.TABLE_NAME)) {
      right.setArtifactsName(criteriaObj.get(FieldNames.TABLE_NAME).getAsString());
    }

    if (criteriaObj.has(FieldNames.COLUMN_NAME)) {
      right.setColumnName(criteriaObj.get(FieldNames.COLUMN_NAME).getAsString());
    }

    return right;
  }
}
