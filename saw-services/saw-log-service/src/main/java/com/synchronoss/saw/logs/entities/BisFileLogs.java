package com.synchronoss.saw.logs.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "SIP_LOGS", name = "BIS_FILE_LOGS")
public class BisFileLogs {

  @Id
  @Column(name = "MFL_PID")
  String pid;

  @Column(name = "BIS_ROUTE_SYS_ID")
  long routeSysId;

  @Column(name = "BIS_CHANNEL_SYS_ID")
  long channelSysId;

  @Column(name = "BIS_CHANNEL_TYPE")
  String channelType;

  @Column(name = "BIS_FILE_PATTERN")
  String filePattern;

  @Column(name = "BIS_FILE_NAME")
  String fileName;

  @Column(name = "BIS_ACTUAL_FILE_RCV_DATE")
  Date actualFileRecDate;

  @Column(name = "BIS_RECD_FILE_NAME")
  String recdFileName;

  @Column(name = "BIS_RECD_FILE_SIZE_BYTES")
  Double recdFileSize;

  @Column(name = "MFL_FILE_VALID_STATUS")
  String mflFileStatus;

  @Column(name = "DATE_OF_ENTRY")
  Date dateOfEntry;

  @Column(name = "DATE_OF_CHANGE")
  Date dateOfChange;

  @Column(name = "MFLD_PROCESS_STATE")
  String mfldProcessDate;

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

  public long getBisChannelSysId() {
    return channelSysId;
  }

  public void setBisChannelSysId(long bisChannelSysId) {
    this.channelSysId = bisChannelSysId;
  }

  public String getBisChannelType() {
    return channelType;
  }

  public void setBisChannelType(String bisChannelType) {
    this.channelType = bisChannelType;
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

  public Double getRecdFileSize() {
    return recdFileSize;
  }

  public void setRecdFileSize(Double recdFileSize) {
    this.recdFileSize = recdFileSize;
  }

  public String getMflFileStatus() {
    return mflFileStatus;
  }

  public void setMflFileStatus(String mflFileStatus) {
    this.mflFileStatus = mflFileStatus;
  }

  public Date getDateOfEntry() {
    return dateOfEntry;
  }

  public void setDateOfEntry(Date dateOfEntry) {
    this.dateOfEntry = dateOfEntry;
  }

  public Date getDateOfChange() {
    return dateOfChange;
  }

  public void setDateOfChange(Date dateOfChange) {
    this.dateOfChange = dateOfChange;
  }

  public String getMfldProcessDate() {
    return mfldProcessDate;
  }

  public void setMfldProcessDate(String mfldProcessDate) {
    this.mfldProcessDate = mfldProcessDate;
  }

}
