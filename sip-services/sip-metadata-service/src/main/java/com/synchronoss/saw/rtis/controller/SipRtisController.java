package com.synchronoss.saw.rtis.controller;

import static com.synchronoss.saw.util.SipMetadataUtils.getTicket;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.rtis.model.ConfigResponse;
import com.synchronoss.saw.rtis.model.request.RtisConfiguration;
import com.synchronoss.saw.rtis.service.RtisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is used to perform CRUD operation for the RTIS metadata requests.
 *
 * @author alok.kumarr
 * @since 3.4.0
 */
@RestController
@Api(
    value =
        "The controller provides operations perform to on demand RTIS ")
@ApiResponses(
    value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Internal server Error. Contact System administrator")
    })
@RequestMapping("/internal/rtisconfig")
public class SipRtisController {

  private static final Logger logger = LoggerFactory.getLogger(SipRtisController.class);

  @Autowired
  private RtisService rtisService;

  /**
   * create RTIS configuration API.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @param config   RTIS config definition
   * @return ConfigResponse
   */
  @ApiOperation(
      value = " create config API ",
      nickname = "CreateRTISConfiguration",
      notes = "",
      response = ConfigResponse.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ConfigResponse createConfig(
      @ApiParam(
          value = "RTIS config object that needs to be added to the store",
          required = true)
          HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody RtisConfiguration config) {
    logger.trace("RTIS Config Body : " + config.toString());
    ConfigResponse configResponse = new ConfigResponse();

    if (config == null) {
      configResponse.setMessage("Configuration definition can't be null to register RTIS config");
      response.setStatus(400);
      return configResponse;
    }

    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
      configResponse.setMessage("Invalid authentication token");
    }
    config.setCustomerCode(authTicket.getCustCode());
    rtisService.createConfig(config);

    configResponse.setAppKey(config.getAppKey());
    configResponse.setCustomerCode(authTicket.getCustCode());
    configResponse.setMessage("Configuration created");
    return configResponse;
  }

  /**
   * Create fetch API to return app keys by customer code.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return ConfigResponse
   */
  @ApiOperation(
      value = " fetch app keys API ",
      nickname = "FetchAppKeys",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "/appKeys",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object getAppKeysByCustomerCode(
      HttpServletRequest request,
      HttpServletResponse response) {
    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
    }
    return rtisService.fetchAppKeys(authTicket.getCustCode());
  }

  /**
   * Create fetch API to return configuration by app key.
   *
   * @param appKey customer code
   * @return Object
   */
  @ApiOperation(
      value = " fetch configuration API ",
      nickname = "FetchConfiguration",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "/config/{appKey}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object getConfigByAppKey(@PathVariable(name = "appKey") String appKey) {
    return rtisService.fetchConfigByAppKeys(appKey);
  }

  /**
   * Create delete API to return configuration by app key.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @param appKey   app key
   * @return Object
   */
  @ApiOperation(
      value = " delete configuration API ",
      nickname = "FetchConfiguration",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "/{appKey}",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Boolean deleteConfigByAppKey(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "appKey") String appKey) {

    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
    }

    return rtisService.deleteConfiguration(appKey);
  }
}