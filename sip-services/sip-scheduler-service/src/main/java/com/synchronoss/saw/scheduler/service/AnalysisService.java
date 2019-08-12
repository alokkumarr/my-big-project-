package com.synchronoss.saw.scheduler.service;


import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;

public interface AnalysisService {
    void executeDslAnalysis(String analysisId);
    void scheduleDispatch(SchedulerJobDetail analysis);
}
