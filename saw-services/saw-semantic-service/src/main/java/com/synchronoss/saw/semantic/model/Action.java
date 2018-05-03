
package com.synchronoss.saw.semantic.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "verb",
    "content"
})
public class Action {

    /**
     * The Node Category Schema
     * <p>
     * 
     * 
     */
    @JsonProperty("verb")
    private Action.Verb verb = Action.Verb.fromValue("create");
    /**
     * The content
     * <p>
     * 
     * 
     */
    @JsonProperty("content")
    private Content content;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Node Category Schema
     * <p>
     * 
     * 
     */
    @JsonProperty("verb")
    public Action.Verb getVerb() {
        return verb;
    }

    /**
     * The Node Category Schema
     * <p>
     * 
     * 
     */
    @JsonProperty("verb")
    public void setVerb(Action.Verb verb) {
        this.verb = verb;
    }

    /**
     * The content
     * <p>
     * 
     * 
     */
    @JsonProperty("content")
    public Content getContent() {
        return content;
    }

    /**
     * The content
     * <p>
     * 
     * 
     */
    @JsonProperty("content")
    public void setContent(Content content) {
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
        DELETE("delete");
        private final String value;
        private final static Map<String, Action.Verb> CONSTANTS = new HashMap<String, Action.Verb>();

        static {
            for (Action.Verb c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Verb(String value) {
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
        public static Action.Verb fromValue(String value) {
            Action.Verb constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
