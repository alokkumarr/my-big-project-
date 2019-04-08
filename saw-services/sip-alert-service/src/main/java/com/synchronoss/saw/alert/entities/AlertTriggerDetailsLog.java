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

  /**
   * Gets alertTriggerSysId.
   *
   * @return value of alertTriggerSysId
   */
  public Long getAlertTriggerSysId() {
    return alertTriggerSysId;
  }

  /** Sets alertTriggerSysId. */
  public void setAlertTriggerSysId(Long alertTriggerSysId) {
    this.alertTriggerSysId = alertTriggerSysId;
  }

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
   * Gets startTime.
   *
   * @return value of startTime
   */
  public Date getStartTime() {
    return startTime;
  }

  /** Sets startTime. */
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
}
