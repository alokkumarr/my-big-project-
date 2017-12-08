package com.synchronoss.saw.observe.model;

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
@JsonPropertyOrder({"id", "categoryId", "name", "description", "options", "tiles", "filters"})
public class Observe {

  @JsonProperty("id")
  private String id;
  @JsonProperty("categoryId")
  private String categoryId;
  @JsonProperty("name")
  private String name;
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

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
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
