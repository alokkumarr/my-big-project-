package com.synchronoss.saw.storage.proxy.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "contents"
})
public class Content implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @JsonProperty("observe")
  private List<StorageProxy> observe = null;
 
  
  @JsonProperty("observe")
  public List<StorageProxy> getObserve() {
  return observe;
  }

  @JsonProperty("observe")
  public void setObserve(List<StorageProxy> observe) {
  this.observe = observe;
  }

}
