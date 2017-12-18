package com.synchronoss.saw.observe.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "contents"
})
public class Content {
  @JsonProperty("observe")
  private List<Observe> observe = null;
 
  
  @JsonProperty("observe")
  public List<Observe> getObserve() {
  return observe;
  }

  @JsonProperty("observe")
  public void setObserve(List<Observe> observe) {
  this.observe = observe;
  }

}
