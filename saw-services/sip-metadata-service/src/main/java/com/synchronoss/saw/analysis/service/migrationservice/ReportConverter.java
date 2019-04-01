package com.synchronoss.saw.analysis.service.migrationservice;

import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;

public class ReportConverter implements AnalysisSipDslConverter {
  @Override
  public Analysis convert(JsonObject oldAnalysisDefinition) {
    Analysis analysis = new Analysis();

    analysis = setCommonParams(analysis, oldAnalysisDefinition);

    return analysis;
  }
}
