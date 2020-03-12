package com.synchronoss.saw.logs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.models.BisFileLogDetails;
import com.synchronoss.saw.logs.models.BisLogs;
import com.synchronoss.saw.logs.models.BisRouteHistory;
import com.synchronoss.saw.logs.models.ScheduleDetail;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;
import com.synchronoss.sip.utils.RestUtil;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.management.OperationsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/ingestion/batch")
public class SawBisFileLogsController {
  @Value("${bis.scheduler-url}")
  private String bisSchedulerUrl;
  
  private String scheduleUri = "/scheduler/bisscheduler";

  @Autowired
  private RestUtil restUtil;


  private RestTemplate restTemplate = null;

  @PostConstruct
  public void init() {
    restTemplate = restUtil.restTemplate();
  }
 
  
  
  private static final Logger logger = LoggerFactory.getLogger(SawBisFileLogsController.class);
  @Autowired
  private BisFileLogsRepository bisLogsRepository;
  
  @ApiOperation(value = "Retrieve all logs of all routes", 
      nickname = "all routes history", notes = "",
      response = BisRouteHistory.class)
  @RequestMapping(value = "/internal/logs", method = RequestMethod.GET)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), 
          @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  public List<BisFileLog> retrieveAllLogs() {
    return this.bisLogsRepository.findAll(bisLogsRepository.orderBy("createdDate"));
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
   * Retrive file logs by job id.
   *
   * @param jobId job identifer
   * @param offset pagination offset
   * @param size size of page
   * @param sort direction
   * @param column column to sort
   * @return
   */
  @ApiOperation(
      value = "Retrieve log record by log Id",
      nickname = "routeLogWithId",
      notes = "",
      response = BisLogs.class)
  @RequestMapping(value = "/logs/job/{jobId}", method = RequestMethod.GET)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Request has been " + "succeeded without any error"),
        @ApiResponse(
            code = 404,
            message = "The resource " + "you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. " + "Contact System adminstrator"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(
            code = 415,
            message = "Unsupported Type. " + "Representation not supported for the resource")
      })
  public BisLogs retriveLogByJobId(
      @PathVariable Long jobId,
      @ApiParam(value = "offset number", required = false)
          @RequestParam(name = "offset", defaultValue = "0")
          int offset,
      @ApiParam(value = "number of objects per page", required = false)
          @RequestParam(name = "size", defaultValue = "10")
          int size,
      @ApiParam(value = "sort order", required = false)
          @RequestParam(name = "sort", defaultValue = "desc")
          String sort,
      @ApiParam(value = "column name to be sorted", required = false)
          @RequestParam(name = "column", defaultValue = "createdDate")
          String column) {

    Page<BisFileLog> fileLogs =
        this.bisLogsRepository.findByJob_JobId(
            jobId, PageRequest.of(offset, size, Sort.Direction.fromString(sort), column));
    BisLogs bisLogs = new BisLogs(fileLogs.getTotalElements(), fileLogs.getTotalPages());
    List<BisFileLog> bisFileLogList = fileLogs.getContent();
    bisLogs.setBisFileLogs(bisFileLogList);
    return bisLogs;
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
        .getForObject(bisSchedulerUrl + scheduleUri + "/jobs?categoryId=" 
            + channelId + "&groupkey=" + routeId, String.class);

    logger.trace("response from scheduler on last fire, next fire" + response);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode;
    JsonNode dataNode;
    Long latFired = null;
    Long nextFired = null;
    try {
      String sanitizedResponse = SipCommonUtils.sanitizeJson(response);
      rootNode = objectMapper.readTree(sanitizedResponse);
      dataNode = rootNode.get("data");
      logger.trace("data node from response " + dataNode);
      if (dataNode.isArray() && dataNode.size() > 0) {
        JsonNode objNode = dataNode.get(0);
        if (!objNode.get("lastFiredTime").isNull()) {
          logger.trace(
              "Retrieve from Database lastFiredTime :" + objNode.get("lastFiredTime").asLong());
          latFired = objNode.get("lastFiredTime").asLong();
        }
        if (!objNode.get("nextFireTime").isNull()) {
          logger.trace(
              "Retrieve from Database nextFireTime :" + objNode.get("nextFireTime").asLong());
          nextFired = objNode.get("nextFireTime").asLong();
        }
        
        logger.trace("latFired from response after parsing in long" + latFired);
        logger.trace("nextFired from response after parsing in long" + nextFired);

      }
    } catch (IOException exception) {
      logger.error(exception.getMessage());
    } 
    
    List<BisFileLog> bisFileLogs =
        this.bisLogsRepository.findByRouteSysId(routeId, bisLogsRepository.orderBy("createdDate"));
    List<BisFileLogDetails> bisFileLogDtos = new ArrayList<BisFileLogDetails>();
    for (BisFileLog bisFIleLog : bisFileLogs) {
      BisFileLogDetails logDto = new BisFileLogDetails();
      BeanUtils.copyProperties(bisFIleLog, logDto);
      bisFileLogDtos.add(logDto);
    }
    BisRouteHistory bisRouteHistory = new BisRouteHistory();
   
    if (latFired != null) {
      bisRouteHistory.setLastFireTime(latFired);
    }

    if (nextFired != null) {
      bisRouteHistory.setNextFireTime(nextFired);
    }
    logger.trace("latFired from response  after setting to "
        + "response " + bisRouteHistory.getLastFireTime());
    logger.trace("nextFired from response  after setting to "
        + "response " + bisRouteHistory.getNextFireTime());
    bisRouteHistory.setLogs(bisFileLogDtos);
    return bisRouteHistory;

  }

  /**
   * This method is used for integration test cases for tear down the entries.
   * Note : should not be used from outside
   * @return boolean true or false
   * @throws OperationsException exception.
   */
  @RequestMapping(value = "/internal/logs", method = RequestMethod.DELETE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Map<String, Boolean> removeAll() throws OperationsException {
    Map<String, Boolean> responseMap = new HashMap<String, Boolean>();
    Boolean result = false;
    logger.trace("deleting all records");
    try {
      bisLogsRepository.deleteAll();
      result = true;
    } catch (Exception ex) {
      throw new OperationsException("Exception occurred while performing delete operation");
    }
    responseMap.put("isDuplicate", result);
    return responseMap;
  }

}
