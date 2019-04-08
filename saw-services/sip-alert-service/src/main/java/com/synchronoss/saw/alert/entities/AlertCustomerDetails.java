package com.synchronoss.saw.alert.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ALERT_CUSTOMER_DETAILS")
public class AlertCustomerDetails implements Serializable {

  @Id
  @Column(name = "ALERT_CUSTOMER_SYS_ID")
  Long alertCustomerSysId;

  @Column(name = "CUSTOMER_CODE")
  String customerCode;

  @Column(name = "PRODUCT_CODE")
  String productCode;

  @Column(name = "ACTIVE_IND")
  Boolean activeInd;

  @Column(name = "CREATED_BY")
  String createdBy;

  @Column(name = "CREATED_TIME")
  Date createdTime;

  @Column(name = "MODIFIED_TIME")
  Date modifiedTime;

  @Column(name = "MODIFIED_BY")
  String modifiedBy;

  /**
   * Gets alertCustomerSysId.
   *
   * @return value of alertCustomerSysId
   */
  public Long getAlertCustomerSysId() {
    return alertCustomerSysId;
  }

  /** Sets alertCustomerSysId. */
  public void setAlertCustomerSysId(Long alertCustomerSysId) {
    this.alertCustomerSysId = alertCustomerSysId;
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
   * Gets productCode.
   *
   * @return value of productCode
   */
  public String getProductCode() {
    return productCode;
  }

  /** Sets productCode. */
  public void setProductCode(String productCode) {
    this.productCode = productCode;
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
