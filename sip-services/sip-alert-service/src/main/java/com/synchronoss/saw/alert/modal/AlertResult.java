package com.synchronoss.saw.alert.modal;

import com.synchronoss.saw.model.SipQuery;

public class AlertResult {

  private String alertTriggerSysId;

  private String alertRulesSysId;

  private AlertState alertState;

  private AlertSeverity alertSeverity;

  private Long startTime;

  private Double thresholdValue;

  private Double metricValue;

  private Long lastUpdatedTime;

  private String alertRuleName;

  private String alertRuleDescription;

  private String categoryId;

  private SipQuery sipQuery;

  private Integer alertCount;

  /**
   * Gets alertTriggerSysId.
   *
   * @return value of alertTriggerSysId
   */
  public String getAlertTriggerSysId() {
    return alertTriggerSysId;
  }

  /** Sets alertTriggerSysId. */
  public void setAlertTriggerSysId(String alertTriggerSysId) {
    this.alertTriggerSysId = alertTriggerSysId;
  }

  /**
   * Gets alertRulesSysId.
   *
   * @return value of alertRulesSysId
   */
  public String getAlertRulesSysId() {
    return alertRulesSysId;
  }

  /** Sets alertRulesSysId . */
  public void setAlertRulesSysId(String alertRulesSysId) {
    this.alertRulesSysId = alertRulesSysId;
  }

  /**
   * Gets alertState.
   *
   * @return value of alertState
   */
  public AlertState getAlertState() {
    return alertState;
  }

  /** Sets alertState. */
  public void setAlertState(AlertState alertState) {
    this.alertState = alertState;
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
   * Gets startTime.
   *
   * @return value of startTime
   */
  public Long getStartTime() {
    return startTime;
  }

  /** Sets startTime. */
  public void setStartTime(Long startTime) {
    this.startTime = startTime;
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
   * Gets metricValue.
   *
   * @return value of metricValue
   */
  public Double getMetricValue() {
    return metricValue;
  }

  /** Sets metricValue. */
  public void setMetricValue(Double metricValue) {
    this.metricValue = metricValue;
  }

  /**
   * Gets lastUpdatedTime.
   *
   * @return value of lastUpdatedTime
   */
  public Long getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  /** Sets lastUpdatedTime. */
  public void setLastUpdatedTime(Long lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
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
   * Gets alertRuleDescription.
   *
   * @return alertRuleDescription
   */
  public String getAlertRuleDescription() {
    return alertRuleDescription;
  }

  /** Sets alertRuleDescription. */
  public void setAlertRuleDescription(String alertRuleDescription) {
    this.alertRuleDescription = alertRuleDescription;
  }

  /**
   * Gets category id.
   *
   * @return category id
   */
  public String getCategoryId() {
    return categoryId;
  }

  /** Sets category id. */
  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Gets sipQuery.
   *
   * @return value of sipQuery
   */
  public SipQuery getSipQuery() {
    return sipQuery;
  }

  /** Sets sipQuery. */
  public void setSipQuery(SipQuery sipQuery) {
    this.sipQuery = sipQuery;
  }
}
