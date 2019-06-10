package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.exceptions.MissingFieldException;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.GroupInterval;
import com.synchronoss.saw.model.Store;
import java.util.LinkedList;
import java.util.List;

public class PivotConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) throws MissingFieldException {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);

    String artifactName = null;

    JsonArray artifactsArray = oldAnalysisDefinition.getAsJsonArray("artifacts");

    // Handling artifact name for charts and pivots
    JsonObject artifact = artifactsArray.get(0).getAsJsonObject();
    artifactName = artifact.get("artifactName").getAsString();

    Store store = buildStoreObject(oldAnalysisDefinition);

    JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
    if (sqlQueryBuilderElement != null) {
      JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
      analysis.setSipQuery(
          generateSipQuery(artifactName, sqlQueryBuilderObject, artifactsArray, store));
    }

    return analysis;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder, JsonArray artifactsArray) {
    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has("dataFields")) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");

      for (JsonElement dataField : dataFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject(), artifactsArray));
      }
    }

    if (sqlBuilder.has("rowFields")) {
      JsonArray rowFields = sqlBuilder.getAsJsonArray("rowFields");

      for (JsonElement rowField : rowFields) {
        fields.add(buildArtifactField(rowField.getAsJsonObject(), artifactsArray));
      }
    }

    if (sqlBuilder.has("columnFields")) {
      JsonArray columnFields = sqlBuilder.getAsJsonArray("columnFields");

      for (JsonElement columnField : columnFields) {
        fields.add(buildArtifactField(columnField.getAsJsonObject(), artifactsArray));
      }
    }

    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject, JsonArray artifactsArray) {
    Field field = new Field();
    field = setCommonFieldProperties(field, fieldObject, artifactsArray);

    // Set area
    if (fieldObject.has("area")) {
      field.setArea(fieldObject.get("area").getAsString());
    } else {
      String area = null;
      String columnName = field.getColumnName();

      for (JsonElement artifactElement : artifactsArray) {
        area = getAreaFromArtifactObject(columnName, artifactElement.getAsJsonObject(), "area");

        if (area != null) {
          field.setArea(area);
          break;
        }
      }
    }

    // Set groupInterval/dateInterval
    if (fieldObject.has("dateInterval")) {
      JsonElement dateInterval = fieldObject.get("dateInterval");

      if (!dateInterval.isJsonNull() && dateInterval != null) {
        field.setGroupInterval(GroupInterval.fromValue(dateInterval.getAsString()));
      }
    }

    if (fieldObject.has("areaIndex")) {
      field.setAreaIndex(fieldObject.get("areaIndex").getAsInt());
    } else {
      String columnName = field.getColumnName();

      for (JsonElement artifactElement : artifactsArray) {
        String areaIndexAsString =
            getAreaFromArtifactObject(columnName, artifactElement.getAsJsonObject(), "areaIndex");

        if (areaIndexAsString != null) {
          int areaIndex = Integer.parseInt(areaIndexAsString);
          field.setAreaIndex(areaIndex);
          break;
        }
      }
    }

    return field;
  }
}
