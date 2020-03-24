package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class NotificationSubscriber {
  @JsonProperty("subscriberId")
  private String subscriberId;

  @JsonProperty("alertRulesSysId")
  private String alertRulesSysId;

  @JsonProperty("subscriberName")
  private String subscriberName;

  @JsonProperty("channelType")
  @ApiModelProperty(notes = "SIP supported Channel Types", name = "channelType")
  private NotificationChannelType channelType;

  @JsonProperty("channelValue")
  private String channelValue;

  @JsonProperty("customerCode")
  private String customerCode;

  @JsonProperty("active")
  private Boolean active;

  @JsonProperty("createdTime")
  private Date createdTime;

  @JsonProperty("modifiedTime")
  private Date modifiedTime;

  @JsonProperty("subscriberId")
  public String getSubscriberId() {
    return subscriberId;
  }

  @JsonProperty("subscriberId")
  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  @JsonProperty("alertRulesSysId")
  public String getAlertRulesSysId() {
    return alertRulesSysId;
  }

  @JsonProperty("alertRulesSysId")
  public void setAlertRulesSysId(String alertRulesSysId) {
    this.alertRulesSysId = alertRulesSysId;
  }

  @JsonProperty("subscriberName")
  public String getSubscriberName() {
    return subscriberName;
  }

  @JsonProperty("subscriberName")
  public void setSubscriberName(String subscriberName) {
    this.subscriberName = subscriberName;
  }

  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  @JsonProperty("active")
  public void setActive(Boolean active) {
    this.active = active;
  }

  @JsonProperty("createdTime")
  public Date getCreatedTime() {
    return createdTime;
  }

  @JsonProperty("createdTime")
  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  @JsonProperty("modifiedTime")
  public Date getModifiedTime() {
    return modifiedTime;
  }

  @JsonProperty("modifiedTime")
  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  @JsonProperty("channelType")
  public NotificationChannelType getChannelType() {
    return channelType;
  }

  @JsonProperty("channelType")
  public void setChannelType(NotificationChannelType channelType) {
    this.channelType = channelType;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("subscriberId", subscriberId)
        .append("alertRulesSysId", alertRulesSysId)
        .append("subscriberName", subscriberName)
        .append("channelType", channelType)
        .append("channelValue", channelValue)
        .append("customerCode", customerCode)
        .append("active", active)
        .append("createdTime", createdTime)
        .append("modifiedTime", modifiedTime)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    NotificationSubscriber that = (NotificationSubscriber) o;

    return new EqualsBuilder()
        .append(subscriberId, that.subscriberId)
        .append(alertRulesSysId, that.alertRulesSysId)
        .append(subscriberName, that.subscriberName)
        .append(channelType, that.channelType)
        .append(channelValue, that.channelValue)
        .append(customerCode, that.customerCode)
        .append(active, that.active)
        .append(createdTime, that.createdTime)
        .append(modifiedTime, that.modifiedTime)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(subscriberId)
        .append(alertRulesSysId)
        .append(subscriberName)
        .append(channelType)
        .append(channelValue)
        .append(customerCode)
        .append(active)
        .append(createdTime)
        .append(modifiedTime)
        .toHashCode();
  }
}
