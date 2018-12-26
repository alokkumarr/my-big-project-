
package com.synchronoss.querybuilder.model.chart;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonProperty("limitType")
    private DataField.LimitType limitType;

    @JsonProperty("limitValue")
    private Integer limitValue;

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

    /**
     * Gets limitType
     *
     * @return value of limitType
     */
    public LimitType getLimitType() {
        return limitType;
    }

    /**
     * Sets limitType
     */
    public void setLimitType(LimitType limitType) {
        this.limitType = limitType;
    }

    /**
     * Gets limitValue
     *
     * @return value of limitValue
     */
    public Integer getLimitValue() {
        return limitValue;
    }

    /**
     * Sets limitValue
     */
    public void setLimitValue(Integer limitValue) {
        this.limitValue = limitValue;
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
        PERCENTAGE("percentage"),
        PERCENTAGE_BY_ROW("percentageByRow");
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

    public enum Type {

        DATE("date"),
        TIMESTAMP("timestamp"),
        LONG("long"),
        DOUBLE("double"),
        FLOAT("float"),
        INTEGER("integer"),
        STRING("string");
        private final String value;
        private final static Map<String, DataField.Type> CONSTANTS = new HashMap<String, DataField.Type>();

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

    public enum LimitType {

        TOP("top"),
        BOTTOM("bottom");
        private final String value;
        private final static Map<String, DataField.LimitType> CONSTANTS = new HashMap<>();

        static {
            for (DataField.LimitType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private LimitType(String value) {
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
        public static DataField.LimitType fromValue(String value) {
            DataField.LimitType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
