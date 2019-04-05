package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.GroupInterval;
import java.util.LinkedList;
import java.util.List;

public class PivotConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

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

    if (sqlBuilder.has("rowFields")) {
      JsonArray rowFields = sqlBuilder.getAsJsonArray("rowFields");

      for (JsonElement rowField : rowFields) {
        fields.add(buildArtifactField(rowField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has("columnFields")) {
      JsonArray columnFields = sqlBuilder.getAsJsonArray("columnFields");

      for (JsonElement columnField : columnFields) {
        fields.add(buildArtifactField(columnField.getAsJsonObject()));
      }
    }

    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject) {
    Field field = new Field();
    field = setCommonFieldProperties(field, fieldObject);

    // Set area
    if (fieldObject.has("area")) {
      field.setArea(fieldObject.get("area").getAsString());
    }

    // Set groupInterval/dateInterval
    if (fieldObject.has("dateInterval")) {
      String dateInterval = fieldObject.get("dateInterval").getAsString();

      field.setGroupInterval(GroupInterval.fromValue(dateInterval));
    }

    return field;
  }
}
