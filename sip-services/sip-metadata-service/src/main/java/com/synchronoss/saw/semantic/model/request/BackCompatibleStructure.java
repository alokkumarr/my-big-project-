package com.synchronoss.saw.semantic.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BackCompatibleStructure {

  @JsonProperty("contents")
  private List<Content> contents;

  @JsonProperty("contents")
  public List<Content> getContents() {
    return contents;
  }

  @JsonProperty("contents")
  public void setContents(List<Content> contents) {
    this.contents = contents;
  }
}
