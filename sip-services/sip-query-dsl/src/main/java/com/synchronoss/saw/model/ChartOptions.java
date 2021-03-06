package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
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

  @JsonProperty("labelOptions")
  Object labelOptions;

  @JsonProperty("xAxis")
  Axis xAxis;

  @JsonProperty("yAxis")
  Axis yAxis;

  @JsonProperty("limitByAxis")
  private LimitByAxis limitByAxis;

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

  @JsonProperty("labelOptions")
  public Object getLabelOptions() {
    return this.labelOptions;
  }

  @JsonProperty("labelOptions")
  public void setLabelOptions(Object labelOptions) {
    this.labelOptions = labelOptions;
  }

  @JsonProperty("xAxis")
  public Axis getxAxis() {
    return this.xAxis;
  }

  @JsonProperty("xAxis")
  public void setxAxis(Axis xAxis) {
    this.xAxis = xAxis;
  }

  @JsonProperty("yAxis")
  public Axis getyAxis() {
    return this.yAxis;
  }

  @JsonProperty("yAxis")
  public void setyAxis(Axis yAxis) {
    this.yAxis = yAxis;
  }

  @JsonProperty("limitByAxis")
  public LimitByAxis getLimitByAxis() {
    return limitByAxis;
  }

  @JsonProperty("limitByAxis")
  public void setLimitByAxis(LimitByAxis limitByAxis) {
    this.limitByAxis = limitByAxis;
  }

  public enum LimitByAxis {
    DIMENSION("dimension"),
    GROUPBY("groupBy");
    private static final Map<String, LimitByAxis> CONSTANTS = new HashMap<>();
    public static EnumMap<LimitByAxis, String> axisEnumMap = new EnumMap<>(LimitByAxis.class);

    static {
      for (LimitByAxis c : values()) {
        CONSTANTS.put(c.value, c);
      }
        axisEnumMap.put(DIMENSION, "x");
        axisEnumMap.put(GROUPBY, "g");
    }

    private final String value;

    private LimitByAxis(String value) {
      this.value = value;
    }

    @JsonCreator
    public static LimitByAxis fromValue(String value) {
      LimitByAxis constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("isInverted", isInverted)
      .append("legend", legend)
      .append("chartTitle", chartTitle)
      .append("chartType", chartType)
      .append("labelOptions", labelOptions)
      .append("xAxis", xAxis)
      .append("yAxis", yAxis)
      .append("limitByAxis", limitByAxis)
      .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(isInverted)
        .append(legend)
        .append(chartTitle)
        .append(chartType)
        .append(labelOptions)
        .append(xAxis)
        .append(yAxis)
        .append(limitByAxis)
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
        .append(chartTitle, rhs.chartTitle)
        .append(chartType, rhs.chartType)
        .append(labelOptions, rhs.labelOptions)
        .append(xAxis, rhs.xAxis)
        .append(yAxis, rhs.yAxis)
        .append(limitByAxis,rhs.limitByAxis)
        .isEquals();
  }
}
