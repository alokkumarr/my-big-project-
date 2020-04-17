package com.synchronoss.bda.sip.dsk;

public enum Operator {
  ISIN("ISIN"),
  EQ("EQ");

  private String value;

  Operator(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
