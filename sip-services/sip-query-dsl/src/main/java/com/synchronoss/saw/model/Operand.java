package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

class Operand {
  @JsonProperty("value")
  Number value;

  @JsonProperty("aggregation")
  String aggregation;

  @JsonProperty("columnm")
  String column;

  @JsonProperty("operator")
  String operator;

  @JsonProperty("operand1")
  Operand operand1;

  @JsonProperty("operand2")
  Operand operand2;

  @JsonProperty("value")
  public Number getValue() {
    return value;
  }

  @JsonProperty("value")
  public void setValue(Number value) {
    this.value = value;
  }

  @JsonProperty("aggregation")
  public String getAggregation() {
    return aggregation;
  }

  @JsonProperty("aggregation")
  public void setAggregation(String aggregation) {
    this.aggregation = aggregation;
  }

  @JsonProperty("column")
  public String getColumn() {
    return column;
  }

  @JsonProperty("column")
  public void setColumn(String column) {
    this.column = column;
  }

  @JsonProperty("operator")
  public String getOperator() {
    return operator;
  }

  @JsonProperty("operator")
  public void setOperator(String operator) {
    this.operator = operator;
  }

  @JsonProperty("operand1")
  public Operand getOperand1() {
    return operand1;
  }

  @JsonProperty("operand2")
  public void setOperand1(Operand operand1) {
    this.operand1 = operand1;
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
  public String toString() {
    StringBuilder builder = new StringBuilder();

    if (this.value != null) {
      builder.append(this.value);
    } else if (this.aggregation != null) {
      builder.append(aggregation).append("(").append(this.column).append(")");
    } else {
      builder.append("(");
      if (operand1 != null) {
        builder.append(operand1.toString());
      }

      builder.append(" ").append(operator).append(" ");

      if (operand2 != null) {
        builder.append(operand2.toString());
      }
      builder.append(")");
    }

    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Operand operand = (Operand) o;

    return new EqualsBuilder()
        .append(value, operand.value)
        .append(aggregation, operand.aggregation)
        .append(column, operand.column)
        .append(operator, operand.operator)
        .append(operand1, operand.operand1)
        .append(operand2, operand.operand2)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(value)
        .append(aggregation)
        .append(column)
        .append(operator)
        .append(operand1)
        .append(operand2)
        .toHashCode();
  }
}
