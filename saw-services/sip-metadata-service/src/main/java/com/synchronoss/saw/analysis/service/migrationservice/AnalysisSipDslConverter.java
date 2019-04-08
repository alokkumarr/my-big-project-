package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Format;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.model.Store;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface AnalysisSipDslConverter {
  public Analysis convert(JsonObject oldAnalysisDefinition);

  /**
   * Set all the common parameters across all types of analysis.
   *
   * @param analysis SIP DSL analysis definition object
   * @param oldAnalysisDefinition Old analysis definition
   * @return Analysis Object
   */
  default Analysis setCommonParams(Analysis analysis, JsonObject oldAnalysisDefinition) {
    if (analysis == null) {
      return null;
    }

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

    analysis.setMetricName(oldAnalysisDefinition.get("metricName").getAsString());

    return analysis;
  }

  /**
   * Migrate EsRepository{} to Store{}.
   *
   * @param esRepository Old Analysis definition contains Store information.
   * @return Store Object
   */
  default Store extractStoreInfo(JsonObject esRepository) {
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

  /**
   * Prepare new DSL Analysis sipQuery.
   *
   * @param artifactName Old Analysis Table Name
   * @param sqlQueryBuilder sqlBuilder Object
   * @param store Repository
   * @return SipQuery Object
   */
  default SipQuery generateSipQuery(String artifactName, JsonObject sqlQueryBuilder, Store store) {
    SipQuery sipQuery = new SipQuery();

    sipQuery.setArtifacts(generateArtifactsList(artifactName, sqlQueryBuilder));

    String booleanCriteriaValue = sqlQueryBuilder.get("booleanCriteria").getAsString();
    SipQuery.BooleanCriteria booleanCriteria =
        SipQuery.BooleanCriteria.fromValue(booleanCriteriaValue);
    sipQuery.setBooleanCriteria(booleanCriteria);

    sipQuery.setFilters(generateFilters(sqlQueryBuilder));
    sipQuery.setSorts(generateSorts(artifactName, sqlQueryBuilder));
    sipQuery.setStore(store);

    return sipQuery;
  }

  /**
   * Prepare list of Artifacts as new DSL structure.
   *
   * @param artifactName Old Analysis Table Name
   * @param sqlBuilder sqlBuilder Object
   * @return {@link List} of {@link Artifact}
   */
  default List<Artifact> generateArtifactsList(String artifactName, JsonObject sqlBuilder) {
    List<Artifact> artifacts = new LinkedList<>();

    Artifact artifact = generateArtifact(artifactName, sqlBuilder);

    artifacts.add(artifact);

    return artifacts;
  }

  /**
   * Prepare Filters as new DSL structure.
   *
   * @param sqlBuilder sqlBuilder Object
   * @return {@link List} of {@link Filter}
   */
  default List<Filter> generateFilters(JsonObject sqlBuilder) {
    List<Filter> filters = new LinkedList<>();

    if (sqlBuilder.has("filters")) {
      JsonArray filtersArray = sqlBuilder.getAsJsonArray("filters");

      for (JsonElement filterElement : filtersArray) {
        filters.add(generateFilter(filterElement.getAsJsonObject()));
      }
    }

    return filters;
  }

  /**
   * Prepare each filterObject.
   *
   * @param filterObject Old Analysis filter definition
   * @return Filter Object
   */
  default Filter generateFilter(JsonObject filterObject) {
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

    if (filterObject.has("model")) {
      JsonObject modelObject = filterObject.getAsJsonObject("model");

      Model model = createModel(modelObject);

      filter.setModel(model);
    }

    return filter;
  }

  /**
   * Creates a Model object.
   *
   * @param modelObject Old Analysis model
   * @return Model Object
   */
  default Model createModel(JsonObject modelObject) {
    Model model = new Model();

    if (modelObject.has("preset")) {
      model.setPreset(Model.Preset.fromValue(modelObject.get("preset").getAsString()));
    }

    if (modelObject.has("booleanCriteria")) {
      model.setBooleanCriteria(
          Model.BooleanCriteria.fromValue(modelObject.get("booleanCriteria").getAsString()));
    }

    if (modelObject.has("operator")) {
      model.setOperator(Model.Operator.fromValue(modelObject.get("operator").getAsString()));
    }

    if (modelObject.has("value")) {
      model.setValue(modelObject.get("value").getAsDouble());
    }

    if (modelObject.has("otherValue")) {
      model.setOtherValue(modelObject.get("otherValue").getAsDouble());
    }

    if (modelObject.has("gte")) {
      model.setGte(modelObject.get("gte").getAsString());
    }

    if (modelObject.has("lte")) {
      model.setLte(modelObject.get("lte").getAsString());
    }

    if (modelObject.has("format")) {
      model.setFormat(modelObject.get("format").getAsString());
    }

    if (modelObject.has("modelValues")) {
      JsonArray obj = modelObject.get("modelValues").getAsJsonArray();
      List<Object> modelValues = new ArrayList<>();
      for (JsonElement arr : obj) {
        modelValues.add(arr.getAsJsonPrimitive());
      }
      model.setModelValues(modelValues);
    }

    return model;
  }

  /**
   * Generates a list of sort objects.
   *
   * @param artifactName Old Analysis Table Name
   * @param sqlBuilder sqlBuilder
   * @return {@link List} of {@link Sort}
   */
  default List<Sort> generateSorts(String artifactName, JsonObject sqlBuilder) {
    List<Sort> sorts = new LinkedList<>();

    if (sqlBuilder.has("sorts")) {
      JsonArray sortsArray = sqlBuilder.getAsJsonArray("sorts");

      for (JsonElement sortsElement : sortsArray) {
        sorts.add(generateSortObject(artifactName, sortsElement.getAsJsonObject()));
      }
    }
    return sorts;
  }

  /**
   * Generates a sort object.
   *
   * @param artifactName Old Analysis Table Name
   * @param sortObject Old Analysis Sort definition
   * @return Sort Object
   */
  default Sort generateSortObject(String artifactName, JsonObject sortObject) {
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

  /**
   * Generates artifacts.
   *
   * @param artifactName Old Analysis Table Name
   * @param sqlBuilder SqlBuilder Object
   * @return Artifact Object
   */
  default Artifact generateArtifact(String artifactName, JsonObject sqlBuilder) {
    Artifact artifact = new Artifact();

    artifact.setArtifactsName(artifactName);
    artifact.setFields(generateArtifactFields(sqlBuilder));

    return artifact;
  }

  /**
   * Generates a List of fields.
   *
   * @param sqlBuilder SqlBuilder Object
   * @return {@link List} of {@link Field}
   */
  public abstract List<Field> generateArtifactFields(JsonObject sqlBuilder);

  /**
   * Generates Field.
   *
   * @param fieldObject Old Analysis field definition
   * @return Field Object
   */
  public abstract Field buildArtifactField(JsonObject fieldObject);

  /**
   * Build only common properties of fields across Charts, Pivots and Reports.
   *
   * @param field New DSL field
   * @param fieldObject Old Analysis field definition
   * @return Field Object
   */
  default Field buildCommonsInArtifactField(Field field, JsonObject fieldObject) {

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

    if (fieldObject.has("aggregate")) {
      String aggVal = fieldObject.get("aggregate").getAsString();
      field.setAggregate(Field.Aggregate.fromValue(aggVal));
    }

    if (fieldObject.has("groupInterval")) {
      String groupIntVal = fieldObject.get("groupInterval").getAsString();

      field.setGroupInterval(Field.GroupInterval.fromValue(groupIntVal));
    }

    if (fieldObject.has("type")) {
      String typeVal = fieldObject.get("type").getAsString();

      field.setType(Field.Type.fromValue(typeVal));

      if (field.getType() == Field.Type.DATE) {
        if (fieldObject.has("dateFormat")) {
          String dateFormat = fieldObject.get("dateFormat").getAsString();

          field.setDateFormat(dateFormat);
        } else if (fieldObject.has("format")) {
          String dateFormat = fieldObject.get("format").getAsString();

          field.setDateFormat(dateFormat);
        }
      } else {
        if (fieldObject.has("format")) {
          JsonObject formatObject = fieldObject.getAsJsonObject("format");

          Format format = createFormatObject(formatObject);
          field.setFormat(format);
        }
      }
    }

    field.nullifyAdditionalProperties();

    return field;
  }

  /**
   * Creates a format object.
   *
   * @param formatObject Old Analysis format definition
   * @return Format Object
   */
  default Format createFormatObject(JsonObject formatObject) {
    Format format = new Format();

    if (formatObject.has("precision")) {
      format.setPrecision(formatObject.get("precision").getAsInt());
    }
    if (formatObject.has("comma")) {
      format.setComma(formatObject.get("comma").getAsBoolean());
    }
    if (formatObject.has("currency")) {
      format.setCurrency(formatObject.get("currency").getAsString());
    }
    if (formatObject.has("currencySymbol")) {
      format.setCurrencySymbol(formatObject.get("currencySymbol").getAsString());
    }
    if (formatObject.has("percentage")) {
      format.setPercentage(formatObject.get("percentage").getAsBoolean());
    }

    return format;
  }
}
