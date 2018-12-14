package com.synchronoss.saw.logs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.models.BisFileLogDetails;
import com.synchronoss.saw.logs.models.BisRouteHistory;
import com.synchronoss.saw.logs.models.ScheduleDetail;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
@RequestMapping(value = "/ingestion/batch")
public class SawBisFileLogsController {
  @Value("${bis.scheduler-url}")
  private String bisSchedulerUrl;

  RestTemplate restTemplate = new RestTemplate();
  private static final Logger logger = LoggerFactory.getLogger(SawBisFileLogsController.class);
  @Autowired
  private BisFileLogsRepository bisLogsRepository;
  
  @ApiOperation(value = "Retrieve all logs of all routes", nickname = "all routes history", notes = "",
      response = BisRouteHistory.class)
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), 
          @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  public List<BisFileLog> retrieveAllLogs() {
    return this.bisLogsRepository.findAll();
  }

  @ApiOperation(value = "Retrieve log record by log Id", nickname = "routeLogWithId", notes = "",
      response = BisRouteHistory.class)
  @RequestMapping(value = "/logs/{id}", method = RequestMethod.GET)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), 
          @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  public BisFileLog retriveLogById(@PathVariable String id) {

    return this.bisLogsRepository.findByPid(id);
  }
  
  /**
   * Route history including status
   * of each job and last fire time, next fire time.
   * 
   * @param channelId channel
   * @param routeId route
   * @return route history
   */
  @ApiOperation(value = "Retrieve logs of route as history", nickname = "routeHistory", notes = "",
      response = BisRouteHistory.class)
  @RequestMapping(value = "/logs/{channelId}/{routeId}", method = RequestMethod.GET)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), 
          @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  public BisRouteHistory retrieveRouteLogHistory(
      @PathVariable Long channelId, @PathVariable Long routeId) {
    logger.trace("Constructing request for job with group key: " 
        + routeId + "and CategoryID: " + channelId);
    ScheduleDetail params = new ScheduleDetail();
    params.setGroupkey(String.valueOf(routeId));
    params.setCategoryId(String.valueOf(channelId));
    logger.trace("Invoking scheduler for last fire time and next fire time "
        + "values. URL :  " + bisSchedulerUrl + "/jobs?categoryId=" 
        + channelId + "&groupkey=" + routeId);
    String response = restTemplate
        .getForObject(bisSchedulerUrl + "/jobs?categoryId=" 
            + channelId + "&groupkey=" + routeId, String.class);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode;
    JsonNode dataNode;
    JsonNode latFired = null;
    JsonNode nextFired = null;
    try {
      rootNode = objectMapper.readTree(response);
      dataNode = rootNode.get("data");
      if (dataNode.isArray() && dataNode.size() > 0) {
        JsonNode objNode = dataNode.get(0);
        latFired = objNode.get("lastFiredTime");
        nextFired = objNode.get("nextFireTime");

      }
    } catch (IOException exception) {
      logger.error(exception.getMessage());
    }
    
    
    List<BisFileLog> bisFileLogs = this.bisLogsRepository.findByRouteSysId(routeId);
    List<BisFileLogDetails> bisFileLogDtos = new ArrayList<BisFileLogDetails>();
    for (BisFileLog bisFIleLog : bisFileLogs) {
      BisFileLogDetails logDto = new BisFileLogDetails();
      BeanUtils.copyProperties(bisFIleLog, logDto);
      bisFileLogDtos.add(logDto);
    }
    BisRouteHistory bisRouteHistory = new BisRouteHistory();
    if (latFired != null) {
      bisRouteHistory.setLastFireTime(latFired.asLong());
    }

    if (nextFired != null) {
      bisRouteHistory.setLastFireTime(nextFired.asLong());
    }
    bisRouteHistory.setLogs(bisFileLogDtos);
    return bisRouteHistory;

  }
}
