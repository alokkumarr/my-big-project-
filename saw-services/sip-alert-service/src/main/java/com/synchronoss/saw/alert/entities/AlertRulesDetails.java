package com.synchronoss.saw.alert.entities;

import com.synchronoss.saw.alert.modal.Aggregation;
import com.synchronoss.saw.alert.modal.AlertSeverity;
import com.synchronoss.saw.alert.modal.Operator;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ALERT_RULES_DETAILS")
public class AlertRulesDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ALERT_RULES_SYS_ID")
  Long alertRulesSysId;

  @Column(name = "DATAPOD_ID")
  String datapodId;

  @Column(name = "ALERT_NAME")
  String alertName;

  @Column(name = "ALERT_DESCRIPTIONS")
  String alertDescriptions;

  @Column(name = "CATEGORY")
  String category;

  @Enumerated(EnumType.STRING)
  @Column(name = "SEVERITY")
  AlertSeverity alertSeverity;

  @Column(name = "MONITORING_ENTITY")
  String monitoringEntity;

  @Enumerated(EnumType.STRING)
  @Column(name = "AGGREGATION")
  Aggregation aggregation;

  @Enumerated(EnumType.STRING)
  @Column(name = "OPERATOR")
  Operator operator;

  @Column(name = "THRESHOLD_VALUE")
  Double thresholdValue;

  @Column(name = "ACTIVE_IND")
  Boolean activeInd;

  @Column(name = "CREATED_BY")
  String createdBy;

  @CreationTimestamp
  @Column(name = "CREATED_TIME")
  Date createdTime;

  @Column(name = "MODIFIED_TIME")
  Date modifiedTime;

  @Column(name = "MODIFIED_BY")
  String modifiedBy;

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
   * Gets AlertName.
   *
   * @return value of AlertName
   */
  public String getAlertName() {
    return alertName;
  }

  /** Sets AlertName. */
  public void setAlertName(String alertName) {
    this.alertName = alertName;
  }

  /**
   * Gets alertDescriptions.
   *
   * @return value of alertDescriptions
   */
  public String getAlertDescriptions() {
    return alertDescriptions;
  }

  /** Sets alertDescriptions. */
  public void setAlertDescriptions(String alertDescriptions) {
    this.alertDescriptions = alertDescriptions;
  }

  /**
   * Gets category.
   *
   * @return value of category
   */
  public String getCategory() {
    return category;
  }

  /** Sets category. */
  public void setCategory(String category) {
    this.category = category;
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

  /**
   * Gets createdBy.
   *
   * @return value of createdBy
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /** Sets createdBy. */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Gets createdTime.
   *
   * @return value of createdTime
   */
  public Date getCreatedTime() {
    return createdTime;
  }

  /** Sets createdTime. */
  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  /**
   * Gets modifiedTime.
   *
   * @return value of modifiedTime
   */
  public Date getModifiedTime() {
    return modifiedTime;
  }

  /** Sets modifiedTime. */
  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  /**
   * Gets modifiedBy.
   *
   * @return value of modifiedBy
   */
  public String getModifiedBy() {
    return modifiedBy;
  }

  /** Sets modifiedBy. */
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }
}
