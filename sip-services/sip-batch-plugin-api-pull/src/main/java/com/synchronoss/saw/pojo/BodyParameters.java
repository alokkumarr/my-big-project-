package com.synchronoss.saw.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"content"})
public class BodyParameters {

  /** (Required) */
  @JsonProperty("content")
  private String content;

  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /** (Required) */
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  /** (Required) */
  @JsonProperty("content")
  public void setContent(String content) {
    this.content = content;
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
    return new ToStringBuilder(this)
        .append("content", content)
        .append("additionalProperties", additionalProperties)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(content).append(additionalProperties).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof BodyParameters) == false) {
      return false;
    }
    BodyParameters rhs = ((BodyParameters) other);
    return new EqualsBuilder()
        .append(content, rhs.content)
        .append(additionalProperties, rhs.additionalProperties)
        .isEquals();
  }
}
