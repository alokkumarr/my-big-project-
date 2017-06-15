
package com.synchronoss.querybuilder.model.chart;

import java.util.HashMap;
import java.util.List;
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
    "operator",
    "value",
    "otherValue",
    "gte",
    "lte",
    "modelValues"
})
public class Model {

    @JsonProperty("operator")
    private Model.Operator operator;
    @JsonProperty("value")
    private Integer value;
    @JsonProperty("otherValue")
    private Integer otherValue;
    @JsonProperty("gte")
    private Integer gte;
    @JsonProperty("lte")
    private Integer lte;
    @JsonProperty("modelValues")
    private List<Object> modelValues = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("operator")
    public Model.Operator getOperator() {
        return operator;
    }

    @JsonProperty("operator")
    public void setOperator(Model.Operator operator) {
        this.operator = operator;
    }

    @JsonProperty("value")
    public Integer getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Integer value) {
        this.value = value;
    }

    @JsonProperty("otherValue")
    public Integer getOtherValue() {
        return otherValue;
    }

    @JsonProperty("otherValue")
    public void setOtherValue(Integer otherValue) {
        this.otherValue = otherValue;
    }

    @JsonProperty("gte")
    public Integer getGte() {
        return gte;
    }

    @JsonProperty("gte")
    public void setGte(Integer gte) {
        this.gte = gte;
    }

    @JsonProperty("lte")
    public Integer getLte() {
        return lte;
    }

    @JsonProperty("lte")
    public void setLte(Integer lte) {
        this.lte = lte;
    }

    @JsonProperty("modelValues")
    public List<Object> getModelValues() {
        return modelValues;
    }

    @JsonProperty("modelValues")
    public void setModelValues(List<Object> modelValues) {
        this.modelValues = modelValues;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Operator {

        GT("GT"),
        LT("LT"),
        GTE("GTE"),
        LTE("LTE"),
        EQ("EQ"),
        NEQ("NEQ"),
        BTW("BTW");
        private final String value;
        private final static Map<String, Model.Operator> CONSTANTS = new HashMap<String, Model.Operator>();

        static {
            for (Model.Operator c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Operator(String value) {
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
        public static Model.Operator fromValue(String value) {
            Model.Operator constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
