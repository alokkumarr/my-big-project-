package com.synchronoss.saw.alert.modal;

import java.util.Date;

public class AlertNotificationLog {

  String notificationId;

  Long alertTriggerSysId;

  Boolean isNotified;

  Date createdTime;

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

  public Boolean getIsNotified() {
    return isNotified;
  }

  public void setIsNotified(Boolean isNotified) {
    this.isNotified = isNotified;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }
}
