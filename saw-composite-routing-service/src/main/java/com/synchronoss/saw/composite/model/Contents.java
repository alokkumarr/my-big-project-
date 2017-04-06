
package com.synchronoss.saw.composite.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "keys",
    "observe",
    "analyze",
    "alert",
        
})
@JsonRootName(value = "contents")
public class Contents implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 499876471490845497L;
	@JsonProperty("action")
    private Contents.Action action;
    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("alert")
    @JsonPropertyDescription("Allows ANY array but describes nothing.")
    private List<Object> alert = null;
    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("analyze")
    @JsonPropertyDescription("Allows ANY array but describes nothing.")
    private List<Object> analyze = null;
    /**
     * Empty object schema.
     * <p>
     * Allows ANY object but describes nothing.
     * 
     */
    @JsonProperty("keys")
    @JsonPropertyDescription("Allows ANY object but describes nothing.")
    private Keys keys;
    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("observe")
    @JsonPropertyDescription("Allows ANY array but describes nothing.")
    private List<Object> observe = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("action")
    public Contents.Action getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(Contents.Action action) {
        this.action = action;
    }

    public Contents withAction(Contents.Action action) {
        this.action = action;
        return this;
    }

    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("alert")
    public List<Object> getAlert() {
        return alert;
    }

    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("alert")
    public void setAlert(List<Object> alert) {
        this.alert = alert;
    }

    public Contents withAlert(List<Object> alert) {
        this.alert = alert;
        return this;
    }

    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("analyze")
    public List<Object> getAnalyze() {
        return analyze;
    }

    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("analyze")
    public void setAnalyze(List<Object> analyze) {
        this.analyze = analyze;
    }

    public Contents withAnalyze(List<Object> analyze) {
        this.analyze = analyze;
        return this;
    }

    /**
     * Empty object schema.
     * <p>
     * Allows ANY object but describes nothing.
     * 
     */
    @JsonProperty("keys")
    public Keys getKeys() {
        return keys;
    }

    /**
     * Empty object schema.
     * <p>
     * Allows ANY object but describes nothing.
     * 
     */
    @JsonProperty("keys")
    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public Contents withKeys(Keys keys) {
        this.keys = keys;
        return this;
    }

    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
    @JsonProperty("observe")
    public List<Object> getObserve() {
        return observe;
    }

    /**
     * Empty array schema.
     * <p>
     * Allows ANY array but describes nothing.
     * 
     */
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
    


	public enum Action {

        CREATE("create"),
        UPDATE("update"),
        READ("read"),
        DELETE("delete");
        private final String value;
        private final static Map<String, Contents.Action> CONSTANTS = new HashMap<String, Contents.Action>();

        static {
            for (Contents.Action c: values()) {
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
        public static Contents.Action fromValue(String value) {
            Contents.Action constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
