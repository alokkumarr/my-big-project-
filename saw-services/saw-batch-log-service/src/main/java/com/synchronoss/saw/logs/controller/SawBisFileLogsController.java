package com.synchronoss.saw.logs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.models.BisFileLogDetails;
import com.synchronoss.saw.logs.models.BisRouteHistory;
import com.synchronoss.saw.logs.models.ScheduleDetail;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  @Autowired
  private BisFileLogsRepository bisLogsRepository;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public List<BisFileLog> retrieveAllLogs() {
    return this.bisLogsRepository.findAll();
  }

  @RequestMapping(value = "/logs/{id}", method = RequestMethod.GET)
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
  @RequestMapping(value = "/logs/{channelId}/{routeId}", method = RequestMethod.GET)
  public BisRouteHistory retrieveRouteLogHistory(
      @PathVariable Long channelId, @PathVariable Long routeId) {
    
    ScheduleDetail params = new ScheduleDetail();
    params.setGroupkey(String.valueOf(routeId));
    params.setCategoryId(String.valueOf(channelId));

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
    } catch (IOException e) {
      e.printStackTrace();
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
