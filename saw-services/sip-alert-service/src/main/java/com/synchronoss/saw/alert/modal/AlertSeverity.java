package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum AlertSeverity {
  CRITICAL("CRITICAL"),
  MEDIUM("MEDIUM"),
  LOW("LOW");
  private static final Map<String, AlertSeverity> CONSTANTS = new HashMap<>();

  static {
    for (AlertSeverity c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  private AlertSeverity(String value) {
    this.value = value;
  }

  @JsonCreator
  public static AlertSeverity fromValue(String value) {
    AlertSeverity constant = CONSTANTS.get(value);
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }

  @Override
  public String toString() {
    return this.value;
  }

  @JsonValue
  public String value() {
    return this.value;
  }
}
