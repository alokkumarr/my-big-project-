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
  @JsonProperty("proxy")
  private List<StorageProxy> proxy = null;
 
  
  @JsonProperty("proxy")
  public List<StorageProxy> getProxy() {
  return proxy;
  }

  @JsonProperty("proxy")
  public void setProxy(List<StorageProxy> proxy) {
  this.proxy = proxy;
  }
  
  

}
