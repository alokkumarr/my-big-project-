package com.synchronoss.saw.scheduler.service;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.antlr.v4.runtime.misc.Nullable;
import org.immutables.value.Value;


@Value.Immutable
@JsonSerialize(as = ImmutableDispatchBean.class)
@JsonDeserialize(as = ImmutableDispatchBean.class)
interface DispatchBean {
    @Nullable
    String emailList();

    String fileType();

    @Nullable
    String ftp();

    String name();

    @Nullable
    String description();

    String metricName();

    String userFullName();

    String publishedTime();
}
