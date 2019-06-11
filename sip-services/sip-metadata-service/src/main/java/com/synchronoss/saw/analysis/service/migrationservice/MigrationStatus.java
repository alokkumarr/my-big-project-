package com.synchronoss.saw.analysis.service.migrationservice;

import java.util.List;

/** This Bean is used to write all Analysis definition migration status to maprDB at once. */
public class MigrationStatus {
  List<MigrationStatusObject> migrationStatus;
  Integer totalAnalysis;
  Integer successCount;
  Integer failureCount;

  public List<MigrationStatusObject> getMigrationStatus() {
    return migrationStatus;
  }

  public void setMigrationStatus(List<MigrationStatusObject> migrationStatus) {
    this.migrationStatus = migrationStatus;
  }

  public Integer getTotalAnalysis() {
    return totalAnalysis;
  }

  public void setTotalAnalysis(Integer totalAnalysis) {
    this.totalAnalysis = totalAnalysis;
  }

  public Integer getSuccessCount() {
    return successCount;
  }

  public void setSuccessCount(Integer successCount) {
    this.successCount = successCount;
  }

  public Integer getFailureCount() {
    return failureCount;
  }

  public void setFailureCount(Integer failureCount) {
    this.failureCount = failureCount;
  }
}
