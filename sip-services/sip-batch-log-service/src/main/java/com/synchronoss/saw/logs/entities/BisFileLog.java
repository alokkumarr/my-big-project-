package com.synchronoss.saw.logs.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import io.swagger.annotations.ApiModelProperty;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "BIS_FILE_LOGS", catalog = "sip_bis", schema = "",
    indexes = {@Index(columnList = "BIS_FILE_NAME", name = "bis_file_name_index")})
public class BisFileLog implements Serializable {

  private static final long serialVersionUID = 26663931172032006L;

  @ApiModelProperty(value = "Unique identifier for log resource", dataType = "String")
  @Id
  @Column(name = "BIS_PID")
  String pid;
  @ApiModelProperty(value = "Unique identifier for route resource", dataType = "Long")
  @Column(name = "BIS_ROUTE_SYS_ID")
  long routeSysId;
  @ApiModelProperty(value = "Unique identifier for channel resource", dataType = "Long")
  @Column(name = "BIS_CHANNEL_SYS_ID")
  long channelSysId;
  @ApiModelProperty(value = "Indicates the channel type", dataType = "String",
      allowEmptyValue = false, allowableValues = "SFTP")
  @Column(name = "BIS_CHANNEL_TYPE")
  String channelType;

  @ApiModelProperty(value = "Indicates the file pattern being accepted by the route",
      dataType = "String", allowEmptyValue = false)
  @Column(name = "BIS_FILE_PATTERN")
  String filePattern;
  

  @ApiModelProperty(value = "Indicates the file pattern being accepted by the route",
      dataType = "String", allowEmptyValue = false)
  @Column(name = "SOURCE")
  String source;

 

  @ApiModelProperty(value = "Indicates the file name at the source location", dataType = "String",
      allowEmptyValue = false)
  @Column(name = "BIS_FILE_NAME")
  String fileName;

  @ApiModelProperty(value = "Indicates the date received at the source location",
      dataType = "Date", allowEmptyValue = false)
  @Column(name = "BIS_ACTUAL_FILE_RCV_DATE")
  Date actualFileRecDate;

  @ApiModelProperty(value = "Indicates the file name at the destination location",
      dataType = "String", allowEmptyValue = false)
  @Column(name = "BIS_RECD_FILE_NAME")
  String recdFileName;

  @ApiModelProperty(value = "Indicates the file size at the destination location",
      dataType = "Long", allowEmptyValue = false)
  @Column(name = "BIS_RECD_FILE_SIZE_BYTES")
  Long recdFileSize;

  @ApiModelProperty(value = "Indicates the file status while downloading the file",
      dataType = "String", allowEmptyValue = false, allowableValues = "SUCCESS, FAILED, INPROGRESS")
  @Column(name = "BIS_FILE_VALID_STATUS")
  String mflFileStatus;

  @ApiModelProperty(value = "Indicates the status of the component process", dataType = "String",
      allowEmptyValue = false, allowableValues = "DATA_RECEIVED, DATA_REMOVED, HOST_NOT_REACHABLE")
  @Column(name = "BIS_PROCESS_STATE")
  String bisProcessState;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "MODIFIED_DATE")
  private Date modifiedDate;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "CREATED_DATE")
  private Date createdDate;

  @Column(name = "BIS_TRANSFER_START_TIME")
  private Date transferStartTime;

  @Column(name = "BIS_TRANSFER_END_TIME")
  private Date transferEndTime;

  @Column(name = "BIS_TRANSFER_DURATION")
  private Long transferDuration;

  @Column(name = "CHECKPOINT_TIMESTAMP")
  @Temporal(TemporalType.TIMESTAMP)
  private Date checkpointDate;
  
  @ManyToOne
  @JoinColumn(name="JOB_ID")
  private BisJobEntity job;
  
   
  @ApiModelProperty(value = "Indicates the status of the component process", dataType = "String",
  allowEmptyValue = false, allowableValues = "DATA_RECEIVED, DATA_REMOVED, HOST_NOT_REACHABLE")
  @Column(name = "STATE_REASON")
  private String reason;


  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public BisJobEntity getJob() {
    return job;
  }

  public void setJob(BisJobEntity job) {
    this.job = job;
  }

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

  public Date getTransferStartTime() {
    return transferStartTime;
  }

  public void setTransferStartTime(Date transferStartTime) {
    this.transferStartTime = transferStartTime;
  }

  public Date getTransferEndTime() {
    return transferEndTime;
  }

  public void setTransferEndTime(Date transferEndTime) {
    this.transferEndTime = transferEndTime;
  }

  public Long getTransferDuration() {
    return transferDuration;
  }

  public void setTransferDuration(Long transferDuration) {
    this.transferDuration = transferDuration;
  }


  @Temporal(TemporalType.TIMESTAMP)
  public Date getCheckpointDate() {
    return checkpointDate;
  }

  public void setCheckpointDate(Date checkpointDate) {
    this.checkpointDate = checkpointDate;
  }
  
  public String getSource() {
		return source;
  }

  public void setSource(String source) {
	  this.source = source;
  }

}
