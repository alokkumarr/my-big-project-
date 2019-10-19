package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Format;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FieldsSipDslConverter {
  SipQuery convert(JsonObject queryBuilderObject);
    static final Logger LOGGER = LoggerFactory.getLogger(FieldsSipDslConverter.class);

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

      if (presetVal != null) {
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
          modelValues.add(arr.getAsString());
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
    sort.setArtifactsName(artifactName);

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

    if (sortObject.has(FieldNames.AGGREGATE)) {
      JsonElement aggValElement = sortObject.get(FieldNames.AGGREGATE);

      if (!aggValElement.isJsonNull() && aggValElement != null) {
        sort.setAggregate(Aggregate.fromValue(aggValElement.getAsString()));
      }
    }

    return sort;
  }

  /**
   * Prepare new DSL Analysis sipQuery.
   *
   * @param artifactName Old Analysis Table Name
   * @param sqlQueryBuilder sqlBuilder Object
   * @return SipQuery Object
   */
  default SipQuery generateSipQuery(String artifactName, JsonObject sqlQueryBuilder) {
    SipQuery sipQuery = new SipQuery();

    sipQuery.setArtifacts(generateArtifactsList(artifactName, sqlQueryBuilder));

    String booleanCriteriaValue = sqlQueryBuilder.get(FieldNames.BOOLEAN_CRITERIA).getAsString();
    SipQuery.BooleanCriteria booleanCriteria =
        SipQuery.BooleanCriteria.fromValue(booleanCriteriaValue);
    sipQuery.setBooleanCriteria(booleanCriteria);
    sipQuery.setFilters(generateFilters(sqlQueryBuilder));
    sipQuery.setSorts(generateSorts(artifactName, sqlQueryBuilder));

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
    } else if (fieldObject.has(FieldNames.COLUMN_NAME)) {
      String displayName = fieldObject.get(FieldNames.COLUMN_NAME).getAsString();
      field.setDisplayName(displayName);
    }

    // alias and aliasName are used alternatively in different types of analysis.
    // Both should be handled
    if (fieldObject.has(FieldNames.ALIAS) || fieldObject.has(FieldNames.ALIAS_NAME)) {
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
    }

    if (fieldObject.has(FieldNames.AGGREGATE)) {
      JsonElement aggValElement = fieldObject.get(FieldNames.AGGREGATE);

      if (!aggValElement.isJsonNull() && aggValElement != null) {
        field.setAggregate(Aggregate.fromValue(aggValElement.getAsString()));
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
