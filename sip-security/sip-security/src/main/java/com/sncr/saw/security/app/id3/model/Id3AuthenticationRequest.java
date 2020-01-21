package com.sncr.saw.security.app.id3.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Id3AuthenticationRequest {
  @JsonProperty("domainName")
  @ApiModelProperty(notes = "Id3 Domain Name ", name = "domainName")
  private String domainName;

  @JsonProperty("clientId")
  @ApiModelProperty(notes = "Id3 Client Id ", name = "clientId")
  private String clientId;

  @JsonProperty("redirectUrl")
  @ApiModelProperty(notes = "SIP redirect Url ", name = "sipRedirectUrl")
  private String redirectUrl;

  @JsonProperty("idToken")
  @ApiModelProperty(notes = "Id3 Identity token", name = "idToken")
  private String idToken;

  @JsonProperty("domainName")
  public String getDomainName() {
    return domainName;
  }

  @JsonProperty("domainName")
  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  @JsonProperty("clientId")
  public String getClientId() {
    return clientId;
  }

  @JsonProperty("clientId")
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

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
