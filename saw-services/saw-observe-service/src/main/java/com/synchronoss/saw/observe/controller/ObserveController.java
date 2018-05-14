package com.synchronoss.saw.observe.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.observe.ObserveUtils;
import com.synchronoss.saw.observe.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.observe.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.observe.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.observe.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveRequestBody;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.service.ObserveService;

/**
 * @author spau0004
 * This class is used to perform CRUD operation for the dashboard metadata
 * The requests are JSON documents in the following formats
 * {
 *  "contents":{
 *         "keys":[],
 *         "action":"execute",
 *               "observe":[
 *                      {
 *                          id: 'string',
 *                           categoryId: 'string',
 *                           name: 'string',
 *                           description: 'string',
 *                           options: 'json',
 *                           tiles: [{ type: 'analysis', id: 'analysisId - string', cols: 'number', rows: 'number', x: 'number', y: 'number', options: 'json' }],
 *                            filters: []
 *                        }
 *                           ]
 *               }
 *  }
 */
@RestController
public class ObserveController {

  private static final Logger logger = LoggerFactory.getLogger(ObserveController.class);

  @Autowired
  private ObserveService observeService;
  
  /**
   * This method is used to create a dashboard entity in mapr store with id
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/observe/dashboards/create", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ObserveResponse addDashboard(@RequestBody ObserveRequestBody requestBody) {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    ObserveResponse responseObjectFuture = null;
   try {
     ObjectMapper objectMapper = new ObjectMapper();
     objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
     objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      Observe observe = ObserveUtils.getObserveNode(objectMapper.writeValueAsString(requestBody), "contents");
      logger.trace("Observe request object : {} ", objectMapper.writeValueAsString(observe));
      observe.setEntityId(observeService.generateId());
      logger.trace("Invoking service with entity id : {} ", observe.getEntityId());
      responseObjectFuture = observeService.addDashboard(observe);
    } catch (IOException e) {
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (CreateEntitySAWException ex) {
      throw new CreateEntitySAWException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  @RequestMapping(value = "/observe/dashboards/{Id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardById(@PathVariable(name = "Id", required = true) String Id,
      HttpServletRequest request, HttpServletResponse response) {
    logger.debug("dashboardId {}", Id);
    ObserveResponse responseObjectFuture = null;
    Observe observe = new Observe();
    observe.setEntityId(Id);
    responseObjectFuture = observeService.getDashboardbyCriteria(observe);
    return responseObjectFuture;
  }
  
  @RequestMapping(value = "/observe/dashboards/{categoryId}/{userId}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardByCategoryId(@PathVariable(name = "categoryId", required = true) String categoryId,
      @PathVariable(name = "userId", required = true) String userId, HttpServletRequest request, HttpServletResponse response) {
    logger.debug("categoryId {}", categoryId);
    logger.debug("userId {}", userId);
    ObserveResponse responseObjectFuture = null;
    Observe observe = new Observe();
    observe.setCategoryId(categoryId);
    /** Ignore the the user Id for now list out all the dashboard for category.
     *  TO DO : User Id is required to handle the My DashBoard (private)feature.
     */
    observe.setCreatedBy(userId);
    responseObjectFuture = observeService.getDashboardbyCategoryId(observe);
    return responseObjectFuture;
  }

    @RequestMapping(value = "/observe/dashboards/update/{Id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse updateDashboard(HttpServletRequest request, HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String Id, @RequestBody ObserveRequestBody requestBody) {
    logger.debug("dashboardId {}", Id);
    logger.debug("Request Body", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    ObserveResponse responseObjectFuture = null;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      Observe observe = ObserveUtils.getObserveNode(objectMapper.writeValueAsString(requestBody), "contents");
      observe.setEntityId(Id);
      responseObjectFuture = observeService.updateDashboard(observe);
    } catch (IOException e) {
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (UpdateEntitySAWException ex) {
      throw new UpdateEntitySAWException("Entity does not exist.");
    }
    return responseObjectFuture;
  }

  @RequestMapping(value = "/observe/dashboards/{Id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse deleteDashboard(HttpServletRequest request, HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String Id) {
    logger.debug("dashboard Id {}", Id);
    ObserveResponse responseObjectFuture = null;
    Observe observe = new Observe();
    observe.setEntityId(Id);
    responseObjectFuture = observeService.deleteDashboard(observe);
    return responseObjectFuture;
  }

  @RequestMapping(value = "/observe/dashboard/generateId", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse generateDashboardId(HttpServletRequest request,
      HttpServletResponse response) {
    ObserveResponse observeResponse = new ObserveResponse();
    observeResponse.setId(observeService.generateId());
    return observeResponse;
  }
}
