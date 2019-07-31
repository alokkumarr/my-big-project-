package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.exceptions.MissingFieldException;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Join;
import com.synchronoss.saw.model.Join.JoinType;
import com.synchronoss.saw.model.JoinCondition;
import com.synchronoss.saw.model.Left;
import com.synchronoss.saw.model.Right;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DlReportConverter implements AnalysisSipDslConverter {

  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) throws MissingFieldException {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);

    if (oldAnalysisDefinition.has("parentAnalysisId")) {
      String parentAnalysisId = oldAnalysisDefinition.get("parentAnalysisId").getAsString();
      analysis.setParentAnalysisId(parentAnalysisId);
    }

    JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
    JsonArray artifactsArray = oldAnalysisDefinition.getAsJsonArray("artifacts");
    if (sqlQueryBuilderElement != null) {
      JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
      SipQuery sipQuery = buildSipQuery(sqlQueryBuilderObject, artifactsArray);
      if (oldAnalysisDefinition.has("query")) {
        String query = oldAnalysisDefinition.get("query").getAsString();
        sipQuery.setQuery(query);
      }
      analysis.setSipQuery(sipQuery);
    }
    return analysis;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder, JsonArray artifactsArray) {

    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has("columns")) {
      JsonArray columnsList = (JsonArray) sqlBuilder.get("columns");
      for (JsonElement col : columnsList) {
        fields.add(buildArtifactField(col.getAsJsonObject(), artifactsArray));
      }
    }
    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject, JsonArray artifactsArray) {

    Field field = new Field();
    field = setCommonFieldProperties(field, fieldObject, artifactsArray);
    return field;
  }

  /**
   * Generates new DSL SipQuery.
   *
   * @param sqlQueryBuilder oldAnalysis sqlBuilder
   * @return SipQuery Object
   */
  public SipQuery buildSipQuery(JsonObject sqlQueryBuilder, JsonArray artifactsArray) {
    SipQuery sipQuery = new SipQuery();

    sipQuery.setArtifacts(buildArtifactsList(sqlQueryBuilder, artifactsArray));

    String booleanCriteriaValue = sqlQueryBuilder.get("booleanCriteria").getAsString();
    SipQuery.BooleanCriteria booleanCriteria =
        SipQuery.BooleanCriteria.fromValue(booleanCriteriaValue);
    sipQuery.setBooleanCriteria(booleanCriteria);

    sipQuery.setFilters(generateFilters(sqlQueryBuilder));
    sipQuery.setSorts(buildOrderbyCols(sqlQueryBuilder));
    sipQuery.setJoins(buildJoins(sqlQueryBuilder));
    return sipQuery;
  }

  /**
   * Generates List of Dsl Artifact.
   *
   * @param sqlBuilder oldAnalysis sqlBuilder
   * @return {@link List} of {@link Artifact}
   */
  public List<Artifact> buildArtifactsList(JsonObject sqlBuilder, JsonArray artifactsArray) {
    List<Artifact> artifacts = new LinkedList<>();
    Artifact artifact;

    if (sqlBuilder.has("dataFields")) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");
      for (Object projectObj : dataFields) {
        JsonObject proj = (JsonObject) projectObj;
        artifact = buildArtifact(proj, artifactsArray);
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
  public Artifact buildArtifact(JsonObject sqlBuilder, JsonArray artifactsArray) {
    Artifact artifact = new Artifact();
    if (sqlBuilder.has("tableName")) {
      artifact.setArtifactsName(sqlBuilder.get("tableName").getAsString());
    }
    artifact.setFields(generateArtifactFields(sqlBuilder, artifactsArray));

    return artifact;
  }

  /**
   * Generates List of DSL Joins.
   *
   * @param sqlBuilder oldAnalysis sqlBuilder
   * @return {@link List} of {@link Join}
   */
  public List<Join> buildJoins(JsonObject sqlBuilder) {
    List<Join> joinsList = new ArrayList<>();
    if (sqlBuilder.has("joins")) {
      JsonArray joinList = sqlBuilder.getAsJsonArray("joins");
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
    if (joinObj.has("type")) {
      String joinType = joinObj.get("type").getAsString();
      join.setJoinType(JoinType.fromValue(joinType));
    }
    if (joinObj.has("criteria")) {
      JsonArray criteriaList = joinObj.getAsJsonArray("criteria");
      JoinCondition joinCondition = new JoinCondition();
      Criteria criteria = new Criteria();
      joinCondition.setOperator("EQ");
      for (JsonElement criteriaElement : criteriaList) {
        JsonObject criteiaObj = criteriaElement.getAsJsonObject();

        if (criteiaObj.get("side").getAsString().equalsIgnoreCase("left")) {
          joinCondition.setLeft(buildLeft(criteiaObj));
        }
        if (criteiaObj.get("side").getAsString().equalsIgnoreCase("right")) {
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
    if (criteriaObj.has("tableName")) {
      left.setArtifactsName(criteriaObj.get("tableName").getAsString());
    }

    if (criteriaObj.has("columnName")) {
      left.setColumnName(criteriaObj.get("columnName").getAsString());
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
    if (criteriaObj.has("tableName")) {
      right.setArtifactsName(criteriaObj.get("tableName").getAsString());
    }

    if (criteriaObj.has("columnName")) {
      right.setColumnName(criteriaObj.get("columnName").getAsString());
    }

    return right;
  }

  /**
   * Generates Order By columns.
   *
   * @param orderByObj oldAnalysis orderBy Object
   * @return {@link List} of {@link Sort}
   */
  public List<Sort> buildOrderbyCols(JsonObject orderByObj) {
    List<Sort> sorts = new LinkedList<>();

    if (orderByObj.has("orderByColumns")) {
      JsonArray sortsArray = orderByObj.getAsJsonArray("orderByColumns");

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

    if (sortObject.has("order")) {
      String orderVal = sortObject.get("order").getAsString();

      sort.setOrder(Sort.Order.fromValue(orderVal));
    }

    if (sortObject.has("columnName")) {
      String columnName = sortObject.get("columnName").getAsString();

      sort.setColumnName(columnName);
    }

    if (sortObject.has("type")) {
      String typeVal = sortObject.get("type").getAsString();

      sort.setType(Sort.Type.fromValue(typeVal));
    }

    if (sortObject.has("tableName")) {
      String tableName = sortObject.get("tableName").getAsString();

      sort.setArtifactsName(tableName);
    }

    return sort;
  }
}
