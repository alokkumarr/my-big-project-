package com.synchronoss.saw.model.geomap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Children {
  @JsonProperty("icon")
  String icon;

  @JsonProperty("label")
  String label;

  @JsonProperty("type")
  String type;

  @JsonProperty("icon")
  public String getIcon() {
    return icon;
  }

  @JsonProperty("icon")
  public void setIcon(String icon) {
    this.icon = icon;
  }

  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  @JsonProperty("label")
  public void setLabel(String label) {
    this.label = label;
  }

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("icon", icon)
        .append("label", label)
        .append("type", type)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(icon).append(label).append(type).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Children) == false) {
      return false;
    }

    Children rhs = (Children) other;
    return new EqualsBuilder()
        .append(icon, rhs.icon)
        .append(label, rhs.label)
        .append(type, rhs.type)
        .isEquals();
  }
}
