package com.synchronoss.saw.alert.modal;

import java.util.Date;

public class AlertNotificationLog {

  String notificationId;

  Long alertTriggerSysId;

  Boolean notified;

  Date createdTime;

  String message;

  public String getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public Long getAlertTriggerSysId() {
    return alertTriggerSysId;
  }

  public void setAlertTriggerSysId(Long alertTriggerSysId) {
    this.alertTriggerSysId = alertTriggerSysId;
  }

  public Boolean getNotified() {
    return notified;
  }

  public void setNotified(Boolean notified) {
    this.notified = notified;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
