
package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "artifacts",
    "columnName",
    "type",
    "order"
})
public class Sort {

    @JsonProperty("artifacts")
    private String artifacts;
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("order")
    private Order order;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("artifacts")
    public String getArtifacts() {
        return artifacts;
    }

    @JsonProperty("artifacts")
    public void setArtifacts(String artifacts) {
        this.artifacts = artifacts;
    }

    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("order")
    public Order getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(Order order) {
        this.order = order;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Order {

        DESC("desc"),
        ASC("asc");
        private final String value;
        private final static Map<String, Sort.Order> CONSTANTS = new HashMap<String, Sort.Order>();

        static {
            for (Sort.Order c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Order(String value) {
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
        public static Sort.Order fromValue(String value) {
            Sort.Order constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Type {

        DATE("date"),
        TIMESTAMP("timestamp"),
        LONG("long"),
        DOUBLE("double"),
        INTEGER("integer"),
        STRING("string"),
        FLOAT("float");
        private final String value;
        private final static Map<String, Sort.Type> CONSTANTS = new HashMap<String, Sort.Type>();

        static {
            for (Sort.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
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
        public static Sort.Type fromValue(String value) {
            Sort.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("artifacts", artifacts).append("columnName", columnName)
        		.append("type", type).append("order", order)
        		.append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(order).append(additionalProperties).append(columnName)
        		.append(type).append(artifacts).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Sort) == false) {
            return false;
        }
        Sort rhs = ((Sort) other);
        return new EqualsBuilder().append(order, rhs.order)
        		.append(additionalProperties, rhs.additionalProperties).append(columnName, rhs.columnName).append(type, rhs.type).append(artifacts, rhs.artifacts).isEquals();
    }

}
