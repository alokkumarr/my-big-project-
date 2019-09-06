package com.synchronoss.saw.rtis.model.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"topic", "queue"})
public class StreamsInfo {

  private static final long serialVersionUID = 1L;

  @JsonProperty("topic")
  private String topic;
  @JsonProperty("queue")
  private String queue;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("topic")
  public String getTopic() {
    return topic;
  }

  @JsonProperty("topic")
  public void setTopic(String topic) {
    this.topic = topic;
  }

  @JsonProperty("queue")
  public String getQueue() {
    return queue;
  }

  @JsonProperty("queue")
  public void setQueue(String queue) {
    this.queue = queue;
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
  public String toString() {
    return new ToStringBuilder(this).append("topic", topic).append("queue", queue)
        .append("additionalProperties", additionalProperties).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StreamsInfo)) {
      return false;
    }
    StreamsInfo that = (StreamsInfo) o;
    return getTopic().equals(that.getTopic()) && getQueue().equals(that.getQueue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTopic(), getQueue());
  }
}