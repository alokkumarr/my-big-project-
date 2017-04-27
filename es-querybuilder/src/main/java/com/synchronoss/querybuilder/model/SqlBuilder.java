
package com.synchronoss.querybuilder.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "filters",
    "sort",
    "rowFields",
    "columnFields",
    "dataFields"
})
public class SqlBuilder {

    @JsonProperty("filters")
    private List<Filter> filters = null;
    @JsonProperty("sort")
    private List<Sort> sort = null;
    @JsonProperty("rowFields")
    private List<RowField> rowFields = null;
    @JsonProperty("columnFields")
    private List<ColumnField> columnFields = null;
    @JsonProperty("dataFields")
    private List<DataField> dataFields = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("filters")
    public List<Filter> getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @JsonProperty("sort")
    public List<Sort> getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    @JsonProperty("rowFields")
    public List<RowField> getRowFields() {
        return rowFields;
    }

    @JsonProperty("rowFields")
    public void setRowFields(List<RowField> rowFields) {
        this.rowFields = rowFields;
    }

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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
