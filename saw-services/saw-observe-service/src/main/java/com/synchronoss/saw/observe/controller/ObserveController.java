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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.synchronoss.saw.observe.ObserveUtils;
import com.synchronoss.saw.observe.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.observe.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.observe.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.observe.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Observe;
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
@RequestMapping(value = "/observe/")
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
  @RequestMapping(value = "/dashboards/create", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ObserveResponse addDashboard(HttpServletRequest request, HttpServletResponse response,
      @RequestBody String requestBody) {
    logger.debug("Request Body", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    ObserveResponse responseObjectFuture = null;
    try {
      Observe observe = ObserveUtils.getObserveNode(requestBody, "observe");
      observe.setId(observeService.generateId().getId());
      responseObjectFuture = observeService.addDashboard(observe);
    } catch (IOException e) {
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (CreateEntitySAWException ex) {
      throw new CreateEntitySAWException("Problem on the storage while creating an entity",
          ex.getCause());
    }
    return responseObjectFuture;
  }

  @RequestMapping(value = "/dashboards/read/{Id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse getDashboardById(@PathVariable(name = "Id", required = true) String Id,
      HttpServletRequest request, HttpServletResponse response) {
    logger.debug("dashboardId {}", Id);
    ObserveResponse responseObjectFuture = null;
    Observe observe = new Observe();
    observe.setId(Id);
    responseObjectFuture = observeService.getDashboardbyCriteria(observe);
    return responseObjectFuture;
  }

  @RequestMapping(value = "/dashboards/list", params = {"size"}, method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse listOfDashboard(HttpServletRequest request, HttpServletResponse response,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    logger.debug("size {}", size);
    ObserveResponse responseObjectFuture = null;
    responseObjectFuture = observeService.listOfDashboardByCriteria(size);
    return responseObjectFuture;
  }

  @RequestMapping(value = "/dashboards/update/{Id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse updateDashboard(HttpServletRequest request, HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String Id, @RequestBody String requestBody) {
    logger.debug("dashboardId {}", Id);
    logger.debug("Request Body", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    ObserveResponse responseObjectFuture = null;
    try {
      Observe observe = ObserveUtils.getObserveNode(requestBody, "observe");
      observe.setId(Id);
      responseObjectFuture = observeService.addDashboard(observe);
    } catch (IOException e) {
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (UpdateEntitySAWException ex) {
      throw new UpdateEntitySAWException("Entity does not exist : ", ex.getCause());
    }
    return responseObjectFuture;
  }

  @RequestMapping(value = "/dashboards/{Id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse deleteDashboard(HttpServletRequest request, HttpServletResponse response,
      @PathVariable(name = "Id", required = true) String Id) {
    logger.debug("dashboard Id {}", Id);
    ObserveResponse responseObjectFuture = null;
    Observe observe = new Observe();
    observe.setId(Id);
    responseObjectFuture = observeService.deleteDashboard(observe);
    return responseObjectFuture;
  }

  @RequestMapping(value = "/dashboard/generateId", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ObserveResponse generateDashboardId(HttpServletRequest request,
      HttpServletResponse response) {
    ObserveResponse responseObjectFuture = observeService.generateId();
    return responseObjectFuture;
  }
}
