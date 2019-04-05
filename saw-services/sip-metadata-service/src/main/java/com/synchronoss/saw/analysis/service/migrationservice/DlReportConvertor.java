package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Field;
import java.util.LinkedList;
import java.util.List;

public class DlReportConvertor implements AnalysisSipDslConverter {

    @Override
    public Analysis convert(JsonObject oldAnalysisDefinition) {
        Analysis analysis = new Analysis();

        analysis = setCommonParams(analysis, oldAnalysisDefinition);

        if (oldAnalysisDefinition.has("query")) {
            String parentAnalysisId = oldAnalysisDefinition.get("query").getAsString();

            // TODO : Query match to sipQuery
//            analysis.setSipQuery(parentAnalysisId);
        }

        // TODO : Old Analysis 'artifacts' contains 2 artifactName.

        JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
        if (sqlQueryBuilderElement != null) {
            JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();
            analysis.setSipQuery(generateSipQuery(sqlQueryBuilderObject));
        }
        return analysis;
    }

    @Override
    public List<Field> generateArtifactFields(JsonObject sqlBuilder) {

        List<Field> fields = new LinkedList<>();

        if (sqlBuilder.has("dataFields")) {
            JsonArray dataFields = sqlBuilder.getAsJsonArray("dataFields");

            for (Object projectObj : dataFields) {
                JsonObject proj = (JsonObject) projectObj;
                JsonArray columnsList = (JsonArray) proj.get("columns");
                for (JsonElement col : columnsList) {
                    fields.add(buildArtifactField(col.getAsJsonObject()));
                }
            }
        }

        return fields;
    }

    @Override
    public Field buildArtifactField(JsonObject fieldObject) {

        Field field = new Field();
        field = buildCommonsInArtifactField(field, fieldObject);
        return field;
    }
}
