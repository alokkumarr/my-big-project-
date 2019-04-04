package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum Aggregation {
  AVG("avg"),
  SUM("sum"),
  MIN("min"),
  MAX("max"),
  COUNT("count"),
  PERCENTAGE("percentage");
  private static final Map<String, Aggregation> CONSTANTS = new HashMap<>();

  static {
    for (Aggregation c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  private Aggregation(String value) {
    this.value = value;
  }

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
