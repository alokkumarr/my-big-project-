package com.synchronoss.saw.storage.proxy.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageProxyRequestBody implements Serializable {

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
