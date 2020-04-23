package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Set;

public class Email {

  @JsonPropertyOrder({
      "template",
      "recipients",
  })

  @JsonProperty("recipients")
  Set<String> recipients;

  String template;

  /**
   * Gets template.
   *
   * @return value of template
   */
  public String getTemplate() {
    return template;
  }

  /**
   * Sets template.
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * Gets recipients.
   *
   * @return value of recipients
   */
  public Set<String> getRecipients() {
    return recipients;
  }

  /**
   * Sets recipients.
   */
  public void setRecipients(Set<String> recipients) {
    this.recipients = recipients;
  }
}
