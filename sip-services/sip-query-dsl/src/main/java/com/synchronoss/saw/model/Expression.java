package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Expression {
  @JsonProperty("operand1")
  Operand operand1;

  @JsonProperty("operator")
  String operator;

  @JsonProperty("operand2")
  Operand operand2;

  @JsonProperty("operand1")
  public Operand getOperand1() {
    return operand1;
  }

  @JsonProperty("operand1")
  public void setOperand1(Operand operand1) {
    this.operand1 = operand1;
  }

  @JsonProperty("operator")
  public String getOperator() {
    return operator;
  }

  @JsonProperty("operator")
  public void setOperator(String operator) {
    this.operator = operator;
  }

  @JsonProperty("operand2")
  public Operand getOperand2() {
    return operand2;
  }

  @JsonProperty("operand2")
  public void setOperand2(Operand operand2) {
    this.operand2 = operand2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Expression that = (Expression) o;

    return new EqualsBuilder()
        .append(operand1, that.operand1)
        .append(operator, that.operator)
        .append(operand2, that.operand2)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(operand1).append(operator).toHashCode();
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    if (operand1 != null) {
      builder.append(operand1.toString());
    }

    builder.append(" ").append(operator).append(" ");

    if (operand2 != null) {
      builder.append(operand2.toString());
    }

    return builder.toString();
  }
}
