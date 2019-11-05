package com.synchronoss.sip.alert.modal;

import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.SipQuery;
import org.codehaus.jackson.annotate.JsonProperty;

public class AlertResult {
  @JsonProperty("alertTriggerSysId")
  private String alertTriggerSysId;

  @JsonProperty("alertRulesSysId")
  private String alertRulesSysId;

  @JsonProperty("alertState")
  private AlertState alertState;

  @JsonProperty("alertSeverity")
  private AlertSeverity alertSeverity;

  @JsonProperty("startTime")
  private Long startTime;

  @JsonProperty("thresholdValue")
  private Double thresholdValue;

  @JsonProperty("otherThresholdValue")
  private Double otherThresholdValue;

  @JsonProperty("metricValue")
  private Double metricValue;

  @JsonProperty("lastUpdatedTime")
  private Long lastUpdatedTime;

  @JsonProperty("alertRuleName")
  private String alertRuleName;

  @JsonProperty("alertRuleDescription")
  private String alertRuleDescription;

  @JsonProperty("categoryId")
  private String categoryId;

  @JsonProperty("sipQuery")
  private SipQuery sipQuery;

  @JsonProperty("customerCode")
  private String customerCode;

  @JsonProperty("alertCount")
  private Integer alertCount;

  @JsonProperty("attributeValue")
  private String attributeValue;

  @JsonProperty("operator")
  private Operator operator;

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
   * Gets otherThresholdValue.
   *
   * @return value of otherThresholdValue
   */
  public Double getOtherThresholdValue() {
    return otherThresholdValue;
  }

  /** Sets otherThresholdValue. */
  public void setOtherThresholdValue(Double otherThresholdValue) {
    this.otherThresholdValue = otherThresholdValue;
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

  /**
   * Gets customerCode.
   *
   * @return value of customerCode
   */
  public String getCustomerCode() {
    return customerCode;
  }

  /** Sets customerCode.*/
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  /**
   * Gets alertCount.
   *
   * @return value of alertCount
   */
  public Integer getAlertCount() {
    return alertCount;
  }

  /** Sets alertCount. */
  public void setAlertCount(Integer alertCount) {
    this.alertCount = alertCount;
  }

  /**
   * Gets attributeValue.
   *
   * @return value of attributeValue
   */
  public String getAttributeValue() {
    return attributeValue;
  }

  /** Sets attributeValue. */
  public void setAttributeValue(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  /**
   * Gets operator.
   *
   * @return value of operator
   */
  public Operator getOperator() {
    return operator;
  }

  /** Sets operator. */
  public void setOperator(Operator operator) {
    this.operator = operator;
  }
}
