package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "columnName",
    "groupInterval",
    "type"
})
public class ColumnField {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("groupInterval")
    private ColumnField.GroupInterval groupInterval;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    private ColumnField.Type type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @JsonProperty("groupInterval")
    public ColumnField.GroupInterval getGroupInterval() {
        return groupInterval;
    }

    @JsonProperty("groupInterval")
    public void setGroupInterval(ColumnField.GroupInterval groupInterval) {
        this.groupInterval = groupInterval;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    public ColumnField.Type getType() {
        return type;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("type")
    public void setType(ColumnField.Type type) {
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

    public enum GroupInterval {

        YEAR("year"),
        MONTH("month"),
        DAY("day"),
        QUARTER("quarter"),
        HOUR("hour"),
        WEEK("week");
        private final String value;
        private final static Map<String, GroupInterval> CONSTANTS = new HashMap<String, GroupInterval>();

        static {
            for (ColumnField.GroupInterval c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private GroupInterval(String value) {
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
        public static ColumnField.GroupInterval fromValue(String value) {
            ColumnField.GroupInterval constant = CONSTANTS.get(value);
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
            for (ColumnField.Type c: values()) {
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
        public static ColumnField.Type fromValue(String value) {
            ColumnField.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
