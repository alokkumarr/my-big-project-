package com.synchronoss.saw.workbench.model.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "_index",
    "_type",
    "_id",
    "_score",
    "_source"
})
public class Hit<T> {

  @JsonProperty("_index")
  private String index;
  @JsonProperty("_type")
  private String type;
  @JsonProperty("_id")
  private String id;
  @JsonProperty("_score")
  private Double score;
  @JsonProperty("_source")
  private Map<String,Object> source;
  @JsonProperty("sort")
  private List<Object> sort;
  
  @JsonProperty("_index")
  public String getIndex() {
    return index;
  }
  @JsonProperty("_index")
  public void setIndex(String index) {
    this.index = index;
  }
  @JsonProperty("_type")
  public String getType() {
    return type;
  }
  @JsonProperty("_type")
  public void setType(String type) {
    this.type = type;
  }
  @JsonProperty("_id")
  public String getId() {
    return id;
  }
  @JsonProperty("_id")
  public void setId(String id) {
    this.id = id;
  }
  @JsonProperty("_score")
  public Double getScore() {
    return score;
  }
  @JsonProperty("_score")
  public void setScore(Double score) {
    this.score = score;
  }
  @JsonProperty("_source")
  public Map<String, Object> getSource() {
    return source;
  }
  @JsonProperty("_source")
  public void setSource(Map<String, Object> source) {
    this.source = source;
  }
  @JsonProperty("sort")
  public List<Object> getSort() {
    return sort;
  }
  @JsonProperty("sort")
  public void setSort(List<Object> sort) {
    this.sort = sort;
  }
  
  
  
}
