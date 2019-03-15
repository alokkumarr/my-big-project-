package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum DataTypes {
  DATE("date"),
  TIMESTAMP("timestamp"),
  LONG("long"),
  DOUBLE("double"),
  INTEGER("integer"),
  STRING("string"),
  FLOAT("float");
  private static final Map<String, DataTypes> CONSTANTS = new HashMap<String, DataTypes>();

  static {
    for (DataTypes c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  private DataTypes(String value) {
    this.value = value;
  }

  @JsonCreator
  public static DataTypes fromValue(String value) {
    DataTypes constant = CONSTANTS.get(value);
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
