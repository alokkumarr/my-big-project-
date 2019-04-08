package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Criteria;
import com.synchronoss.saw.model.Criteria.Side;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Join;
import com.synchronoss.saw.model.Join.JoinType;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DlReportConvertor implements AnalysisSipDslConverter {

  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);

    if (oldAnalysisDefinition.has("query")) {
      String parentAnalysisId = oldAnalysisDefinition.get("query").getAsString();

      // TODO : Query match to sipQuery
      //            analysis.setSipQuery(parentAnalysisId);
    }

    // TODO : Old Analysis 'artifacts' contains 2 artifactName.

    JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
    if (sqlQueryBuilderElement != null) {
      JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
      analysis.setSipQuery(buildSipQuery(sqlQueryBuilderObject));
    }
    return analysis;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder) {

    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has("columns")) {
      JsonArray columnsList = (JsonArray) sqlBuilder.get("columns");
      for (JsonElement col : columnsList) {
        fields.add(buildArtifactField(col.getAsJsonObject()));
      }
    }
    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject) {

    Field field = new Field();
    field = buildCommonsInArtifactField(field, fieldObject);
    return field;
  }

  public SipQuery buildSipQuery(JsonObject sqlQueryBuilder) {
    SipQuery sipQuery = new SipQuery();

    sipQuery.setArtifacts(buildArtifactsList(sqlQueryBuilder));

    String booleanCriteriaValue = sqlQueryBuilder.get("booleanCriteria").getAsString();
    SipQuery.BooleanCriteria booleanCriteria =
        SipQuery.BooleanCriteria.fromValue(booleanCriteriaValue);
    sipQuery.setBooleanCriteria(booleanCriteria);

    sipQuery.setFilters(generateFilters(sqlQueryBuilder));
    sipQuery.setSorts(buildOrderbyCols(sqlQueryBuilder));
    sipQuery.setJoins(buildJoins(sqlQueryBuilder));
    return sipQuery;
  }

  public List<Artifact> buildArtifactsList(JsonObject sqlBuilder) {
    List<Artifact> artifacts = new LinkedList<>();
    Artifact artifact;

    if (sqlBuilder.has("dataFields")) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");
      for (Object projectObj : dataFields) {
        JsonObject proj = (JsonObject) projectObj;
        artifact = buildArtifact(proj);
        artifacts.add(artifact);
      }
    }
    return artifacts;
  }

  public Artifact buildArtifact(JsonObject sqlBuilder) {
    Artifact artifact = new Artifact();
    if (sqlBuilder.has("tableName")) {
      artifact.setArtifactsName(sqlBuilder.get("tableName").getAsString());
    }
    artifact.setFields(generateArtifactFields(sqlBuilder));

    return artifact;
  }

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

  public Join buildJoin(JsonObject joinObj) {
    Join join = new Join();
    List<Criteria> criList = new ArrayList<>();
    if (joinObj.has("type")) {
      String joinType = joinObj.get("type").getAsString();
      join.setJoinType(JoinType.fromValue(joinType));
    }
    if (joinObj.has("criteria")) {
      JsonArray criteriaList = joinObj.getAsJsonArray("criteria");
      for (JsonElement criteriaElement : criteriaList) {
        JsonObject criteiaObj = criteriaElement.getAsJsonObject();
        Criteria criteria = new Criteria();
        criteria.setSide(Side.fromValue(criteiaObj.get("side").getAsString()));
        criteria.setArtifactsName(criteiaObj.get("tableName").getAsString());
        criteria.setColumnName(criteiaObj.get("columnName").getAsString());
        criList.add(criteria);
      }
      join.setCriteria(criList);
    }
    return join;
  }

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

      sort.setArtifacts(tableName);
    }

    return sort;
  }
}
