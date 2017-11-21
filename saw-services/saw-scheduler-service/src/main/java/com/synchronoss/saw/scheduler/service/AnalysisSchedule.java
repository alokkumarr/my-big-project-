package com.synchronoss.saw.scheduler.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * Analysis schedule view representation used when requesting
 * schedules from Analysis Service
 */
@Value.Immutable
@Value.Enclosing
@JsonSerialize(as = ImmutableAnalysisSchedule.class)
@JsonDeserialize(as = ImmutableAnalysisSchedule.class)
interface AnalysisSchedule {
    String id();
    Schedule schedule();
    String name();
    String description();
    String metricName();
    String userFullName();

    @Value.Immutable
    @JsonSerialize(as = ImmutableAnalysisSchedule.Schedule.class)
    @JsonDeserialize(as = ImmutableAnalysisSchedule.Schedule.class)
    abstract class Schedule {
        abstract String repeatUnit();
        abstract Integer repeatInterval();
        abstract List<String> email();
        
        @Value.Default
        DaysOfWeek repeatOnDaysOfWeek() {
            return ImmutableAnalysisSchedule.DaysOfWeek.builder()
                .sunday(false)
                .monday(false)
                .tuesday(false)
                .wednesday(false)
                .thursday(false)
                .friday(false)
                .saturday(false)
                .build();
        }
        abstract List<String> emails();
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableAnalysisSchedule.DaysOfWeek.class)
    @JsonDeserialize(as = ImmutableAnalysisSchedule.DaysOfWeek.class)
    interface DaysOfWeek {
        boolean monday();
        boolean tuesday();
        boolean wednesday();
        boolean thursday();
        boolean friday();
        boolean saturday();
        boolean sunday();
    }
}

@Value.Immutable
@JsonSerialize(as = ImmutableAnalysisSchedulesResponse.class)
@JsonDeserialize(as = ImmutableAnalysisSchedulesResponse.class)
interface AnalysisSchedulesResponse {
    AnalysisSchedule[] analyses();
}
