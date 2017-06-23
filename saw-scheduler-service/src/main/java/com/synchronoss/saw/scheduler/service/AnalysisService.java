package com.synchronoss.saw.scheduler.service;

import java.util.List;

public interface AnalysisService {
    List<AnalysisSchedule> getAnalysisSchedules();
    void executeAnalysis(String analysisId);
}
