package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class AlertCount {
  private AlertCount.Preset preset;
  private Long startTime;
  private Long endTime;
  private AlertCount.GroupBy groupBy;

  public Preset getPreset() {
    return preset;
  }

  public void setPreset(Preset preset) {
    this.preset = preset;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public GroupBy getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(GroupBy groupBy) {
    this.groupBy = groupBy;
  }

  public enum Preset {
    Yesterday("Yesterday"),
    Today("Today"),
    YTD("YTD"),
    MTD("MTD"),
    LTM("LTM"),
    LSM("LSM"),
    LM("LM"),
    LQ("LQ"),
    LY("LY"),
    LW("LW"),
    TW("TW"),
    LSW("LSW"),
    LTW("LTW"),
    BTW("BTW");
    private final String value;
    private static final Map<String, AlertCount.Preset> CONSTANTS =
        new HashMap<String, AlertCount.Preset>();

    static {
      for (AlertCount.Preset c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private Preset(String value) {
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
     * Creates Preset Object from a string value.
     *
     * @param value Enum value in String
     * @return
     */
    @JsonCreator
    public static AlertCount.Preset fromValue(String value) {
      AlertCount.Preset constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }

  public enum GroupBy {
    Date("StartTime"),
    Severity("Severity");
    private final String value;
    private static final Map<String, AlertCount.GroupBy> CONSTANTS =
        new HashMap<String, AlertCount.GroupBy>();

    static {
      for (AlertCount.GroupBy c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private GroupBy(String value) {
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
     * Creates GroupBy Object from a string value.
     *
     * @param value Enum value in String
     * @return
     */
    @JsonCreator
    public static AlertCount.GroupBy fromValue(String value) {
      AlertCount.GroupBy constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }
}
