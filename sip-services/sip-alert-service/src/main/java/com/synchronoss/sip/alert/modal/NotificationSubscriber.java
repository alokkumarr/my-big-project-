package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class NotificationSubscriber {
  @JsonProperty("subscriberId")
  private String subscriberId;

  @JsonProperty("alertRulesSysId")
  private String alertRulesSysId;

  @JsonProperty("subscriberName")
  private String subscriberName;

  @JsonProperty("channelValue")
  private String channelValue;

  @JsonProperty("active")
  private Boolean active;

  @JsonProperty("createdTime")
  private Date createdTime;

  @JsonProperty("modifiedTime")
  private Date modifiedTime;

  @JsonProperty("NotificationChannel")
  @ApiModelProperty(notes = "SIP supported Channel Types", name = "NotificationChannel")
  private NotificationChannel notificationChannel;

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

  @JsonProperty("channelValue")
  public String getChannelValue() {
    return channelValue;
  }

  @JsonProperty("channelValue")
  public void setChannelValue(String channelValue) {
    this.channelValue = channelValue;
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

  @JsonProperty("NotificationChannel")
  public NotificationChannel getNotificationChannel() {
    return notificationChannel;
  }

  @JsonProperty("NotificationChannel")
  public void setNotificationChannel(
      NotificationChannel notificationChannel) {
    this.notificationChannel = notificationChannel;
  }

  @Override
  public String toString() {
    return "NotificationSubscriber{" +
        "subscriberId='" + subscriberId + '\'' +
        ", alertRulesSysId='" + alertRulesSysId + '\'' +
        ", subscriberName='" + subscriberName + '\'' +
        ", channelValue='" + channelValue + '\'' +
        ", active=" + active +
        ", createdTime=" + createdTime +
        ", modifiedTime=" + modifiedTime +
        ", notificationChannel=" + notificationChannel +
        '}';
  }
}
