package com.synchronoss.saw.alert.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "DATAPOD_DETAILS")
public class DatapodDetails {

  @Id
  @Column(name = "DATAPOD_ID")
  String datapodId;

  @Column(name = "DATAPOD_NAME")
  String datapodName;

  @Column(name = "ALERT_CUSTOMER_SYS_ID")
  Long alertCustomerSysId;

  @Column(name = "CREATED_TIME")
  Date createdTime;

  @Column(name = "CREATED_BY")
  String createdBy;

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
   * Gets datapodName.
   *
   * @return value of datapodName
   */
  public String getDatapodName() {
    return datapodName;
  }

  /** Sets datapodName. */
  public void setDatapodName(String datapodName) {
    this.datapodName = datapodName;
  }

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
}
