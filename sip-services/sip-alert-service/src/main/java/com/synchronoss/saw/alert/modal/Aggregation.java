package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/** This ENUM class contains Aggregation type for Monitoring entity. */
public enum Aggregation {
  AVG("AVG"),
  SUM("SUM"),
  MIN("MIN"),
  MAX("MAX"),
  COUNT("COUNT");
  private static final Map<String, Aggregation> CONSTANTS = new HashMap<>();

  static {
    for (Aggregation c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  /**
   * Aggregation value.
   *
   * @param value Aggregation
   */
  private Aggregation(String value) {
    this.value = value;
  }

  /**
   * Fetch from values Aggregation.
   *
   * @param value string
   * @return Aggregation
   */
  @JsonCreator
  public static Aggregation fromValue(String value) {
    Aggregation constant = CONSTANTS.get(value);
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
