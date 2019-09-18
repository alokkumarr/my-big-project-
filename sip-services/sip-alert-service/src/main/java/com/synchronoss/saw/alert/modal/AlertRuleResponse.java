package com.synchronoss.saw.alert.modal;

import java.util.List;

public class AlertRuleResponse {

  private List<AlertRuleDetails> alertRuleDetailsList;
  private Long numberOfRecords;

  /**
   * Gets alertRuleDetailsList.
   *
   * @return value of alertRuleDetailsList
   */
  public List<AlertRuleDetails> getAlertRuleDetailsList() {
    return alertRuleDetailsList;
  }

  /** Sets alertRuleDetailsList. */
  public void setAlertRuleDetailsList(List<AlertRuleDetails> alertRuleDetailsList) {
    this.alertRuleDetailsList = alertRuleDetailsList;
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
