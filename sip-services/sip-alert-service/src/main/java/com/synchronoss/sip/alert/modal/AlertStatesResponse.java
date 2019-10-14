package com.synchronoss.sip.alert.modal;

import java.util.List;

public class AlertStatesResponse {

  private List<AlertResult> alertStatesList;
  private String message;
  private Long numberOfRecords;

  /**
   * Gets alertstatesList.
   *
   * @return alertstatesList
   */
  public List<AlertResult> getAlertStatesList() {
    return alertStatesList;
  }

  /** Sets alertstatesList. */
  public void setAlertStatesList(List<AlertResult> alertStatesList) {
    this.alertStatesList = alertStatesList;
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

  /**
   * Gets numberOfRecords.
   *
   * @return value of numberOfRecords
   */
  public Long getNumberOfRecords() {
    return numberOfRecords;
  }

  /** Sets numberOfRecords. */
  public void setNumberOfRecords(Long numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
  }
}
