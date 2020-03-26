package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NotificationChannelType {
  @JsonProperty("email")
  EMAIL("email"),

  @JsonProperty("sms")
  SMS("sms");

  //  Only Email is supported for now, Phone("Phone"), Slack("Slack"),
  //  WebHooks can be included later to the same channel;

  private String value;

  NotificationChannelType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
