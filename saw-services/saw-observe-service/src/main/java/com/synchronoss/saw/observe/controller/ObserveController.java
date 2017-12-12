package com.synchronoss.saw.observe.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.observe.model.ObserveResponse;

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

  /**
   * This method is used to create a dashboard entity in mapr store with id
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/dashboards/{Id}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ListenableFuture<ResponseEntity<ObserveResponse>> addDashboard(@PathVariable("Id") String Id, 
      HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody){
    logger.debug("dashboardId {}", Id);
    logger.debug("Request Body", requestBody);
    ListenableFuture<ResponseEntity<ObserveResponse>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
 
  @RequestMapping(value = "/dashboards/{Id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<ObserveResponse>> getDashboardById (@PathVariable("Id") String Id, 
      HttpServletRequest request, HttpServletResponse response){
    logger.debug("dashboardId {}", Id);
    ListenableFuture<ResponseEntity<ObserveResponse>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
  
  @RequestMapping(value = "/dashboards", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<ObserveResponse>> listOfDashboard (HttpServletRequest request, HttpServletResponse response){
    ListenableFuture<ResponseEntity<ObserveResponse>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
  
  @RequestMapping(value = "/dashboards/{Id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<?>> updateDashboard (HttpServletRequest request, HttpServletResponse response, @PathVariable("Id") String Id){
    ListenableFuture<ResponseEntity<?>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
  @RequestMapping(value = "/dashboards/{Id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<ObserveResponse>> deleteDashboard (HttpServletRequest request, HttpServletResponse response, @PathVariable("Id") String Id){
    ListenableFuture<ResponseEntity<ObserveResponse>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
  
  @RequestMapping(value = "/dashboard/generateId", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<ObserveResponse>> generateDashboardId( 
      HttpServletRequest request, HttpServletResponse response){
    ListenableFuture<ResponseEntity<ObserveResponse>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
}
