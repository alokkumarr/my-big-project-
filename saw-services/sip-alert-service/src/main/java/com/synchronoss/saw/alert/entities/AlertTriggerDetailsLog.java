package com.synchronoss.saw.alert.entities;

import com.synchronoss.saw.alert.modal.AlertState;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ALERT_TRIGGER_DETAILS_LOG")
public class AlertTriggerDetailsLog {

  @Id
  @Column(name = "ALERT_TRIGGER_SYS_ID")
  Long alertTriggerSysId;

  @Column(name = "ALERT_RULES_SYS_ID")
  Long alertRulesSysId;

  @Column(name = "ALERT_STATE")
  AlertState alertState;

  @Column(name = "START_TIME")
  Date startTime;
}
