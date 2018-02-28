package com.synchronoss.saw.scheduler.service;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.validation.constraints.Null;

@Value.Immutable
@JsonSerialize(as = ImmutableDispatchBean.class)
@JsonDeserialize(as = ImmutableDispatchBean.class)
 interface DispatchBean {
     String emailList();
     String fileType();
     String name();
     @Null
     String description();
     String metricName();
     String userFullName();
     String publishedTime();
}
