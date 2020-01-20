package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.id3.service.ValidateId3IdentityToken;
import com.sncr.saw.security.app.repository.Id3Repository;
import com.sncr.saw.security.app.sso.SSORequestHandler;
import com.sncr.saw.security.app.sso.SSOResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(
    value =
        "The controller provides operations to Id3 related API with SIP"
            + "synchronoss insights platform ")
@RequestMapping("/sip-security/v1/id3")
@ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System administrator"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(
          code = 415,
          message = "Unsupported Type. Representation not supported for the resource")
    })
public class SipId3Controller {

  private final ValidateId3IdentityToken validateId3IdentityToken;

  private final Id3Repository id3Repository;
  private final SSORequestHandler ssoRequestHandler;
  private static final String CACHE_CONTROL = "Cache-Control";
  private static final String BEARER = "Bearer";

  @Autowired
  public SipId3Controller(
      ValidateId3IdentityToken validateId3IdentityToken,
      Id3Repository id3Repository,
      SSORequestHandler ssoRequestHandler) {
    this.validateId3IdentityToken = validateId3IdentityToken;
    this.id3Repository = id3Repository;
    this.ssoRequestHandler = ssoRequestHandler;
  }
  /**
   * To Obtain the SIP access and Refresh token based on ID3 Identity Token.
   *
   * @param token
   * @param id3AuthenticationRequest
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  @PostMapping(value = "/token")
  public SSOResponse id3TokenToSipAuthentication(
      @RequestHeader("Authorization") String token,
      @RequestBody Id3AuthenticationRequest id3AuthenticationRequest,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    response.setHeader(CACHE_CONTROL, "private");
    String idToken = token.replace(BEARER, "").trim();
    SSOResponse ssoResponse =
        ssoRequestHandler.processId3SSORequest(idToken, id3AuthenticationRequest);
    if (ssoResponse == null)
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(),
          "Request not valid, Id3 Token may be Malformed or already expired");
    return ssoResponse;
  }

  /**
   * @param token
   * @param id3AuthenticationRequest
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  @ApiOperation(
      value = "Validate the SIP authorization Code and obtain SIP Access and Refresh token",
      nickname = "ssoAuthentications",
      notes = "SIP authenticate with authorization Code allowed for one time use")
  @PostMapping(value = "/login/authenticate")
  public SSOResponse ssoAuthentications(
      @RequestHeader("Authorization") String token,
      @RequestBody Id3AuthenticationRequest id3AuthenticationRequest,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    response.setHeader(CACHE_CONTROL, "private");
    String authorizationCode = token.replace(BEARER, "").trim();
    SSOResponse ssoResponse = null;
    AuthorizationCodeDetails authorizationCodeDetails =
        id3Repository.validateAuthorizationCode(authorizationCode, id3AuthenticationRequest);
    if (authorizationCodeDetails.getMasterLoginId() != null) {
      ssoResponse =
          ssoRequestHandler.createSAWToken(authorizationCodeDetails.getMasterLoginId(), true);
    }
    if (ssoResponse == null)
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(),
          "Request not valid, Id3 Token may be Malformed or already expired");
    return ssoResponse;
  }

  /**
   * This method validates the Id3 identity token and provides the authorization code.
   *
   * @param token
   * @param id3AuthenticationRequest
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  @ApiOperation(
      value = "To fetch an SIP authorization Code as session cookies for SSO login with ID3",
      nickname = "id3SipAuthorization",
      notes = "SIP provides authorization Code for one time use")
  @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public void id3SsoAuthentication(
      @CookieValue("ID3_IDENTITY_TOKEN") String token,
      @RequestBody Id3AuthenticationRequest id3AuthenticationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    response.setHeader(CACHE_CONTROL, "no-store,must-revalidate, max-age=0");
    String authorizationCode;
    String masterLoginId = validateId3IdentityToken.validateToken(token, id3AuthenticationRequest);
    if (masterLoginId != null) {
      authorizationCode =
          id3Repository.obtainAuthorizationCode(masterLoginId, id3AuthenticationRequest);
      ssoRequestHandler.setSsoCookies(response, authorizationCode, id3AuthenticationRequest);
      response.setStatus(HttpStatus.FOUND.value());
      response.setHeader("location", id3AuthenticationRequest.getRedirectUrl());
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }
}
