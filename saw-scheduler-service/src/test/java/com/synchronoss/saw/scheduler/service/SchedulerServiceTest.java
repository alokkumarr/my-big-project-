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
    private static final String START_INSTANT = "2017-01-01T00:00:00Z";
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
    public void testEmptyAnalysisSchedules() {
        schedulerService.processSchedules("daily");
        assertThat(analysisService.getExecutions()).hasSize(0);
    }

    @Test
    public void testSimpleAnalysisSchedules() {
        AnalysisSchedule analysis = mockAnalysisSchedule();
        analysisService.addAnalysisSchedule(analysis);
        /* Assert no executions before starting */
        assertThat(analysisService.getExecutions()).hasSize(0);
        schedulerService.processSchedules("daily");
        /* Assert that analysis was executed according to schedule */
        assertThat(analysisService.getExecutions()).hasSize(1);
        schedulerService.processSchedules("daily");
        /* Assert that analysis was not executed a second time within
         * the same time period */
        assertThat(analysisService.getExecutions()).hasSize(1);
        /* Move the clock forward one day*/
        clock = Clock.offset(clock, Duration.ofDays(1));
        schedulerService = new SchedulerServiceImpl(
            analysisService, schedulerStore, clock);
        schedulerService.processSchedules("daily");
        /* Assert that analysis was executed again on the new day */
        assertThat(analysisService.getExecutions()).hasSize(2);
    }

    private AnalysisSchedule mockAnalysisSchedule() {
        String ANALYSIS_ID = "123";
        return ImmutableAnalysisSchedule.builder()
            .id(ANALYSIS_ID)
            .schedule(
                ImmutableAnalysisSchedule.Schedule.builder()
                .repeatUnit("daily")
                .repeatInterval(1)
                .repeatOnDaysOfWeek(
                    ImmutableAnalysisSchedule.DaysOfWeek.builder()
                    .sunday(false)
                    .monday(true)
                    .tuesday(false)
                    .wednesday(false)
                    .thursday(false)
                    .friday(false)
                    .saturday(false)
                    .build())
                .build())
            .build();
    };
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

    public List<String> getExecutions() {
        return executions;
    }
}    

class SchedulerStoreMock implements SchedulerStore {
    private Map<String, String> store = new HashMap<>();

    public String getLastExecutionId(String analysisId) {
        return store.get(analysisId);
    }

    public void setLastExecutionId(String analysisId, String lastExecutionId) {
        store.put(analysisId, lastExecutionId);
    }

    public void close() {}
}
