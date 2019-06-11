package com.synchronoss.saw.logs.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;

@ApiModel(value="BIS_JOB", 
description="Model for storing sip job entity")
@Table(name = "BIS_JOB", catalog = "sip_bis", schema = "")
@Entity
public class BisJobEntity implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = -3249526955895533260L;
  
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "JOB_ID", nullable = false)
  @Id
  private Long jobId;
  @Column(name = "JOB_NAME")
  private String jobName;
  @Column(name = "START_TIME")
  private Date startTime;
  @Column(name = "END_TIME")
  private Date endTime;
  @Column(name = "JOB_STATUS")
  private String jobStatus;
  @Column(name = "TOTAL_FILES_COUNT")
  private long totalCount;
  
  @Column(name = "SUCCESS_FILES_COUNT")
  private long successCount;
  @Column(name = "FILE_PATTERN")
  private String filePattern;
  @Column(name = "CHANNEL_TYPE")
  private String channelType;
  @Column(name = "CREATED_DATE")
  private Date createdDate;
  @Column(name = "CREATED_BY")
  private String createdBy;
  @Column(name = "UPDATED_DATE")
  private Date updatedDate;
  @Column(name = "UPDATED_BY")
  private String updatedBy;
  
  @Column(name = "BIS_CHANNEL_SYS_ID")
  private Long bisChannelSysId;
  
  @Column(name = "BIS_ROUTE_SYS_ID")
  private Long bisRouteSysId;
  
  
  public Long getBisChannelSysId() {
	return bisChannelSysId;
  }
  public void setBisChannelSysId(Long bisChannelSysId) {
	this.bisChannelSysId = bisChannelSysId;
  }
  public Long getBisRouteSysId() {
	return bisRouteSysId;
  }
  public void setBisRouteSysId(Long bisRouteSysId) {
	this.bisRouteSysId = bisRouteSysId;
  }
  public Long getJobId() {
    return jobId;
  }
  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }
  public String getJobName() {
    return jobName;
  }
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }
  public Date getStartTime() {
    return startTime;
  }
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
  public Date getEndTime() {
    return endTime;
  }
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
  public String getJobStatus() {
    return jobStatus;
  }
  public void setJobStatus(String jobStatus) {
    this.jobStatus = jobStatus;
  }
  public long getTotalCount() {
    return totalCount;
  }
  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }
  public long getSuccessCount() {
    return successCount;
  }
  public void setSuccessCount(long successCount) {
    this.successCount = successCount;
  }
  public String getFilePattern() {
    return filePattern;
  }
  public void setFilePattern(String filePattern) {
    this.filePattern = filePattern;
  }
  public String getChannelType() {
    return channelType;
  }
  public void setChannelType(String jobType) {
    this.channelType = jobType;
  }
  public Date getCreatedDate() {
    return createdDate;
  }
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
  public String getCreatedBy() {
    return createdBy;
  }
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }
  public Date getUpdatedDate() {
    return updatedDate;
  }
  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }
  public String getUpdatedBy() {
    return updatedBy;
  }
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }
  
}
