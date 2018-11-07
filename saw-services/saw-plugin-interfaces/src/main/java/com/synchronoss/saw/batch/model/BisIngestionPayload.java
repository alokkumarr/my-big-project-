package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.Message;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"entityId",
"log",
"transfer"
})
public class BisIngestionPayload {

@JsonProperty("entityId")
private Long entityId;

@JsonProperty("log")
private Boolean log = false;

@JsonProperty("transfer")
private Object transfer;

private Message<?>  messageSource;

@JsonProperty("channelType")
private ChannelType channelType = ChannelType.SFTP;

@JsonProperty("log")
public Boolean getLog() {
	return log;
}
@JsonProperty("log")
public void setLog(Boolean log) {
	this.log = log;
}
@JsonProperty("transfer")
public Object getTransfer() {
	return transfer;
}
@JsonProperty("transfer")
public void setTransfer(Object transfer) {
	this.transfer = transfer;
}
@JsonProperty("entityId")
public Long getEntityId() {
	return entityId;
}
@JsonProperty("entityId")
public void setEntityId(Long entityId) {
	this.entityId = entityId;
}

public Message<?> getMessageSource() {
	return messageSource;
}
public void setMessageSource(Message<?> messageSource) {
	this.messageSource = messageSource;
}

@JsonProperty("channelType")
public ChannelType getChannelType() {
	return channelType;
}
@JsonProperty("channelType")
public void setChannelType(ChannelType channelType) {
	this.channelType = channelType;
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
