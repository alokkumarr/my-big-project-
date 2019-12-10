package com.sncr.saw.security.common.bean.repo.dsk;

public enum Conjunction {
  AND("AND"),
  OR ("OR");

  private String value;

  Conjunction(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
