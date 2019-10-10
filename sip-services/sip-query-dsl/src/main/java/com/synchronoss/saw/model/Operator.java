package com.synchronoss.saw.model;

public enum Operator {
  ADD("+"),
  SUBTRACT("-"),
  MULTIPLY("*"),
  DIVIDE("/"),
  MODULUS("%");

  String operation;

  Operator(String operation) {
    this.operation = operation;
  }

  public String getOperation() {
    return this.operation;
  }

  public String toString() {
    return this.operation;
  }
}
