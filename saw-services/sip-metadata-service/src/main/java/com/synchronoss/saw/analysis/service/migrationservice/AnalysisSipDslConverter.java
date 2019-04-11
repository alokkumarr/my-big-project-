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
import com.synchronoss.saw.util.FieldNames;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public interface AnalysisSipDslConverter {
  Analysis convert(JsonObject oldAnalysisDefinition);

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

    analysis.setId(oldAnalysisDefinition.get(FieldNames.ID).getAsString());
    analysis.setName(oldAnalysisDefinition.get(FieldNames.NAME).getAsString());
    analysis.setType(oldAnalysisDefinition.get(FieldNames.TYPE).getAsString());
    analysis.setModule(oldAnalysisDefinition.get(FieldNames.MODULE).getAsString());
    analysis.setCreatedBy(oldAnalysisDefinition.get(FieldNames.USER_NAME).getAsString());
    analysis.setMetricName(oldAnalysisDefinition.get(FieldNames.METRIC_NAME).getAsString());
    analysis.setSemanticId(oldAnalysisDefinition.get(FieldNames.SEMANTIC_ID).getAsString());
    analysis.setProjectCode(oldAnalysisDefinition.get(FieldNames.PROJECT_CODE).getAsString());
    analysis.setCustomerCode(oldAnalysisDefinition.get(FieldNames.CUSTOMER_CODE).getAsString());
    analysis.setCreatedTime(oldAnalysisDefinition.get(FieldNames.CREATED_TIMESTAMP).getAsLong());

    if (oldAnalysisDefinition.has(FieldNames.EDIT)
        && !oldAnalysisDefinition.get(FieldNames.EDIT).isJsonNull()) {
      Boolean designerEdit = oldAnalysisDefinition.get(FieldNames.EDIT).getAsBoolean();
      analysis.setDesignerEdit(designerEdit);
    }

    if (oldAnalysisDefinition.has(FieldNames.DESCRIPTION)
        && !oldAnalysisDefinition.get(FieldNames.DESCRIPTION).isJsonNull()) {
      analysis.setDescription(oldAnalysisDefinition.get(FieldNames.DESCRIPTION).getAsString());
    }

    if (oldAnalysisDefinition.has(FieldNames.CATEGORY_ID)
        && !oldAnalysisDefinition.get(FieldNames.CATEGORY_ID).isJsonNull()) {
      analysis.setCategory(oldAnalysisDefinition.get(FieldNames.CATEGORY_ID).getAsString());
    }

    if (oldAnalysisDefinition.has(FieldNames.UPDATED_USER_NAME)
        && !oldAnalysisDefinition.get(FieldNames.UPDATED_USER_NAME).isJsonNull()) {
      analysis.setModifiedBy(oldAnalysisDefinition.get(FieldNames.UPDATED_USER_NAME).getAsString());
    }

    if (oldAnalysisDefinition.has(FieldNames.UPDATED_TIMESTAMP)
        && !oldAnalysisDefinition.get(FieldNames.UPDATED_TIMESTAMP).isJsonNull()) {
      analysis.setModifiedTime(oldAnalysisDefinition.get(FieldNames.UPDATED_TIMESTAMP).getAsLong());
    }

    if (oldAnalysisDefinition.has(FieldNames.PARENT_ANALYSIS_ID)
        && !oldAnalysisDefinition.get(FieldNames.PARENT_ANALYSIS_ID).isJsonNull()) {
      String parentAnalysisId =
          oldAnalysisDefinition.get(FieldNames.PARENT_ANALYSIS_ID).getAsString();
      analysis.setParentAnalysisId(parentAnalysisId);
    }
    return analysis;
  }

  /**
   * Builds a store object from the old analysis definition.
   *
   * @param oldAnalysisDefinition Old analysis deginition
   * @return Store Object
   */
  default Store buildStoreObject(JsonObject oldAnalysisDefinition) {
    JsonObject esRepository = oldAnalysisDefinition.getAsJsonObject(FieldNames.ES_REPOSITORY);
    return esRepository != null ? extractStoreInfo(esRepository) : null;
  }

  /**
   * Migrate EsRepository{} to Store{}.
   *
   * @param esRepository esRepository object extracted from oldDefinition
   * @return Store Object
   */
  default Store extractStoreInfo(JsonObject esRepository) {
    Store store = new Store();

    if (esRepository.has(FieldNames.STORAGE_TYPE)) {
      store.setStorageType(esRepository.get(FieldNames.STORAGE_TYPE).getAsString());
    }

    if (esRepository.has(FieldNames.INDEX_NAME) && esRepository.has(FieldNames.TYPE)) {
      String index = esRepository.get(FieldNames.INDEX_NAME).getAsString();
      String type = esRepository.get(FieldNames.TYPE).getAsString();
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

    String booleanCriteriaValue = sqlQueryBuilder.get(FieldNames.BOOLEAN_CRITERIA).getAsString();
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

    if (sqlBuilder.has(FieldNames.FILTERS)) {
      JsonArray filtersArray = sqlBuilder.getAsJsonArray(FieldNames.FILTERS);

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

    if (filterObject.has(FieldNames.TYPE)) {
      String typeVal = filterObject.get(FieldNames.TYPE).getAsString();
      filter.setType(Filter.Type.fromValue(typeVal));
    }

    if (filterObject.has(FieldNames.TABLE_NAME)) {
      filter.setArtifactsName(filterObject.get(FieldNames.TABLE_NAME).getAsString());
    }

    if (filterObject.has(FieldNames.IS_OPTIONAL)) {
      filter.setIsOptional(filterObject.get(FieldNames.IS_OPTIONAL).getAsBoolean());
    }

    if (filterObject.has(FieldNames.COLUMN_NAME)) {
      filter.setColumnName(filterObject.get(FieldNames.COLUMN_NAME).getAsString());
    }

    if (filterObject.has(FieldNames.IS_RUNTIME_FILTER)) {
      filter.setIsRuntimeFilter(filterObject.get(FieldNames.IS_RUNTIME_FILTER).getAsBoolean());
    }

    if (filterObject.has(FieldNames.IS_GLOBAL_FILTER)) {
      filter.setIsGlobalFilter(filterObject.get(FieldNames.IS_GLOBAL_FILTER).getAsBoolean());
    }

    if (filterObject.has(FieldNames.MODEL)) {
      JsonObject modelObject = filterObject.getAsJsonObject(FieldNames.MODEL);
      Model model = createModel(modelObject);
      filter.setModel(model);
    }

    return filter;
  }

  /**
   * Creates a Model object. If preset is NA in old analysis definition, don't set anything in the
   * new analysis definition.
   *
   * @param modelObject Old Analysis model
   * @return Model Object
   */
  default Model createModel(JsonObject modelObject) {
    Model model = new Model();

    if (modelObject.has(FieldNames.BOOLEAN_CRITERIA)) {
      model.setBooleanCriteria(
          Model.BooleanCriteria.fromValue(
              modelObject.get(FieldNames.BOOLEAN_CRITERIA).getAsString()));
    }

    if (modelObject.has(FieldNames.PRESENT)) {
      String presetVal = modelObject.get(FieldNames.PRESENT).getAsString();

      if (!presetVal.equalsIgnoreCase(FieldNames.NA)) {
        model.setPreset(Model.Preset.fromValue(modelObject.get(FieldNames.PRESENT).getAsString()));
      }
    }

    if (modelObject.has(FieldNames.OPERATOR)) {
      model.setOperator(
          Model.Operator.fromValue(modelObject.get(FieldNames.OPERATOR).getAsString()));
    }

    if (modelObject.has(FieldNames.VALUE)) {
      model.setValue(modelObject.get(FieldNames.VALUE).getAsDouble());
    }

    if (modelObject.has(FieldNames.OTHER_VALUE)) {
      model.setOtherValue(modelObject.get(FieldNames.OTHER_VALUE).getAsDouble());
    }

    if (modelObject.has(FieldNames.GTE)) {
      model.setGte(modelObject.get(FieldNames.GTE).getAsString());
    }

    if (modelObject.has(FieldNames.LTE)) {
      model.setLte(modelObject.get(FieldNames.LTE).getAsString());
    }

    if (modelObject.has(FieldNames.FORMAT)) {
      model.setFormat(modelObject.get(FieldNames.FORMAT).getAsString());
    }

    if (modelObject.has(FieldNames.MODEL_VALUE)) {
      JsonArray obj = modelObject.get(FieldNames.MODEL_VALUE).getAsJsonArray();
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

    if (sqlBuilder.has(FieldNames.SORTS)) {
      JsonArray sortsArray = sqlBuilder.getAsJsonArray(FieldNames.SORTS);
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

    if (sortObject.has(FieldNames.TYPE)) {
      String typeVal = sortObject.get(FieldNames.TYPE).getAsString();
      sort.setType(Sort.Type.fromValue(typeVal));
    }

    if (sortObject.has(FieldNames.ORDER)) {
      String orderVal = sortObject.get(FieldNames.ORDER).getAsString();
      sort.setOrder(Sort.Order.fromValue(orderVal));
    }

    if (sortObject.has(FieldNames.COLUMN_NAME)) {
      String columnName = sortObject.get(FieldNames.COLUMN_NAME).getAsString();
      sort.setColumnName(columnName);
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
  default Field setCommonFieldProperties(Field field, JsonObject fieldObject) {

    if (fieldObject.has(FieldNames.COLUMN_NAME)) {
      field.setColumnName(fieldObject.get(FieldNames.COLUMN_NAME).getAsString());
      // For analysis migration, we will use column name as dataField
      field.setDataField(field.getColumnName());
    }

    if (fieldObject.has(FieldNames.DISPLAY_NAME)) {
      field.setDisplayName(fieldObject.get(FieldNames.DISPLAY_NAME).getAsString());
    }

    // alias and aliasName are used alternatively in different types of analysis.
    // Both should be handled
    if (fieldObject.has(FieldNames.ALIAS_NAME)) {
      String alias = fieldObject.get(FieldNames.ALIAS_NAME).getAsString();

      if (alias.length() != 0) {
        field.setAlias(fieldObject.get(FieldNames.ALIAS_NAME).getAsString());
      }
    }

    if (fieldObject.has(FieldNames.ALIAS)) {
      String alias = fieldObject.get(FieldNames.ALIAS).getAsString();

      if (alias.length() != 0) {
        field.setAlias(fieldObject.get(FieldNames.ALIAS).getAsString());
      }
    }

    if (fieldObject.has(FieldNames.AGGREGATE)) {
      JsonElement aggValElement = fieldObject.get(FieldNames.AGGREGATE);

      if (!aggValElement.isJsonNull() && aggValElement != null) {
        field.setAggregate(Field.Aggregate.fromValue(aggValElement.getAsString()));
      }
    }

    if (fieldObject.has(FieldNames.GROUP_INTERVAL)) {
      String groupIntVal = fieldObject.get(FieldNames.GROUP_INTERVAL).getAsString();

      field.setGroupInterval(Field.GroupInterval.fromValue(groupIntVal));
    }

    if (fieldObject.has(FieldNames.TYPE)) {
      String typeVal = fieldObject.get(FieldNames.TYPE).getAsString();
      field.setType(Field.Type.fromValue(typeVal));

      if (field.getType() == Field.Type.DATE) {
        if (fieldObject.has(FieldNames.DATE_FORMAT)) {
          String dateFormat = fieldObject.get(FieldNames.DATE_FORMAT).getAsString();
          field.setDateFormat(dateFormat);
        } else if (fieldObject.has(FieldNames.FORMAT)) {
          String dateFormat = fieldObject.get(FieldNames.FORMAT).getAsString();
          field.setDateFormat(dateFormat);
        }
      } else {
        if (fieldObject.has(FieldNames.FORMAT)) {
          JsonObject formatObject = fieldObject.getAsJsonObject(FieldNames.FORMAT);

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

    if (formatObject.has(FieldNames.PRECISION)
        && !formatObject.get(FieldNames.PRECISION).isJsonNull()) {
      format.setPrecision(formatObject.get(FieldNames.PRECISION).getAsInt());
    }
    if (formatObject.has(FieldNames.COMMA) && !formatObject.get(FieldNames.COMMA).isJsonNull()) {
      format.setComma(formatObject.get(FieldNames.COMMA).getAsBoolean());
    }
    if (formatObject.has(FieldNames.CURRENCY)
        && !formatObject.get(FieldNames.CURRENCY).isJsonNull()) {
      format.setCurrency(formatObject.get(FieldNames.CURRENCY).getAsString());
    }
    if (formatObject.has(FieldNames.PERCENTAGE)
        && !formatObject.get(FieldNames.PERCENTAGE).isJsonNull()) {
      format.setPercentage(formatObject.get(FieldNames.PERCENTAGE).getAsBoolean());
    }
    if (formatObject.has(FieldNames.CURRENCY_SYMBOL)
        && !formatObject.get(FieldNames.CURRENCY_SYMBOL).isJsonNull()) {
      format.setCurrencySymbol(formatObject.get(FieldNames.CURRENCY_SYMBOL).getAsString());
    }
    return format;
  }
}
