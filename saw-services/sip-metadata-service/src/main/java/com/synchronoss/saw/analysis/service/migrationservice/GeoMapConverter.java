package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Store;
import com.synchronoss.saw.model.geomap.GeoRegion;
import com.synchronoss.saw.model.geomap.MapOptions;
import com.synchronoss.saw.model.geomap.Supports;
import com.synchronoss.saw.util.FieldNames;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GeoMapConverter implements AnalysisSipDslConverter {

  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);
    String artifactName = null;

    JsonArray artifacts = oldAnalysisDefinition.getAsJsonArray(FieldNames.ARTIFACTS);

    // Handling artifact name
    JsonObject artifact = artifacts.get(0).getAsJsonObject();
    artifactName = artifact.get(FieldNames.ARTIFACT_NAME).getAsString();

    analysis.setMapOptions(createMapOptions(oldAnalysisDefinition));

    Store store = buildStoreObject(oldAnalysisDefinition);
    JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get(FieldNames.SQL_BUILDER);
    if (sqlQueryBuilderElement != null) {
      JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
      analysis.setSipQuery(generateSipQuery(artifactName, sqlQueryBuilderObject, store));
    }
    return analysis;
  }

  @Override
  public List<Field> generateArtifactFields(JsonObject sqlBuilder) {
    List<Field> fields = new LinkedList<>();

    if (sqlBuilder.has(FieldNames.DATAFIELDS)) {
      JsonArray dataFields = sqlBuilder.getAsJsonArray(FieldNames.DATAFIELDS);

      for (JsonElement dataField : dataFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    if (sqlBuilder.has(FieldNames.NODEFIELDS)) {
      JsonArray nodeFields = sqlBuilder.getAsJsonArray(FieldNames.NODEFIELDS);

      for (JsonElement dataField : nodeFields) {
        fields.add(buildArtifactField(dataField.getAsJsonObject()));
      }
    }

    return fields;
  }

  @Override
  public Field buildArtifactField(JsonObject fieldObject) {
    Field field = new Field();
    field = setCommonFieldProperties(field, fieldObject);

    if (fieldObject.has(FieldNames.CHECKED)) {
      String checkedVal = fieldObject.get(FieldNames.CHECKED).getAsString();

      field.setArea(checkedVal + FieldNames.AXIS);
    }

    if (fieldObject.has(FieldNames.GEO_TYPE)) {
      String geoType = fieldObject.get(FieldNames.GEO_TYPE).getAsString();

      field.setGeoType(geoType);
    }

    if (fieldObject.has(FieldNames.REGION)) {
      JsonObject region = fieldObject.getAsJsonObject(FieldNames.REGION);
      field.setGeoRegion(new Gson().fromJson(region, GeoRegion.class));
    }
    return field;
  }

  private MapOptions createMapOptions(JsonObject oldAnalysisDefinition) {
    MapOptions mapOptions = new MapOptions();
    List<Supports> supports = new ArrayList<>();

    if (oldAnalysisDefinition.has(FieldNames.SUPPORTS)) {
      JsonArray supportsArray = oldAnalysisDefinition.getAsJsonArray(FieldNames.SUPPORTS);
      for (JsonElement support : supportsArray) {
        supports.add(new Gson().fromJson(support, Supports.class));
      }
      mapOptions.setSupports(supports);
    }

    if (oldAnalysisDefinition.has(FieldNames.MAP_SETTINGS)) {
      JsonObject mapSetting = oldAnalysisDefinition.getAsJsonObject(FieldNames.MAP_SETTINGS);
      if (mapSetting != null) {
        if (mapSetting.has(FieldNames.MAP_STYLE)) {
          mapOptions.setMapStyle(mapSetting.get(FieldNames.MAP_STYLE).getAsString());
        }
      }
    }

    if (oldAnalysisDefinition.has(FieldNames.CHART_TYPE)) {
      mapOptions.setMapType(oldAnalysisDefinition.get(FieldNames.CHART_TYPE).getAsString());
    }

    return mapOptions;
  }
}
