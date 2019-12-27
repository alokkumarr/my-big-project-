package com.synchronoss.bda.sip.dsk;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class DskDetails {

  @JsonProperty("message")
  @ApiModelProperty(notes = "gives success or error message", name = "message")
  private String message;

  @JsonProperty("dskGroupPayload")
  @ApiModelProperty(notes = "dsk group payload", name = "dskGroupPayload")
  private DskGroupPayload dskGroupPayload;

  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty("dskGroupPayload")
  public DskGroupPayload getDskGroupPayload() {
    return dskGroupPayload;
  }

  @JsonProperty("dskGroupPayload")
  public void setDskGroupPayload(DskGroupPayload dskGroupPayload) {
    this.dskGroupPayload = dskGroupPayload;
  }
}
