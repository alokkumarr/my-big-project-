
package com.synchronoss.querybuilder.model.kpi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tableName",
    "semanticId",
    "dataFields",
    "filters",
    "esRepository"
})
public class Kpi {

    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("semanticId")
    private String semanticId;
    @JsonProperty("dataFields")
    private List<DataField> dataFields = null;
    @JsonProperty("filters")
    private List<Filter> filters = null;
    @JsonProperty("esRepository")
    private EsRepository esRepository;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("tableName")
    public String getTableName() {
        return tableName;
    }

    @JsonProperty("tableName")
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @JsonProperty("semanticId")
    public String getSemanticId() {
        return semanticId;
    }

    @JsonProperty("semanticId")
    public void setSemanticId(String semanticId) {
        this.semanticId = semanticId;
    }

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

    @JsonProperty("esRepository")
    public EsRepository getEsRepository() {
        return esRepository;
    }

    @JsonProperty("esRepository")
    public void setEsRepository(EsRepository esRepository) {
        this.esRepository = esRepository;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("tableName", tableName).append("semanticId", semanticId).append("dataFields", dataFields).append("filters", filters).append("esRepository", esRepository).append("additionalProperties", additionalProperties).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(dataFields).append(esRepository).append(additionalProperties).append(semanticId).append(tableName).append(filters).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Kpi) == false) {
            return false;
        }
        Kpi rhs = ((Kpi) other);
        return new EqualsBuilder().append(dataFields, rhs.dataFields).append(esRepository, rhs.esRepository).append(additionalProperties, rhs.additionalProperties).append(semanticId, rhs.semanticId).append(tableName, rhs.tableName).append(filters, rhs.filters).isEquals();
    }

}
