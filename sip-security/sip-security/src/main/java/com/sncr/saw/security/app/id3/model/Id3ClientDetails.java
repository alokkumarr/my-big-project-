package com.sncr.saw.security.app.id3.model;

/** Store the Id3 client details. */
public class Id3ClientDetails {

  private String masterLoginId;
  private boolean id3Enabled;
  private String customerCode;
  private boolean userActive;
  private boolean id3ClientActive;
  private long id3ClientSysId;

  public String getMasterLoginId() {
    return masterLoginId;
  }

  public void setMasterLoginId(String masterLoginId) {
    this.masterLoginId = masterLoginId;
  }

  public boolean isId3Enabled() {
    return id3Enabled;
  }

  public void setId3Enabled(boolean id3Enabled) {
    this.id3Enabled = id3Enabled;
  }

  public String getCustomerCode() {
    return customerCode;
  }

  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  public boolean isUserActive() {
    return userActive;
  }

  public void setUserActive(boolean userActive) {
    this.userActive = userActive;
  }

  public boolean isId3ClientActive() {
    return id3ClientActive;
  }

  public void setId3ClientActive(boolean id3ClientActive) {
    this.id3ClientActive = id3ClientActive;
  }

  public long getId3ClientSysId() {
    return id3ClientSysId;
  }

  public void setId3ClientSysId(long id3ClientSysId) {
    this.id3ClientSysId = id3ClientSysId;
  }
}
