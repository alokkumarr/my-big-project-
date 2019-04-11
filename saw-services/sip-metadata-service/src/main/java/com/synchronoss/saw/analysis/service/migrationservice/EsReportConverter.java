package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Store;
import java.util.LinkedList;
import java.util.List;

public class EsReportConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);

    String artifactName = null;

    // Extract artifact name from "artifacts"
    JsonArray artifactsArray = oldAnalysisDefinition.getAsJsonArray("artifacts");
    JsonObject artifact = artifactsArray.get(0).getAsJsonObject();
    artifactName = artifact.get("artifactName").getAsString();

    JsonObject esRepository = oldAnalysisDefinition.getAsJsonObject("esRepository");
    Store store = null;
    if (esRepository != null) {
      store = extractStoreInfo(esRepository);
    }
    JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
    if (sqlQueryBuilderElement != null) {
      JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
      analysis.setSipQuery(generateSipQuery(artifactName, sqlQueryBuilderObject, artifactsArray, store));
    }

    return analysis;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder, JsonArray artifactsArray) {
    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has("dataFields")) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");

      for (Object projectObj : dataFields) {
        JsonObject proj = (JsonObject) projectObj;
        JsonArray columnsList = (JsonArray) proj.get("columns");
        for (JsonElement col : columnsList) {
          fields.add(buildArtifactField(col.getAsJsonObject(), artifactsArray));
        }
      }
    }

    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject, JsonArray artifactsArray) {
    Field field = new Field();
    field = setCommonFieldProperties(field, fieldObject);
    return field;
  }
}
