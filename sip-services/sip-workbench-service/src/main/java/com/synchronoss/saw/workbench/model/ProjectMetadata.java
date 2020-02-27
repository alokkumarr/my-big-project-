package com.synchronoss.saw.workbench.model;

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
    "_id",
    "description",
    "allowableTags",
    "projectLevelParameters"
})
public class ProjectMetadata {

    @JsonProperty("_id")
    private String projectId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("allowableTags")
    private String[] allowableTags;

    @JsonProperty("projectLevelParameters")
    private Object[] projectLevelParameters;

    @JsonProperty("_id")
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty("_id")
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @JsonProperty("allowableTags")
    public String[] getAllowableTags() {
        return allowableTags;
    }
    @JsonProperty("allowableTags")
    public void setAllowableTags(String[] allowableTags) {
        this.allowableTags = allowableTags;
    }
    @JsonProperty("projectLevelParameters")
    public Object[] getProjectLevelParameters() {
        return projectLevelParameters;
    }
    @JsonProperty("projectLevelParameters")
    public void setProjectLevelParameters(Object[] projectLevelParameters) {
        this.projectLevelParameters = projectLevelParameters;
    }

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
