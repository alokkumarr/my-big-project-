package com.synchronoss.saw.logs.models;

import java.util.Date;

public interface JobDetails {
  Long getJobId();

  String getJobName();

  Date getStartTime();

  Date getEndTime();

  Date getDuration();

  String getJobStatus();

  long getTotalCount();

  long getSuccessCount();

  String getFilePattern();

  String getSourceType();

  Date getCreatedDate();

  String getCreatedBy();

  Date getUpdatedDate();

  String getUpdatedBy();

  String getChannelName();

  String getRouteName();
}
