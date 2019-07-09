package com.synchronoss.saw.logs.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

@ApiModel("This model payload holds the details to jobs in the system")
public class SipJobDetails implements Serializable {

  /** serialization id. */
  private static final long serialVersionUID = -3249526955895533260L;

  @ApiModelProperty(value = "Job id")
  @JsonProperty("jobId")
  private Long jobId;

  @ApiModelProperty(value = "name of job")
  @JsonProperty("jobName")
  private String jobName;

  @ApiModelProperty(value = "Time at which job started")
  @JsonProperty("startTime")
  private Date startTime;

  @ApiModelProperty(value = "Time at which job ended")
  @JsonProperty("endTime")
  private Date endTime;

  @ApiModelProperty(value = "Current status of the job")
  @JsonProperty("jobStatus")
  private String jobStatus;

  @ApiModelProperty(value = "count of files per job")
  @JsonProperty("totalCount")
  private long totalCount;

  @ApiModelProperty(value = "total count of files successfully ingested")
  @JsonProperty("successCount")
  private long successCount;

  @ApiModelProperty(value = "Extensions, Pattern could be of wildcard expressions")
  @JsonProperty("filePattern")
  private String filePattern;

  @ApiModelProperty(value = "JobType/ChannelType")
  @JsonProperty("sourceType")
  private String sourceType;

  @ApiModelProperty(value = "System time at which job has created")
  @JsonProperty("createdDate")
  private Date createdDate;

  @ApiModelProperty(value = "The user who has created the job")
  @JsonProperty("createdBy")
  private String createdBy;

  @ApiModelProperty(value = "System time at which job has updated")
  @JsonProperty("updatedDate")
  private Date updatedDate;

  @ApiModelProperty(value = "The user who has updated the job")
  @JsonProperty("updatedBy")
  private String updatedBy;

  @ApiModelProperty(value = "Name given to the channel(By user)")
  @JsonProperty("channelName")
  private String channelName;

  @ApiModelProperty(value = "Name given to the route(By user)")
  @JsonProperty("routeName")
  private String routeName;

  @JsonProperty("jobId")
  public Long getJobId() {
    return jobId;
  }

  @JsonProperty("jobId")
  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  @JsonProperty("jobName")
  public String getJobName() {
    return jobName;
  }

  @JsonProperty("jobName")
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  @JsonProperty("startTime")
  public Date getStartTime() {
    return startTime;
  }

  @JsonProperty("startTime")
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @JsonProperty("endTime")
  public Date getEndTime() {
    return endTime;
  }

  @JsonProperty("endTime")
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  @JsonProperty("jobStatus")
  public String getJobStatus() {
    return jobStatus;
  }

  @JsonProperty("jobStatus")
  public void setJobStatus(String jobStatus) {
    this.jobStatus = jobStatus;
  }

  @JsonProperty("totalCount")
  public long getTotalCount() {
    return totalCount;
  }

  @JsonProperty("totalCount")
  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }

  @JsonProperty("successCount")
  public long getSuccessCount() {
    return successCount;
  }

  @JsonProperty("successCount")
  public void setSuccessCount(long successCount) {
    this.successCount = successCount;
  }

  @JsonProperty("filePattern")
  public String getFilePattern() {
    return filePattern;
  }

  @JsonProperty("filePattern")
  public void setFilePattern(String filePattern) {
    this.filePattern = filePattern;
  }

  @JsonProperty("sourceType")
  public String getSourceType() {
    return sourceType;
  }

  @JsonProperty("sourceType")
  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  @JsonProperty("createdBy")
  public Date getCreatedDate() {
    return createdDate;
  }

  @JsonProperty("createdBy")
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }

  @JsonProperty("createdBy")
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  @JsonProperty("updatedDate")
  public Date getUpdatedDate() {
    return updatedDate;
  }

  @JsonProperty("updatedDate")
  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }

  @JsonProperty("updatedBy")
  public String getUpdatedBy() {
    return updatedBy;
  }

  @JsonProperty("updatedBy")
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  @JsonProperty("channelName")
  public String getChannelName() {
    return channelName;
  }

  @JsonProperty("channelName")
  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  @JsonProperty("routeName")
  public String getRouteName() {
    return routeName;
  }

  @JsonProperty("routeName")
  public void setRouteName(String routeName) {
    this.routeName = routeName;
  }
}
