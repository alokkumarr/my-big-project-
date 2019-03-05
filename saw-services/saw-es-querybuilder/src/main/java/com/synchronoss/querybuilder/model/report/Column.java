package com.synchronoss.querybuilder.model.report;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonPropertyOrder({
    "aggregate",
    "columnName",
    "name",
    "type"
})
public class Column {
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("aggregate")
    private Column.Aggregate aggregate;
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
    private Column.Type type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("aggregate")
    public Column.Aggregate getAggregate() {
        return aggregate;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("aggregate")
    public void setAggregate(Column.Aggregate aggregate) {
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
    public Column.Type getType() {
        return type;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    public void setType(Column.Type type) {
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
        DISTINCT_COUNT("distinctCount"),
        PERCENTAGE("percentage");
        private final String value;
        private final static Map<String, Column.Aggregate> CONSTANTS = new HashMap<>();

        static {
            for (Column.Aggregate c: values()) {
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
        public static Column.Aggregate fromValue(String value) {
            Column.Aggregate constant = CONSTANTS.get(value);
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
        FLOAT("float"),
        INTEGER("integer"),
        STRING("string");
        private final String value;
        private final static Map<String, Column.Type> CONSTANTS = new HashMap<>();

        static {
            for (Column.Type c: values()) {
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
        public static Column.Type fromValue(String value) {
            Column.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }
}
