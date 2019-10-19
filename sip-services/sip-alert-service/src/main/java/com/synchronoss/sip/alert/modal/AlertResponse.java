package com.synchronoss.sip.alert.modal;

public class AlertResponse {

  private AlertRuleDetails alertRuleDetails;
  private String message;

  /**
   * Gets alert.
   *
   * @return value of alert
   */
  public AlertRuleDetails getAlert() {
    return alertRuleDetails;
  }

  /** Sets alert. */
  public void setAlert(AlertRuleDetails alertRuleDetails) {
    this.alertRuleDetails = alertRuleDetails;
  }

  /**
   * Gets message.
   *
   * @return value of message
   */
  public String getMessage() {
    return message;
  }

  /** Sets message. */
  public void setMessage(String message) {
    this.message = message;
  }
}
