package com.synchronoss.saw.model.globalfilter;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "globalFilters"
})
public class GlobalFilters {

private List<GlobalFilter> globalFilterList;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Gets globalFilterList
     *
     * @return value of globalFilterList
     */
    @JsonProperty("globalFilters")
    public List<GlobalFilter> getGlobalFilterList() {
        return globalFilterList;
    }

    /**
     * Sets globalFilterList
     */
    @JsonProperty("globalFilters")
    public void setGlobalFilterList(List<GlobalFilter> globalFilterList) {
        this.globalFilterList = globalFilterList;
    }

    /**
     *
     * @return
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     *
     * @param name
     * @param value
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
