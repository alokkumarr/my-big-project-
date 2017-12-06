package com.synchronoss.saw.scheduler.service;

public interface AnalysisService {
    AnalysisSchedule[] getAnalysisSchedules();
    void executeAnalysis(String analysisId);
    void scheduleDispatch(AnalysisSchedule analysis);
}
