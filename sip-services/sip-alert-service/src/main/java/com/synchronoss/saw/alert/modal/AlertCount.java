package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.synchronoss.saw.model.Model.Preset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertCount {


  private AlertCount.GroupBy groupBy;
  private List<AlertFilter> filters;


  /**
   * Gets groupby.
   *
   * @return groupby
   */
  public GroupBy getGroupBy() {
    return groupBy;
  }

  /** Sets groupby. */
  public void setGroupBy(GroupBy groupBy) {
    this.groupBy = groupBy;
  }

  /**
   * Gets filter.
   *
   * @return groupby
   */
  public List<AlertFilter> getFilters() {
    return filters;
  }

  /** Sets filter. */
  public void setFilters(List<AlertFilter> filters) {
    this.filters = filters;
  }

  public enum GroupBy {
    DATE("date"),
    SEVERITY("Severity"),
    ATTRIBUTEVALUE("AttributeValue");

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
