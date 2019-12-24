package com.sncr.saw.security.common.bean.repo.admin;

import com.sncr.saw.security.common.bean.UserDetails;

public class UserDetailsResponse {
  private UserDetails user;
  private String error;
  private String ValidityMessage;
  private Boolean valid;

  public UserDetails getUser() {
    return user;
  }

  public void setUser(UserDetails user) {
    this.user = user;
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
}
