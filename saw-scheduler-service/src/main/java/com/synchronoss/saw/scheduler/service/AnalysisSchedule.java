package com.synchronoss.saw.scheduler.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Analysis schedule view representation used when requesting
 * schedules from Analysis Service.
 */
@Value.Immutable
@Value.Enclosing
@JsonSerialize(as = ImmutableAnalysisSchedule.class)
@JsonDeserialize(as = ImmutableAnalysisSchedule.class)
interface AnalysisSchedule {
    String id();
    Schedule schedule();

    @Value.Immutable
    @JsonSerialize(as = ImmutableAnalysisSchedule.Schedule.class)
    @JsonDeserialize(as = ImmutableAnalysisSchedule.Schedule.class)
    interface Schedule {
        String repeatUnit();
        Integer repeatInterval();
        DaysOfWeek repeatOnDaysOfWeek();

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
