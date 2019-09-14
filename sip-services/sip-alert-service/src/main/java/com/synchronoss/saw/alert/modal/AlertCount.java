package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.synchronoss.saw.model.Model.Preset;
import java.util.HashMap;
import java.util.Map;

public class AlertCount {
  private Preset preset;
  private String startTime;
  private String endTime;
  private AlertCount.GroupBy groupBy;

  public Preset getPreset() {
    return preset;
  }

  public void setPreset(Preset preset) {
    this.preset = preset;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public GroupBy getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(GroupBy groupBy) {
    this.groupBy = groupBy;
  }


  public enum GroupBy {
    STARTTIME("StartTime"),
    SEVERITY("Severity");
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
