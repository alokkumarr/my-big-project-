package com.synchronoss.sip.alert.modal;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlertSubscriberToken {

  @JsonProperty("alertRulesSysId")
  private String alertRulesSysId;

  @JsonProperty("alertRuleName")
  private String alertRuleName;

  @JsonProperty("alertRuleDescription")
  private String alertRuleDescription;

  @JsonProperty("alertTriggerSysId")
  private String alertTriggerSysId;

  @JsonProperty("emailId")
  private String emailId;

  public AlertSubscriberToken() {}

  public AlertSubscriberToken(
      String alertRulesSysId,
      String alertRuleName,
      String alertRuleDescription,
      String alertTriggerSysId,
      String emailId) {
    this.alertRulesSysId = alertRulesSysId;
    this.alertRuleName = alertRuleName;
    this.alertRuleDescription = alertRuleDescription;
    this.alertTriggerSysId = alertTriggerSysId;
    this.emailId = emailId;
  }

  public String getAlertRulesSysId() {
    return alertRulesSysId;
  }

  public void setAlertRulesSysId(String alertRulesSysId) {
    this.alertRulesSysId = alertRulesSysId;
  }

  public String getAlertRuleName() {
    return alertRuleName;
  }

  public void setAlertRuleName(String alertRuleName) {
    this.alertRuleName = alertRuleName;
  }

  public String getAlertRuleDescription() {
    return alertRuleDescription;
  }

  public void setAlertRuleDescription(String alertRuleDescription) {
    this.alertRuleDescription = alertRuleDescription;
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
