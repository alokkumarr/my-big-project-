package com.synchronoss.saw.alert.controller;

import com.synchronoss.saw.alert.modal.Alert;
import com.synchronoss.saw.alert.modal.AlertResponse;
import com.synchronoss.saw.alert.service.AlertService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sip/alerts")
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
public class SipAlertController {

  private static final Logger logger = LoggerFactory.getLogger(SipAlertController.class);

  @Autowired AlertService alertService;

  /**
   * create Alert API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param alert alert definition
   * @return Alert
   */
  @ApiOperation(
      value = " create Alert API ",
      nickname = "createAlertRule",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Object createAlertRule(
      HttpServletRequest request, HttpServletResponse response, @RequestBody Alert alert) {

    AlertResponse alertResponse = new AlertResponse();
    if (alert == null) {
      alertResponse.setMessage("Alert rule definition can't be null for create request");
      response.setStatus(400);
      return alertResponse;
    }

    return alertResponse;
  }

  /**
   * update Alert API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param alert alert definition
   * @return Alert
   */
  @ApiOperation(
      value = " update Alert API ",
      nickname = "updateAlertRule",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Object updateAlertRule(
      HttpServletRequest request, HttpServletResponse response, @RequestBody Alert alert) {

    AlertResponse alertResponse = new AlertResponse();
    if (alert == null) {
      alertResponse.setMessage("Alert rule definition can't be null for create request");
      response.setStatus(400);
      return alertResponse;
    }

    return alertResponse;
  }

  /**
   * List Alert rule API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert
   */
  @ApiOperation(
      value = " List Alert rule API ",
      nickname = "List Alert Rules",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Object listAlertRules(HttpServletRequest request, HttpServletResponse response) {

    AlertResponse alertResponse = new AlertResponse();

    return alertResponse;
  }

  /**
   * List Alert rule API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert
   */
  @ApiOperation(
      value = " List Alert rule API ",
      nickname = "List Alert Rules",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Object getAlertRules(HttpServletRequest request, HttpServletResponse response) {

    AlertResponse alertResponse = new AlertResponse();

    return alertResponse;
  }

  /**
   * Delete Alert rule API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert
   */
  @ApiOperation(
      value = "/{id}",
      nickname = "Delete Alert Rules",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Object deleteAlertRules(HttpServletRequest request, HttpServletResponse response) {

    AlertResponse alertResponse = new AlertResponse();

    return alertResponse;
  }
}
