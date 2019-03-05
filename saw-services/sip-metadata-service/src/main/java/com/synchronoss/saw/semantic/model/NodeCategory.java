package com.synchronoss.saw.semantic.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"node-category", "action"})
public class NodeCategory {

  /**
   * The Node Category Schema
   *
   * <p>
   */
  @JsonProperty("node-category")
  private String nodeCategory = "SemanticNode";
  /**
   * The Projectcode Schema
   *
   * <p>
   */
  @JsonProperty("action")
  private Action action;

  @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<>();

  /**
   * The Node Category Schema
   *
   * <p>
   */
  @JsonProperty("node-category")
  public String getNodeCategory() {
    return nodeCategory;
  }

  /**
   * The Node Category Schema
   *
   * <p>
   */
  @JsonProperty("node-category")
  public void setNodeCategory(String nodeCategory) {
    this.nodeCategory = nodeCategory;
  }

  /**
   * The Projectcode Schema
   *
   * <p>
   */
  @JsonProperty("action")
  public Action getAction() {
    return action;
  }

  @JsonProperty("action")
  public void setAction(Action action) {
    this.action = action;
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
