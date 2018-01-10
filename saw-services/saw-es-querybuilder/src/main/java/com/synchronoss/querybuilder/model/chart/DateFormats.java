package com.synchronoss.querybuilder.model.chart;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DateFormats {

  DATE("date"),
  TIMESTAMP("timestamp"),
  LONG("long"),
  DOUBLE("double"),
  INTEGER("integer"),
  STRING("string"),
  FLOAT("float");
  private final String value;
  private final static Map<String, DateFormats> CONSTANTS = new HashMap<String, DateFormats>();

  static {
      for (DateFormats c: values()) {
          CONSTANTS.put(c.value, c);
      }
  }

  private DateFormats(String value) {
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
  public static DateFormats fromValue(String value) {
    DateFormats constant = CONSTANTS.get(value);
      if (constant == null) {
          throw new IllegalArgumentException(value);
      } else {
          return constant;
      }
  }
}
