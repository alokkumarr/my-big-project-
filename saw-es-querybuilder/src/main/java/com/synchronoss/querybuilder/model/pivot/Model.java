
package com.synchronoss.querybuilder.model.pivot;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "booleanCriteria",
    "operator",
    "value",
    "otherValue",
    "gte",
    "lte"
})
public class Model {

    @JsonProperty("booleanCriteria")
    private Object booleanCriteria;
    @JsonProperty("operator")
    private Object operator;
    @JsonProperty("value")
    private Object value;
    @JsonProperty("otherValue")
    private Object otherValue;
    @JsonProperty("gte")
    private Object gte;
    @JsonProperty("lte")
    private Object lte;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("booleanCriteria")
    public Object getBooleanCriteria() {
        return booleanCriteria;
    }

    @JsonProperty("booleanCriteria")
    public void setBooleanCriteria(Object booleanCriteria) {
        this.booleanCriteria = booleanCriteria;
    }

    @JsonProperty("operator")
    public Object getOperator() {
        return operator;
    }

    @JsonProperty("operator")
    public void setOperator(Object operator) {
        this.operator = operator;
    }

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Object value) {
        this.value = value;
    }

    @JsonProperty("otherValue")
    public Object getOtherValue() {
        return otherValue;
    }

    @JsonProperty("otherValue")
    public void setOtherValue(Object otherValue) {
        this.otherValue = otherValue;
    }

    @JsonProperty("gte")
    public Object getGte() {
        return gte;
    }

    @JsonProperty("gte")
    public void setGte(Object gte) {
        this.gte = gte;
    }

    @JsonProperty("lte")
    public Object getLte() {
        return lte;
    }

    @JsonProperty("lte")
    public void setLte(Object lte) {
        this.lte = lte;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
