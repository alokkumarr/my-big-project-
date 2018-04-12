package com.synchronoss.saw.observe.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"_id","entityId", "categoryId", "name", "description","createdBy","updatedBy","createdAt","updatedAt", "options", "tiles", "filters"})
public class Observe implements Serializable {
  
  private static final long serialVersionUID = 7077848381430952958L;
  
  @JsonProperty("_id")
  private String _id;
  @JsonProperty("entityId")
  private String entityId;
  @JsonProperty("categoryId")
  private String categoryId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("createdBy")
  private String createdBy;
  @JsonProperty("updatedBy")
  private String updatedBy;
  @JsonProperty("createdAt")
  private String createdAt;
  @JsonProperty("updatedAt")
  private String updatedAt;
  @JsonProperty("description")
  private String description;
  @JsonProperty("options")
  private List<Object> options = null;
  @JsonProperty("tiles")
  private List<Object> tiles = null;
  @JsonProperty("filters")
  private List<Object> filters = null;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("_id")
  public String get_id() {
    return _id;
  }
  
  @JsonProperty("_id")
  public void set_id(String _id) {
    this._id = _id;
  }

  @JsonProperty("entityId")
  public String getEntityId() {
    return entityId;
  }

  @JsonProperty("entityId")
  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  @JsonProperty("categoryId")
  public String getCategoryId() {
    return categoryId;
  }

  @JsonProperty("categoryId")
  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }
  
  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }
  @JsonProperty("createdBy")
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }
  @JsonProperty("updatedBy")
  public String getUpdatedBy() {
    return updatedBy;
  }
  @JsonProperty("updatedBy")
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }
  @JsonProperty("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }
  @JsonProperty("createdAt")
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }
  @JsonProperty("updatedAt")
  public String getUpdatedAt() {
    return updatedAt;
  }
  @JsonProperty("updatedAt")
  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  @JsonProperty("options")
  public List<Object> getOptions() {
    return options;
  }

  @JsonProperty("options")
  public void setOptions(List<Object> options) {
    this.options = options;
  }

  @JsonProperty("tiles")
  public List<Object> getTiles() {
    return tiles;
  }

  @JsonProperty("tiles")
  public void setTiles(List<Object> tiles) {
    this.tiles = tiles;
  }

  @JsonProperty("filters")
  public List<Object> getFilters() {
    return filters;
  }

  @JsonProperty("filters")
  public void setFilters(List<Object> filters) {
    this.filters = filters;
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
