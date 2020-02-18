package com.sncr.saw.security.common.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDetails {

  @JsonProperty("activeStatusInd")
  private Boolean activeStatusInd;

  @JsonProperty("customerCode")
  private String customerCode;

  @JsonProperty("email")
  private String email;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("middleName")
  private String middleName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("masterLoginId")
  private String masterLoginId;

  @JsonProperty("password")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String password;

  @JsonProperty("roleName")
  private String roleName;

  @JsonProperty("userId")
  private Long userId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("id3Enabled")
  private boolean id3Enabled;

  @JsonProperty("roleId")
  private Long roleId;

  @JsonProperty("secGroupSysId")
  private Long secGroupSysId;

  @JsonProperty("securityGroupName")
  private String securityGroupName;

  @JsonProperty("customerId")
  private Long customerId;

  /**
   * Gets activeStatusInd.
   *
   * @return value of activeStatusInd
   */
  @JsonProperty("activeStatusInd")
  public Boolean getActiveStatusInd() {
    return activeStatusInd;
  }

  /** Sets activeStatusInd. */
  @JsonProperty("activeStatusInd")
  public void setActiveStatusInd(Boolean activeStatusInd) {
    this.activeStatusInd = activeStatusInd;
  }

  /**
   * Gets customerCode.
   *
   * @return value of customerCode
   */
  @JsonProperty("customerCode")
  public String getCustomerCode() {
    return customerCode;
  }

  /** Sets customerCode . */
  @JsonProperty("customerCode")
  public void setCustomerCode(String customerCode) {
    this.customerCode = customerCode;
  }

  /**
   * Gets email.
   *
   * @return email
   */
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  /** Sets email. */
  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets firstName.
   *
   * @return firstName
   */
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  /** Sets firstName . */
  @JsonProperty("firstName")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets lastName.
   *
   * @return lastName
   */
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  /** Sets lastName . */
  @JsonProperty("lastName")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets masterLoginId.
   *
   * @return masterLoginId
   */
  @JsonProperty("masterLoginId")
  public String getMasterLoginId() {
    return masterLoginId;
  }

  /** Sets masterLoginId . */
  @JsonProperty("masterLoginId")
  public void setMasterLoginId(String masterLoginId) {
    this.masterLoginId = masterLoginId;
  }

  /**
   * Gets password.
   *
   * @return password
   */
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  /** Sets password .. */
  @JsonProperty("password")
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets roleName.
   *
   * @return roleName
   */
  @JsonProperty("roleName")
  public String getRoleName() {
    return roleName;
  }

  /** Sets roleName . */
  @JsonProperty("roleName")
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  /**
   * Gets id3Enabled.
   *
   * @return id3Enabled
   */
  @JsonProperty("id3Enabled")
  public boolean getId3Enabled() {
    return id3Enabled;
  }

  /** Sets id3Enabled. */
  @JsonProperty("id3Enabled")
  public void setId3Enabled(boolean id3Enabled) {
    this.id3Enabled = id3Enabled;
  }

  /**
   * Gets middleName.
   *
   * @return middleName
   */
  @JsonProperty("middleName")
  public String getMiddleName() {
    return middleName;
  }

  /** Sets middleName . */
  @JsonProperty("middleName")
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * Gets userId.
   *
   * @return userId
   */
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  /** Sets userId . */
  @JsonProperty("userId")
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Gets roleId.
   *
   * @return roleId
   */
  @JsonProperty("roleId")
  public Long getRoleId() {
    return roleId;
  }

  /** Sets roleId . */
  @JsonProperty("roleId")
  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  /**
   * Gets secGroupSysId.
   *
   * @return secGroupSysId
   */
  @JsonProperty("secGroupSysId")
  public Long getSecGroupSysId() {
    return secGroupSysId;
  }

  /** Sets secGroupSysId . */
  @JsonProperty("secGroupSysId")
  public void setSecGroupSysId(Long secGroupSysId) {
    this.secGroupSysId = secGroupSysId;
  }

  /**
   * Gets securityGroupName.
   *
   * @return securityGroupName
   */
  @JsonProperty("securityGroupName")
  public String getSecurityGroupName() {
    return securityGroupName;
  }

  /** Sets securityGroupName . */
  @JsonProperty("securityGroupName")
  public void setSecurityGroupName(String securityGroupName) {
    this.securityGroupName = securityGroupName;
  }

  /**
   * Gets customerId.
   *
   * @return customerId
   */
  @JsonProperty("customerId")
  public Long getCustomerId() {
    return customerId;
  }

  /** Sets customerId . */
  @JsonProperty("customerId")
  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }
}
