/**
 * 
 */
package com.synchronoss.saw.gateway.utils;


import java.io.Serializable;
import java.util.List;

public class UserRelatedMetaData implements Serializable {

  private static final long serialVersionUID = 7546190895561288031L;
  private String userFullName;
  private String userName;
  private String custCode;
  private String roleCode;
  private String roleType;
  private boolean isValid = false;
  private List<DSKDetails> dataSKey;

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean isValid) {
    this.isValid = isValid;
  }


  public String getCustCode() {
    return custCode;
  }

  public void setCustCode(String custCode) {
    this.custCode = custCode;
  }


  public List<DSKDetails> getDataSKey() {
    return dataSKey;
  }

  public void setDataSKey(List<DSKDetails> dataSKey) {
    this.dataSKey = dataSKey;
  }


  /**
   * @return the userName
   */
  public String getUserFullName() {
    return userFullName;
  }

  /**
   * @param userFullName the userName to set
   */
  public void setUserFullName(String userFullName) {
    this.userFullName = userFullName;
  }

  /**
   * @return the roleName
   */
  public String getRoleCode() {
    return roleCode;
  }

  /**
   * @param roleCode the roleName to set
   */
  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }

  /**
   * @return the roleType
   */
  public String getRoleType() {
    return roleType;
  }

  /**
   * @param roleType the roleType to set
   */
  public void setRoleType(String roleType) {
    this.roleType = roleType;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  

}
