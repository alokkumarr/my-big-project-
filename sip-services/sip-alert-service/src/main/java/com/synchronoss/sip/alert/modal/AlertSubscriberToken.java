package com.synchronoss.sip.alert.modal;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlertSubscriberToken {

  @JsonProperty("alertRulesSysId")
  private String alertRulesSysId;

  @JsonProperty("alertTriggerSysId")
  private String alertTriggerSysId;

  @JsonProperty("emailId")
  private String emailId;

  public AlertSubscriberToken() {}

  public AlertSubscriberToken(String alertRulesSysId, String alertTriggerSysId, String emailId) {
    this.alertRulesSysId = alertRulesSysId;
    this.alertTriggerSysId = alertTriggerSysId;
    this.emailId = emailId;
  }

  public String getAlertRulesSysId() {
    return alertRulesSysId;
  }

  public void setAlertRulesSysId(String alertRulesSysId) {
    this.alertRulesSysId = alertRulesSysId;
  }

  public String getAlertTriggerSysId() {
    return alertTriggerSysId;
  }

  public void setAlertTriggerSysId(String alertTriggerSysId) {
    this.alertTriggerSysId = alertTriggerSysId;
  }

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }
}
