
package com.synchronoss.querybuilder.model.kpi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "columnName",
    "name",
    "aggregate"
})
public class DataField {

    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("aggregate")
    private List<Aggregate> aggregate = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Get column name
     * @return
     */
    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    /**
     *
     * @param columnName
     */
    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("aggregate")
    public List<Aggregate> getAggregate() {
        return aggregate;
    }

    @JsonProperty("aggregate")
    public void setAggregate(List<Aggregate> aggregate) {
        this.aggregate = aggregate;
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
        return new ToStringBuilder(this).append("columnName", columnName).append("name", name).append("aggregate", aggregate).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(aggregate).append(additionalProperties).append(name).append(columnName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DataField) == false) {
            return false;
        }
        DataField rhs = ((DataField) other);
        return new EqualsBuilder().append(aggregate, rhs.aggregate).append(additionalProperties, rhs.additionalProperties).append(name, rhs.name).append(columnName, rhs.columnName).isEquals();
    }

    public enum Aggregate {

        AVG("avg"),
        SUM("sum"),
        MIN("min"),
        MAX("max"),
        COUNT("count"),
        PERCENTAGE("percentage");
        private final String value;
        private final static Map<String, DataField.Aggregate> CONSTANTS = new HashMap<String, DataField.Aggregate>();

        static {
            for (DataField.Aggregate c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Aggregate(String value) {
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
        public static DataField.Aggregate fromValue(String value) {
            DataField.Aggregate constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
