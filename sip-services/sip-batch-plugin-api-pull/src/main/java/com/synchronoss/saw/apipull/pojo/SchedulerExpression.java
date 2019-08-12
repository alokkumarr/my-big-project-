package com.synchronoss.saw.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"cronexp", "activeTab", "activeRadio", "startDate", "endDate", "timezone"})
public class SchedulerExpression {

  @JsonProperty("cronexp")
  private String cronexp;

  @JsonProperty("activeTab")
  private String activeTab;

  @JsonProperty("activeRadio")
  private String activeRadio;

  @JsonProperty("startDate")
  private DateTime startDate;

  @JsonProperty("endDate")
  private DateTime endDate;

  @JsonProperty("timezone")
  private String timezone;

  @JsonProperty("cronexp")
  public String getCronexp() {
    return cronexp;
  }

  @JsonProperty("cronexp")
  public void setCronexp(String cronexp) {
    this.cronexp = cronexp;
  }

  @JsonProperty("activeTab")
  public String getActiveTab() {
    return activeTab;
  }

  @JsonProperty("activeTab")
  public void setActiveTab(String activeTab) {
    this.activeTab = activeTab;
  }

  @JsonProperty("activeRadio")
  public String getActiveRadio() {
    return activeRadio;
  }

  @JsonProperty("activeRadio")
  public void setActiveRadio(String activeRadio) {
    this.activeRadio = activeRadio;
  }

  @JsonProperty("startDate")
  public DateTime getStartDate() {
    return startDate;
  }

  @JsonProperty("startDate")
  public void setStartDate(DateTime startDate) {
    this.startDate = startDate;
  }

  @JsonProperty("endDate")
  public DateTime getEndDate() {
    return endDate;
  }

  @JsonProperty("endDate")
  public void setEndDate(DateTime endDate) {
    this.endDate = endDate;
  }

  @JsonProperty("timezone")
  public String getTimezone() {
    return timezone;
  }

  @JsonProperty("timezone")
  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("cronexp", cronexp)
        .append("activeTab", activeTab)
        .append("activeRadio", activeRadio)
        .append("startDate", startDate)
        .append("endDate", endDate)
        .append("timezone", timezone)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(startDate)
        .append(timezone)
        .append(cronexp)
        .append(activeRadio)
        .append(endDate)
        .append(activeTab)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof SchedulerExpression) == false) {
      return false;
    }
    SchedulerExpression rhs = ((SchedulerExpression) other);
    return new EqualsBuilder()
        .append(startDate, rhs.startDate)
        .append(timezone, rhs.timezone)
        .append(cronexp, rhs.cronexp)
        .append(activeRadio, rhs.activeRadio)
        .append(endDate, rhs.endDate)
        .append(activeTab, rhs.activeTab)
        .isEquals();
  }
}
