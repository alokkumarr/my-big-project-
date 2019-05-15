package com.synchronoss.saw.analysis.modal;

import java.util.List;

/** This Class required to store the security token details to filter analysis metadata etc. */
public class Ticket {

  private String userID;

  private String useFullName;

  private List<String> dataSecurityKey;

  private String roleType;

  private List<Object> products;

  private String masterLoginId;

  private String customerCode;

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUseFullName() {
    return useFullName;
  }

  public void setUseFullName(String useFullName) {
    this.useFullName = useFullName;
  }

  public List<String> getDataSecurityKey() {
    return dataSecurityKey;
  }

  public void setDataSecurityKey(List<String> dataSecurityKey) {
    this.dataSecurityKey = dataSecurityKey;
  }

  public String getRoleType() {
    return roleType;
  }

  public void setRoleType(String roleType) {
    this.roleType = roleType;
  }

  public List<Object> getProducts() {
    return products;
  }

  public void setProducts(List<Object> products) {
    this.products = products;
  }

  public String getMasterLoginId() {
    return masterLoginId;
  }

  public void setMasterLoginId(String masterLoginId) {
    this.masterLoginId = masterLoginId;
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }
}
