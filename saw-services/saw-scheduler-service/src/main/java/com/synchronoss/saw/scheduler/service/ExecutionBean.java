package com.synchronoss.saw.scheduler.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableExecutionBean.class)
@JsonDeserialize(as = ImmutableExecutionBean.class)
interface ExecutionBean {
     String id();
     String finished();
     String status();
}

@Value.Immutable
@JsonSerialize(as = ImmutableExecutionResponse.class)
@JsonDeserialize(as = ImmutableExecutionResponse.class)
interface ExecutionResponse {
    ExecutionBean[] executions();
}
