package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisMetaData {

    @JsonProperty("analysis")
    private List<Analysis> analyses = null;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("analysis")
    public List<Analysis> getAnalyses() {
        return analyses;
    }

    @JsonProperty("analysis")
    public void setData(List<Analysis> analyses) {
        this.analyses = analyses;
    }

    public AnalysisMetaData withAnalyses(List<Analysis> analyses) {
        this.analyses = analyses;
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

    public AnalysisMetaData withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
