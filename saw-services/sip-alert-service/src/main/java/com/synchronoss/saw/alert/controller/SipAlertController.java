package com.synchronoss.saw.alert.controller;

import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.modal.Alert;
import com.synchronoss.saw.alert.modal.AlertResponse;
import com.synchronoss.saw.alert.modal.AlertStates;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alerts")
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
    Ticket ticket = getTicket(request);
    if (ticket != null) {
      alertResponse.setAlert(alertService.createAlertRule(alert, ticket));
      if (alert == null) {
        alertResponse.setMessage("Alert rule definition can't be null for create request");
        response.setStatus(400);
        return alertResponse;
      }
      alertResponse.setMessage("Alert rule created successfully");
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
    Ticket ticket = getTicket(request);
    if (ticket != null) {
      alertResponse.setAlert(alertService.updateAlertRule(alert, id, ticket));
      if (alert == null) {
        alertResponse.setMessage("Alert rule definition can't be null for create request");
        response.setStatus(400);
        return alertResponse;
      }
      alertResponse.setMessage("Alert rule updated successfully");
    }

    return alertResponse;
  }

  /**
   * List All Alert rule API.
   *
   * @param request HttpServletRequest
   * @return AlertRulesDetails
   */
  @ApiOperation(value = "", nickname = "List All Alert Rules", notes = "", response = Object.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public List<AlertRulesDetails> listAlertRules(HttpServletRequest request) {
    Ticket ticket = getTicket(request);
    return ticket != null ? alertService.retrieveAllAlerts(ticket) : null;
  }

  /**
   * List Alert operators API.
   *
   * @param request HttpServletRequest
   * @return String return all operators details
   */
  @ApiOperation(
      value = "/operators",
      nickname = "List All Alert Rules operators",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "/operators",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public String listAlertOperators(HttpServletRequest request) {
    Ticket ticket = getTicket(request);
    return ticket != null ? alertService.retrieveOperatorsDetails(ticket) : null;
  }

  /**
   * List Alert aggregation API.
   *
   * @param request HttpServletRequest
   * @return String return all operators details
   */
  @ApiOperation(
      value = "/aggregations",
      nickname = "List All Alert aggregation",
      notes = "",
      response = Object.class)
  @RequestMapping(
      value = "/aggregations",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public String listAlertAggregation(HttpServletRequest request) {
    Ticket ticket = getTicket(request);
    return ticket != null ? alertService.retrieveAggregations(ticket) : null;
  }

  /**
   * List Alert rule API by category.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert List of alert rle details
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
  public List<AlertRulesDetails> listAlertRulesByCategory(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "categoryId") String categoryId) {

    Ticket ticket = getTicket(request);
    return ticket != null ? alertService.getAlertRulesByCategory(categoryId, ticket) : null;
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
    Ticket ticket = getTicket(request);
    if (ticket != null) {
      alertResponse.setAlert(alertService.getAlertRule(id, ticket));
      alertResponse.setMessage("Alert rule retrieved successfully");
    }
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
    Ticket ticket = getTicket(request);
    if (ticket != null) {
      Boolean flag = alertService.deleteAlertRule(id, ticket);
      if (flag) {
        alertResponse.setMessage("Alert rule deleted successfully");
      } else {
        response.setStatus(401);
        alertResponse.setMessage("You are not authorized to delete alertId : " + id);
      }
    }
    return alertResponse;
  }

  /**
   * List Alert states API by Alert Id.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert
   */
  @ApiOperation(
      value = "/{id}/states",
      nickname = "List Alert Rules",
      notes = "",
      response = List.class)
  @RequestMapping(
      value = "/{id}/states",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public List<AlertStates> getAlertState(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") Long id,
      @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", required = false) Integer pageSize) {
    Ticket ticket = getTicket(request);
    if (ticket != null) {
      if (pageNumber == null) {
        pageNumber = 0;
      }
      if (pageSize == null) {
        pageSize = 25;
      }
      List<AlertStates> alertStates = alertService.getAlertStates(id, pageNumber, pageSize, ticket);
      if (alertStates != null) {
        return alertStates;
      } else {
        response.setStatus(401);
      }
    }
    return null;
  }

  /**
   * List Alert states API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert
   */
  @ApiOperation(
      value = "/states",
      nickname = "List Alert Rules",
      notes = "",
      response = List.class)
  @RequestMapping(
      value = "/states",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public List<AlertStates> listAlertStates(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
      @RequestParam(name = "pageSize", required = false) Integer pageSize) {
    Ticket ticket = getTicket(request);
    if (ticket != null) {
      if (pageNumber == null) {
        pageNumber = 0;
      }
      if (pageSize == null) {
        pageSize = 25;
      }
      return alertService.listAlertStates(pageNumber, pageSize, ticket);
    }
    return null;
  }

  /**
   * This method to validate jwt token then return the validated ticket for further processing.
   *
   * @param request HttpServletRequest
   * @return Ticket
   */
  private Ticket getTicket(HttpServletRequest request) {
    try {
      String token = getToken(request);
      return TokenParser.retrieveTicket(token);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert details", e);
    }
    return null;
  }
}
