package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum DispatchMethod {
  DISPATCH_TO_MAIL("dispatchToMail"),
  DISPATCH_TO_S3("dispatchToS3"),
  DISPATCH_TO_FTP("dispatchToFtp");

  private final String value;
  private static final Map<String, DispatchMethod> CONSTANTS =
      new HashMap<String, DispatchMethod>();

  static {
    for (DispatchMethod c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private DispatchMethod(String value) {
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
  public static DispatchMethod fromValue(String value) {
    DispatchMethod constant = CONSTANTS.get(value);
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }
}
