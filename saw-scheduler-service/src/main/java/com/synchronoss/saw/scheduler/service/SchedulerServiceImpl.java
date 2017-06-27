package com.synchronoss.saw.scheduler.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SchedulerServiceImpl implements SchedulerService, CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private AnalysisService analysisService;
    private SchedulerStore schedulerStore;
    private Clock clock;

    public SchedulerServiceImpl(
        AnalysisService analysisService, SchedulerStore schedulerStore,
        Clock clock) {
        this.analysisService = analysisService;
        this.schedulerStore = schedulerStore;
        this.clock = clock;
    }

    public void run(String... args) {
        log.info("Starting");
        processSchedules("daily");
        processSchedules("weekly");
        log.info("Finished");
    }

    public void processSchedules(String type) {
        log.info("Starting to process schedules of type: {}", type);
        log.info("Type: {}", type);
        String executionId = getCurrentExecutionId(type);
        log.info("Execution ID: {}", executionId);
        /* Get the list of analyses that have a schedule for the given
         * execution type, for example hourly or daily, from the
         * Analysis Service */
        AnalysisSchedule[] analyses = analysisService.getAnalysisSchedules();
        log.info("Processing analyses");
        for (AnalysisSchedule analysis : analyses) {
            processAnalysis(schedulerStore, executionId, analysis);
        }
        log.info("Finished processing analyses");
    }

    private void processAnalysis(
        SchedulerStore store, String executionId, AnalysisSchedule analysis) {
        String analysisId = analysis.id();
        log.info("Process analysis: {}", analysisId);
        /* Get the last execution ID from the persistent store */
        String lastExecutionId = store.getLastExecutionId(analysisId);
        boolean execute = false;
        log.info("Last execution ID: {}",
                 lastExecutionId == null ? "not found" : lastExecutionId);
        if (executionId == null) {
            /* If analysis has never been executed before, execute it
             * for the first time now */
            execute = true;
            log.info("Execute: {}, because never executed before", execute);
        } else {
            /* If analysis has previously been executed, execute it
             * only if the current invocation is for a new period.
             * This mechanism of comparing execution IDs ensures that
             * an analysis is executed only once within each
             * period.  */
            execute = (!executionId.equals(lastExecutionId));
            log.info("Execute: {}, by comparing execution IDs", execute);
        }
        if (execute) {
            /* Post execute request to Analysis Service */
            analysisService.executeAnalysis(analysisId);
            /* Store the last execution ID to prevent from executing a
             * further times during the same period */
            log.info("Set last execution ID: {}", executionId);
            store.setLastExecutionId(analysisId, executionId);
        }
    }

    /**
     * Calculate an execution ID that identifies the current period
     * which can be used to ensure that an analysis is only executed
     * once within the period
     */
    private String getCurrentExecutionId(String type) {
        String pattern = null;
        if (type.equals("daily")) {
            pattern = "yyyyMMdd";
        } else if (type.equals("hourly")) {
            pattern = "yyyyMMddHH";
        } else {
            throw new IllegalArgumentException(
                "Unknown execution type: " + type);
        }
        ZonedDateTime date = ZonedDateTime.now(clock);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return String.format(
            "%s-%s", type.toUpperCase(), date.format(formatter));
    }
}
