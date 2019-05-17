package com.synchronoss.saw.batch.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApiModel("This model payload holds the details to intiate & check status "
    + "of the source & destination system.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BisDataMetaInfo {

  @ApiModelProperty(hidden = true)
  @JsonProperty("processId")
  private String processId;

  @ApiModelProperty(hidden = true)
  @JsonProperty("routeId")
  private Long routeId;

  @ApiModelProperty(hidden = true)
  @JsonProperty("channelId")
  private Long channelId;

  @ApiModelProperty(value = "Channel Type", allowableValues = "SFTP")
  @JsonProperty("channelType")
  private BisChannelType channelType = BisChannelType.SFTP;

  @ApiModelProperty(value = "Indicates the file name at the source location", dataType = "String",
      allowEmptyValue = false)
  @JsonProperty("actualDataName")
  private String actualDataName;

  @ApiModelProperty(value = "Indicates the status of the component process", dataType = "Long",
      allowEmptyValue = false, allowableValues = "DATA_RECEIVED, DATA_REMOVED, HOST_NOT_REACHABLE")
  @JsonProperty("componentState")
  private String componentState;

  @ApiModelProperty(value = "Indicates the file name at the destination location",
      dataType = "String", allowEmptyValue = false)
  @JsonProperty("receivedDataName")
  private String receivedDataName;

  @ApiModelProperty(value = "Indicates the file size at the destination location",
      dataType = "Long", allowEmptyValue = false)
  @JsonProperty("dataSizeInBytes")
  private Long dataSizeInBytes;

  @ApiModelProperty(value = "Indicates the file status while downloading the file",
      dataType = "String", allowEmptyValue = false, allowableValues = "SUCCESS, FAILED, INPROGRESS")
  @JsonProperty("processState")
  private String processState;

  @JsonProperty("reasonCode")
  private String reasonCode;

  @ApiModelProperty(value = "Indicates the file pattern being accepted by the route",
      dataType = "String", allowEmptyValue = false)
  @JsonProperty("filePattern")
  private String filePattern;

  @ApiModelProperty(value = "Indicates the actual date when file has been received",
      dataType = "String", allowEmptyValue = false)
  @JsonProperty("actualReceiveDate")
  private Date actualReceiveDate;
  
  @JsonProperty("fileTransferStartTime")
  private Date fileTransferStartTime;
  
  @JsonProperty("fileTransferEndTime")
  private Date fileTransferEndTime;
  
  @JsonProperty("fileTransferDuration")
  private Long fileTransferDuration;
  
  @JsonProperty("source")
  private String source;
  
  @JsonProperty("jobId")
  private Long jobId;





  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  public Date getFileTransferStartTime() {
    return fileTransferStartTime;
  }

  public void setFileTransferStartTime(Date fileTransferStartTime) {
    this.fileTransferStartTime = fileTransferStartTime;
  }

  public Date getFileTransferEndTime() {
    return fileTransferEndTime;
  }

  public void setFileTransferEndTime(Date fileTransferEndTime) {
    this.fileTransferEndTime = fileTransferEndTime;
  }

  public Long getFileTransferDuration() {
    return fileTransferDuration;
  }

  public void setFileTransferDuration(Long fileTransferDuration) {
    this.fileTransferDuration = fileTransferDuration;
  }

  @JsonIgnore
  private String destinationPath;

  @JsonProperty("actualReceiveDate")
  public Date getActualReceiveDate() {
    return actualReceiveDate;
  }

  @JsonProperty("actualReceiveDate")
  public void setActualReceiveDate(Date actualReceiveDate) {
    this.actualReceiveDate = actualReceiveDate;
  }

  @JsonProperty("filePattern")
  public String getFilePattern() {
    return filePattern;
  }

  @JsonProperty("filePattern")
  public void setFilePattern(String pattern) {
    this.filePattern = pattern;
  }

  @JsonProperty("reasonCode")
  public String getReasonCode() {
    return reasonCode;
  }

  @JsonProperty("reasonCode")
  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  @JsonProperty("processId")
  public String getProcessId() {
    return processId;
  }

  @JsonProperty("processId")
  public void setProcessId(String processId) {
    this.processId = processId;
  }

  @JsonProperty("routeId")
  public Long getRouteId() {
    return routeId;
  }

  @JsonProperty("routeId")
  public void setRouteId(Long routeId) {
    this.routeId = routeId;
  }

  @JsonProperty("channelId")
  public Long getChannelId() {
    return channelId;
  }

  @JsonProperty("channelId")
  public void setChannelId(Long channelId) {
    this.channelId = channelId;
  }

  @JsonProperty("channelType")
  public BisChannelType getChannelType() {
    return channelType;
  }

  @JsonProperty("channelType")
  public void setChannelType(BisChannelType channelType) {
    this.channelType = channelType;
  }

  @JsonProperty("actualDataName")
  public String getActualDataName() {
    return actualDataName;
  }

  @JsonProperty("actualDataName")
  public void setActualDataName(String actualDataName) {
    this.actualDataName = actualDataName;
  }

  @JsonProperty("receivedDataName")
  public String getReceivedDataName() {
    return receivedDataName;
  }

  @JsonProperty("receivedDataName")
  public void setReceivedDataName(String receivedDataName) {
    this.receivedDataName = receivedDataName;
  }

  @JsonProperty("dataSizeInBytes")
  public Long getDataSizeInBytes() {
    return dataSizeInBytes;
  }

  @JsonProperty("dataSizeInBytes")
  public void setDataSizeInBytes(Long dataSizeInBytes) {
    this.dataSizeInBytes = dataSizeInBytes;
  }

  @JsonProperty("processState")
  public String getProcessState() {
    return processState;
  }

  @JsonProperty("processState")
  public void setProcessState(String processState) {
    this.processState = processState;
  }

  @JsonProperty("componentState")
  public String getComponentState() {
    return componentState;
  }

  @JsonProperty("componentState")
  public void setComponentState(String componentState) {
    this.componentState = componentState;
  }

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public String getDestinationPath() {
    return destinationPath;
  }

  public void setDestinationPath(String destinationPath) {
    this.destinationPath = destinationPath;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


}
