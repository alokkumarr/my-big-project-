package com.synchronoss.querybuilder.model.globalfilter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "storageType",
        "indexName",
        "type"
})
public class EsRepository {

    @JsonProperty("storageType")
    private String storageType;
    @JsonProperty("indexName")
    private String indexName;
    @JsonProperty("type")
    private String type;

    @JsonProperty("storageType")
    public String getStorageType() {
        return storageType;
    }

    @JsonProperty("storageType")
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    @JsonProperty("indexName")
    public String getIndexName() {
        return indexName;
    }

    @JsonProperty("indexName")
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("storageType", storageType).append("indexName", indexName).append("type", type).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(storageType).append(indexName).append(type).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof EsRepository) == false) {
            return false;
        }
        EsRepository rhs = ((EsRepository) other);
        return new EqualsBuilder().append(storageType, rhs.storageType).append(indexName, rhs.indexName).append(type, rhs.type).isEquals();
    }
}