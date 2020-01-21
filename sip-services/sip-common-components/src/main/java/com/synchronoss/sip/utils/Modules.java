package com.synchronoss.sip.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum has all the module names.
 *
 * @author alok.kumarr
 * @since 3.5.0
 */
public enum Modules {

  ALERTS("ALERTS"),
  ANALYZE("ANALYZE"),
  OBSERVE("OBSERVE"),
  WORKBENCH("WORKBENCH");

  private static final Map<Modules, String> CONSTANTS = new HashMap<>();

  private final String value;

  Modules(String value) {
    this.value = value;
  }

  static {
    for (Modules c : values()) {
      CONSTANTS.put(c, c.value);
    }
  }
}
