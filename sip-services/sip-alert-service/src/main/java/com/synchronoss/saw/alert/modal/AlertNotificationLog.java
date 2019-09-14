package com.synchronoss.saw.alert.modal;

import java.util.Date;

public class AlertNotificationLog {

  private String notificationSysId;

  private String alertTriggerSysId;

  private Boolean notifiedStatus;

  private Date createdTime;

  private String message;

  private String alertRuleName;

  private String attributeName;

  private Double thresholdValue;

  private AlertSeverity alertSeverity;

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

  /**
   * Gets alertRuleName.
   *
   * @return alertRuleName
   */
  public String getAlertRuleName() {
    return alertRuleName;
  }

  /** Sets alertRuleName. */
  public void setAlertRuleName(String alertRuleName) {
    this.alertRuleName = alertRuleName;
  }

  /**
   * Gets alertSeverity.
   *
   * @return value of alertSeverity
   */
  public AlertSeverity getAlertSeverity() {
    return alertSeverity;
  }

  /** Sets alertSeverity. */
  public void setAlertSeverity(AlertSeverity alertSeverity) {
    this.alertSeverity = alertSeverity;
  }

  /**
   * Gets thresholdValue.
   *
   * @return value of thresholdValue
   */
  public Double getThresholdValue() {
    return thresholdValue;
  }

  /** Sets thresholdValue. */
  public void setThresholdValue(Double thresholdValue) {
    this.thresholdValue = thresholdValue;
  }

  /**
   * Gets attributeName.
   *
   * @return value of attributeName
   */
  public String getAttributeName() {
    return attributeName;
  }

  /** Sets attributeName. */
  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }
}
