package com.synchronoss.saw.scheduler.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerServiceTest {
    /* The starting time point for tests (must be a Monday) */
    private static final String START_INSTANT = "2017-01-02T00:00:00Z";
    private AnalysisServiceMock analysisService;
    private SchedulerStoreMock schedulerStore;
    private SchedulerService schedulerService;
    private Clock clock;

    @Before
    public void setUp() {
        analysisService = new AnalysisServiceMock();
        schedulerStore = new SchedulerStoreMock();
        clock = Clock.fixed(Instant.parse(START_INSTANT), ZoneOffset.UTC);
        schedulerService = new SchedulerServiceImpl(
            analysisService, schedulerStore, clock);
    }

    @Test
    public void testNoSchedules() {
        /* Assert no executions if no schedules */
        processAndAssertExecutions(0);
    }

    @Test
    public void testAlreadyExecuted() {
        addAnalysisSchedule("daily", 1, false, false);
        /* Assert no executions before starting */
        assertThat(analysisService.getExecutions()).hasSize(0);
        /* Assert that analysis was executed according to schedule */
        processAndAssertExecutions(1);
        /* Assert that analysis was not executed a second time within
         * the same time period */
        processAndAssertExecutions(1);
    }

    @Test
    public void testDaily() {
        addAnalysisSchedule("daily", 1, false, false);
        /* Assert executed on first day */
        processAndAssertExecutions(1);
        /* Move the clock forward one day */
        moveClock(1);
        /* Assert exected on the next day again */
        processAndAssertExecutions(2);
    }

    @Test
    public void testDailyIntervalTwo() {
        addAnalysisSchedule("daily", 2, false, false);
        /* Assert executed on first day */
        processAndAssertExecutions(1);
        /* Assert not exected on the next day, because repeat interval
         * is two */
        moveClock(1);
        processAndAssertExecutions(1);
        /* Assert exected on the next day again */
        moveClock(1);
        processAndAssertExecutions(2);
    }

    @Test
    public void testWeeklyDaysOfWeek() {
        addAnalysisSchedule("weekly", 1, true, true);
        /* Assert executed on Monday of the first week */
        processAndAssertExecutions(1);
        /* Assert executed on Tuesday of the first week */
        moveClock(1);
        processAndAssertExecutions(2);
        /* Assert not executed on Wednesday of the first week */
        moveClock(1);
        processAndAssertExecutions(2);
        /* Assert executed again on Monday the next week, because
         * repeat interval is 1 */
        moveClock(5);
        processAndAssertExecutions(3);
    }

    @Test
    public void testWeeklyIntervalTwo() {
        addAnalysisSchedule("weekly", 2, true, false);
        /* Assert executed on Monday of the first week */
        processAndAssertExecutions(1);
        /* Assert not executed on Monday of the next week, because
         * repeat interval is 2 */
        moveClock(7);
        processAndAssertExecutions(1);
        /* Assert executed again on Monday the next week, because
         * repeat interval is 2 */
        moveClock(7);
        processAndAssertExecutions(2);
    }

    private void addAnalysisSchedule(
        String unit, int interval, boolean monday, boolean tuesday) {
        String ANALYSIS_ID = "123";
        analysisService.addAnalysisSchedule(
            ImmutableAnalysisSchedule.builder()
            .id(ANALYSIS_ID)
            .schedule(
                ImmutableAnalysisSchedule.Schedule.builder()
                .repeatUnit(unit)
                .repeatInterval(interval)
                .repeatOnDaysOfWeek(
                    ImmutableAnalysisSchedule.DaysOfWeek.builder()
                    .sunday(false)
                    .monday(monday)
                    .tuesday(tuesday)
                    .wednesday(false)
                    .thursday(false)
                    .friday(false)
                    .saturday(false)
                    .build())
                .build()).name("ReportTest")
                    .description("Description")
                    .metricName("Dispatch_Metrics")
                    .userFullName("system")
            .build());
    }

    private void processAndAssertExecutions(int executionsSize) {
        schedulerService.processSchedules();
        assertThat(analysisService.getExecutions()).hasSize(executionsSize);
    }

    private void moveClock(int days) {
        clock = Clock.offset(clock, Duration.ofDays(days));
        schedulerService = new SchedulerServiceImpl(
            analysisService, schedulerStore, clock);
    }
}

class AnalysisServiceMock implements AnalysisService {
    private List<AnalysisSchedule> analyses = new ArrayList<>();
    private List<String> executions = new ArrayList();

    public void addAnalysisSchedule(AnalysisSchedule analysis) {
        analyses.add(analysis);
    }

    public AnalysisSchedule[] getAnalysisSchedules() {
        return analyses.toArray(new AnalysisSchedule[] {});
    }

    public void executeAnalysis(String analysisId) {
        executions.add(analysisId);
    }

    public void scheduleDispatch(AnalysisSchedule analysis) {analyses.add(analysis); }

    public List<String> getExecutions() {
        return executions;
    }
}    

class SchedulerStoreMock implements SchedulerStore {
    private Map<String, String> store = new HashMap<>();

    public String getLastExecutedPeriodId(String analysisId) {
        return store.get(analysisId);
    }

    public void setLastExecutedPeriodId(
        String analysisId, String lastExecutedPeriodId) {
        store.put(analysisId, lastExecutedPeriodId);
    }

    public void close() {}
}
