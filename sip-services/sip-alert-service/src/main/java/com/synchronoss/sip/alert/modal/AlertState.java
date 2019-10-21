package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

/** This ENUM defines Alert state changes. */
public enum AlertState {
  @SerializedName("ALARM")
  ALARM("ALARM"),
  @SerializedName("OK")
  OK("OK");
  private static final Map<String, AlertState> CONSTANTS = new HashMap<>();

  static {
    for (AlertState c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  /**
   * AlertState value.
   *
   * @param value AlertState
   */
  private AlertState(String value) {
    this.value = value;
  }

  /**
   * Fetch from value.
   *
   * @param value String
   * @return AlertState
   */
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
