
package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "artifacts",
    "booleanCriteria",
    "filters",
    "sorts",
    "joins",
    "store"
})
public class SipQuery {

    @JsonProperty("artifacts")
    private List<Artifact> artifacts = null;
    @JsonProperty("booleanCriteria")
    private BooleanCriteria booleanCriteria;
    @JsonProperty("filters")
    private List<Filter> filters = null;
    @JsonProperty("sorts")
    private List<Sort> sorts = null;
    @JsonProperty("joins")
    private List<Join> joins = null;
    @JsonProperty("store")
    private Store store;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("artifacts")
    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    @JsonProperty("artifacts")
    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @JsonProperty("booleanCriteria")
    public BooleanCriteria getBooleanCriteria() {
        return booleanCriteria;
    }

    @JsonProperty("booleanCriteria")
    public void setBooleanCriteria(BooleanCriteria booleanCriteria) {
        this.booleanCriteria = booleanCriteria;
    }

    @JsonProperty("filters")
    public List<Filter> getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @JsonProperty("sorts")
    public List<Sort> getSorts() {
        return sorts;
    }

    @JsonProperty("sorts")
    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    @JsonProperty("joins")
    public List<Join> getJoins() {
        return joins;
    }

    @JsonProperty("joins")
    public void setJoins(List<Join> joins) {
        this.joins = joins;
    }

    @JsonProperty("store")
    public Store getStore() {
        return store;
    }

    @JsonProperty("store")
    public void setStore(Store store) {
        this.store = store;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum BooleanCriteria {

        AND("AND"),
        OR("OR");
        private final String value;
        private final static Map<String, SipQuery.BooleanCriteria> CONSTANTS = new HashMap<String, SipQuery.BooleanCriteria>();

        static {
            for (SipQuery.BooleanCriteria c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private BooleanCriteria(String value) {
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
        public static SipQuery.BooleanCriteria fromValue(String value) {
            SipQuery.BooleanCriteria constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("artifacts", artifacts).append("booleanCriteria", booleanCriteria)
        		.append("filters", filters).append("sorts", sorts).append("joins",joins).append("store", store)
        		.append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(store).append(additionalProperties).append(booleanCriteria)
        		.append(sorts).append(filters).append(joins).append(artifacts).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SipQuery) == false) {
            return false;
        }
        SipQuery rhs = ((SipQuery) other);
        return new EqualsBuilder().append(store, rhs.store).append(additionalProperties, rhs.additionalProperties)
        		.append(booleanCriteria, rhs.booleanCriteria).append(sorts, rhs.sorts).append(joins,rhs.joins).append(filters, rhs.filters)
        		.append(artifacts, rhs.artifacts).isEquals();
    }

}
