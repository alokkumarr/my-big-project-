package com.synchronoss.saw.alert.modal;

import com.synchronoss.saw.model.SipQuery;
import java.util.List;

public class AlertRuleDetails {

  private String alertRulesSysId;
  private String datapodId;
  private String datapodName;
  private String categoryId;
  private String product;
  private String alertName;
  private String alertDescription;
  private AlertSeverity alertSeverity;
  private Boolean activeInd;
  private String customerCode;
  private SipQuery sipQuery;
  private List<Notification> notification;

  /**
   * Gets alertRulesSysId.
   *
   * @return value of alertRulesSysId
   */
  public String getAlertRulesSysId() {
    return alertRulesSysId;
  }

  /** Sets alertRulesSysId. */
  public void setAlertRulesSysId(String alertRulesSysId) {
    this.alertRulesSysId = alertRulesSysId;
  }

  /**
   * Gets datapodId.
   *
   * @return value of datapodId
   */
  public String getDatapodId() {
    return datapodId;
  }

  /** Sets datapodId. */
  public void setDatapodId(String datapodId) {
    this.datapodId = datapodId;
  }

  /**
   * Gets datapod Name.
   *
   * @return value of datapod Name
   */
  public String getDatapodName() {
    return datapodName;
  }

  /** Sets datapodName. */
  public void setDatapodName(String datapodName) {
    this.datapodName = datapodName;
  }

  /**
   * Gets categoryId.
   *
   * @return value of categoryId
   */
  public String getCategoryId() {
    return categoryId;
  }

  /** Sets categoryID. */
  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Gets product.
   *
   * @return value of product
   */
  public String getProduct() {
    return product;
  }

  /** Sets product. */
  public void setProduct(String product) {
    this.product = product;
  }

  /**
   * Gets alertName.
   *
   * @return value of alertName
   */
  public String getAlertName() {
    return alertName;
  }

  /** Sets alertName. */
  public void setAlertName(String alertName) {
    this.alertName = alertName;
  }

  /**
   * Gets alertDescription.
   *
   * @return value of alertDescription
   */
  public String getAlertDescription() {
    return alertDescription;
  }

  /** Sets alertDescription. */
  public void setAlertDescription(String alertDescription) {
    this.alertDescription = alertDescription;
  }

  /**
   * Gets alertSeverity.
   *
   * @return value of alertSeverity
   */
  public AlertSeverity getAlertSeverity() {
    return alertSeverity;
  }

  /** Sets alertSeverity. */
  public void setAlertSeverity(AlertSeverity alertSeverity) {
    this.alertSeverity = alertSeverity;
  }

  /**
   * Gets activeInd.
   *
   * @return value of activeInd
   */
  public Boolean getActiveInd() {
    return activeInd;
  }

  /** Sets activeInd. */
  public void setActiveInd(Boolean activeInd) {
    this.activeInd = activeInd;
  }

  /**
   * Gets customerCode.
   *
   * @return value of customerCode
   */
  public String getCustomerCode() {
    return customerCode;
  }

  /** Sets customerCode. */
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  /**
   * Gets sipQuery.
   *
   * @return value of sipQuery
   */
  public SipQuery getSipQuery() {
    return sipQuery;
  }

  /** Sets sipQuery. */
  public void setSipQuery(SipQuery sipQuery) {
    this.sipQuery = sipQuery;
  }

  /**
   * Gets notification.
   *
   * @return list of notification.
   */
  public List<Notification> getNotification() {
    return notification;
  }

  /** Sets notification. */
  public void setNotification(List<Notification> notification) {
    this.notification = notification;
  }
}
