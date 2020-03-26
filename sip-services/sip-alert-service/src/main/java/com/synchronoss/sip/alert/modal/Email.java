package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Set;

public class Email {

  @JsonPropertyOrder({
      "template",
      "subscribers",
  })

  @JsonProperty("subscribers")
  Set<String> subscribers;

  String template;

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

  public Set<String> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(Set<String> subscribers) {
    this.subscribers = subscribers;
  }
}
