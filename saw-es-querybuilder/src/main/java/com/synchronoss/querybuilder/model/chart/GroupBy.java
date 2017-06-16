
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
    "columnName",
    "type",
    "tableName",
    "name",
    "groupInterval"
})
public class GroupBy {

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
    @JsonProperty("type")
    private GroupBy.Type type;
    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("groupInterval")
    private GroupBy.GroupInterval groupInterval;
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

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public GroupBy.Type getType() {
        return type;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("type")
    public void setType(GroupBy.Type type) {
        this.type = type;
    }

    @JsonProperty("tableName")
    public String getTableName() {
        return tableName;
    }

    @JsonProperty("tableName")
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("groupInterval")
    public GroupBy.GroupInterval getGroupInterval() {
        return groupInterval;
    }

    @JsonProperty("groupInterval")
    public void setGroupInterval(GroupBy.GroupInterval groupInterval) {
        this.groupInterval = groupInterval;
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
        private final static Map<String, GroupBy.GroupInterval> CONSTANTS = new HashMap<String, GroupBy.GroupInterval>();

        static {
            for (GroupBy.GroupInterval c: values()) {
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
        public static GroupBy.GroupInterval fromValue(String value) {
            GroupBy.GroupInterval constant = CONSTANTS.get(value);
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
        INT("int"),
        STRING("string"),
        FLOAT("float");
        private final String value;
        private final static Map<String, GroupBy.Type> CONSTANTS = new HashMap<String, GroupBy.Type>();

        static {
            for (GroupBy.Type c: values()) {
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
        public static GroupBy.Type fromValue(String value) {
            GroupBy.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
