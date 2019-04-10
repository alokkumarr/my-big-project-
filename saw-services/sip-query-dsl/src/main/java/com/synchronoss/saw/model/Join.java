package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"join", "criteria"})
public class Join {

  @JsonProperty("join")
  private JoinType joinType;

  @JsonProperty("criteria")
  private List<Criteria> criteria = null;

  @JsonProperty("join")
  public JoinType getJoinType() {
    return joinType;
  }

  @JsonProperty("join")
  public void setJoinType(JoinType joinType) {
    this.joinType = joinType;
  }

  @JsonProperty("criteria")
  public List<Criteria> getCriteria() {
    return criteria;
  }

  @JsonProperty("criteria")
  public void setCriteria(List<Criteria> criteria) {
    this.criteria = criteria;
  }

  public enum JoinType {
    INNER("inner"),
    LEFT("left"),
    RIGHT("right");
    private static final Map<String, Join.JoinType> CONSTANTS = new HashMap<>();

    static {
      for (Join.JoinType c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private JoinType(String value) {
      this.value = value;
    }

    @JsonCreator
    public static JoinType fromValue(String value) {
      JoinType constant = CONSTANTS.get(value.toLowerCase());
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
