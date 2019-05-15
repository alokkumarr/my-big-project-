package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"dataStore", "storageType"})
public class Store {

  @JsonProperty("dataStore")
  private String dataStore;

  @JsonProperty("storageType")
  private String storageType;

  @JsonProperty("dataStore")
  public String getDataStore() {
    return dataStore;
  }

  @JsonProperty("dataStore")
  public void setDataStore(String dataStore) {
    this.dataStore = dataStore;
  }

  @JsonProperty("storageType")
  public String getStorageType() {
    return storageType;
  }

  @JsonProperty("storageType")
  public void setStorageType(String storageType) {
    this.storageType = storageType;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("dataStore", dataStore)
        .append("storageType", storageType)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(dataStore)
        .append(storageType)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Store) == false) {
      return false;
    }
    Store rhs = ((Store) other);
    return new EqualsBuilder()
        .append(dataStore, rhs.dataStore)
        .append(storageType, rhs.storageType)
        .isEquals();
  }
}
