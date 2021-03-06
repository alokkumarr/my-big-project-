
package com.synchronoss.saw.storage.proxy.model;

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
    "isRuntimeFilter",
    "model",
    "tableName",
    "type"
})
public class Filter {

    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("isRuntimeFilter")
    private Filter.IsRuntimeFilter isRuntimeFilter;
    @JsonProperty("model")
    private Model model;
    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("type")
    private Filter.Type type;
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

    @JsonProperty("isRuntimeFilter")
    public Filter.IsRuntimeFilter getIsRuntimeFilter() {
        return isRuntimeFilter;
    }

    @JsonProperty("isRuntimeFilter")
    public void setIsRuntimeFilter(Filter.IsRuntimeFilter isRuntimeFilter) {
        this.isRuntimeFilter = isRuntimeFilter;
    }

    @JsonProperty("model")
    public Model getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(Model model) {
        this.model = model;
    }

    @JsonProperty("tableName")
    public String getTableName() {
        return tableName;
    }

    @JsonProperty("tableName")
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @JsonProperty("type")
    public Filter.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Filter.Type type) {
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

    public enum IsRuntimeFilter {

        FALSE(false),
        TRUE(true);
        private final boolean value;
        private final static Map<Boolean, Filter.IsRuntimeFilter> CONSTANTS = new HashMap<Boolean, Filter.IsRuntimeFilter>();

        static {
            for (Filter.IsRuntimeFilter c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private IsRuntimeFilter(boolean value) {
            this.value = value;
        }

        @JsonValue
        public boolean value() {
            return this.value;
        }

        @JsonCreator
        public static Filter.IsRuntimeFilter fromValue(boolean value) {
            Filter.IsRuntimeFilter constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

    public enum Type {

        LONG("long"),
        STRING("string"),
        INTEGER("integer"),
        DOUBLE("double"),
        DATE("date"),
        TIMESTAMP("timestamp"),
        FLOAT("float");
        private final String value;
        private final static Map<String, Filter.Type> CONSTANTS = new HashMap<String, Filter.Type>();

        static {
            for (Filter.Type c: values()) {
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
        public static Filter.Type fromValue(String value) {
            Filter.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
