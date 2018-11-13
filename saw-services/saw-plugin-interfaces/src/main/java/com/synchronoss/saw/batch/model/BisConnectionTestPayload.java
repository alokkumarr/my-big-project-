package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"username",
"password",
"port"
})
public class BisConnectionTestPayload {

@JsonProperty("hostName")
private String hostName;

@JsonProperty("userName")
private String userName;

@JsonProperty("password")
private String password;

@JsonProperty("url")
private String url;

@JsonProperty("portNo")
private Integer portNo = 21;

@JsonProperty("batchSize")
private Integer batchSize = 10;


@JsonProperty("destinationLocation")
private String destinationLocation;

@JsonProperty("sourceLocation")
private String sourceLocation;

@JsonProperty("channelId")
private String channelId;

@JsonProperty("isLogging")
private Boolean isLogging = false;

@JsonProperty("filePattern")
private String filePattern;

@JsonProperty("channelType")
private BisChannelType channelType = BisChannelType.SFTP;

@JsonProperty("channelType")
public BisChannelType getChannelType() {
	return channelType;
}
@JsonProperty("channelType")
public void setChannelType(BisChannelType channelType) {
	this.channelType = channelType;
}

@JsonProperty("isLogging")
public Boolean isLogging() {
	return isLogging;
}
@JsonProperty("isLogging")
public void setIsLogging(Boolean isLogging) {
	this.isLogging = isLogging;
}
@JsonProperty("userName")
public String getUserName() {
	return userName;
}
@JsonProperty("userName")
public void setUserName(String userName) {
	this.userName = userName;
}
@JsonProperty("password")
public String getPassword() {
	return password;
}
@JsonProperty("password")
public void setPassword(String password) {
	this.password = password;
}
@JsonProperty("url")
public String getUrl() {
	return url;
}
@JsonProperty("url")
public void setUrl(String url) {
	this.url = url;
}
@JsonProperty("portNo")
public Integer getPortNo() {
	return portNo;
}
@JsonProperty("portNo")
public void setPortNo(Integer portNo) {
	this.portNo = portNo;
}
@JsonProperty("destinationLocation")
public String getDestinationLocation() {
	return destinationLocation;
}

@JsonProperty("destinationLocation")
public void setDestinationLocation(String destinationLocation) {
	this.destinationLocation = destinationLocation;
}
@JsonProperty("hostName")
public String getHostName() {
	return hostName;
}
@JsonProperty("hostName")
public void setHostName(String hostName) {
	this.hostName = hostName;
}
@JsonProperty("sourceLocation")
public String getSourceLocation() {
	return sourceLocation;
}
@JsonProperty("sourceLocation")
public void setSourceLocation(String sourceLocation) {
	this.sourceLocation = sourceLocation;
}
public void setAdditionalProperties(Map<String, Object> additionalProperties) {
	this.additionalProperties = additionalProperties;
}
@JsonProperty("filePattern")
public String getFilePattern() {
	return filePattern;
}
@JsonProperty("filePattern")
public void setFilePattern(String filePattern) {
	this.filePattern = filePattern;
}
@JsonProperty("batchSize")
public Integer getBatchSize() {
	return batchSize;
}
@JsonProperty("batchSize")
public void setBatchSize(Integer batchSize) {
	this.batchSize = batchSize;
}
@JsonProperty("channelId")
public String getChannelId() {
	return channelId;
}
@JsonProperty("channelId")
public void setChannelId(String channelId) {
	this.channelId = channelId;
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
