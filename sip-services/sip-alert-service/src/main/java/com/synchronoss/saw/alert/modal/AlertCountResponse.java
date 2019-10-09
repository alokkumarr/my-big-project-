package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AlertCountResponse {

  private String date;
  private Long count;
  private AlertSeverity alertSeverity;
  private String attributeValue;

  /** constructs the alertcountresponse with parameters. */
  public AlertCountResponse(
      String date, Long count, AlertSeverity alertSeverity, String attributeValue) {
    this.date = date;
    this.count = count;
    this.alertSeverity = alertSeverity;
    this.attributeValue = attributeValue;
  }

  /**
   * Gets date.
   *
   * @return date
   */
  public String getDate() {
    return date;
  }

  /** Sets date. */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * Gets alert count based on the groupby.
   *
   * @return alert count
   */
  public Long getCount() {
    return count;
  }

  /** Sets alert count. */
  public void setCount(Long count) {
    this.count = count;
  }

  /**
   * Gets alertseverity.
   *
   * @return alertseverity
   */
  public AlertSeverity getAlertSeverity() {
    return alertSeverity;
  }

  /** Sets alertseverity. */
  public void setAlertSeverity(AlertSeverity alertSeverity) {
    this.alertSeverity = alertSeverity;
  }

  /**
   * Gets attributeValue.
   *
   * @return value of attributeValue
   */
  public String getAttributeValue() {
    return attributeValue;
  }

  /** Sets attributeValue. */
  public void setAttributeValue(String attributeValue) {
    this.attributeValue = attributeValue;
  }
}
