package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;

@JsonInclude(Include.NON_NULL)
public class AlertCountResponse {

  private String date;
  private Long count;
  private AlertSeverity alertSeverity;

  /** constructs the alertcountresponse with parameters. */
  public AlertCountResponse(String date, Long count, AlertSeverity alertSeverity) {
    this.date = date;
    this.count = count;
    this.alertSeverity = alertSeverity;
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
}
