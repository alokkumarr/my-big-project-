package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

/** This ENUM classs defines the Monitoring Type of Alerts. */
public enum MonitoringType {
  @SerializedName("ROW_METRICS")
  ROW_METRICS("ROW_METRICS"),
  @SerializedName("AGGREGATION_METRICS")
  AGGREGATION_METRICS("AGGREGATION_METRICS"),
  @SerializedName("AGGREGATION_METRICS")
  CONTINUOUS_MONITORING("CONTINUOUS_MONITORING");
  private static final Map<String, MonitoringType> CONSTANTS = new HashMap<>();

  static {
    for (MonitoringType c : values()) {
      CONSTANTS.put(c.value, c);
    }
  }

  private final String value;

  /**
   * MonitoringType Value.
   *
   * @param value MonitoringType
   */
  private MonitoringType(String value) {
    this.value = value;
  }

  /**
   * Fetch value.
   *
   * @param value MonitoringType
   * @return MonitoringType
   */
  @JsonCreator
  public static MonitoringType fromValue(String value) {
    MonitoringType constant = CONSTANTS.get(value);
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
