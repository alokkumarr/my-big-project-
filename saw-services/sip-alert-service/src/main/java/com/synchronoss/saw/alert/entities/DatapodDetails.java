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
}
