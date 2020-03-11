package com.sncr.saw.security.app.id3.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Id3AuthenticationRequest {

  @JsonProperty("redirectUrl")
  @ApiModelProperty(notes = "SIP redirect Url ", name = "sipRedirectUrl")
  private String redirectUrl;

  @JsonProperty("idToken")
  @ApiModelProperty(notes = "Id3 Identity token", name = "idToken")
  private String idToken;

  @JsonProperty("redirectUrl")
  public String getRedirectUrl() {
    return redirectUrl;
  }

  @JsonProperty("redirectUrl")
  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public String getIdToken() {
    return idToken;
  }

  public void setIdToken(String idToken) {
    this.idToken = idToken;
  }
}
