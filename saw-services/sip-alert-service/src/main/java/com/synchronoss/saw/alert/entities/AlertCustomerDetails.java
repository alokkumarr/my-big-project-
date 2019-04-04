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
}
