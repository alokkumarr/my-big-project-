package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.ChartOptions;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.model.Store;

import java.util.LinkedList;
import java.util.List;

public class ChartConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    analysis.setId(oldAnalysisDefinition.get("id").getAsString());

    if (oldAnalysisDefinition.has("parentAnalysisId")) {
      String parentAnalysisId = oldAnalysisDefinition.get("parentAnalysisId").getAsString();

      analysis.setParentAnalysisId(parentAnalysisId);
    }

    if (oldAnalysisDefinition.has("description")) {
      analysis.setDescription(oldAnalysisDefinition.get("description").getAsString());
    }

    analysis.setSemanticId(oldAnalysisDefinition.get("semanticId").getAsString());
    analysis.setName(oldAnalysisDefinition.get("name").getAsString());

    analysis.setType(oldAnalysisDefinition.get("type").getAsString());

    analysis.setCategory(oldAnalysisDefinition.get("categoryId").getAsString());

    analysis.setCustomerCode(oldAnalysisDefinition.get("customerCode").getAsString());
    analysis.setProjectCode(oldAnalysisDefinition.get("projectCode").getAsString());
    analysis.setModule(oldAnalysisDefinition.get("module").getAsString());

    analysis.setCreatedTime(oldAnalysisDefinition.get("createdTimestamp").getAsLong());
    analysis.setCreatedBy(oldAnalysisDefinition.get("username").getAsString());

    analysis.setModifiedTime(oldAnalysisDefinition.get("updatedTimestamp").getAsLong());
    analysis.setModifiedBy(oldAnalysisDefinition.get("updatedUserName").getAsString());


    // Extract artifact name from "artifacts"
    // NOTE: For charts and pivots, there will be only one object in artifacts. For reports,
    // there will be 2 objects
    String artifactName = null;

    JsonArray artifacts = oldAnalysisDefinition.getAsJsonArray("artifacts");

    // Handling artifact name for charts and pivots
    JsonObject artifact = artifacts.get(0).getAsJsonObject();
    artifactName = artifact.get("artifactName").getAsString();

    // Set chartProperties
    Boolean isInverted = null;
    JsonObject legendObject = null;
    String chartType = null;
    String chartTitle = null;

    if (oldAnalysisDefinition.has("isInverted")) {
      isInverted = oldAnalysisDefinition.get("isInverted").getAsBoolean();
    }

    if (oldAnalysisDefinition.has("legend")) {
      legendObject = oldAnalysisDefinition.getAsJsonObject("legend");
    }


    if (oldAnalysisDefinition.has("chartType")) {
      chartType = oldAnalysisDefinition.get("chartType").getAsString();
    }

    if (oldAnalysisDefinition.has("chartTitle")) {
      chartTitle = oldAnalysisDefinition.get("chartTitle").getAsString();
    }
    analysis.setChartOptions(createChartOptions(isInverted, legendObject, chartTitle, chartType));

    if (oldAnalysisDefinition.has("edit")) {
      Boolean designerEdit = oldAnalysisDefinition.get("edit").getAsBoolean();

      analysis.setDesignerEdit(designerEdit);
    }

    JsonObject esRepository = oldAnalysisDefinition.getAsJsonObject("esRepository");
    Store store = null;
    if (esRepository != null) {
      store = extractStoreInfo(esRepository);
    }
    JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
    if (sqlQueryBuilderElement != null) {
      JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
      analysis.setSipQuery(generateSipQuery(artifactName, sqlQueryBuilderObject, store));
    }

    //TODO: Understand the dynamic parameters

    //TODO: Any additional parameters required???

    return analysis;
  }

  private SipQuery generateSipQuery(String artifactName, JsonObject sqlQueryBuilder,
                                      Store store) {
    SipQuery sipQuery = new SipQuery();

    sipQuery.setArtifacts(generateArtifactsList(artifactName, sqlQueryBuilder));

    String booleanCriteriaValue = sqlQueryBuilder.get("booleanCriteria").getAsString();
    SipQuery.BooleanCriteria booleanCriteria
            = SipQuery.BooleanCriteria.fromValue(booleanCriteriaValue);
    sipQuery.setBooleanCriteria(booleanCriteria);

    sipQuery.setFilters(generateFilters(sqlQueryBuilder));
    sipQuery.setSorts(generateSorts(artifactName, sqlQueryBuilder));
    sipQuery.setStore(store);

    return sipQuery;
  }

  private List<Artifact> generateArtifactsList(String artifactName, JsonObject sqlBuilder) {
    List<Artifact> artifacts = new LinkedList<>();

    Artifact artifact = generateArtifact(artifactName, sqlBuilder);

    artifacts.add(artifact);

    return artifacts;
  }

  private List<Filter> generateFilters(JsonObject sqlBuilder) {
    List<Filter> filters = new LinkedList<>();

    if (sqlBuilder.has("filters")) {
      JsonArray filtersArray = sqlBuilder.getAsJsonArray("filters");

      for (JsonElement filterElement: filtersArray) {
        filters.add(generateFilter(filterElement.getAsJsonObject()));
      }
    }

    return filters;
  }

  private ChartOptions createChartOptions(boolean isInverted, JsonObject legend, String chartTitle,
                                          String chartType) {
    ChartOptions chartOptions = new ChartOptions();

    chartOptions.setInverted(isInverted);
    chartOptions.setLegend(legend);
    chartOptions.setChartTitle(chartTitle);
    chartOptions.setChartType(chartType);
    return chartOptions;
  }

  private Filter generateFilter(JsonObject filterObject) {
    Filter filter = new Filter();

    if (filterObject.has("type")) {
      String typeVal = filterObject.get("type").getAsString();

      filter.setType(Filter.Type.fromValue(typeVal));
    }

    if (filterObject.has("tableName")) {
      filter.setArtifactsName(filterObject.get("tableName").getAsString());
    }

    if (filterObject.has("isOptional")) {
      filter.setIsOptional(filterObject.get("isOptional").getAsBoolean());
    }

    if (filterObject.has("columnName")) {
      filter.setColumnName(filterObject.get("columnName").getAsString());
    }

    if (filterObject.has("isRuntimeFilter")) {
      filter.setIsRuntimeFilter(filterObject.get("isRuntimeFilter").getAsBoolean());
    }

    if (filterObject.has("isGlobalFilter")) {
      filter.setIsGlobalFilter(filterObject.get("isGlobalFilter").getAsBoolean());
    }

    return filter;
  }

  private List<Sort> generateSorts(String artifactName, JsonObject sqlBuilder) {
    List<Sort> sorts = new LinkedList<>();

    if (sqlBuilder.has("sorts")) {
      JsonArray sortsArray = sqlBuilder.getAsJsonArray("sorts");

      for (JsonElement sortsElement: sortsArray) {
        sorts.add(generateSortObject(artifactName, sortsElement.getAsJsonObject()));
      }
    }
    return sorts;
  }

  private Sort generateSortObject(String artifactName, JsonObject sortObject) {
    Sort sort = new Sort();

    sort.setArtifacts(artifactName);

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

    return sort;
  }

  // For charts, there will be dataFields and nodefields
  // For pivots, there will be rowfields, columnFIelds and dataFields
  private Artifact generateArtifact(String artifactName, JsonObject sqlBuilder) {
    Artifact artifact = new Artifact();

    artifact.setArtifactsName(artifactName);
    artifact.setFields(generateArtifactFields(sqlBuilder));

    return artifact;
  }

  private List<Field> generateArtifactFields(JsonObject sqlBuilder) {
    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has("dataFields")) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");

      for (JsonElement dataField: dataFields) {
        fields.add(generateArtifactField(dataField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has("nodeFields")) {
      JsonArray nodeFields = sqlBuilder.getAsJsonArray("nodeFields");

      for (JsonElement dataField: nodeFields) {
        fields.add(generateArtifactField(dataField.getAsJsonObject()));
      }
    }

    return fields;
  }

  private Field generateArtifactField(JsonObject fieldObject) {
    Field field = new Field();

    if (fieldObject.has("columnName")) {
      field.setColumnName(fieldObject.get("columnName").getAsString());

      // For analysis migration, we will use column name as dataField
      field.setDataField(field.getColumnName());
    }
    if (fieldObject.has("displayName")) {
      field.setDisplayName(fieldObject.get("displayName").getAsString());
    }
    if (fieldObject.has("aliasName")) {
      String alias = fieldObject.get("aliasName").getAsString();

      if (alias.length() != 0) {
        field.setAlias(fieldObject.get("aliasName").getAsString());
      }
    }

    if (fieldObject.has("comboType")) {
      field.setDisplayType(fieldObject.get("comboType").getAsString());
    }

    if (fieldObject.has("aggregate")) {
      String aggVal = fieldObject.get("aggregate").getAsString();
      field.setAggregate(Field.Aggregate.fromValue(aggVal));
    }

    if (fieldObject.has("groupInterval")) {
      String groupIntVal = fieldObject.get("groupInterval").getAsString();

      field.setGroupInterval(Field.GroupInterval.fromValue(groupIntVal));
    }

    if (fieldObject.has("checked")) {
      String checkedVal = fieldObject.get("checked").getAsString();

      field.setArea(checkedVal + "-axis");
    }

    if (fieldObject.has("type")) {
      String typeVal = fieldObject.get("type").getAsString();

      field.setType(Field.Type.fromValue(typeVal));

      if (field.getType() == Field.Type.DATE) {
        if (fieldObject.has("dateFormat")) {
          String dateFormat = fieldObject.get("dateFormat").getAsString();

          field.setDateFormat(dateFormat);
        }
      }
    }

    field.nullifyAdditionalProperties();

    return field;
  }

  private Store extractStoreInfo(JsonObject esRepository) {
    Store store = new Store();

    if (esRepository.has("storageType")) {
      store.setStorageType(esRepository.get("storageType").getAsString());
    }

    if (esRepository.has("indexName") && esRepository.has("type")) {
      String index = esRepository.get("indexName").getAsString();
      String type = esRepository.get("type").getAsString();
      store.setDataStore(index + "/" + type);
    }

    return store;
  }
}
