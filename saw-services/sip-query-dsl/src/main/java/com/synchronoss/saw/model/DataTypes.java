package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DataTypes {
  DATE("date"),
  TIMESTAMP("timestamp"),
  LONG("long"),
  DOUBLE("double"),
  INTEGER("integer"),
  STRING("string"),
  FLOAT("float");
  private final String value;
  private final static Map<String, DataTypes> CONSTANTS = new HashMap<String, DataTypes>();

  static {
      for (DataTypes c: values()) {
          CONSTANTS.put(c.value, c);
      }
  }

  private DataTypes(String value) {
      this.value = value;
  }

  @Override
  public String toString() {
      return this.value;
  }

  @JsonValue
  public String value() {
      return this.value;
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
}
