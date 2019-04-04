package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum AlertState {
  CRITICAL("ALARM"),
  MEDIUM("OK");
  private static final Map<String, AlertState> CONSTANTS = new HashMap<>();

  static {
    for (AlertState c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  private AlertState(String value) {
    this.value = value;
  }

  @JsonCreator
  public static AlertState fromValue(String value) {
    AlertState constant = CONSTANTS.get(value);
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
