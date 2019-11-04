package com.synchronoss.saw.apipull.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE");
  private final String value;
  private static final Map<String, HttpMethod> CONSTANTS =
      new HashMap<String, HttpMethod>();

  static {
    for (HttpMethod c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private HttpMethod(String value) {
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
  public static HttpMethod fromValue(String value) {
    HttpMethod constant = CONSTANTS.get(value);
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }
}
