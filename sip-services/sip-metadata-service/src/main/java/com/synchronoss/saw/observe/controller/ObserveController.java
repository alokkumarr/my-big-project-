package com.synchronoss.saw.observe.controller;

import static com.synchronoss.sip.utils.SipCommonUtils.validatePrivilege;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipJsonMissingException;
import com.synchronoss.saw.exceptions.SipJsonProcessingException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.observe.ObserveUtils;

import com.synchronoss.saw.observe.model.Content;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveRequestBody;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.service.ObserveService;
import com.synchronoss.sip.utils.Modules;
import com.synchronoss.sip.utils.Privileges;
import com.synchronoss.sip.utils.SipCommonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/observe/dashboards")
public class ObserveController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObserveController.class);

  private static final String ANALYSIS = "Analysis Id";
  private static final String ERROR_MESSAGE = "Expected missing on the request body.";
  private static final String UNAUTHORIZED =
      "UNAUTHORIZED ACCESS : User don't have the %s privileges for dashboard!!";

  @Autowired
  private ObserveService observeService;

  /**
   * This method will create the dashboard.
   *
   * @param requestBody of type object ObserveRequest.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ObserveResponse addDashboard(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody ObserveRequestBody requestBody) {
    LOGGER.trace("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new SipJsonMissingException("json body is missing in request body");
    }
    ObserveResponse observeResponse = new ObserveResponse();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      Observe observe =
          ObserveUtils.getObserveNode(objectMapper.writeValueAsString(requestBody), "contents");
      LOGGER.trace("Observe request object : {} ", objectMapper.writeValueAsString(observe));

      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;

      Long categoryId = Long.valueOf(observe.getCategoryId());
      if (!validatePrivilege(productList, categoryId,
          Privileges.PrivilegeNames.CREATE, Modules.OBSERVE.name())) {
        return buildPrivilegesResponse("Create", UNAUTHORIZED, response, observeResponse);
      }

      // validate the given analysis for observe
      List<Object> tiles = observe.getTiles();
      if (tiles != null && !tiles.isEmpty()) {
        boolean checkValidAnalysis = observeService.haveValidAnalysis(tiles, ticket);
        if (!checkValidAnalysis) {
          return buildPrivilegesResponse(ANALYSIS, UNAUTHORIZED, response, observeResponse);
        }
      }

      observe.setEntityId(observeService.generateId());
      LOGGER.trace("Invoking service with entity id : {} ", observe.getEntityId());
      observeResponse = observeService.addDashboard(observe);
    } catch (IOException e) {
      LOGGER.error(ERROR_MESSAGE, e);
      throw new SipJsonProcessingException(ERROR_MESSAGE);
    } catch (SipCreateEntityException ex) {
      LOGGER.error("Problem on the storage while creating an entity", ex);
      throw new SipCreateEntityException("Problem on the storage while creating an entity");
    }
    return observeResponse;
  }

  /**
   * This method will return instance of dashboard.
   *
   * @param entityId is of type string.
   * @param request  of type object.
   * @param response is of type object.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/{Id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardById(
      @PathVariable(name = "Id", required = true) String entityId,
      HttpServletRequest request,
      HttpServletResponse response) {
    LOGGER.debug("dashboardId {}", entityId);

    Observe observe = new Observe();
    observe.setEntityId(entityId);
    ObserveResponse observeResponse = observeService.getDashboardbyCriteria(observe);

    String category = observeResponse.getContents().getObserve()
        .stream().findFirst().get().getCategoryId();
    Long categoryId = !StringUtils.isEmpty(category) ? Long.valueOf(category) : 0L;
    Ticket ticket = SipCommonUtils.getTicket(request);

    ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
    if (!validatePrivilege(productList, Long.valueOf(categoryId),
        Privileges.PrivilegeNames.ACCESS, Modules.OBSERVE.name())) {
      observeResponse = new ObserveResponse();
      return buildPrivilegesResponse("Access", UNAUTHORIZED, response, observeResponse);
    } else {
      return observeResponse;
    }
  }

  /**
   * This method will return instance of dashboard by category Id.
   *
   * @param categoryId is of type string.
   * @param userId     is of type string.
   * @param request    of type object.
   * @param response   of type object.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/{categoryId}/{userId}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardByCategoryId(
      @PathVariable(name = "categoryId", required = true) String categoryId,
      @PathVariable(name = "userId", required = true) String userId,
      HttpServletRequest request,
      HttpServletResponse response) {
    LOGGER.debug("categoryId {}", categoryId);
    LOGGER.debug("userId {}", userId);
    ObserveResponse observeResponse = new ObserveResponse();

    Ticket ticket = SipCommonUtils.getTicket(request);
    ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
    if (!validatePrivilege(productList, Long.valueOf(categoryId),
        Privileges.PrivilegeNames.ACCESS, Modules.OBSERVE.name())) {
      return buildPrivilegesResponse("Access", UNAUTHORIZED, response, observeResponse);
    }

    Observe observe = new Observe();
    observe.setCategoryId(categoryId);
    /**
     * Ignore the the user Id for now list out all the dashboard for category. TO DO : User Id is
     * required to handle the My DashBoard (private)feature.
     */
    return observeService.getDashboardbyCategoryId(observe);

  }

  /**
   * This method will update dashboard instance.
   *
   * @param request     of type object.
   * @param response    of type object.
   * @param entityId    is of type string.
   * @param requestBody of type object.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/update/{Id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse updateDashboard(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String entityId,
      @RequestBody ObserveRequestBody requestBody) {
    LOGGER.debug("dashboardId {}", entityId);
    LOGGER.debug("Request Body : {}", requestBody);
    if (requestBody == null) {
      throw new SipJsonMissingException("json body is missing in request body");
    }
    ObserveResponse observeResponse = new ObserveResponse();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      Observe observe = ObserveUtils
          .getObserveNode(objectMapper.writeValueAsString(requestBody), "contents");

      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;

      Long categoryId = !StringUtils.isEmpty(observe.getCategoryId())
          ? Long.valueOf(observe.getCategoryId()) : 0L;
      if (!validatePrivilege(productList, Long.valueOf(categoryId),
          Privileges.PrivilegeNames.EDIT, Modules.OBSERVE.name())) {
        return buildPrivilegesResponse("Edit", UNAUTHORIZED, response, observeResponse);
      }
      // validate the given analysis for observe
      List<Object> tiles = observe.getTiles();
      if (tiles != null && !tiles.isEmpty()) {
        boolean haveValidAnalysis = observeService.haveValidAnalysis(tiles, ticket);
        if (!haveValidAnalysis) {
          return buildPrivilegesResponse(ANALYSIS, UNAUTHORIZED, response, observeResponse);
        }
      }

      observe.setEntityId(entityId);
      observeResponse = observeService.updateDashboard(observe);
    } catch (IOException e) {
      LOGGER.error(ERROR_MESSAGE, e);
      throw new SipJsonProcessingException(ERROR_MESSAGE);
    } catch (SipUpdateEntityException ex) {
      LOGGER.error("Entity does not exist", ex);
      throw new SipUpdateEntityException("Entity does not exist.");
    }
    return observeResponse;
  }

  /**
   * This method will delete the dashboard.
   *
   * @param request  of type object.
   * @param response of type object.
   * @param entityId is of type string.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/{Id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse deleteDashboard(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String entityId) {
    LOGGER.debug("dashboard Id {}", entityId);
    ObserveResponse responseObjectFuture = new ObserveResponse();
    try {
      Observe observe = new Observe();
      observe.setEntityId(entityId);
      ObserveResponse observeResponse = observeService.getDashboardbyCriteria(observe);
      Content content = observeResponse.getContents();
      String category = content != null
          ? content.getObserve().stream().findFirst().get().getCategoryId() : null;


      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;

      Long categoryId = !StringUtils.isEmpty(category) ? Long.valueOf(category) : 0L;
      if (!validatePrivilege(productList, Long.valueOf(categoryId),
          Privileges.PrivilegeNames.DELETE, Modules.OBSERVE.name())) {
        return buildPrivilegesResponse("Delete", UNAUTHORIZED, response, responseObjectFuture);
      }
      responseObjectFuture = observeService.deleteDashboard(observe);
    } catch (Exception ex) {
      LOGGER.error(ERROR_MESSAGE, ex);
      throw new SipJsonProcessingException(ERROR_MESSAGE);
    }
    return responseObjectFuture;
  }


  /**
   * Check valid permission if exist return the Alert rule response.
   *
   * @param response        HttpServletResponse
   * @param observeResponse ObserveResponse
   * @return AlertRuleResponse
   */
  private ObserveResponse buildPrivilegesResponse(String privileges,
                                                 String message,
                                                 HttpServletResponse response,
                                                 ObserveResponse observeResponse) {
    try {
      LOGGER.error(String.format(UNAUTHORIZED, privileges));
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.sendError(HttpStatus.UNAUTHORIZED.value(),
          String.format(message, privileges));
      observeResponse.setMessage(String.format(message, privileges));
      return observeResponse;
    } catch (IOException ex) {
      LOGGER.error("Error while validating permission", ex);
      return observeResponse;
    }
  }

  /**
   * This method generates unique Id.
   *
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/generateId", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse generateDashboardId() {
    ObserveResponse observeResponse = new ObserveResponse();
    observeResponse.setId(observeService.generateId());
    return observeResponse;
  }
}
