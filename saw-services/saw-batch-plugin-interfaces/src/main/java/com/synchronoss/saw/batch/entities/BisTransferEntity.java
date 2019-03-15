package com.synchronoss.saw.batch.entities;

import io.swagger.annotations.ApiModel;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@ApiModel(value = "SIP_BIS_TRANSFER", 
    description = "Model for storing sip transfer details")
@Entity(name = "sip transfer")
@Table(name = "SIP_BIS_TRANSFER", catalog = "sip_bis", schema = "")
public class BisTransferEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "TRANSFER_ID", nullable = false)
  @Id
  private long transferId;
  @Column(name = "JOB_ID")
  private long jobId;
  @Column(name = "FILE_NAME")
  private String fileName;
  @Column(name = "FILE_PATH")
  private String filePath;
  @Column(name = "START_TIME")
  private Date startTime;
  @Column(name = "END_TIME")
  private Date endTime;
  @Column(name = "DURATION")
  private long duration;
  @Column(name = "STATUS")
  private String status;
  @Column(name = "FILE_PATTERN")
  private String filePattern;
  @Column(name = "TARGET_FILE_PATH")
  private String targetFilePath;
  @Column(name = "CREATED_TIME")
  private Date createdTime;
  @Column(name = "UPDATED_TIME")
  private Date updatedTime;
  @Column(name = "STATE_REASON")
  private String reason;

  public long getTransferId() {
    return transferId;
  }

  public void setTransferId(long transferId) {
    this.transferId = transferId;
  }

  public long getJobId() {
    return jobId;
  }

  public void setJobId(long jobId) {
    this.jobId = jobId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
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

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getFilePattern() {
    return filePattern;
  }

  public void setFilePattern(String filePattern) {
    this.filePattern = filePattern;
  }

  public String getTargetFilePath() {
    return targetFilePath;
  }

  public void setTargetFilePath(String targetFilePath) {
    this.targetFilePath = targetFilePath;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  public Date getUpdatedTime() {
    return updatedTime;
  }

  public void setUpdatedTime(Date updatedTime) {
    this.updatedTime = updatedTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
