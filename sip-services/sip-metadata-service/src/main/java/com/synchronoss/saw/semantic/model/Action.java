package com.synchronoss.saw.semantic.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"verb", "content"})
public class Action {

  /** The Node Category Schema. */
  @JsonProperty("verb")
  private Action.Verb verb = Action.Verb.fromValue("create");
  /** The content. */
  @JsonProperty("content")
  private Object content;

  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  /** The Node Category Schema. */
  @JsonProperty("verb")
  public Action.Verb getVerb() {
    return verb;
  }

  /** The Node Category Schema. */
  @JsonProperty("verb")
  public void setVerb(Action.Verb verb) {
    this.verb = verb;
  }

  /** The content. */
  @JsonProperty("content")
  public Object getContent() {
    return content;
  }

  /** The content. */
  @JsonProperty("content")
  public void setContent(Object content) {
    this.content = content;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  public enum Verb {
    CREATE("create"),
    READ("read"),
    UPDATE("update"),
    INIT("init"),
    DELETE("delete");
    private static final Map<String, Action.Verb> CONSTANTS = new HashMap<String, Action.Verb>();

    static {
      for (Action.Verb c : values()) {
        CONSTANTS.put(c.value, c);
      }
    }

    private final String value;

    private Verb(String value) {
      this.value = value;
    }

    /**
     * Fetch from Action Verb.
     *
     * @param value Verb
     * @return Action Verb
     */
    @JsonCreator
    public static Action.Verb fromValue(String value) {
      Action.Verb constant = CONSTANTS.get(value);
      if (constant == null) {
        throw new IllegalArgumentException(value);
      } else {
        return constant;
      }
    }

    @Override
    public String toString() {
      return this.value;
    }

    @JsonValue
    public String value() {
      return this.value;
    }
  }
}
