package com.synchronoss.saw.alert.modal;

public class Alert {

  Long alertRulesSysId;
  String datapodId;
  String datapodName;
  String categoryId;
  String product;
  String alertName;
  String alertDescription;
  String monitoringEntity;
  Aggregation aggregation;
  Operator operator;
  AlertSeverity alertSeverity;
  Double thresholdValue;
  Boolean activeInd;

  /**
   * Gets alertRulesSysId.
   *
   * @return value of alertRulesSysId
   */
  public Long getAlertRulesSysId() {
    return alertRulesSysId;
  }

  /** Sets alertRulesSysId. */
  public void setAlertRulesSysId(Long alertRulesSysId) {
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
   * Gets alertName.
   *
   * @return value of alertName
   */
  public String getAlertName() {
    return alertName;
  }

  /** Sets alertName. */
  public void setAlertName(String alertName) {
    this.alertName = alertName;
  }

  /**
   * Gets alertDescription.
   *
   * @return value of alertDescription
   */
  public String getAlertDescription() {
    return alertDescription;
  }

  /** Sets alertDescription. */
  public void setAlertDescription(String alertDescription) {
    this.alertDescription = alertDescription;
  }

  /**
   * Gets monitoringEntity.
   *
   * @return value of monitoringEntity
   */
  public String getMonitoringEntity() {
    return monitoringEntity;
  }

  /** Sets monitoringEntity. */
  public void setMonitoringEntity(String monitoringEntity) {
    this.monitoringEntity = monitoringEntity;
  }

  /**
   * Gets aggregation.
   *
   * @return value of aggregation
   */
  public Aggregation getAggregation() {
    return aggregation;
  }

  /** Sets aggregation. */
  public void setAggregation(Aggregation aggregation) {
    this.aggregation = aggregation;
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
}
