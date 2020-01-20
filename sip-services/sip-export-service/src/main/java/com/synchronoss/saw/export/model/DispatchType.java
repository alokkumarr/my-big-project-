package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum DispatchType {
  FTP("ftp"),
  MAIL("mail"),
  S3("s3");

  private final String value;
  private static final Map<String, DispatchType> CONSTANTS = new HashMap<String, DispatchType>();

  static {
    for (DispatchType c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private DispatchType(String value) {
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

  /** Getting Enum value. */
  @JsonCreator
  public static DispatchType fromValue(String value) {
    DispatchType constant = CONSTANTS.get(value);
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }
}
