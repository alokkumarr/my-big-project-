package com.synchronoss.saw.logs.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BIS_FILE_LOGS", catalog = "sip_bis", schema = "")
public class BisFileLog implements Serializable {

  private static final long serialVersionUID = 26663931172032006L;

  @Id
  @Column(name = "BIS_PID")
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
  Long recdFileSize;

  @Column(name = "BIS_FILE_VALID_STATUS")
  String mflFileStatus;

  @Column(name = "BIS_PROCESS_STATE")
  String bisProcessState;

  @Column(name = "MODIFIED_DATE")
  private Date modifiedDate;

  @Column(name = "CREATED_DATE")
  private Date createdDate;

  @Column(name = "CHECKPOINT_DATE")
  private Date checkpointDate;

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


  public Date getCreatedDate() {
    return createdDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getCheckpointDate() {
    return checkpointDate;
  }

  public void setCheckpointDate(Date checkpointDate) {
    this.checkpointDate = checkpointDate;
  }

}
