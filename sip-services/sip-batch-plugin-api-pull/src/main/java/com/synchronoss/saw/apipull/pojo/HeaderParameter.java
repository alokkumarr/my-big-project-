package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"key", "value", "datatype"})
public class HeaderParameter {

  @JsonProperty("key")
  private String key;

  @JsonProperty("value")
  private Object value;

  @JsonProperty("datatype")
  private String datatype = "string";

  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  @JsonProperty("key")
  public void setKey(String key) {
    this.key = key;
  }

  @JsonProperty("value")
  public Object getValue() {
    return value;
  }

  @JsonProperty("value")
  public void setValue(Object value) {
    this.value = value;
  }

  @JsonProperty("datatype")
  public String getDatatype() {
    return datatype;
  }

  @JsonProperty("datatype")
  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("key", key)
        .append("value", value)
        .append("datatype", datatype)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(value).append(datatype).append(key).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof HeaderParameter) == false) {
      return false;
    }
    HeaderParameter rhs = ((HeaderParameter) other);
    return new EqualsBuilder()
        .append(value, rhs.value)
        .append(datatype, rhs.datatype)
        .append(key, rhs.key)
        .isEquals();
  }
}
