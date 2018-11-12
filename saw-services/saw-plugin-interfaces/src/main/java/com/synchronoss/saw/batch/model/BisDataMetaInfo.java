package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BisDataMetaInfo {
	@ApiModelProperty(hidden=true)
	@JsonProperty("processId")
	private String processId;
	
	@ApiModelProperty(hidden=true)
	@JsonProperty("routeId")
	private String routeId;
	
	@ApiModelProperty(hidden=true)
	@JsonProperty("channelId")
	private String channelId;
	
	@JsonProperty("channelType")
	private BisChannelType channelType = BisChannelType.SFTP;
	
	@JsonProperty("actualDataName")
	private String actualDataName;
	
	@JsonProperty("receivedDataName")
	private String receivedDataName;
	
	@JsonProperty("dataSizeInBytes")
	private Double dataSizeInBytes;
	
	@ApiModelProperty(hidden=true)
	@JsonProperty("dataValidStatus")
	private String dataValidStatus;
	
	@JsonProperty("processState")
	private String processState;
	
	@JsonProperty("processId")
	public String getProcessId() {
		return processId;
	}
	@JsonProperty("processId")
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	@JsonProperty("routeId")
	public String getRouteId() {
		return routeId;
	}
	@JsonProperty("routeId")
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	@JsonProperty("channelId")
	public String getChannelId() {
		return channelId;
	}
	@JsonProperty("channelId")
	public void setChannelId(String channelId) {
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
	public Double getDataSizeInBytes() {
		return dataSizeInBytes;
	}
	@JsonProperty("dataSizeInBytes")
	public void setDataSizeInBytes(Double dataSizeInBytes) {
		this.dataSizeInBytes = dataSizeInBytes;
	}
	@JsonProperty("dataValidStatus")
	public String getDataValidStatus() {
		return dataValidStatus;
	}
	@JsonProperty("dataValidStatus")
	public void setDataValidStatus(String dataValidStatus) {
		this.dataValidStatus = dataValidStatus;
	}
	@JsonProperty("processState")
	public String getProcessState() {
		return processState;
	}
	@JsonProperty("processState")
	public void setProcessState(String processState) {
		this.processState = processState;
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
