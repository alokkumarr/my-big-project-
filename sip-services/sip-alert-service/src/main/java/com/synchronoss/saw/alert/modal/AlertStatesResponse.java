package com.synchronoss.saw.alert.modal;

import java.util.List;

public class AlertStatesResponse {

  Long numberOfRecords;
  List<AlertStates> alertStatesList;
  String message;

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

  /**
   * Gets alertStatesList.
   *
   * @return value of alertStatesList
   */
  public List<AlertStates> getAlertStatesList() {
    return alertStatesList;
  }

  /** Sets alertStatesList. */
  public void setAlertStatesList(List<AlertStates> alertStatesList) {
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
}
