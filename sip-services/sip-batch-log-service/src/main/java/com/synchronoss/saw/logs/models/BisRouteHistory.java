package com.synchronoss.saw.logs.models;

import java.util.List;

public class BisRouteHistory {

  Long lastFireTime;
  Long nextFireTime;

  public Long getLastFireTime() {
    return lastFireTime;
  }

  public void setLastFireTime(Long lastFireTime) {
    this.lastFireTime = lastFireTime;
  }

  public Long getNextFireTime() {
    return nextFireTime;
  }

  public void setNextFireTime(Long nextFireTime) {
    this.nextFireTime = nextFireTime;
  }

  public List<BisFileLogDetails> getLogs() {
    return logs;
  }

  public void setLogs(List<BisFileLogDetails> logs) {
    this.logs = logs;
  }

  List<BisFileLogDetails> logs;
}
