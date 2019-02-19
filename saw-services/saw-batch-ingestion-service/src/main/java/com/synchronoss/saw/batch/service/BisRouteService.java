package com.synchronoss.saw.batch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.ResourceNotFoundException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisScheduleKeys;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class BisRouteService {

  @Value("${bis.scheduler-url}")
  private String bisSchedulerUrl;
  private String pauseUrl = "/pause";
  private String resumeUrl = "/resume";
  private static final Long STATUS_ACTIVE = 1L;
  private static final Long STATUS_INACTIVE = 0L;

  private static final Logger logger = LoggerFactory.getLogger(BisRouteService.class);
  @Autowired
  BisRouteDataRestRepository bisRouteRepository;
  RestTemplate restTemplate = new RestTemplate();
  
  /**
   * Temporarily deactivate or re activate a route 
   * and schedules of the route.
   * 
   * @param channelId id of chanel
   * @param routeId id of route
   * @param isActivate true or false
   */
  public void activateOrDeactivateRoute(Long channelId, Long routeId, boolean isActivate) {
    // update bis_route table status
    // resume schedule for route
    logger.trace("Retrieve route");
    Optional<BisRouteEntity> route = bisRouteRepository.findById(routeId);
    BisRouteEntity routeEntity = null;
    if (route.isPresent()) {
      logger.trace("Updating route status");
      routeEntity =  route.get();
      routeEntity.setStatus(isActivate ? STATUS_ACTIVE : STATUS_INACTIVE);
      bisRouteRepository.saveAndFlush(routeEntity);
      
      BisScheduleKeys scheduleKeys = new BisScheduleKeys();
      scheduleKeys.setGroupName(String.valueOf(routeId));
      scheduleKeys.setJobName(BisChannelType.SFTP.name() + channelId + routeId);
      updateScheduledJobsStatus(isActivate, scheduleKeys);

    } else {
      new ResourceNotFoundException("No route found with route " + routeId);
    }
  }
  
  /**
   * Temporarily deactivates or re activates all routes of a
   * channel.
   * 
   * @param channelId id of channel
   * @param isActivate true or false
   */
  public void activateOrDeactivateRoutes(long channelId, boolean isActivate) {

    logger.trace("Retriving routes");
    List<BisRouteEntity> routes = bisRouteRepository.findByBisChannelSysId(
        channelId, Pageable.unpaged()).getContent();

    // update route status

    if (routes.isEmpty()) {
      logger.trace("No routes exists for this channel");
    } else {
      
     
      /**
       * For each route pause the scheduled jobs.
       */
      for (BisRouteEntity bisRouteEntity : routes) {
        logger.trace("Updating routes....");
        bisRouteEntity.setStatus(isActivate ? STATUS_ACTIVE : STATUS_INACTIVE);
        bisRouteRepository.saveAndFlush(bisRouteEntity);
        
        BisScheduleKeys scheduleKeys = new BisScheduleKeys();
        scheduleKeys.setGroupName(String.valueOf(bisRouteEntity.getBisRouteSysId()));
        scheduleKeys.setJobName(BisChannelType.SFTP.name() 
            + channelId + bisRouteEntity.getBisRouteSysId());
        updateScheduledJobsStatus(isActivate, scheduleKeys);
      }
      logger.trace("Updating routes completed");
    }
    

  }

  private void updateScheduledJobsStatus(boolean isActivate, BisScheduleKeys scheduleKeys) {
    String url;
    logger.trace("Updating route scheduled jobs");
    
    if (isActivate) {
      url = bisSchedulerUrl + resumeUrl;
      
    } else {
      url = bisSchedulerUrl + pauseUrl;
    }
    logger.trace("Invoking URL:" + url);
    
    //invoke scheduler
    restTemplate.postForLocation(url, scheduleKeys);
  }
  
  /**
   * checks if there is a route already with given
   * name.
   * 
   * @param channelId id of the channel
   * @param routeName name of the route
   * @return true or false
   */
  public boolean isRouteNameExists(Long channelId, String routeName) {
    List<BisRouteEntity> routeEntities = bisRouteRepository
        .findByBisChannelSysId(channelId, Pageable.unpaged())
        .getContent();
    ObjectMapper objectMapper = new ObjectMapper();
    logger.trace("Retrieving route metadata");
    Optional<BisRouteEntity> route = routeEntities.stream().filter(bisRouteEntity -> {
      JsonNode metaDataNode;
      JsonNode existingRoute;
      try {
        metaDataNode = objectMapper.readTree(bisRouteEntity.getRouteMetadata());
        logger.trace("Parsing route metadata");
        existingRoute = metaDataNode.get("routeName");
        if (existingRoute != null && existingRoute.asText().equalsIgnoreCase(routeName)) {
          return true;
        }
      } catch (IOException exception) {
        logger.error(exception.getMessage());
      }
      return false;
    }).findAny();
    return route.isPresent();
  }

}
