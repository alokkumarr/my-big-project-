
package com.synchronoss.querybuilder.model;

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
    "lte",
    "gte"
})
public class Range {

    @JsonProperty("lte")
    private String lte;
    @JsonProperty("gte")
    private String gte;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("lte")
    public String getLte() {
        return lte;
    }

    @JsonProperty("lte")
    public void setLte(String lte) {
        this.lte = lte;
    }

    @JsonProperty("gte")
    public String getGte() {
        return gte;
    }

    @JsonProperty("gte")
    public void setGte(String gte) {
        this.gte = gte;
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
