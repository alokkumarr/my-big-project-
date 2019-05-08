package com.synchronoss.saw.alert.modal;

public class AlertResponse {

  private Alert alert;
  private String message;

  /**
   * Gets alert.
   *
   * @return value of alert
   */
  public Alert getAlert() {
    return alert;
  }

  /** Sets alert. */
  public void setAlert(Alert alert) {
    this.alert = alert;
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
