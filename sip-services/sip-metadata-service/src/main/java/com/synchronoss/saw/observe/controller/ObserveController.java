package com.synchronoss.saw.observe.controller;

import static com.synchronoss.sip.utils.SipCommonUtils.setUnAuthResponse;
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
import com.synchronoss.sip.utils.Privileges;
import com.synchronoss.sip.utils.SipCommonUtils;

import java.io.IOException;
import java.util.ArrayList;
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
public class ObserveController {

  private static final Logger logger = LoggerFactory.getLogger(ObserveController.class);

  private static String UNAUTHORIZED =
      "UNAUTHORIZED ACCESS : User don't have the %s dashboard!!";

  @Autowired
  private ObserveService observeService;

  /**
   * This method will create the dashboard.
   *
   * @param requestBody of type object ObserveRequest.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/observe/dashboards/create", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ObserveResponse addDashboard(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody ObserveRequestBody requestBody) {
    logger.debug("Request Body:{}", requestBody);
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
      logger.trace("Observe request object : {} ", objectMapper.writeValueAsString(observe));

      Ticket ticket = SipCommonUtils.getTicket(request);
      Long categoryId = Long.valueOf(observe.getCategoryId());
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
      if (!validatePrivilege(productList, categoryId, Privileges.PrivilegeNames.CREATE)) {
        logger.error(String.format(UNAUTHORIZED, "CREATE"));
        setUnAuthResponse(response);
        observeResponse.setMessage(String.format(UNAUTHORIZED, "CREATE"));
        return observeResponse;
      }

      observe.setEntityId(observeService.generateId());
      logger.trace("Invoking service with entity id : {} ", observe.getEntityId());
      observeResponse = observeService.addDashboard(observe);
    } catch (IOException e) {
      throw new SipJsonProcessingException("expected missing on the request body");
    } catch (SipCreateEntityException ex) {
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
  @RequestMapping(value = "/observe/dashboards/{Id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardById(
      @PathVariable(name = "Id", required = true) String entityId,
      HttpServletRequest request,
      HttpServletResponse response) {
    logger.debug("dashboardId {}", entityId);
    ObserveResponse observeResponse = null;
    try {
      Observe observe = new Observe();
      observe.setEntityId(entityId);
      observeResponse = observeService.getDashboardbyCriteria(observe);

      String category = observeResponse.getContents().getObserve()
          .stream().findFirst().get().getCategoryId();
      Long categoryId = !StringUtils.isEmpty(category) ? Long.valueOf(category) : 0L;
      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
      if (!validatePrivilege(productList, Long.valueOf(categoryId),
          Privileges.PrivilegeNames.ACCESS)) {
        observeResponse = new ObserveResponse();
        logger.error(String.format(UNAUTHORIZED, "ACCESS"));
        setUnAuthResponse(response);
        observeResponse.setMessage(String.format(UNAUTHORIZED, "ACCESS"));
        return observeResponse;
      }
    } catch (IOException ex) {
      throw new SipCreateEntityException("Problem on the storage while creating an entity");
    }
    return observeResponse;
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
  @RequestMapping(value = "/observe/dashboards/{categoryId}/{userId}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardByCategoryId(
      @PathVariable(name = "categoryId", required = true) String categoryId,
      @PathVariable(name = "userId", required = true) String userId,
      HttpServletRequest request,
      HttpServletResponse response) {
    logger.debug("categoryId {}", categoryId);
    logger.debug("userId {}", userId);
    ObserveResponse observeResponse = new ObserveResponse();

    try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
      if (!validatePrivilege(productList, Long.valueOf(categoryId),
          Privileges.PrivilegeNames.ACCESS)) {
        logger.error(String.format(UNAUTHORIZED, "ACCESS"));
        setUnAuthResponse(response);
        observeResponse.setMessage(String.format(UNAUTHORIZED, "ACCESS"));
        return observeResponse;
      }

      Observe observe = new Observe();
      observe.setCategoryId(categoryId);
      /**
       * Ignore the the user Id for now list out all the dashboard for category. TO DO : User Id is
       * required to handle the My DashBoard (private)feature.
       */
      // observe.setCreatedBy(userId);
      observeResponse = observeService.getDashboardbyCategoryId(observe);
    } catch (IOException e) {
      throw new SipCreateEntityException("Problem on the while fetching an entity");
    }
    return observeResponse;
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
  @RequestMapping(value = "/observe/dashboards/update/{Id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse updateDashboard(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String entityId,
      @RequestBody ObserveRequestBody requestBody) {
    logger.debug("dashboardId {}", entityId);
    logger.debug("Request Body", requestBody);
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

      Long categoryId = !StringUtils.isEmpty(observe.getCategoryId())
          ? Long.valueOf(observe.getCategoryId()) : 0L;
      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
      if (!validatePrivilege(productList, Long.valueOf(categoryId),
          Privileges.PrivilegeNames.EDIT)) {
        logger.error(String.format(UNAUTHORIZED, "EDIT"));
        setUnAuthResponse(response);
        observeResponse.setMessage(String.format(UNAUTHORIZED, "EDIT"));
        return observeResponse;
      }

      observe.setEntityId(entityId);
      observeResponse = observeService.updateDashboard(observe);
    } catch (IOException e) {
      throw new SipJsonProcessingException("Expected missing on the request body.");
    } catch (SipUpdateEntityException ex) {
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
  @RequestMapping(value = "/observe/dashboards/{Id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse deleteDashboard(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String entityId) {
    logger.debug("dashboard Id {}", entityId);
    ObserveResponse responseObjectFuture = new ObserveResponse();
    try {
      Observe observe = new Observe();
      observe.setEntityId(entityId);
      ObserveResponse observeResponse = observeService.getDashboardbyCriteria(observe);
      Content content = observeResponse.getContents();
      String category = content.getObserve().stream().findFirst().get().getCategoryId();

      Long categoryId = !StringUtils.isEmpty(category) ? Long.valueOf(category) : 0L;
      Ticket ticket = SipCommonUtils.getTicket(request);
      ArrayList<Products> productList = ticket != null ? ticket.getProducts() : null;
      if (!validatePrivilege(productList, Long.valueOf(categoryId),
          Privileges.PrivilegeNames.DELETE)) {
        logger.error(String.format(UNAUTHORIZED, "DELETE"));
        setUnAuthResponse(response);
        responseObjectFuture.setMessage(String.format(UNAUTHORIZED, "DELETE"));
        return responseObjectFuture;
      }
      responseObjectFuture = observeService.deleteDashboard(observe);
    } catch (Exception ex) {
      throw new SipJsonProcessingException("Expected missing on the request body.");
    }
    return responseObjectFuture;
  }

  /**
   * This method generates unique Id.
   *
   * @param request  of type object.
   * @param response of type object.
   * @return ObserveResponse which will hold the response structure.
   */
  @RequestMapping(value = "/observe/dashboard/generateId", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse generateDashboardId(
      HttpServletRequest request, HttpServletResponse response) {
    ObserveResponse observeResponse = new ObserveResponse();
    observeResponse.setId(observeService.generateId());
    return observeResponse;
  }
}
