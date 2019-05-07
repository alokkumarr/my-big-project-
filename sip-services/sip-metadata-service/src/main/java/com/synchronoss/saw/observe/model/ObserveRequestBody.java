package com.synchronoss.saw.observe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class ObserveRequestBody implements Serializable {

  private static final long serialVersionUID = -5457651692537639669L;

  @JsonProperty("contents")
  private Content content;

  @JsonProperty("contents")
  public Content getContent() {
    return content;
  }

  @JsonProperty("contents")
  public void setContent(Content content) {
    this.content = content;
  }
}
