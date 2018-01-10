package com.synchronoss.saw.storage.proxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "contents"})
public class StorageProxyResponse {

  @JsonProperty("message")
  private String message;
  @JsonProperty("contents")
  private Content contents = null;
  
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }
  
  @JsonProperty("contents")
  public Content getContents() {
    return contents;
  }
  @JsonProperty("contents")
  public void setContents(Content contents) {
    this.contents = contents;
  }
 }
