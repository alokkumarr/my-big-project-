package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class Subscriber {
  @JsonProperty("subscriberId")
  private String subscriberId;

  @JsonProperty("alertRulesSysId")
  private String alertRulesSysId;

  @JsonProperty("alertTriggerSysId")
  private String alertTriggerSysId;

  @JsonProperty("email")
  private String email;

  @JsonProperty("active")
  private Boolean active;

  @JsonProperty("createdTime")
  private Date createdTime;

  @JsonProperty("modifiedTime")
  private Date modifiedTime;

  public String getSubscriberId() {
    return subscriberId;
  }

  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  public Date getModifiedTime() {
    return modifiedTime;
  }

  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  @Override
  public String toString() {
    return "Subscriber{"
        + "subscriberId='"
        + subscriberId
        + '\''
        + ", alertRulesSysId='"
        + alertRulesSysId
        + '\''
        + ", alertTriggerSysId='"
        + alertTriggerSysId
        + '\''
        + ", email='"
        + email
        + '\''
        + ", active="
        + active
        + ", createdTime="
        + createdTime
        + ", modifiedTime="
        + modifiedTime
        + '}';
    }
}
