
package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "artifactsName",
    "fields"
})
public class Artifact {

    @JsonProperty("artifactsName")
    private String artifactsName;
    @JsonProperty("fields")
    private List<Field> fields = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("artifactsName")
    public String getArtifactsName() {
        return artifactsName;
    }

    @JsonProperty("artifactsName")
    public void setArtifactsName(String artifactsName) {
        this.artifactsName = artifactsName;
    }

    @JsonProperty("fields")
    public List<Field> getFields() {
        return fields;
    }

    @JsonProperty("fields")
    public void setFields(List<Field> fields) {
        this.fields = fields;
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
    public String toString() {
        return new ToStringBuilder(this).append("artifactsName", artifactsName).append("fields", fields)
        		.append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(additionalProperties).append(artifactsName).append(fields)
        		.toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Artifact) == false) {
            return false;
        }
        Artifact rhs = ((Artifact) other);
        return new EqualsBuilder().append(additionalProperties, rhs.additionalProperties)
        		.append(artifactsName, rhs.artifactsName)
        		.append(fields, rhs.fields).isEquals();
    }

}
