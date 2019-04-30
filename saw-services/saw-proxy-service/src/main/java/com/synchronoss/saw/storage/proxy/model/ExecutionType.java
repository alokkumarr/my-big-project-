package com.synchronoss.saw.storage.proxy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public enum ExecutionType {
  @SerializedName("onetime")
  onetime("onetime"),
  @SerializedName("scheduled")
  scheduled("scheduled"),
  @SerializedName("preview")
  preview("preview"),
  @SerializedName("publish")
  publish("publish"),
  @SerializedName("regularExecution")
  regularExecution("regularExecution");
  private static final Map<String, ExecutionType> CONSTANTS = new HashMap<>();

  static {
    for (ExecutionType c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  ExecutionType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ExecutionType fromValue(String value) {
    ExecutionType constant = CONSTANTS.get(value);
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
