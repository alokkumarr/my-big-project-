
package com.synchronoss.querybuilder.model.chart;

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
    "dataFields",
    "filters",
    "sorts",
    "groupBy",
    "splitBy",
    "required",
    "type"
})
public class SqlBuilder {

    @JsonProperty("dataFields")
    private List<DataField> dataFields = null;
    @JsonProperty("filters")
    private List<Filter> filters = null;
    @JsonProperty("sorts")
    private List<Sort> sorts = null;
    @JsonProperty("groupBy")
    private GroupBy groupBy;
    @JsonProperty("splitBy")
    private SplitBy splitBy;
    @JsonProperty("required")
    private Object required;
    @JsonProperty("type")
    private Object type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dataFields")
    public List<DataField> getDataFields() {
        return dataFields;
    }

    @JsonProperty("dataFields")
    public void setDataFields(List<DataField> dataFields) {
        this.dataFields = dataFields;
    }

    @JsonProperty("filters")
    public List<Filter> getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @JsonProperty("sorts")
    public List<Sort> getSorts() {
        return sorts;
    }

    @JsonProperty("sorts")
    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    @JsonProperty("groupBy")
    public GroupBy getGroupBy() {
        return groupBy;
    }

    @JsonProperty("groupBy")
    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    @JsonProperty("splitBy")
    public SplitBy getSplitBy() {
        return splitBy;
    }

    @JsonProperty("splitBy")
    public void setSplitBy(SplitBy splitBy) {
        this.splitBy = splitBy;
    }

    @JsonProperty("required")
    public Object getRequired() {
        return required;
    }

    @JsonProperty("required")
    public void setRequired(Object required) {
        this.required = required;
    }

    @JsonProperty("type")
    public Object getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Object type) {
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

}
