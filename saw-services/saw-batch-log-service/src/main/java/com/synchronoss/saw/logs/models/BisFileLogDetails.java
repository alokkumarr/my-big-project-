package com.synchronoss.saw.logs.models;

import java.util.Date;

import javax.persistence.Column;

public class BisFileLogDetails {

  String pid;

  long routeSysId;

  long channelSysId;

  String channelType;

  String filePattern;

  String fileName;

  Date actualFileRecDate;

  String recdFileName;

  Long recdFileSize;

  String mflFileStatus;

  String bisProcessState;

  private Date modifiedDate;

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public long getRouteSysId() {
    return routeSysId;
  }

  public void setRouteSysId(long routeSysId) {
    this.routeSysId = routeSysId;
  }

  public long getChannelSysId() {
    return channelSysId;
  }

  public void setChannelSysId(long channelSysId) {
    this.channelSysId = channelSysId;
  }

  public String getChannelType() {
    return channelType;
  }

  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }

  public String getFilePattern() {
    return filePattern;
  }

  public void setFilePattern(String filePattern) {
    this.filePattern = filePattern;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Date getActualFileRecDate() {
    return actualFileRecDate;
  }

  public void setActualFileRecDate(Date actualFileRecDate) {
    this.actualFileRecDate = actualFileRecDate;
  }

  public String getRecdFileName() {
    return recdFileName;
  }

  public void setRecdFileName(String recdFileName) {
    this.recdFileName = recdFileName;
  }

  public Long getRecdFileSize() {
    return recdFileSize;
  }

  public void setRecdFileSize(Long recdFileSize) {
    this.recdFileSize = recdFileSize;
  }

  public String getMflFileStatus() {
    return mflFileStatus;
  }

  public void setMflFileStatus(String mflFileStatus) {
    this.mflFileStatus = mflFileStatus;
  }

  public String getBisProcessState() {
    return bisProcessState;
  }

  public void setBisProcessState(String bisProcessState) {
    this.bisProcessState = bisProcessState;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  private Date createdDate;

}
