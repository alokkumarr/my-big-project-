package com.synchronoss.saw.scheduler.service;


import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.scheduler.modal.SchedulerJobDetail;
import java.util.List;

public interface AnalysisService {
    void executeDslAnalysis(String analysisId,String auth, List<Filter> filters);
    void scheduleDispatch(SchedulerJobDetail analysis);
}
