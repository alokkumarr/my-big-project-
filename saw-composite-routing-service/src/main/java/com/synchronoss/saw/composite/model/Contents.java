
package com.synchronoss.saw.composite.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

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
    "actions",
    "alerts",
    "analyze",
    "keys",
    "observe"
})
@Component
public class Contents {

    /**
     * The Actions schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("actions")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Contents.Actions actions = Contents.Actions.fromValue("create");
    @JsonProperty("alerts")
    private List<Object> alerts = null;
    @JsonProperty("analyze")
    private List<Object> analyze = null;
    @JsonProperty("keys")
    private Keys keys;
    @JsonProperty("observe")
    private List<Object> observe = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Actions schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("actions")
    public Contents.Actions getActions() {
        return actions;
    }

    /**
     * The Actions schema.
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("actions")
    public void setActions(Contents.Actions actions) {
        this.actions = actions;
    }

    public Contents withActions(Contents.Actions actions) {
        this.actions = actions;
        return this;
    }

    @JsonProperty("alerts")
    public List<Object> getAlerts() {
        return alerts;
    }

    @JsonProperty("alerts")
    public void setAlerts(List<Object> alerts) {
        this.alerts = alerts;
    }

    public Contents withAlerts(List<Object> alerts) {
        this.alerts = alerts;
        return this;
    }

    @JsonProperty("analyze")
    public List<Object> getAnalyze() {
        return analyze;
    }

    @JsonProperty("analyze")
    public void setAnalyze(List<Object> analyze) {
        this.analyze = analyze;
    }

    public Contents withAnalyze(List<Object> analyze) {
        this.analyze = analyze;
        return this;
    }

    @JsonProperty("keys")
    public Keys getKeys() {
        return keys;
    }

    @JsonProperty("keys")
    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public Contents withKeys(Keys keys) {
        this.keys = keys;
        return this;
    }

    @JsonProperty("observe")
    public List<Object> getObserve() {
        return observe;
    }

    @JsonProperty("observe")
    public void setObserve(List<Object> observe) {
        this.observe = observe;
    }

    public Contents withObserve(List<Object> observe) {
        this.observe = observe;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    

    public Contents withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public enum Actions {

        DELETE("delete"),
        CREATE("create"),
        UPDATE("update"),
        SEARCH("search");
        private final String value;
        private final static Map<String, Contents.Actions> CONSTANTS = new HashMap<String, Contents.Actions>();

        static {
            for (Contents.Actions c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Actions(String value) {
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
        public static Contents.Actions fromValue(String value) {
            Contents.Actions constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
