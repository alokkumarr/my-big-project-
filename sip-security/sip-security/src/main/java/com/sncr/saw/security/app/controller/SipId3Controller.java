package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.id3.model.AuthorizationCodeDetails;
import com.sncr.saw.security.app.id3.model.Id3AuthenticationRequest;
import com.sncr.saw.security.app.id3.model.Id3Claims;
import com.sncr.saw.security.app.id3.service.ValidateId3IdentityToken;
import com.sncr.saw.security.app.repository.Id3Repository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.service.TicketHelper;
import com.sncr.saw.security.app.sso.SSORequestHandler;
import com.sncr.saw.security.app.sso.SSOResponse;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  private static final Logger logger = LoggerFactory.getLogger(SipId3Controller.class);
  private final ValidateId3IdentityToken validateId3IdentityToken;

  private final Id3Repository id3Repository;
  private final SSORequestHandler ssoRequestHandler;
  private static final String BEARER = "Bearer";
  private static final String MALFORMED_TOKEN = "Request not valid,"
      + " Id3 Token may be Malformed or already expired";
  private static final String PRIVATE = "private";
  private static final String TOKEN_EXPIRED = "Token has expired";

  @Autowired
  private TicketHelper tHelper;

  @Autowired public UserRepository userRepository;

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
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  @PostMapping(value = "/token")
  public SSOResponse id3TokenToSipAuthentication(
      @RequestHeader("Authorization") String token,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    response.setHeader(HttpHeaders.CACHE_CONTROL, PRIVATE);
    String idToken = token.replace(BEARER, "").trim();
    SSOResponse ssoResponse = ssoRequestHandler.processId3SipAuthentication(idToken);
    if (ssoResponse == null)
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), MALFORMED_TOKEN);
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
    response.setHeader(HttpHeaders.CACHE_CONTROL, PRIVATE);
    String authorizationCode = token.replace(BEARER, "").trim();
    SSOResponse ssoResponse = null;
    AuthorizationCodeDetails authorizationCodeDetails =
        id3Repository.validateAuthorizationCode(authorizationCode, id3AuthenticationRequest);
    if (authorizationCodeDetails == null || !authorizationCodeDetails.isValid()) {
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), MALFORMED_TOKEN);
      return ssoResponse;
    } else if (authorizationCodeDetails.getMasterLoginId() != null) {
      ssoResponse =
          ssoRequestHandler.createSAWToken(authorizationCodeDetails.getMasterLoginId(),
              true, authorizationCodeDetails.getSipTicketId());
    }
    if (ssoResponse == null)
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), MALFORMED_TOKEN);
    return ssoResponse;
  }

  /**
   * This method validates the Id3 identity token and provides the authorization code.
   *
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
      @RequestBody MultiValueMap<String, String> params,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (!request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString())) {
      Map<String, String> map = params.toSingleValueMap();
      response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store,must-revalidate, max-age=0");
      String authorizationCode;
      String domain = map.get("domainName");
      String clientId = map.get("clientId");
      Id3AuthenticationRequest id3AuthenticationRequest = new Id3AuthenticationRequest();
      id3AuthenticationRequest.setRedirectUrl(map.get("redirectUrl"));
      id3AuthenticationRequest.setIdToken(map.get("idToken"));
      Id3Claims id3Claims =
          validateId3IdentityToken.validateToken(
              id3AuthenticationRequest.getIdToken(), Id3Claims.Type.ID);
      if (id3Claims != null) {
        AuthorizationCodeDetails authorizationCodeDetails = new AuthorizationCodeDetails();
        authorizationCodeDetails.setMasterLoginId(id3Claims.getMasterLoginId());
        authorizationCodeDetails.setId3ClientId(clientId);
        authorizationCodeDetails.setId3DomainName(id3Claims.getDomainName());
        authorizationCode = id3Repository.obtainAuthorizationCode(authorizationCodeDetails);
        ssoRequestHandler.setSsoCookies(
            response, authorizationCode, id3AuthenticationRequest, domain, clientId);
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("location", id3AuthenticationRequest.getRedirectUrl());
      } else {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
      }
    }
  }

  /**
   * This method reads the Id3 identity token and invalidates the SIP token.
   *
   * @param token
   * @param params
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public SSOResponse sipTokenInvalidatorForId3(
      @RequestHeader("Authorization") String token,
      @RequestBody MultiValueMap<String, String> params,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    final String SIP_TICKET_ID = "sipTicketId";
    response.setHeader(HttpHeaders.CACHE_CONTROL, PRIVATE);
    String idToken = token.replace(BEARER, "").trim();
    SSOResponse ssoResponse = new SSOResponse();
    Map<String, String> map = params.toSingleValueMap();
    String sipTicketId = null;
    Id3Claims id3Claims =
        validateId3IdentityToken.validateToken(idToken, Id3Claims.Type.ID);

    if (id3Claims == null) {
      logger.error("id3Claims is null");
      response.sendError(
          HttpStatus.UNAUTHORIZED.value(), MALFORMED_TOKEN);
      return ssoResponse;
    } else if (!map.containsKey(SIP_TICKET_ID) && map.get(SIP_TICKET_ID) == null) {
      response.sendError(
          HttpStatus.BAD_REQUEST.value(), "sipTicketId is mandatory");
      return ssoResponse;
    }
    String userId = id3Claims.getMasterLoginId();
    
    try {
      sipTicketId = map.get(SIP_TICKET_ID).trim();
      Ticket ticket = userRepository.getTicketForId3(sipTicketId, userId);
      if (ticket == null || ticket.getMasterLoginId() == null) {
        response.sendError(
            HttpStatus.BAD_REQUEST.value(), "Invalid SipTicketId !!");
        return ssoResponse;
      }
      Boolean validity =
          ticket.getValidUpto() != null ? (ticket.getValidUpto() > (new Date().getTime()))
              && ticket.isValid() : false;
      if (!validity) {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "ticket already expired!!");
        return ssoResponse;
      }
      ssoResponse.setMessage(tHelper.logout(sipTicketId));
      ssoResponse.setValidity(Boolean.TRUE);
    } catch (DataAccessException de) {
      logger.error("unable to logout {}", de.getMessage());
      ssoResponse.setValidity(Boolean.FALSE);
      ssoResponse.setMessage("Error occurred while logging out! Contact ADMIN!!");
    }
    return ssoResponse;
  }
}
