package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

public class Email {

  @JsonPropertyOrder({
      "template",
      "recipients",
  })
  @JsonProperty("recipients")
  List<String> recipients;

  String template;

  /**
   * Gets recipients.
   *
   * @return value of recipients
   */
  public List<String> getRecipients() {
    return recipients;
  }

  /** Sets recipients. */
  public void setRecipients(List<String> recipients) {
    this.recipients = recipients;
  }

  /**
   * Gets template.
   *
   * @return value of template
   */
  public String getTemplate() {
    return template;
  }

  /** Sets template. */
  public void setTemplate(String template) {
    this.template = template;
  }
}
