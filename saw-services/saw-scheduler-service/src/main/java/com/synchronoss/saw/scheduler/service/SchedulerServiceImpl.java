package com.synchronoss.saw.scheduler.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class SchedulerServiceImpl
    implements SchedulerService, CommandLineRunner {
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
        processSchedules();
        log.info("Finished");
    }

    public void processSchedules() {
        log.info("Starting to process analyses");
        String periodId = getCurrentPeriodId();
        log.info("Current period ID: {}", periodId);
        /* Get the list of analyses that have a schedule for the given
         * execution type, for example hourly or daily, from the
         * Analysis Service */
        AnalysisSchedule[] analyses = analysisService.getAnalysisSchedules();
        log.info("Processing analyses");
        for (AnalysisSchedule analysis : analyses) {
            try {
                processAnalysis(analysis, periodId);
            } catch (RestClientResponseException e) {
                log.error("Error while processing analysis", e);
                log.error("Response body: {}", e.getResponseBodyAsString());
            }
        }
        log.info("Finished processing analyses");
    }

    private void processAnalysis(
        AnalysisSchedule analysis, String periodId) {
        String analysisId = analysis.id();
        log.info("Process analysis: {}", analysisId);
        /* If analysis has already been executed in this period, skip */
        if (analysisAlreadyExecutedInPeriod(analysisId, periodId)) {
            log.info("Analysis already executed in current period, skipping");
            return;
        }
        /* If analysis is scheduled for execution in this period,
         * execute */
        if (analysisScheduledForExecutionInPeriod(analysis, periodId)) {
            /* Send execute request to Analysis Service */
            log.info("Execute analysis");
            analysisService.executeAnalysis(analysisId);
            /* Store the last executed period ID to prevent from
             * executing multiple times during the same period */
            log.info("Set last executed period ID");
            schedulerStore.setLastExecutedPeriodId(analysisId, periodId);
        }
    }

    /**
     * Return true if analysis has already been executed within the
     * given time period
     */
    private boolean analysisAlreadyExecutedInPeriod(
        String analysisId, String periodId) {
        String lastPeriodId = schedulerStore.getLastExecutedPeriodId(analysisId);
        log.info("Last executed period ID: {}",
                 lastPeriodId == null ? "not found" : lastPeriodId);
        if (lastPeriodId == null) {
            /* If analysis has never been executed before, execute it
             * for the first time now */
            log.info("Already executed: false, because not executed before");
            return false;
        }
        boolean alreadyExecuted = lastPeriodId.equals(periodId);
        log.info("Already executed: {}, by comparing last executed " +
                 "period ID", alreadyExecuted);
        return alreadyExecuted;
    }

    /**
     * Return true if analysis should be executed in the given time
     * period according to its schedule
     */
    private boolean analysisScheduledForExecutionInPeriod(
        AnalysisSchedule analysis, String periodId) {
        AnalysisSchedule.Schedule schedule = analysis.schedule();
        String repeatUnit = schedule.repeatUnit().toLowerCase();
        if (repeatUnit.equals("daily")) {
            return daysSinceEpoch() % schedule.repeatInterval() == 0;
        } else if (repeatUnit.equals("weekly")) {
            if (weeksSinceEpoch() % schedule.repeatInterval() == 0) {
                ZonedDateTime zdt = ZonedDateTime.now(clock);
                AnalysisSchedule.DaysOfWeek days =
                    schedule.repeatOnDaysOfWeek();
                switch (zdt.getDayOfWeek()) {
                case SUNDAY: return days.sunday();
                case MONDAY: return days.monday();
                case TUESDAY: return days.tuesday();
                case WEDNESDAY: return days.wednesday();
                case THURSDAY: return days.thursday();
                case FRIDAY: return days.friday();
                case SATURDAY: return days.saturday();
                default:
                    throw new RuntimeException("Unknown day of week");
                }
            }
            return false;
        } else {
            log.warn("Unknown repeat unit: {}", schedule.repeatUnit());
            return false;
        }
    }

    /**
     * Get time period ID for the current period
     * 
     * The period ID is used to track when an analysis was last
     * executed to ensure that it is only executed once within each
     * period.  Currently periods are one day long.
     */
    private String getCurrentPeriodId() {
        return String.format("DAY-%d", daysSinceEpoch());
    }

    private long daysSinceEpoch() {
        return ChronoUnit.DAYS.between(getEpoch(), getNow());
    }

    private long weeksSinceEpoch() {
        return ChronoUnit.WEEKS.between(getEpoch(), getNow());
    }

    private Temporal getEpoch() {
        return Instant.ofEpochMilli(0).atZone(ZoneOffset.UTC);
    }

    private Temporal getNow() {
        return ZonedDateTime.now(clock);
    }
}
