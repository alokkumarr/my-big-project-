package com.synchronoss.saw.alert.controller;

import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.modal.Alert;
import com.synchronoss.saw.alert.modal.AlertResponse;
import com.synchronoss.saw.alert.service.AlertService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
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
   * Get JWT token details.
   *
   * @param req http Request
   * @return String
   * @throws IllegalAccessException If Authorization not found
   */
  public static String getToken(final HttpServletRequest req) throws IllegalAccessException {
    if (!("OPTIONS".equals(req.getMethod()))) {
      final String authHeader = req.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new IllegalAccessException("Missing or invalid Authorization header.");
      }
      return authHeader.substring(7); // The part after "Bearer "
    }
    return null;
  }

  /**
   * create Alert API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param alert alert definition
   * @return Alert
   */
  @ApiOperation(value = "", nickname = "createAlertRule", notes = "", response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertResponse createAlertRule(
      HttpServletRequest request, HttpServletResponse response, @RequestBody Alert alert) {
    AlertResponse alertResponse = new AlertResponse();
    try {
      String token = getToken(request);
      Ticket ticket = TokenParser.retrieveTicket(token);
      alertResponse.setAlert(alertService.createAlertRule(alert, ticket));
      if (alert == null) {
        alertResponse.setMessage("Alert rule definition can't be null for create request");
        response.setStatus(400);
        return alertResponse;
      }
      alertResponse.setMessage("Alert rule created successfully");

    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while creating the alert list by category", e);
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
  @ApiOperation(value = "/{id}", nickname = "updateAlertRule", notes = "", response = Object.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertResponse updateAlertRule(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") Long id,
      @RequestBody Alert alert) {
    AlertResponse alertResponse = new AlertResponse();
    try {
      String token = getToken(request);
      Ticket ticket = TokenParser.retrieveTicket(token);
      alertResponse.setAlert(alertService.updateAlertRule(alert, id, ticket));
      if (alert == null) {
        alertResponse.setMessage("Alert rule definition can't be null for create request");
        response.setStatus(400);
        return alertResponse;
      }

      alertResponse.setMessage("Alert rule updated successfully");

    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while updating the alert list by category", e);
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
      value = "/list/{categoryId}",
      nickname = "List Alert Rules",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "/list/{categoryId}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public List<AlertRulesDetails> listAlertRules(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "categoryId") String categoryId) {

    try {
      String token = getToken(request);
      Ticket ticket = TokenParser.retrieveTicket(token);
      return alertService.getAlertRulesByCategory(categoryId, ticket);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert list by category");
    }
    return null;
  }

  /**
   * List Alert rule API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert
   */
  @ApiOperation(
      value = "/{id}",
      nickname = "List Alert Rules",
      notes = "",
      response = AlertResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertResponse getAlertRules(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") Long id) {

    AlertResponse alertResponse = new AlertResponse();

    try {
      String token = getToken(request);
      Ticket ticket = TokenParser.retrieveTicket(token);
      alertResponse.setAlert(alertService.getAlertRule(id, ticket));
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert details", e);
    }
    alertResponse.setMessage("Alert rule retrieved successfully");
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
      response = AlertResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertResponse deleteAlertRules(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") Long id) {

    AlertResponse alertResponse = new AlertResponse();
    try {
      String token = getToken(request);
      Ticket ticket = TokenParser.retrieveTicket(token);
      alertService.deleteAlertRule(id, ticket);
      alertResponse.setMessage("Alert rule deleted successfully");
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert details");
    }

    return alertResponse;
  }
}
