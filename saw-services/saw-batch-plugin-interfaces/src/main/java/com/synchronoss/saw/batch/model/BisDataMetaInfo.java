package com.synchronoss.saw.batch.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

  @JsonProperty("channelType")
  private BisChannelType channelType = BisChannelType.SFTP;

  @JsonProperty("actualDataName")
  private String actualDataName;

  @JsonProperty("componentState")
  private String componentState;

  @JsonProperty("receivedDataName")
  private String receivedDataName;

  @JsonProperty("dataSizeInBytes")
  private Long dataSizeInBytes;

  @JsonProperty("processState")
  private String processState;

  @JsonProperty("reasonCode")
  private String reasonCode;

  @JsonProperty("filePattern")
  private String filePattern;

  @JsonProperty("actualReceiveDate")
  private Date actualReceiveDate;

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



}
