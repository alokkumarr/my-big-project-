package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
  "email",
})
public class Notification {

  @JsonProperty("email")
  Email email;

  /**
   * Gets email.
   *
   * @return value of email
   */
  public Email getEmail() {
    return email;
  }

  /** Sets email. */
  public void setEmail(Email email) {
    this.email = email;
  }
}
