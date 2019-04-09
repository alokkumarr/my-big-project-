package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/**
 * This Enum class defines the Operator like GT > , LT < , GTE >= , LTE <= , EQ =, NEQ != , bTW -
 * between , SW - start with , EW- End With Contains, Is IN and Is not In.
 */
public enum Operator {
  GT("GT"),
  LT("LT"),
  GTE("GTE"),
  LTE("LTE"),
  EQ("EQ"),
  NEQ("NEQ"),
  BTW("BTW"),
  SW("SW"),
  EW("EW"),
  CONTAINS("CONTAINS"),
  ISIN("ISIN"),
  ISNOTIN("ISNOTIN");
  private static final Map<String, Operator> CONSTANTS = new HashMap<>();

  static {
    for (Operator c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  /**
   * Operator value.
   *
   * @param value Operator
   */
  private Operator(String value) {
    this.value = value;
  }

  /**
   * fetch from value.
   *
   * @param value Operator
   * @return Operator
   */
  @JsonCreator
  public static Operator fromValue(String value) {
    Operator constant = CONSTANTS.get(value);
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
