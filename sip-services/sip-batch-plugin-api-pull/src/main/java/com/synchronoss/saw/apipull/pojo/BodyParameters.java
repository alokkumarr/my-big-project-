package com.synchronoss.saw.apipull.pojo;

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
  private Object content;

  /** (Required) */
  @JsonProperty("type")
  private String type;

  /** (Required) */
  @JsonProperty("content")
  public Object getContent() {
    return content;
  }

  /** (Required) */
  @JsonProperty("content")
  public void setContent(Object content) {
    this.content = content;
  }

  /** (Required) */
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  /** (Required) */
  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("content", content).append("type", type).toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(content).append(type).toHashCode();
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
    return new EqualsBuilder().append(content, rhs.content).append(type, rhs.type).isEquals();
  }
}
