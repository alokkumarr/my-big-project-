package com.synchronoss.saw.alert.modal;

import java.util.Date;

public class AlertNotificationLog {

  private String notificationSysId;

  private String alertTriggerSysId;

  private Boolean notifiedStatus;

  private Date createdTime;

  private String message;

  /**
   * Gets notificationSysId.
   *
   * @return notificationSysId
   */
  public String getNotificationSysId() {
    return notificationSysId;
  }

  /** Sets notificationSysId. */
  public void setNotificationSysId(String notificationSysId) {
    this.notificationSysId = notificationSysId;
  }

  /**
   * Gets alertTriggerSysId.
   *
   * @return alertTriggerSysId
   */
  public String getAlertTriggerSysId() {
    return alertTriggerSysId;
  }

  /** Sets alertTriggerSysId. */
  public void setAlertTriggerSysId(String alertTriggerSysId) {
    this.alertTriggerSysId = alertTriggerSysId;
  }

  /**
   * Gets notifiedStatus.
   *
   * @return notifiedStatus
   */
  public Boolean getNotifiedStatus() {
    return notifiedStatus;
  }

  /** Sets notifiedStatus. */
  public void setNotifiedStatus(Boolean notifiedStatus) {
    this.notifiedStatus = notifiedStatus;
  }

  /**
   * Gets createdTime.
   *
   * @return createdTime
   */
  public Date getCreatedTime() {
    return createdTime;
  }

  /** Sets createdTime. */
  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  /**
   * Gets message.
   *
   * @return message
   */
  public String getMessage() {
    return message;
  }

  /** Sets message. */
  public void setMessage(String message) {
    this.message = message;
  }
}
