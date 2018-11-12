package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

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
@NotNull
@JsonProperty("host")
private String host;
@NotNull
@JsonProperty("username")
private String username;
@NotNull
@JsonProperty("password")
private String password;

@JsonProperty("url")
private String url;
@NotNull
@JsonProperty("port")
private Integer port = 21;

@JsonProperty("batchSize")
private Integer batchSize = 10;
@NotNull
@JsonProperty("destinationLocation")
private String destinationLocation;

@JsonProperty("sourceLocation")
private String sourceLocation;

@JsonProperty("pattern")
private String pattern;

@JsonProperty("isLogging")
private Boolean isLogging = false;

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
@JsonProperty("username")
public String getUsername() {
	return username;
}
@JsonProperty("username")
public void setUsername(String username) {
	this.username = username;
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
@JsonProperty("port")
public Integer getPort() {
	return port;
}
@JsonProperty("port")
public void setPort(Integer port) {
	this.port = port;
}
@JsonProperty("destinationLocation")
public String getDestinationLocation() {
	return destinationLocation;
}

@JsonProperty("destinationLocation")
public void setDestinationLocation(String destinationLocation) {
	this.destinationLocation = destinationLocation;
}
@JsonProperty("host")
public String getHost() {
	return host;
}
@JsonProperty("host")
public void setHost(String host) {
	this.host = host;
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
@JsonProperty("pattern")
public String getPattern() {
	return pattern;
}
@JsonProperty("pattern")
public void setPattern(String pattern) {
	this.pattern = pattern;
}
@JsonProperty("batchSize")
public Integer getBatchSize() {
	return batchSize;
}
@JsonProperty("batchSize")
public void setBatchSize(Integer batchSize) {
	this.batchSize = batchSize;
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
