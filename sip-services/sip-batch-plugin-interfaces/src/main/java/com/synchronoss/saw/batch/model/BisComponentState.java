package com.synchronoss.saw.batch.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum BisComponentState {
  OPEN("OPEN"),DATA_RECEIVED("DATA_RECEIVED"), DATA_REMOVED("DATA_REMOVED"),
  DATA_INPROGRESS("DATA_INPROGRESS"),
  DUPLICATE("DUPLICATE"),
  FAILED("FAILED"),
  HOST_NOT_REACHABLE("HOST_NOT_REACHABLE"),
  HOST_NOT_REACHABLE_OBSOLETE("HOST_NOT_REACHABLE_OBSOLETE");
  private final String value;
  private static final Map<String, BisComponentState> CONSTANTS =
      new HashMap<String, BisComponentState>();

  static {
    for (BisComponentState c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private BisComponentState(String value) {
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

  /**
   * Getting enum values.
   */
  @JsonCreator
  public static BisComponentState fromValue(String value) {
    BisComponentState constant = CONSTANTS.get(value);
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }
}
