package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public enum NotificationChannelType {
  @JsonProperty("email")
  EMAIL("email"),

  @JsonProperty("sms")
  SMS("sms");

  //  Only Email is supported for now, Phone("Phone"), Slack("Slack"),
  //  WebHooks can be included later to the same channel;

  private String value;

  private static Map<String, NotificationChannelType> map = new HashMap<>();

  static {
    for (NotificationChannelType d : NotificationChannelType.values()) {
      map.put(d.value, d);
    }
  }

  NotificationChannelType(String value) {
    this.value = value;
  }

  public static NotificationChannelType fromValue(String channelTypeStr) {
    return map.get(channelTypeStr);
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
