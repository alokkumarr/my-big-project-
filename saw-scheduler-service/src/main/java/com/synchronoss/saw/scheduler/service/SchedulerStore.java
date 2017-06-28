package com.synchronoss.saw.scheduler.service;

interface SchedulerStore extends AutoCloseable {
    String getLastExecutionId(String analysisId);
    void setLastExecutionId(String analysisId, String lastExecutionId);
    void close();
}
