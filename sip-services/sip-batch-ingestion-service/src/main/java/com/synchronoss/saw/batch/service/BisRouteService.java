package com.synchronoss.saw.batch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.dto.BisRouteDto;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.ResourceNotFoundException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisScheduleKeys;
import com.synchronoss.sip.utils.RestUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

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
  private String scheduleUri = "/scheduler/bisscheduler";
  private String pauseUrl = "/pause";
  private String resumeUrl = "/resume";
  private static final Long STATUS_ACTIVE = 1L;
  private static final Long STATUS_INACTIVE = 0L;

  private static final Logger logger = LoggerFactory.getLogger(BisRouteService.class);
  @Autowired
  BisRouteDataRestRepository bisRouteRepository;
  
  @Autowired
  private ChannelTypeService channelTypeService;
  
  @Autowired
  private RestUtil restUtil;


  private RestTemplate restTemplate = null;

  @PostConstruct
  public void init() {
    restTemplate = restUtil.restTemplate();
  }
  
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
      String channelType = channelTypeService
          .findChannelTypeFromChannelId(channelId);
      scheduleKeys.setJobName(channelType + channelId + routeId);
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
        String channelType = channelTypeService
            .findChannelTypeFromChannelId(channelId);
        scheduleKeys.setJobName(channelType 
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
      url = bisSchedulerUrl + scheduleUri + resumeUrl;
      
    } else {
      url = bisSchedulerUrl + scheduleUri + pauseUrl;
    }
    logger.info("Invoking URL:" + url);
    
    try {
      restTemplate.postForLocation(url, scheduleKeys);
    } catch (ResourceAccessException exception) {
      logger.warn("ResourceAccessException Activate/Deactivate updated from next fire time only" 
          + scheduleKeys.getJobName() + " with " + bisSchedulerUrl + pauseUrl
          + " " + exception.getMessage());
    } catch (Exception exception) {
      logger.warn("Activate/Deactivate updated from next fire time only" 
          +  scheduleKeys.getJobName() 
           + "with" + bisSchedulerUrl + pauseUrl
          + " " + exception.getMessage());
      
    }
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

  /**
   * sets the last fire time next fire time to routeDto.
   *
   * @param routeDtos list of routes
   * @param channelId id of the channel
   */
  public void setLastNextFireTime(List<BisRouteDto> routeDtos, Long channelId) {
    ObjectMapper objectMapper = new ObjectMapper();
    routeDtos.forEach(
        route -> {
          Long routeId = route.getBisRouteSysId();
          logger.info(
              "Invoking scheduler for last fire time and next fire time "
                  + "values. URL :  "
                  + bisSchedulerUrl
                  + "/jobs?categoryId="
                  + channelId
                  + "&groupkey="
                  + routeId);
          String response =
              restTemplate.getForObject(
                  bisSchedulerUrl
                      + scheduleUri
                      + "/jobs?categoryId="
                      + channelId
                      + "&groupkey="
                      + routeId,
                  String.class);
          logger.info("response from scheduler on last fire, next fire" + response);
          JsonNode rootNode;
          JsonNode dataNode;
          Long lastFired = null;
          Long nextFired = null;
          try {
            rootNode = objectMapper.readTree(response);
            dataNode = rootNode.get("data");
            logger.info("data node from response " + dataNode);
            if (dataNode.isArray() && dataNode.size() > 0) {
              JsonNode objNode = dataNode.get(0);
              if (!objNode.get("lastFiredTime").isNull()) {
                logger.info(
                    "Retreive from Database lastFiredTime :"
                        + objNode.get("lastFiredTime").asLong());
                lastFired = objNode.get("lastFiredTime").asLong();
                route.setLastFireTime(lastFired);
              }
              if (!objNode.get("nextFireTime").isNull()) {
                logger.info(
                    "Retreive from Database nextFireTime :" + objNode.get("nextFireTime").asLong());
                nextFired = objNode.get("nextFireTime").asLong();
                route.setNextFireTime(nextFired);
              }

              logger.info("latFired from response after parsing in long" + lastFired);
              logger.info("nextFired from response after parsing in long" + nextFired);
            }
          } catch (IOException exception) {
            logger.error(exception.getMessage());
          }
        });
  }
}
