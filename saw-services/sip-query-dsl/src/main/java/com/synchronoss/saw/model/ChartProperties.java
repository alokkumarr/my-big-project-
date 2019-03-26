package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartProperties {
  @JsonProperty("isInverted")
  Boolean isInverted;

  @JsonProperty("legend")
  JsonNode legend;

  @JsonProperty("isInverted")
  public Boolean isInverted() {
    return isInverted;
  }

  @JsonProperty("isInverted")
  public void setInverted(Boolean inverted) {
    isInverted = inverted;
  }

  @JsonProperty("legend")
  public JsonNode getLegend() {
    return legend;
  }

  @JsonProperty("legend")
  public void setLegend(JsonNode legend) {
    this.legend = legend;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("isInverted", isInverted)
      .append("legend", legend)
      .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(isInverted)
        .append(legend)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof ChartProperties) == false) {
      return false;
    }

    ChartProperties rhs = (ChartProperties)other;
    return new EqualsBuilder()
        .append(isInverted, rhs.isInverted)
        .append(legend, rhs.legend)
        .isEquals();
  }
}
