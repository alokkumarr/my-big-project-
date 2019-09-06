package com.synchronoss.saw.rtis.model.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "customerCode",
    "app_key",
    "streams_1",
    "streams_2",
    "class",
    "bootstrapServers",
    "batchSize",
    "keySerializer",
    "valueSerializer",
    "blockOnBufferFull",
    "timeoutMs",
    "eventUrl"
})
public class RtisConfiguration {

  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  private String id;
  @JsonProperty("customerCode")
  private String customerCode;
  @JsonProperty("app_key")
  private String appKey;
  @JsonProperty("streams_1")
  private List<StreamsInfo> primaryStreams = null;
  @JsonProperty("streams_2")
  private List<StreamsInfo> secondaryStreams = null;
  @JsonProperty("class")
  private String clazz;
  @JsonProperty("bootstrapServers")
  private String bootstrapServers;
  @JsonProperty("batchSize")
  private Integer batchSize;
  @JsonProperty("keySerializer")
  private String keySerializer;
  @JsonProperty("valueSerializer")
  private String valueSerializer;
  @JsonProperty("blockOnBufferFull")
  private Boolean blockOnBufferFull;
  @JsonProperty("timeoutMs")
  private Integer timeoutMs;
  @JsonProperty("eventUrl")
  private String eventUrl;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("customerCode")
  public String getCustomerCode() {
    return customerCode;
  }

  @JsonProperty("customerCode")
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  @JsonProperty("app_key")
  public String getAppKey() {
    return appKey;
  }

  @JsonProperty("app_key")
  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  @JsonProperty("streams_1")
  public List<StreamsInfo> getPrimaryStreams() {
    return primaryStreams;
  }

  @JsonProperty("streams_1")
  public void setPrimaryStreams(List<StreamsInfo> primaryStreams) {
    this.primaryStreams = primaryStreams;
  }

  @JsonProperty("streams_2")
  public List<StreamsInfo> getSecondaryStreams() {
    return secondaryStreams;
  }

  @JsonProperty("streams_2")
  public void setSecondaryStreams(List<StreamsInfo> secondaryStreams) {
    this.secondaryStreams = secondaryStreams;
  }

  @JsonProperty("class")
  public String getClazz() {
    return clazz;
  }

  @JsonProperty("class")
  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  @JsonProperty("bootstrapServers")
  public String getBootstrapServers() {
    return bootstrapServers;
  }

  @JsonProperty("bootstrapServers")
  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  @JsonProperty("batchSize")
  public Integer getBatchSize() {
    return batchSize;
  }

  @JsonProperty("batchSize")
  public void setBatchSize(Integer batchSize) {
    this.batchSize = batchSize;
  }

  @JsonProperty("keySerializer")
  public String getKeySerializer() {
    return keySerializer;
  }

  @JsonProperty("keySerializer")
  public void setKeySerializer(String keySerializer) {
    this.keySerializer = keySerializer;
  }

  @JsonProperty("valueSerializer")
  public String getValueSerializer() {
    return valueSerializer;
  }

  @JsonProperty("valueSerializer")
  public void setValueSerializer(String valueSerializer) {
    this.valueSerializer = valueSerializer;
  }

  @JsonProperty("blockOnBufferFull")
  public Boolean getBlockOnBufferFull() {
    return blockOnBufferFull;
  }

  @JsonProperty("blockOnBufferFull")
  public void setBlockOnBufferFull(Boolean blockOnBufferFull) {
    this.blockOnBufferFull = blockOnBufferFull;
  }

  @JsonProperty("timeoutMs")
  public Integer getTimeoutMs() {
    return timeoutMs;
  }

  @JsonProperty("timeoutMs")
  public void setTimeoutMs(Integer timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  @JsonProperty("eventUrl")
  public String getEventUrl() {
    return eventUrl;
  }

  @JsonProperty("eventUrl")
  public void setEventUrl(String eventUrl) {
    this.eventUrl = eventUrl;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RtisConfiguration)) {
      return false;
    }
    RtisConfiguration that = (RtisConfiguration) o;
    return getId().equals(that.getId())
        && getCustomerCode().equals(that.getCustomerCode())
        && getAppKey().equals(that.getAppKey())
        && getPrimaryStreams().equals(that.getPrimaryStreams())
        && getSecondaryStreams().equals(that.getSecondaryStreams())
        && getClazz().equals(that.getClazz())
        && getBootstrapServers().equals(that.getBootstrapServers())
        && getBatchSize().equals(that.getBatchSize())
        && getKeySerializer().equals(that.getKeySerializer())
        && getValueSerializer().equals(that.getValueSerializer())
        && getBlockOnBufferFull().equals(that.getBlockOnBufferFull())
        && getTimeoutMs().equals(that.getTimeoutMs())
        && getEventUrl().equals(that.getEventUrl())
        && Objects.equals(getAdditionalProperties(), that.getAdditionalProperties());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getCustomerCode(), getAppKey(), getPrimaryStreams(),
        getSecondaryStreams(), getClazz(), getBootstrapServers(), getBatchSize(),
        getKeySerializer(), getValueSerializer(), getBlockOnBufferFull(),
        getTimeoutMs(), getEventUrl(), getAdditionalProperties());
  }
}