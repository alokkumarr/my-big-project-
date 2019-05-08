package com.synchronoss.saw.scheduler.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Analysis execution representation used in requests to Analysis
 * Service.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAnalysisExecution.class)
@JsonDeserialize(as = ImmutableAnalysisExecution.class)
interface AnalysisExecution {
    String type();
}
