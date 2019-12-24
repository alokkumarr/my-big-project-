package com.sncr.saw.security.app.model;

public enum BooleanCriteria {
  AND("AND"),
  OR("OR");
  private String value;

  BooleanCriteria(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
