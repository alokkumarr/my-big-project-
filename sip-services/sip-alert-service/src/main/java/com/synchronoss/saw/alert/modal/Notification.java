package com.synchronoss.saw.alert.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({
    "type",
    "recipients",
})
public class Notification {

  @JsonProperty("type")
  Type type;

  @JsonProperty("recipients")
  List<String> recipients;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public List<String> getRecipients() {
    return recipients;
  }

  public void setRecipients(List<String> recipients) {
    this.recipients = recipients;
  }

  public enum Type {
    SLACK("slack"),
    EMAIL("email"),
    WEBHOOK("webhook");

    private final String value;
    private static final Map<String, Type> CONSTANTS = new HashMap<>();

    static {
      for (Type c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private Type(String value) {
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

    /**
     * returns the type from the string.
     *
     * @param value enum value
     * @return type
     */
    @JsonCreator
    public static Type fromValue(String value) {
      Type constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }
  }
}
