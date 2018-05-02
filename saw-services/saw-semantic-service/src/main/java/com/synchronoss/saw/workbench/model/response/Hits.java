
package com.synchronoss.saw.workbench.model.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "total",
    "max_score",
    "hits"
})
public class Hits<T> {

    /**
     * The Total Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("total")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Integer total = 0;
    /**
     * The Max_score Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("max_score")
    @JsonPropertyDescription("An explanation about the purpose of this instance.")
    private Double maxScore = 0.0D;
    @JsonProperty("hits")
    private List<Hit<?>> hits = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Total Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    /**
     * The Total Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * The Max_score Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("max_score")
    public Double getMaxScore() {
        return maxScore;
    }

    /**
     * The Max_score Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("max_score")
    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    @JsonProperty("hits")
    public List<Hit<?>> getHits() {
        return hits;
    }

    @JsonProperty("hits")
    public void setHits(List<Hit<?>> hits) {
        this.hits = hits;
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
