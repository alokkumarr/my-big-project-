package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartOptions {
  @JsonProperty("isInverted")
  Boolean isInverted;

  @JsonProperty("legend")
  Object legend;

  @JsonProperty("chartTitle")
  String chartTitle;

  @JsonProperty("chartType")
  String chartType;

  @JsonProperty("isInverted")
  public Boolean isInverted() {
    return isInverted;
  }

  @JsonProperty("isInverted")
  public void setInverted(Boolean inverted) {
    isInverted = inverted;
  }

  @JsonProperty("legend")
  public Object getLegend() {
    return legend;
  }

  @JsonProperty("legend")
  public void setLegend(Object legend) {
    this.legend = legend;
  }

  @JsonProperty("chartTitle")
  public String getChartTitle() {
    return this.chartTitle;
  }

  @JsonProperty("chartTitle")
  public void setChartTitle(String chartTitle) {
    this.chartTitle = chartTitle;
  }

  @JsonProperty("chartType")
  public String getChartType() {
    return this.chartType;
  }

  @JsonProperty("chartType")
  public void setChartType(String chartType) {
    this.chartType = chartType;
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
    if ((other instanceof ChartOptions) == false) {
      return false;
    }

    ChartOptions rhs = (ChartOptions)other;
    return new EqualsBuilder()
        .append(isInverted, rhs.isInverted)
        .append(legend, rhs.legend)
        .isEquals();
  }
}
