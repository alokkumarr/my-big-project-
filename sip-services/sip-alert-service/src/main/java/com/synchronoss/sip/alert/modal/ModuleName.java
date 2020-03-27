package com.synchronoss.sip.alert.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public enum ModuleName {
  @JsonProperty("alert")
  ALERT("alert"),

  @JsonProperty("analyze")
  ANALYZE("analyze");

  private String value;

  private static Map<String, ModuleName> map = new HashMap<>();

  static {
    for (ModuleName d : ModuleName.values()) {
      map.put(d.value, d);
    }
  }

  ModuleName(String value) {
    this.value = value;
  }

  public static ModuleName fromValue(String moduleTypeStr) {
    return map.get(moduleTypeStr);
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
