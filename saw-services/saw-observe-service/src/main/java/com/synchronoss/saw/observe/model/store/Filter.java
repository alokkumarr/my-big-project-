
package com.synchronoss.saw.observe.model.store;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "field-path",
    "condition",
    "value"
})
public class Filter {

    /**
     * The Field-path Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("field-path")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String fieldPath = "";
    /**
     * The Condition Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("condition")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Filter.Condition condition;
    /**
     * The Value Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("value")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private String value = "";
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Field-path Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("field-path")
    public String getFieldPath() {
        return fieldPath;
    }

    /**
     * The Field-path Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("field-path")
    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    /**
     * The Condition Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("condition")
    public Filter.Condition getCondition() {
        return condition;
    }

    /**
     * The Condition Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("condition")
    public void setCondition(Filter.Condition condition) {
        this.condition = condition;
    }

    /**
     * The Value Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * The Value Schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Condition {

        EQ("EQ"),
        NE("NE"),
        GT("GT"),
        LT("LT"),
        GE("GE"),
        LE("LE");
        private final String value;
        private final static Map<String, Filter.Condition> CONSTANTS = new HashMap<String, Filter.Condition>();

        static {
            for (Filter.Condition c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Condition(String value) {
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
        public static Filter.Condition fromValue(String value) {
            Filter.Condition constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
