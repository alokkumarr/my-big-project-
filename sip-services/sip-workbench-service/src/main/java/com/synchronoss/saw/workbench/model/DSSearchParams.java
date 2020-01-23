package com.synchronoss.saw.workbench.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "category",
    "subCategory",
    "catalog",
    "dataSource",
    "dsType"
})
public class DSSearchParams {
    @JsonProperty("category")
    private String[] category;
    @JsonProperty("subCategory")
    private String[] subCategory;
    @JsonProperty("catalog")
    private String[] catalog;
    @JsonProperty("dataSource")
    private String[] dataSource;
    @JsonProperty("dstype")
    private String[] dstype;

    @JsonProperty("category")
    public String[] getCategory() {
        return category;
    }
    @JsonProperty("category")
    public void setCategory(String[] category) {
        this.category = category;
    }

    @JsonProperty("subCategory")
    public String[] getSubCategory() {
        return subCategory;
    }
    @JsonProperty("subCategory")
    public void setSubCategory(String[] subCategory) {
        this.subCategory = subCategory;
    }

    @JsonProperty("catalog")
    public String[] getCatalog() {
        return catalog;
    }
    @JsonProperty("catalog")
    public void setCatalog(String[] catalog) {
        this.catalog = catalog;
    }

    @JsonProperty("dataSource")
    public String[] getDataSource() {
        return dataSource;
    }
    @JsonProperty("dataSource")
    public void setDataSource(String[] dataSource) {
        this.dataSource = dataSource;
    }

    @JsonProperty("dstype")
    public String[] getDstype() {
        return dstype;
    }
    @JsonProperty("dstype")
    public void setDstype(String[] dstype) {
        this.dstype = dstype;
    }

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
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
        return "DSSearchParams{" +
            "category=" + Arrays.toString(category) +
            ", subCategory=" + Arrays.toString(subCategory) +
            ", catalog=" + Arrays.toString(catalog) +
            ", dataSource=" + Arrays.toString(dataSource) +
            ", dstype=" + Arrays.toString(dstype) +
            ", additionalProperties=" + additionalProperties +
            '}';
    }
}
