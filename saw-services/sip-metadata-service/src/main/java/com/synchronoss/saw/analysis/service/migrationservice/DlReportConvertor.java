package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Field;
import java.util.List;

public class DlReportConvertor implements AnalysisSipDslConverter {

    @Override
    public Analysis convert(JsonObject oldAnalysisDefinition) {
        Analysis analysis = new Analysis();

        analysis = setCommonParams(analysis, oldAnalysisDefinition);

        // TODO : Old Analysis 'artifacts' contains 2 artifactName.

        JsonElement sqlQueryBuilderElement = oldAnalysisDefinition.get("sqlBuilder");
        if (sqlQueryBuilderElement != null) {
            JsonObject sqlQueryBuilderObject = sqlQueryBuilderElement.getAsJsonObject();

        }
        return null;
    }

    @Override
    public List<Field> generateArtifactFields(JsonObject sqlBuilder) {
        return null;
    }

    @Override
    public Field buildArtifactField(JsonObject fieldObject) {
        return null;
    }
}
