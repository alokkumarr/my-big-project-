
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
    "name"
})
public class GroupBy {

    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("type")
    private GroupBy.Type type;
    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("columnName")
    public String getColumnName() {
        return columnName;
    }

    @JsonProperty("columnName")
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @JsonProperty("type")
    public GroupBy.Type getType() {
        return type;
    }

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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public enum Type {

        DATE("date"),
        TIMESTAMP("timestamp"),
        LONG("long"),
        DOUBLE("double"),
        INT("int"),
        STRING("string");
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
