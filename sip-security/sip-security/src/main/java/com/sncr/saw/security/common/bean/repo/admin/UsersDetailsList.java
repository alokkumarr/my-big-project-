package com.sncr.saw.security.common.bean.repo.admin;

import com.sncr.saw.security.common.bean.UserDetails;
import java.util.List;

public class UsersDetailsList {

  private List<UserDetails> Users;
  private String error;
  private String ValidityMessage;
  private Boolean valid;
  private long recordCount;

  public List<UserDetails> getUsers() {
    return Users;
  }

  public void setUsers(List<UserDetails> users) {
    Users = users;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getValidityMessage() {
    return ValidityMessage;
  }

  public void setValidityMessage(String validityMessage) {
    ValidityMessage = validityMessage;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public long getRecordCount() {
    return recordCount;
  }

  public void setRecordCount(long recordCount) {
    this.recordCount = recordCount;
  }
}
