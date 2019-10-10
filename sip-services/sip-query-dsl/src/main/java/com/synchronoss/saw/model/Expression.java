package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Expression {
  @JsonProperty("value")
  Number value;

  @JsonProperty("aggregate")
  String aggregate;

  @JsonProperty("column")
  String column;

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

  @JsonProperty("value")
  public Number getValue() {
    return value;
  }

  @JsonProperty("value")
  public void setValue(Number value) {
    this.value = value;
  }

  @JsonProperty("aggregate")
  public String getAggregate() {
    return aggregate;
  }

  @JsonProperty("aggregate")
  public void setAggregate(String aggregate) {
    this.aggregate = aggregate;
  }

  @JsonProperty("column")
  public String getColumn() {
    return column;
  }

  @JsonProperty("column")
  public void setColumn(String column) {
    this.column = column;
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
        .append(value, that.value)
        .append(aggregate, that.aggregate)
        .append(column, that.column)
        .append(operand1, that.operand1)
        .append(operator, that.operator)
        .append(operand2, that.operand2)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(value)
        .append(aggregate)
        .append(column)
        .append(operand1)
        .append(operator)
        .append(operand2)
        .toHashCode();
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();

    if (operator == null) {
      // No Operands or operator
      if (value != null) {
        builder.append(value);
      } else if (aggregate != null && column != null) {
        builder.append(aggregate + "(" + column + ")");
      }

    } else {
      if (operand1 != null) {
        builder.append(operand1.toString());
      }

      builder.append(" ").append(operator).append(" ");

      if (operand2 != null) {
        builder.append(operand2.toString());
      }
    }

    return builder.toString();
  }
}
