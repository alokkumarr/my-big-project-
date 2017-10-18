package com.synchronoss.saw.scheduler.service;

interface SchedulerStore extends AutoCloseable {
    String getLastExecutedPeriodId(String analysisId);
    void setLastExecutedPeriodId(
        String analysisId, String lastExecutedPeriodId);
    void close();
}
