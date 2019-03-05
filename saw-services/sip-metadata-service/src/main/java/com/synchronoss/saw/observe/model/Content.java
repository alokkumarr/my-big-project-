package com.synchronoss.saw.observe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"contents"})
public class Content implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("observe")
  private List<Observe> observe = null;

  /**
   * Get Observe.
   *
   * @return List of Observe
   */
  @JsonProperty("observe")
  public List<Observe> getObserve() {
    return observe;
  }

  /**
   * Set Observe.
   *
   * @param observe List of Observe.
   */
  @JsonProperty("observe")
  public void setObserve(List<Observe> observe) {
    this.observe = observe;
  }
}
