package com.synchronoss.sip.alert.controller;

import static com.synchronoss.sip.utils.SipCommonUtils.setUnAuthResponse;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.alert.modal.AlertCount;
import com.synchronoss.sip.alert.modal.AlertCountResponse;
import com.synchronoss.sip.alert.modal.AlertResponse;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.AlertRuleResponse;
import com.synchronoss.sip.alert.modal.AlertStatesFilter;
import com.synchronoss.sip.alert.modal.AlertStatesResponse;
import com.synchronoss.sip.alert.service.AlertService;
import com.synchronoss.sip.alert.util.AlertUtils;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
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

  @Autowired
  private AlertUtils utils;
  @Autowired
  private AlertService alertService;

  private static final String INVALID_TOKEN = "Invalid Token";
  private static String UNAUTHORIZED =
      "UNAUTHORIZED ACCESS : User don't have the %s permission for alerts!!";

  /**
   * create Alert API.
   *
   * @param request          HttpServletRequest
   * @param response         HttpServletResponse
   * @param alertRuleDetails Alert Rule Details definition
   * @return Alert
   */
  @ApiOperation(
      value = "",
      nickname = "createAlertRule",
      notes = "",
      response = AlertResponse.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertResponse createAlertRule(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody AlertRuleDetails alertRuleDetails) {
    AlertResponse alertResponse = new AlertResponse();

    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertResponse, "Create");
    }

    if (alertRuleDetails == null) {
      alertResponse.setMessage("Alert rule definition can't be null for create request");
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return alertResponse;
    }

    alertResponse.setAlert(alertService.createAlertRule(alertRuleDetails, ticket));
    alertResponse.setMessage("Alert rule created successfully");
    return alertResponse;
  }

  /**
   * update Alert API.
   *
   * @param request          HttpServletRequest
   * @param response         HttpServletResponse
   * @param alertRuleDetails AlertRuleDetails definition
   * @return Alert
   */
  @ApiOperation(
      value = "/{id}",
      nickname = "updateAlertRule",
      notes = "",
      response = AlertResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertResponse updateAlertRule(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") String id,
      @RequestBody AlertRuleDetails alertRuleDetails) {
    AlertResponse alertResponse = new AlertResponse();

    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertResponse, "Edit");
    }

    alertResponse.setAlert(alertService.updateAlertRule(alertRuleDetails, id, ticket));
    if (alertRuleDetails == null) {
      alertResponse.setMessage("Alert rule definition can't be null for create request");
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return alertResponse;
    }
    alertResponse.setMessage("Alert rule updated successfully");
    return alertResponse;
  }

  /**
   * List All Alert rule API.
   *
   * @param request HttpServletRequest
   * @return AlertRulesDetails
   */
  @ApiOperation(
      value = "/listAlerts",
      nickname = "List All Alert Rules",
      notes = "",
      response = AlertRuleResponse.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertRuleResponse listAlertRules(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "page number", required = false, defaultValue = "1")
      @RequestParam(name = "pageNumber", required = false, defaultValue = "1")
          Integer pageNumber,
      @ApiParam(value = "page size", required = false, defaultValue = "1000")
      @RequestParam(name = "pageSize", required = false, defaultValue = "1000")
          Integer pageSize) {
    AlertRuleResponse alertRuleResponse = new AlertRuleResponse();
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertRuleResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertRuleResponse, "Access");
    }

    return alertService.retrieveAllAlerts(pageNumber, pageSize, ticket);
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
      response = String.class)
  @RequestMapping(
      value = "/operators",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public String listAlertOperators(HttpServletRequest request,
                                   HttpServletResponse response) {
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, "Access");
    }
    return alertService.retrieveOperatorsDetails(ticket);
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
      response = String.class)
  @RequestMapping(
      value = "/aggregations",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public String listAlertAggregation(HttpServletRequest request, HttpServletResponse response) {
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, "View");
    }
    return alertService.retrieveAggregations(ticket);
  }

  /**
   * List Alert rule API by category.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return Alert List of alert rle details
   */
  @ApiOperation(
      value = "/list/{categoryId}",
      nickname = "List Alert Rules",
      notes = "",
      response = AlertRuleResponse.class)
  @RequestMapping(
      value = "/list/{categoryId}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertRuleResponse listAlertRulesByCategory(
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "page number", required = false, defaultValue = "1")
      @RequestParam(name = "pageNumber", required = false, defaultValue = "1")
          Integer pageNumber,
      @ApiParam(value = "page size", required = false, defaultValue = "1000")
      @RequestParam(name = "pageSize", required = false, defaultValue = "1000")
          Integer pageSize,
      @PathVariable(name = "categoryId") String categoryId) {
    AlertRuleResponse alertRuleResponse = new AlertRuleResponse();

    Ticket ticket = SipCommonUtils.getTicket(request);

    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertRuleResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertRuleResponse, "Access");
    }

    return alertService.getAlertRulesByCategory(categoryId, pageNumber, pageSize, ticket);

  }

  /**
   * GET Alert rule API for a alert rule id.
   *
   * @param request  HttpServletRequest
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
      @PathVariable(name = "id") String id) {
    AlertResponse alertResponse = new AlertResponse();
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertResponse, "Access");
    }

    alertResponse.setAlert(alertService.getAlertRule(id, ticket));
    alertResponse.setMessage("Alert rule retrieved successfully");
    return alertResponse;
  }

  /**
   * Delete Alert rule API.
   *
   * @param request  HttpServletRequest
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
      @PathVariable(name = "id") String id) {

    AlertResponse alertResponse = new AlertResponse();
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertResponse, "Delete");
    }

    Boolean isAlertDeleted = alertService.deleteAlertRule(id, ticket);
    if (isAlertDeleted) {
      alertResponse.setMessage("Alert rule deleted successfully");
      return alertResponse;
    } else {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      alertResponse.setMessage("You are not authorized to delete alertId : " + id);
      return alertResponse;
    }
  }

  /**
   * List Alert states API by Alert Id.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return AlertStatesResponse alertStatesResponse
   */
  @ApiOperation(
      value = "/{id}/states",
      nickname = "List Alert Rules",
      notes = "",
      response = AlertStatesResponse.class)
  @RequestMapping(
      value = "/{id}/states",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertStatesResponse getAlertState(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") String id,
      @ApiParam(value = "page number", required = false, defaultValue = "1")
      @RequestParam(name = "pageNumber", required = false, defaultValue = "1")
          Integer pageNumber,
      @ApiParam(value = "page size", required = false, defaultValue = "25")
      @RequestParam(name = "pageSize", required = false, defaultValue = "25")
          Integer pageSize) {

    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertStatesResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertStatesResponse, "Access");
    }

    alertStatesResponse = alertService.getAlertStates(id, pageNumber, pageSize, ticket);
    if (alertStatesResponse != null) {
      alertStatesResponse.setMessage("Success");
      return alertStatesResponse;
    } else {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return alertStatesResponse;
    }
  }

  /**
   * List Alert states API.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return AlertStatesResponse alertStatesResponse
   */
  @ApiOperation(
      value = "/states",
      nickname = "List Alert Rules",
      notes = "",
      response = AlertStatesResponse.class)
  @RequestMapping(
      value = "/states",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AlertStatesResponse listAlertStates(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody Optional<AlertStatesFilter> alertStatesFilter,
      @ApiParam(value = "page number", required = false, defaultValue = "1")
      @RequestParam(name = "pageNumber", required = false, defaultValue = "1")
          Integer pageNumber,
      @ApiParam(value = "page size", required = false, defaultValue = "25")
      @RequestParam(name = "pageSize", required = false, defaultValue = "25")
          Integer pageSize) {

    AlertStatesResponse alertStatesResponse = new AlertStatesResponse();
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response, alertStatesResponse);
    }

    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, alertStatesResponse, "Access");
    }

    return
        alertService.listAlertStates(pageNumber, pageSize, ticket, alertStatesFilter);
  }

  /**
   * List of alert count by date or severity based on request payload API.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return AlertStatesResponse alertStatesResponse
   */
  @ApiOperation(
      value = "/count ",
      nickname = "alertCount",
      notes = "returns alertCount by date or severity based on request payload",
      response = AlertCountResponse.class)
  @RequestMapping(
      value = "/count",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public List<AlertCountResponse> alertCountResponses(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody AlertCount alertCount,
      @ApiParam(value = "alert rule id", required = false)
      @RequestParam(name = "alertRuleId", required = false)
          String alertRuleId,
      @ApiParam(value = "page number", required = false, defaultValue = "1")
      @RequestParam(name = "pageNumber", required = false, defaultValue = "1")
          Integer pageNumber,
      @ApiParam(value = "page size", required = false, defaultValue = "1000")
      @RequestParam(name = "pageSize", required = false, defaultValue = "1000")
          Integer pageSize) {

    List<AlertCountResponse> alertCountResponse = new ArrayList<>();
    try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (ticket == null) {
        logger.error(INVALID_TOKEN);
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return alertCountResponse;
      }

      // validate the alerts access privileges
      if (!utils.validAlertPrivileges(ticket.getProducts())) {
        logger.error(String.format(UNAUTHORIZED, "Access"));
        setUnAuthResponse(response);
        response.sendError(HttpStatus.SC_UNAUTHORIZED,
            String.format(UNAUTHORIZED, "Access"));
        return alertCountResponse;
      }

      alertCountResponse =
          alertService.alertCount(alertCount, pageNumber, pageSize, alertRuleId, ticket);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
    return alertCountResponse;
  }

  /**
   * List Attribute Values API.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return AlertStatesResponse alertStatesResponse
   */
  @ApiOperation(
      value = "/attributevalues",
      nickname = "List Attribute Values",
      notes = "",
      response = AlertStatesResponse.class)
  @RequestMapping(
      value = "/attributevalues",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Set<String> listAttributeValues(HttpServletRequest request, HttpServletResponse response) {
    Set<String> attributeValues = new HashSet<>();
    try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (ticket == null) {
        logger.error(INVALID_TOKEN);
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        return attributeValues;
      }

      // validate the alerts access privileges
      if (!utils.validAlertPrivileges(ticket.getProducts())) {
        logger.error(String.format(UNAUTHORIZED, "Access"));
        setUnAuthResponse(response);
        return attributeValues;
      }

      attributeValues = alertService.listAttribueValues(ticket);
    } catch (IOException ex) {
      logger.error(ex.getMessage());
    }
    return attributeValues;
  }

  /**
   * List Monitoring Type..
   *
   * @param request HttpServletRequest
   * @return String return all operators details
   */
  @ApiOperation(
      value = "/monitoringtype",
      nickname = "List All Alert Monitoring Type",
      notes = "",
      response = String.class)
  @RequestMapping(
      value = "/monitoringtype",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public String listMonitoringType(HttpServletRequest request, HttpServletResponse response) {
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket == null) {
      return utils.emptyTicketResponse(response);
    }
    // validate the alerts access privileges
    if (!utils.validAlertPrivileges(ticket.getProducts())) {
      return utils.validatePermissionResponse(response, "Access");
    }
    return alertService.retrieveMonitoringType(ticket);
  }
}
