package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.ChartOptions;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.LimitType;
import com.synchronoss.saw.model.Store;
import java.util.LinkedList;
import java.util.List;

public class ChartConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);

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
    JsonObject labelOptions = null;
    JsonElement xaxis = null;
    JsonElement yaxis = null;

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

    if (oldAnalysisDefinition.has("labelOptions")) {
      labelOptions = oldAnalysisDefinition.get("labelOptions").getAsJsonObject();
    }

    if (oldAnalysisDefinition.has("xAxis")) {
      xaxis = oldAnalysisDefinition.get("xAxis");
    }

    if (oldAnalysisDefinition.has("yAxis")) {
      yaxis = oldAnalysisDefinition.get("yAxis");
    }

    analysis.setChartOptions(
        createChartOptions(
            isInverted, legendObject, chartTitle, chartType, labelOptions, xaxis, yaxis));

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

    // TODO: Understand the dynamic parameters

    // TODO: Any additional parameters required???

    return analysis;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder) {
    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has("dataFields")) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");

      for (JsonElement dataField : dataFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has("nodeFields")) {
      JsonArray nodeFields = sqlBuilder.getAsJsonArray("nodeFields");

      for (JsonElement dataField : nodeFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject) {
    Field field = new Field();
    field = buildCommonsInArtifactField(field, fieldObject);

    if (fieldObject.has("comboType")) {
      field.setDisplayType(fieldObject.get("comboType").getAsString());
    }

    if (fieldObject.has("checked")) {
      String checkedVal = fieldObject.get("checked").getAsString();

      field.setArea(checkedVal + "-axis");
    }

    // Set limit fields
    if (fieldObject.has("limitType")) {
      LimitType limitType = LimitType.fromValue(fieldObject.get("limitType").getAsString());
      field.setLimitType(limitType);
    }

    if (fieldObject.has("limitValue")) {
      int limitValue = fieldObject.get("limitValue").getAsInt();
      field.setLimitValue(limitValue);
    }

    return field;
  }

  private ChartOptions createChartOptions(
      boolean isInverted,
      JsonObject legend,
      String chartTitle,
      String chartType,
      JsonObject labelOptions,
      JsonElement xaxis,
      JsonElement yaxis) {
    ChartOptions chartOptions = new ChartOptions();

    chartOptions.setInverted(isInverted);
    chartOptions.setLegend(legend);
    chartOptions.setChartTitle(chartTitle);
    chartOptions.setChartType(chartType);
    chartOptions.setLabelOptions(labelOptions);
    chartOptions.setxAxis(xaxis);
    chartOptions.setyAxis(yaxis);
    return chartOptions;
  }
}
