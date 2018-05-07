package sncr.bda.store.generic.schema;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Action {
  CREATE("create"),
  DELETE("delete"),
  READ("read"),
  UPDATE("update"),
  SEARCH("search");
  private final String value;
  private final static Map<String, Action> CONSTANTS = new HashMap<String, Action>();

  static {
  for (Action c: values()) {
  CONSTANTS.put(c.value, c);
  }
  }

  private Action(String value) {
  this.value = value;
  }

  @Override
  public String toString() {
  return this.value;
  }

  @JsonValue
  public String value() {
  return this.value;
  }

  @JsonCreator
  public static Action fromValue(String value) {
  Action constant = CONSTANTS.get(value);
  if (constant == null) {
  throw new IllegalArgumentException(value);
  } else {
  return constant;
  }
  }
}
