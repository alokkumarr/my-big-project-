package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.model.Field;
import java.util.List;

public class PivotConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    return analysis;
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
