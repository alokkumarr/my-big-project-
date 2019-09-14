package com.synchronoss.saw.alert.modal;

import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.SipQuery;

public class AlertRuleDetails {

  private String alertRulesSysId;
  private String datapodId;
  private String datapodName;
  private String categoryId;
  private String product;
  private String alertRuleName;
  private String alertRuleDescription;
  private AlertSeverity alertSeverity;
  private Boolean activeInd;
  private String customerCode;
  private String attributeName;
  private String attributeValue;
  private String lookbackColumn;
  private String lookbackPeriod;
  private Aggregate aggregationType;
  private Operator operator;
  private String metricsColumn;
  private Double thresholdValue;
  private Double otherThresholdValue;
  private Long createdTime;
  private Long modifiedTime;
  private String createdBy;
  private String updatedBy;
  private SipQuery sipQuery;
  private Notification notification;
  private MonitoringType monitoringType;

  /**
   * Gets alertRulesSysId.
   *
   * @return value of alertRulesSysId
   */
  public String getAlertRulesSysId() {
    return alertRulesSysId;
  }

  /** Sets alertRulesSysId. */
  public void setAlertRulesSysId(String alertRulesSysId) {
    this.alertRulesSysId = alertRulesSysId;
  }

  /**
   * Gets datapodId.
   *
   * @return value of datapodId
   */
  public String getDatapodId() {
    return datapodId;
  }

  /** Sets datapodId. */
  public void setDatapodId(String datapodId) {
    this.datapodId = datapodId;
  }

  /**
   * Gets datapod Name.
   *
   * @return value of datapod Name
   */
  public String getDatapodName() {
    return datapodName;
  }

  /** Sets datapodName. */
  public void setDatapodName(String datapodName) {
    this.datapodName = datapodName;
  }

  /**
   * Gets categoryId.
   *
   * @return value of categoryId
   */
  public String getCategoryId() {
    return categoryId;
  }

  /** Sets categoryID. */
  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Gets product.
   *
   * @return value of product
   */
  public String getProduct() {
    return product;
  }

  /** Sets product. */
  public void setProduct(String product) {
    this.product = product;
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
   * Gets activeInd.
   *
   * @return value of activeInd
   */
  public Boolean getActiveInd() {
    return activeInd;
  }

  /** Sets activeInd. */
  public void setActiveInd(Boolean activeInd) {
    this.activeInd = activeInd;
  }

  /**
   * Gets customerCode.
   *
   * @return value of customerCode
   */
  public String getCustomerCode() {
    return customerCode;
  }

  /** Sets customerCode. */
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
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
   * Gets lookbackColumn.
   *
   * @return value of lookbackColumn
   */
  public String getLookbackColumn() {
    return lookbackColumn;
  }

  /** Sets lookbackColumn. */
  public void setLookbackColumn(String lookbackColumn) {
    this.lookbackColumn = lookbackColumn;
  }

  /**
   * Gets lookbackPeriod.
   *
   * @return value of lookbackPeriod
   */
  public String getLookbackPeriod() {
    return lookbackPeriod;
  }

  /** Sets lookbackPeriod. */
  public void setLookbackPeriod(String lookbackPeriod) {
    this.lookbackPeriod = lookbackPeriod;
  }

  /**
   * Gets aggregationType.
   *
   * @return value of aggregationType
   */
  public Aggregate getAggregationType() {
    return aggregationType;
  }

  /** Sets aggregationType. */
  public void setAggregationType(Aggregate aggregationType) {
    this.aggregationType = aggregationType;
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

  /**
   * Gets metricsColumn.
   *
   * @return value of metricsColumn
   */
  public String getMetricsColumn() {
    return metricsColumn;
  }

  /** Sets metricsColumn. */
  public void setMetricsColumn(String metricsColumn) {
    this.metricsColumn = metricsColumn;
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
   * Gets createdTime.
   *
   * @return value of createdTime
   */
  public Long getCreatedTime() {
    return createdTime;
  }

  /** Sets createdTime . */
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  /**
   * Gets modifiedTime.
   *
   * @return value of modifiedTime
   */
  public Long getModifiedTime() {
    return modifiedTime;
  }

  /** Sets modifiedTime. */
  public void setModifiedTime(Long modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  /**
   * Gets createdBy.
   *
   * @return value of createdBy.
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /** Sets createdBy. */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Gets updatedBy.
   *
   * @return value of updatedBy
   */
  public String getUpdatedBy() {
    return updatedBy;
  }

  /** Sets updatedBy. */
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
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
   * Gets notification.
   *
   * @return value of notification
   */
  public Notification getNotification() {
    return notification;
  }

  /** Sets notification. */
  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  /**
   * Gets monitoringType.
   *
   * @return value of monitoringType
   */
  public MonitoringType getMonitoringType() {
    return monitoringType;
  }

  /** Sets monitoringType. */
  public void setMonitoringType(MonitoringType monitoringType) {
    this.monitoringType = monitoringType;
  }
}
