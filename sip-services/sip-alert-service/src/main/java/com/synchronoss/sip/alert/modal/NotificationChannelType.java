package com.synchronoss.sip.alert.modal;

public enum NotificationChannelType {
  EMAIL("email");

  //  Only Email is supported for now, Phone("Phone"), Slack("Slack"),
  //  WebHooks can be included later to the same channel;

  private String value;

  NotificationChannelType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
