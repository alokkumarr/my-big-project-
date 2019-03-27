package com.synchronoss.saw.analysis.service.migrationService;

import com.google.gson.JsonObject;
import com.synchronoss.saw.analysis.modal.Analysis;

public interface AnalysisSipDslConverter {
    public Analysis convert(JsonObject oldAnalysisDefinition);
}
