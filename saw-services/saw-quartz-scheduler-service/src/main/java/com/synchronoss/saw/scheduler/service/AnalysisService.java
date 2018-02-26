package com.synchronoss.saw.scheduler.service;

import com.synchronoss.saw.scheduler.modal.JobDetail;

public interface AnalysisService {
    void executeAnalysis(String analysisId);
    void scheduleDispatch(JobDetail analysis);
}
