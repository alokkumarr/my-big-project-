package com.synchronoss.saw.alert.modal;

public class Alert {

  String datapodId;
  String datapodName;
  String categoryId;
  String product;
  String ruleName;
  String ruleDescription;
  String monitoringEntity;
  Aggregation aggregation;
  Operator operator;
  AlertSeverity alertSeverity;
  Double thresholdValue;
  String activeInd;

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
   * Gets ruleName.
   *
   * @return value of ruleName
   */
  public String getRuleName() {
    return ruleName;
  }

  /** Sets ruleName. */
  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  /**
   * Gets ruleDescription.
   *
   * @return value of ruleDescription
   */
  public String getRuleDescription() {
    return ruleDescription;
  }

  /** Sets ruleDescription. */
  public void setRuleDescription(String ruleDescription) {
    this.ruleDescription = ruleDescription;
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
  public String getActiveInd() {
    return activeInd;
  }

  /** Sets activeInd. */
  public void setActiveInd(String activeInd) {
    this.activeInd = activeInd;
  }
}
