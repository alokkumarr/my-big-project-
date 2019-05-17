package com.synchronoss.querybuilder.model.globalfilter;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "filters",
        "esRepository"
})
public class GlobalFilter {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("filters")
    private List<Filter> filters = null;

    /**
     *
     */
    @JsonProperty("esRepository")
    private EsRepository esRepository;


    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    /**
     * Gets filters
     *
     * @return value of filters
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Sets filters
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * Gets esRepository
     *
     * @return value of esRepository
     */
    public EsRepository getEsRepository() {
        return esRepository;
    }

    /**
     * Sets esRepository
     */
    public void setEsRepository(EsRepository esRepository) {
        this.esRepository = esRepository;
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
