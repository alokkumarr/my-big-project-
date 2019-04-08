package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"artifactsName", "columnName", "side"})
public class Criteria {

  @JsonProperty("artifactsName")
  private String artifactsName;

  @JsonProperty("columnName")
  private String columnName;

  @JsonProperty("side")
  private Side side;

  @JsonProperty("artifactsName")
  public String getArtifactsName() {
    return artifactsName;
  }

  @JsonProperty("artifactsName")
  public void setArtifactsName(String artifactsName) {
    this.artifactsName = artifactsName;
  }

  @JsonProperty("columnName")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("columnName")
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  @JsonProperty("side")
  public Side getSide() {
    return side;
  }

  @JsonProperty("side")
  public void setSide(Side side) {
    this.side = side;
  }

  public enum Side {
    LEFT("left"),
    RIGHT("right");

    private static final Map<String, Criteria.Side> CONSTANTS = new HashMap<>();

    static {
      for (Criteria.Side c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Side(String value) {
      this.value = value;
    }

    @JsonCreator
    public static Side fromValue(String value) {
      Side constant = CONSTANTS.get(value.toLowerCase());
      if (constant == null) {
        throw new IllegalArgumentException("Join type not implemented: " + value);
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
}
