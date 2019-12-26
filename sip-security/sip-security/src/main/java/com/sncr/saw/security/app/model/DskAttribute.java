package com.sncr.saw.security.app.model;

import java.util.Set;

public class DskAttribute {
  private String name;
  private Set<String> values;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getValues() {
    return values;
  }

  public void setValues(Set<String> values) {
    this.values = values;
  }
}
