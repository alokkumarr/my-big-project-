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
import com.synchronoss.saw.util.Statics;

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

    analysis.setId(oldAnalysisDefinition.get(Statics.ID).getAsString());
    analysis.setName(oldAnalysisDefinition.get(Statics.NAME).getAsString());
    analysis.setType(oldAnalysisDefinition.get(Statics.TYPE).getAsString());
    analysis.setModule(oldAnalysisDefinition.get(Statics.MODULE).getAsString());
    analysis.setCreatedBy(oldAnalysisDefinition.get(Statics.USER_NAME).getAsString());
    analysis.setMetricName(oldAnalysisDefinition.get(Statics.METRIC_NAME).getAsString());
    analysis.setSemanticId(oldAnalysisDefinition.get(Statics.SEMANTIC_ID).getAsString());
    analysis.setProjectCode(oldAnalysisDefinition.get(Statics.PROJECT_CODE).getAsString());
    analysis.setCustomerCode(oldAnalysisDefinition.get(Statics.CUSTOMER_CODE).getAsString());
    analysis.setCreatedTime(oldAnalysisDefinition.get(Statics.CREATED_TIMESTAMP).getAsLong());

    if (oldAnalysisDefinition.has(Statics.EDIT) && !oldAnalysisDefinition.get(Statics.EDIT).isJsonNull()) {
      Boolean designerEdit = oldAnalysisDefinition.get(Statics.EDIT).getAsBoolean();
      analysis.setDesignerEdit(designerEdit);
    }

    if (oldAnalysisDefinition.has(Statics.DESCRIPTION) && !oldAnalysisDefinition.get(Statics.DESCRIPTION).isJsonNull()) {
      analysis.setDescription(oldAnalysisDefinition.get(Statics.DESCRIPTION).getAsString());
    }

    if (oldAnalysisDefinition.has(Statics.CATEGORY_ID) && !oldAnalysisDefinition.get(Statics.CATEGORY_ID).isJsonNull()) {
      analysis.setCategory(oldAnalysisDefinition.get(Statics.CATEGORY_ID).getAsString());
    }

    if (oldAnalysisDefinition.has(Statics.UPDATED_USER_NAME) && !oldAnalysisDefinition.get(Statics.UPDATED_USER_NAME).isJsonNull()) {
      analysis.setModifiedBy(oldAnalysisDefinition.get(Statics.UPDATED_USER_NAME).getAsString());
    }

    if (oldAnalysisDefinition.has(Statics.UPDATED_TIMESTAMP) && !oldAnalysisDefinition.get(Statics.UPDATED_TIMESTAMP).isJsonNull()) {
      analysis.setModifiedTime(oldAnalysisDefinition.get(Statics.UPDATED_TIMESTAMP).getAsLong());
    }

    if (oldAnalysisDefinition.has(Statics.PARENT_ANALYSIS_ID)  && !oldAnalysisDefinition.get(Statics.PARENT_ANALYSIS_ID).isJsonNull()) {
      String parentAnalysisId = oldAnalysisDefinition.get(Statics.PARENT_ANALYSIS_ID).getAsString();
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
    JsonObject esRepository = oldAnalysisDefinition.getAsJsonObject(Statics.ES_REPOSITORY);
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

    if (esRepository.has(Statics.STORAGE_TYPE)) {
      store.setStorageType(esRepository.get(Statics.STORAGE_TYPE).getAsString());
    }

    if (esRepository.has(Statics.INDEX_NAME) && esRepository.has(Statics.TYPE)) {
      String index = esRepository.get(Statics.INDEX_NAME).getAsString();
      String type = esRepository.get(Statics.TYPE).getAsString();
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

    String booleanCriteriaValue = sqlQueryBuilder.get(Statics.BOOLEAN_CRITERIA).getAsString();
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

    if (sqlBuilder.has(Statics.FILTERS)) {
      JsonArray filtersArray = sqlBuilder.getAsJsonArray(Statics.FILTERS);

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

    if (filterObject.has(Statics.TYPE)) {
      String typeVal = filterObject.get(Statics.TYPE).getAsString();
      filter.setType(Filter.Type.fromValue(typeVal));
    }

    if (filterObject.has(Statics.TABLE_NAME)) {
      filter.setArtifactsName(filterObject.get(Statics.TABLE_NAME).getAsString());
    }

    if (filterObject.has(Statics.IS_OPTIONAL)) {
      filter.setIsOptional(filterObject.get(Statics.IS_OPTIONAL).getAsBoolean());
    }

    if (filterObject.has(Statics.COLUMN_NAME)) {
      filter.setColumnName(filterObject.get(Statics.COLUMN_NAME).getAsString());
    }

    if (filterObject.has(Statics.IS_RUNTIME_FILTER)) {
      filter.setIsRuntimeFilter(filterObject.get(Statics.IS_RUNTIME_FILTER).getAsBoolean());
    }

    if (filterObject.has(Statics.IS_GLOBAL_FILTER)) {
      filter.setIsGlobalFilter(filterObject.get(Statics.IS_GLOBAL_FILTER).getAsBoolean());
    }

    if (filterObject.has(Statics.MODEL)) {
      JsonObject modelObject = filterObject.getAsJsonObject(Statics.MODEL);
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

    if (modelObject.has(Statics.BOOLEAN_CRITERIA)) {
      model.setBooleanCriteria(
              Model.BooleanCriteria.fromValue(modelObject.get(Statics.BOOLEAN_CRITERIA).getAsString()));
    }

    if (modelObject.has(Statics.PRESENT)) {
      String presetVal = modelObject.get(Statics.PRESENT).getAsString();

      if (!presetVal.equalsIgnoreCase(Statics.NA)) {
        model.setPreset(Model.Preset.fromValue(modelObject.get(Statics.PRESENT).getAsString()));
      }
    }

    if (modelObject.has(Statics.OPERATOR)) {
      model.setOperator(Model.Operator.fromValue(modelObject.get(Statics.OPERATOR).getAsString()));
    }

    if (modelObject.has(Statics.VALUE)) {
      model.setValue(modelObject.get(Statics.VALUE).getAsDouble());
    }

    if (modelObject.has(Statics.OTHER_VALUE)) {
      model.setOtherValue(modelObject.get(Statics.OTHER_VALUE).getAsDouble());
    }

    if (modelObject.has(Statics.GTE)) {
      model.setGte(modelObject.get(Statics.GTE).getAsString());
    }

    if (modelObject.has(Statics.LTE)) {
      model.setLte(modelObject.get(Statics.LTE).getAsString());
    }

    if (modelObject.has(Statics.FORMAT)) {
      model.setFormat(modelObject.get(Statics.FORMAT).getAsString());
    }

    if (modelObject.has(Statics.MODEL_VALUE)) {
      JsonArray obj = modelObject.get(Statics.MODEL_VALUE).getAsJsonArray();
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

    if (sqlBuilder.has(Statics.SHORTS)) {
      JsonArray sortsArray = sqlBuilder.getAsJsonArray(Statics.SHORTS);
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

    if (sortObject.has(Statics.TYPE)) {
      String typeVal = sortObject.get(Statics.TYPE).getAsString();
      sort.setType(Sort.Type.fromValue(typeVal));
    }

    if (sortObject.has(Statics.ORDER)) {
      String orderVal = sortObject.get(Statics.ORDER).getAsString();
      sort.setOrder(Sort.Order.fromValue(orderVal));
    }

    if (sortObject.has(Statics.COLUMN_NAME)) {
      String columnName = sortObject.get(Statics.COLUMN_NAME).getAsString();
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

    if (fieldObject.has(Statics.COLUMN_NAME)) {
      field.setColumnName(fieldObject.get(Statics.COLUMN_NAME).getAsString());
      // For analysis migration, we will use column name as dataField
      field.setDataField(field.getColumnName());
    }

    if (fieldObject.has(Statics.DISPLAY_NAME)) {
      field.setDisplayName(fieldObject.get(Statics.DISPLAY_NAME).getAsString());
    }

    // alias and aliasName are used alternatively in different types of analysis.
    // Both should be handled
    if (fieldObject.has(Statics.ALIAS_NAME)) {
      String alias = fieldObject.get(Statics.ALIAS_NAME).getAsString();

      if (alias.length() != 0) {
        field.setAlias(fieldObject.get(Statics.ALIAS_NAME).getAsString());
      }
    }

    if (fieldObject.has(Statics.ALIAS)) {
      String alias = fieldObject.get(Statics.ALIAS).getAsString();

      if (alias.length() != 0) {
        field.setAlias(fieldObject.get(Statics.ALIAS).getAsString());
      }
    }

    if (fieldObject.has(Statics.AGGREGATE)) {
      JsonElement aggValElement = fieldObject.get(Statics.AGGREGATE);

      if (!aggValElement.isJsonNull() && aggValElement != null) {
        field.setAggregate(Field.Aggregate.fromValue(aggValElement.getAsString()));
      }
    }

    if (fieldObject.has(Statics.GROUP_INTERVAL)) {
      String groupIntVal = fieldObject.get(Statics.GROUP_INTERVAL).getAsString();

      field.setGroupInterval(Field.GroupInterval.fromValue(groupIntVal));
    }

    if (fieldObject.has(Statics.TYPE)) {
      String typeVal = fieldObject.get(Statics.TYPE).getAsString();
      field.setType(Field.Type.fromValue(typeVal));

      if (field.getType() == Field.Type.DATE) {
        if (fieldObject.has(Statics.DATE_FORMAT)) {
          String dateFormat = fieldObject.get(Statics.DATE_FORMAT).getAsString();
          field.setDateFormat(dateFormat);
        } else if (fieldObject.has(Statics.FORMAT)) {
          String dateFormat = fieldObject.get(Statics.FORMAT).getAsString();
          field.setDateFormat(dateFormat);
        }
      } else {
        if (fieldObject.has(Statics.FORMAT)) {
          JsonObject formatObject = fieldObject.getAsJsonObject(Statics.FORMAT);

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

    if (formatObject.has(Statics.PRECISION) && !formatObject.get(Statics.PRECISION).isJsonNull()) {
      format.setPrecision(formatObject.get(Statics.PRECISION).getAsInt());
    }
    if (formatObject.has(Statics.COMMA) && !formatObject.get(Statics.COMMA).isJsonNull()) {
      format.setComma(formatObject.get(Statics.COMMA).getAsBoolean());
    }
    if (formatObject.has(Statics.CURRENCY) && !formatObject.get(Statics.CURRENCY).isJsonNull()) {
      format.setCurrency(formatObject.get(Statics.CURRENCY).getAsString());
    }
    if (formatObject.has(Statics.PERCENTAGE) && !formatObject.get(Statics.PERCENTAGE).isJsonNull()) {
      format.setPercentage(formatObject.get(Statics.PERCENTAGE).getAsBoolean());
    }
    if (formatObject.has(Statics.CURRENCY_SYMBOL) && !formatObject.get(Statics.CURRENCY_SYMBOL).isJsonNull()) {
      format.setCurrencySymbol(formatObject.get(Statics.CURRENCY_SYMBOL).getAsString());
    }
    return format;
  }
}