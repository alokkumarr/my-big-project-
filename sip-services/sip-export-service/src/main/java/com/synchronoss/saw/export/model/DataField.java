package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "aggregate",
    "columnName",
    "name",
    "type"
})
public class DataField {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("aggregate")
    private DataField.Aggregate aggregate;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("columnName")
    private String columnName;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("name")
    private String name;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    private DataField.Type type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("aggregate")
    public DataField.Aggregate getAggregate() {
        return aggregate;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("aggregate")
    public void setAggregate(DataField.Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    public DataField.Type getType() {
        return type;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    public void setType(DataField.Type type) {
        this.type = type;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Aggregate {

        AVG("avg"),
        SUM("sum"),
        MIN("min"),
        MAX("max"),
        COUNT("count"),
        PERCENTAGE("percentage");
        private final String value;
        private final static Map<String, Aggregate> CONSTANTS = new HashMap<String, Aggregate>();

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

    public enum Type {

        DATE("date"),
        TIMESTAMP("timestamp"),
        LONG("long"),
        DOUBLE("double"),
        INT("integer"),
        STRING("string"),
        FLOAT("float");
        private final String value;
        private final static Map<String, Type> CONSTANTS = new HashMap<String, Type>();

        static {
            for (DataField.Type c: values()) {
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
        public static DataField.Type fromValue(String value) {
            DataField.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
