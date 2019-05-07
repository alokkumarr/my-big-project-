
package com.synchronoss.saw.workbench.model.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "took",
    "timed_out",
    "_shards",
    "hits",
    "aggregations"
})
public class SearchESResponse<T> {

    /**
     * The Took Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("took")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer took = 0;
    /**
     * The Timed_out Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("timed_out")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Boolean timedOut = false;
    @JsonProperty("_shards")
    private Shards shards;
    @JsonProperty("hits")
    private Hits<?> hits;
    @JsonProperty("aggregations")
    private Object aggregations;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    
    /**
     * The Took Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("took")
    public Integer getTook() {
        return took;
    }

    /**
     * The Took Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("took")
    public void setTook(Integer took) {
        this.took = took;
    }

    /**
     * The Timed_out Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("timed_out")
    public Boolean getTimedOut() {
        return timedOut;
    }

    /**
     * The Timed_out Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("timed_out")
    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }

    @JsonProperty("_shards")
    public Shards getShards() {
        return shards;
    }

    @JsonProperty("_shards")
    public void setShards(Shards shards) {
        this.shards = shards;
    }

    @JsonProperty("hits")
    public Hits<?> getHits() {
        return hits;
    }

    @JsonProperty("hits")
    public void setHits(Hits<?> hits) {
        this.hits = hits;
    }

    @JsonProperty("aggregations")
    public Object getAggregations() {
        return aggregations;
    }

    @JsonProperty("aggregations")
    public void setAggregations(Object aggregations) {
        this.aggregations = aggregations;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
    }

    @Override
    public String toString(){
        return org.apache.commons.lang3.builder.ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

}
