package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"operator", "left", "right"})
public class JoinCondition implements Serializable {

  private static final long serialVersionUID = -5654426544136930172L;
  @JsonProperty("operator")
  private String operator;
  @JsonProperty("left")
  private Left left;
  @JsonProperty("right")
  private Right right;

  @JsonProperty("operator")
  public String getOperator() {
    return operator;
  }

  @JsonProperty("operator")
  public void setOperator(String operator) {
    this.operator = operator;
  }

  @JsonProperty("left")
  public Left getLeft() {
    return left;
  }

  @JsonProperty("left")
  public void setLeft(Left left) {
    this.left = left;
  }

  @JsonProperty("right")
  public Right getRight() {
    return right;
  }

  @JsonProperty("right")
  public void setRight(Right right) {
    this.right = right;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("operator", operator)
        .append("left", left)
        .append("right", right)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(left).append(right).append(operator).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof JoinCondition) == false) {
      return false;
    }
    JoinCondition rhs = ((JoinCondition) other);
    return new EqualsBuilder()
        .append(left, rhs.left)
        .append(right, rhs.right)
        .append(operator, rhs.operator)
        .isEquals();
  }
}
