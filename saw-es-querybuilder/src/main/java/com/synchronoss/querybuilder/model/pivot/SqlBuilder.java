
package com.synchronoss.querybuilder.model.pivot;

import java.util.HashMap;
import java.util.List;
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
    "columnFields",
    "dataFields",
    "booleanCriteria",
    "filters",
    "rowFields",
    "sorts"
})
public class SqlBuilder {

    @JsonProperty("columnFields")
    private List<ColumnField> columnFields = null;
    @JsonProperty("dataFields")
    private List<DataField> dataFields = null;
    @JsonProperty("booleanCriteria")
    private SqlBuilder.BooleanCriteria booleanCriteria;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("filters")
    private List<Filter> filters = null;
    @JsonProperty("rowFields")
    private List<RowField> rowFields = null;
    @JsonProperty("sorts")
    private List<Sort> sorts = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("columnFields")
    public List<ColumnField> getColumnFields() {
        return columnFields;
    }

    @JsonProperty("columnFields")
    public void setColumnFields(List<ColumnField> columnFields) {
        this.columnFields = columnFields;
    }

    @JsonProperty("dataFields")
    public List<DataField> getDataFields() {
        return dataFields;
    }

    @JsonProperty("dataFields")
    public void setDataFields(List<DataField> dataFields) {
        this.dataFields = dataFields;
    }

    @JsonProperty("booleanCriteria")
    public SqlBuilder.BooleanCriteria getBooleanCriteria() {
        return booleanCriteria;
    }

    @JsonProperty("booleanCriteria")
    public void setBooleanCriteria(SqlBuilder.BooleanCriteria booleanCriteria) {
        this.booleanCriteria = booleanCriteria;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("filters")
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("filters")
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @JsonProperty("rowFields")
    public List<RowField> getRowFields() {
        return rowFields;
    }

    @JsonProperty("rowFields")
    public void setRowFields(List<RowField> rowFields) {
        this.rowFields = rowFields;
    }

    @JsonProperty("sorts")
    public List<Sort> getSorts() {
        return sorts;
    }

    @JsonProperty("sorts")
    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
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

        OR("OR"),
        AND("AND");
        private final String value;
        private final static Map<String, SqlBuilder.BooleanCriteria> CONSTANTS = new HashMap<String, SqlBuilder.BooleanCriteria>();

        static {
            for (SqlBuilder.BooleanCriteria c: values()) {
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
        public static SqlBuilder.BooleanCriteria fromValue(String value) {
            SqlBuilder.BooleanCriteria constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
