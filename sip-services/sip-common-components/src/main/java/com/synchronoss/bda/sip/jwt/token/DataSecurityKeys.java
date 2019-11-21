package com.synchronoss.bda.sip.jwt.token;

import java.io.Serializable;
import java.util.List;

/**
 * Model class of data security key.
 *
 * @author alok.kumarr
 * @since 3.4.0
 */
public class DataSecurityKeys implements Serializable {

  private static final long serialVersionUID = 7546190895561288031L;

  private String message;
  private String customerCode;
  private Integer isJvCustomer;
  private Integer filterByCustomerCode;
  private List<TicketDSKDetails> dataSecurityKeys;

  /**
   * Gets message.
   *
   * @return value of message
   */
  public String getMessage() {
    return message;
  }

  /** Sets value of message.  */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets customerCode.
   *
   * @return value of customerCode
   */
  public String getCustomerCode() {
    return customerCode;
  }

  /** Sets value of customerCode.  */
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }


  /**
   * Gets isJVCustomer.
   *
   * @return value of isJVCustomer
   */
  public Integer getIsJvCustomer() {
    return isJvCustomer;
  }

  /** Sets value of isJVCustomer.  */
  public void setIsJvCustomer(Integer isJVCustomer) {
    this.isJvCustomer = isJvCustomer;
  }

  /**
   * Gets filterByCustomerCode.
   *
   * @return value of filterByCustomerCode
   */
  public Integer getFilterByCustomerCode() {
    return filterByCustomerCode;
  }

  /** Sets value of filterByCustomerCode.  */
  public void setFilterByCustomerCode(Integer filterByCustomerCode) {
    this.filterByCustomerCode = filterByCustomerCode;
  }

  /**
   * Gets dataSecurityKeys.
   *
   * @return value of dataSecurityKeys
   */
  public List<TicketDSKDetails> getDataSecurityKeys() {
    return dataSecurityKeys;
  }

  /** Sets value of dataSecurityKeys.  */
  public void setDataSecurityKeys(List<TicketDSKDetails> dataSecurityKeys) {
    this.dataSecurityKeys = dataSecurityKeys;
  }
}
