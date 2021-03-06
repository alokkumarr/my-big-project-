package com.synchronoss.saw.batch.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.dto.BisRouteDto;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.BisException;
import com.synchronoss.saw.batch.exception.ResourceNotFoundException;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.model.BisScheduleKeys;
import com.synchronoss.saw.batch.model.BisSchedulerRequest;
import com.synchronoss.saw.batch.service.BisRouteService;
import com.synchronoss.saw.batch.service.ChannelTypeService;
import com.synchronoss.sip.utils.RestUtil;

import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

@RestController
@Api(value = "The controller provides operations related Route Entity "
    + "synchronoss analytics platform ")
@RequestMapping("/ingestion/batch")
public class SawBisRouteController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisRouteController.class);

  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository;
  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Value("${bis.scheduler-url}")
  private String bisSchedulerUrl;
  private String scheduleUri = "/scheduler/bisscheduler";

  private String insertUrl = "/schedule";
  private String updateUrl = "/update";
  private String deleteUrl = "/delete";
  private static final Long STATUS_ACTIVE = 1L;
  private static final Long STATUS_DEACTIVE = 0L;


  @Value("${bis.default-data-drop-location}")
  private String dropLocation;

  @Autowired
  private RetryTemplate retryTemplate;

  @Autowired
  private BisRouteService bisRouteService;

  @Autowired
  ChannelTypeService channelTypeService;


  @Autowired
  private RestUtil restUtil;


  private RestTemplate restTemplate = null;

  @PostConstruct
  public void init() {
    restTemplate = restUtil.restTemplate();
  }

  /**
   * This API provides an ability to add a source.
   */
  @ApiOperation(value = "Adding a new Route", nickname = "actionBis", notes = "",
      response = BisRouteDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Created"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{channelId}/routes", method = RequestMethod.POST,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<@Valid BisRouteDto> createRoute(
      @ApiParam(value = "Channel Id", required = true) @PathVariable Long channelId,
      @ApiParam(value = "Route related information to store", required = true)
        @Valid @RequestBody BisRouteDto requestBody) throws NullPointerException {
    logger.trace("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    return ResponseEntity.ok(bisChannelDataRestRepository.findById(channelId).map(channel -> {
      BisRouteEntity routeEntity = new BisRouteEntity();
      logger.trace("Channel retrieved :" + channel);
      String routeMetaData = requestBody.getRouteMetadata();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      ObjectNode routeData;
      try {
        requestBody.setBisChannelSysId(channelId);
        BeanUtils.copyProperties(requestBody, routeEntity);
        routeEntity.setCreatedDate(new Date());
        routeEntity.setStatus(STATUS_ACTIVE);
        routeData = (ObjectNode) objectMapper.readTree(routeMetaData);

        /*
         * Check duplicate route.
         */
        JsonNode routeName = routeData.get("routeName");
        boolean isExists = bisRouteService.isRouteNameExists(channelId, routeName.asText());

        if (isExists) {
          throw new BisException("Route Name: " + routeName + "  already exists");
        }
        routeEntity.setRouteMetadata(objectMapper.writeValueAsString(routeData));
      } catch (IOException e) {
        logger.error("Exception occurred while creating routeMetaData ", e);
        throw new SftpProcessorException("Exception occurred while creating routeMetaData ", e);
      }
      // below block to store the route details
      routeEntity = bisRouteDataRestRepository.save(routeEntity);
      requestBody.setBisRouteSysId(routeEntity.getBisRouteSysId());
      requestBody.setCreatedDate(routeEntity.getCreatedDate().getTime());
      JsonNode schedulerExpn = routeData.get("schedulerExpression");
      if (schedulerExpn != null) {
        BisSchedulerRequest schedulerRequest = new BisSchedulerRequest();
        schedulerRequest.setChannelId(channelId.toString());
        schedulerRequest.setRouteId(String.valueOf(routeEntity.getBisRouteSysId()));
        schedulerRequest.setJobName(channel.getChannelType() + routeEntity.getBisChannelSysId()
            + routeEntity.getBisRouteSysId().toString());
        schedulerRequest.setChannelType(channel.getChannelType());
        schedulerRequest.setJobGroup(String.valueOf(requestBody.getBisRouteSysId()));

        // If activeTab is immediate the its immediate job.
        // irrespective of request set expression to empty
        // so that scheduler treats as immediate
        JsonNode activeTab = schedulerExpn.get("activeTab");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        if (activeTab != null && activeTab.asText().equals("immediate")) {
          schedulerRequest.setCronExpression("");
        } else {
          JsonNode cronExp = schedulerExpn.get("cronexp");
          JsonNode startDateStr = schedulerExpn.get("startDate");
          JsonNode endDateStr = schedulerExpn.get("endDate");
          JsonNode timezone = schedulerExpn.get("timezone");
          if (cronExp != null) {
            schedulerRequest.setCronExpression(cronExp.asText());
          }

          // Date in sent in User's locale time along with the timezone.
          // E.g.: 2019-02-07T00:00:26+05:30
          // This will be converted to machine time

          try {
            if (startDateStr != null) {
              logger.debug("Start Date = " + startDateStr.asText());

              Date startDate = dateFormat.parse(startDateStr.asText());
              logger.debug("Start Date in system timezone = " + startDate);
              schedulerRequest.setJobScheduleTime(startDate.getTime());
            }
            if (endDateStr != null && !endDateStr.asText().equals("")) {
              logger.debug("End Date = " + endDateStr.asText());

              Date endDate = dateFormat.parse(endDateStr.asText());
              logger.debug("End Date system timezone = " + endDate);
              schedulerRequest.setEndDate(endDate.getTime());
            } else {
              // Set end date to Dec 31 2999 UTC. This will act as no end date
              schedulerRequest.setEndDate(32503573800000L);
            }
            if (timezone != null) {
              schedulerRequest.setTimezone(timezone.asText());
            }
          } catch (ParseException e) {
            logger.error(e.getMessage());
          }
        }
        logger.info("posting scheduler inserting uri starts here: " + bisSchedulerUrl + scheduleUri
            + insertUrl);
        restTemplate.postForLocation(bisSchedulerUrl + scheduleUri + insertUrl, schedulerRequest);
        logger.info("posting scheduler inserting uri ends here: " + bisSchedulerUrl + scheduleUri
            + insertUrl);
      }
      BeanUtils.copyProperties(routeEntity, requestBody);
      return requestBody;
    }).orElseThrow(() -> new ResourceNotFoundException("channelId " + channelId + " not found")));
  }

  /**
   * This API provides an ability to read a routes with pagination by channelId.
   */

  @ApiOperation(value = "Reading list of routes & paginate by channel id", nickname = "actionBis",
      notes = "", response = BisRouteDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{id}/routes", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<BisRouteDto>> readRoutes(
      @ApiParam(value = "id", required = true) @PathVariable(name = "id", required = true) Long id,
      @ApiParam(value = "page number", required = false) @RequestParam(name = "page",
          defaultValue = "0") int page,
      @ApiParam(value = "number of objects per page", required = false) @RequestParam(name = "size",
          defaultValue = "10") int size,
      @ApiParam(value = "sort order", required = false) @RequestParam(name = "sort",
          defaultValue = "desc") String sort,
      @ApiParam(value = "column name to be sorted", required = false) @RequestParam(name = "column",
          defaultValue = "createdDate") String column)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    final List<BisRouteDto> routeDtos = new ArrayList<>();
    retryTemplate.execute(context -> routeDtos.addAll(listOfRoutes(id, page, size, sort, column)));
    bisRouteService.setLastNextFireTime(routeDtos,id);
    return ResponseEntity.ok(routeDtos);
  }

  private List<BisRouteDto> listOfRoutes(Long id, int page, int size, String sort, String column) {
    List<BisRouteEntity> routeEntities = bisRouteDataRestRepository
            .findByBisChannelSysId(id, PageRequest.of(page, size, Direction.DESC, column))
            .getContent();
    List<BisRouteDto> routeDtos = new ArrayList<>();
    routeEntities.forEach(route -> {
      BisRouteDto routeDto = new BisRouteDto();
      BeanUtils.copyProperties(route, routeDto);
      if (route.getCreatedDate() != null) {
        routeDto.setCreatedDate(route.getCreatedDate().getTime());
      }
      if (route.getModifiedDate() != null) {
        routeDto.setModifiedDate(route.getModifiedDate().getTime());
      }
      routeDtos.add(routeDto);
    });
    return routeDtos;
  }


  /**
   * This API provides an ability to update a source.
   */
  @ApiOperation(value = "Updating an existing routes by channel id", nickname = "actionBis",
      notes = "", response = BisRouteDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Updated"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{channelId}/routes/{routeId}", method = RequestMethod.PUT,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<BisRouteDto> updateRoutes(
      @ApiParam(value = "Channel id", required = true) @PathVariable(name = "channelId",
          required = true) Long channelId,
      @ApiParam(value = "Route id", required = true) @PathVariable(name = "routeId",
          required = true) Long routeId,
      @ApiParam(value = "Routes related information to update",
          required = true) @Valid @RequestBody BisRouteDto requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    if (!bisChannelDataRestRepository.existsById(channelId)) {
      throw new ResourceNotFoundException("channelId " + channelId + " not found");
    }
    return ResponseEntity.ok(bisRouteDataRestRepository.findById(routeId).map(route -> {
      logger.trace("Route updated :" + route);
      BisRouteEntity routeEntity = new BisRouteEntity();
      routeEntity = bisRouteDataRestRepository.getOne(routeId);
      String routeMetaData = requestBody.getRouteMetadata();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      ObjectNode routeData = null;
      try {
        routeData = (ObjectNode) objectMapper.readTree(routeMetaData);
        requestBody.setRouteMetadata(objectMapper.writeValueAsString(routeData));
      } catch (IOException e) {
        logger.error("Exception occurred while updating routeMetaData ", e);
        throw new SftpProcessorException("Exception occurred while updating routeMetaData ", e);
      }
      String schedulerDetails = routeData.get("schedulerExpression").toString();
      String sanitizedSchedulerDetails = SipCommonUtils.sanitizeJson(schedulerDetails);
      JsonNode schedulerExpn = routeData.get("schedulerExpression");
      if (schedulerExpn != null) {
        logger.trace("schedulerExpression  is not null ", schedulerExpn);
        BisSchedulerRequest schedulerRequest = new BisSchedulerRequest();
        schedulerRequest.setChannelId(String.valueOf(channelId.toString()));
        schedulerRequest.setRouteId(String.valueOf(routeId.toString()));
        String channelType = channelTypeService
            .findChannelTypeFromChannelId(channelId);
        schedulerRequest.setJobName(channelType + channelId + routeId);
        schedulerRequest.setChannelType(channelType);
        schedulerRequest.setJobGroup(String.valueOf(routeId));
        JsonNode schedulerData = null;
        try {
          schedulerData = objectMapper.readTree(sanitizedSchedulerDetails);
          logger.trace("schedulerData  is not null ", schedulerData);
        } catch (IOException e) {
          logger.error("Exception occurred while updating schedulerExpression ", e);
          throw new SftpProcessorException("Exception occurred while updating schedulerExpression ",
              e);
        }
        JsonNode activeTab = schedulerData.get("activeTab");
        if (activeTab != null && activeTab.asText().equals("immediate")) {
          logger.trace("schedulerData on activeTab :", activeTab);
          schedulerRequest.setCronExpression("");
        } else {
          logger.trace("schedulerData on cronTab :", schedulerData);
          JsonNode cronExp = schedulerData.get("cronexp");
          JsonNode startDate = schedulerData.get("startDate");
          JsonNode endDate = schedulerData.get("endDate");
          JsonNode timezone = schedulerData.get("timezone");
          if (cronExp != null) {
            schedulerRequest.setCronExpression(cronExp.asText());
          }
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
          try {
            if (startDate != null) {
              schedulerRequest.setJobScheduleTime(dateFormat.parse(startDate.asText()).getTime());
            }
            if (endDate != null && !endDate.asText().equals("")) {
              schedulerRequest.setEndDate(dateFormat.parse(endDate.asText()).getTime());
            } else {
              schedulerRequest.setEndDate(Long.MAX_VALUE);
            }
            if (timezone != null) {
              schedulerRequest.setTimezone(timezone.asText());
            }
          } catch (ParseException e) {
            logger.error(e.getMessage());
          }
        }
        // }
        BeanUtils.copyProperties(requestBody, routeEntity, "modifiedDate", "createdDate");
        routeEntity.setBisChannelSysId(channelId);
        routeEntity.setBisRouteSysId(routeId);
        routeEntity.setModifiedDate(new Date());
        if (routeEntity.getStatus() == STATUS_DEACTIVE) {
          throw new BisException("Update not allowed on a a deactivated route");
        }
        if (routeEntity.getStatus() == null) {
          routeEntity.setStatus(STATUS_ACTIVE);
        }
        routeEntity = bisRouteDataRestRepository.save(routeEntity);
        logger.info(
            "scheduler uri to update starts here : " + bisSchedulerUrl + scheduleUri + updateUrl);
        try {
          logger.trace("Sending the content to " + bisSchedulerUrl + scheduleUri + updateUrl + " : "
              + objectMapper.writeValueAsString(schedulerRequest));
        } catch (JsonProcessingException e) {
          throw new SftpProcessorException(
              "excpetion occurred while writing to" + " schedulerRequest ", e);
        }
        restTemplate.postForLocation(bisSchedulerUrl + scheduleUri + updateUrl, schedulerRequest);
        logger.trace(
            "scheduler uri to update ends here : " + bisSchedulerUrl + scheduleUri + updateUrl);
      }
      BeanUtils.copyProperties(routeEntity, requestBody, "routeMetadata");
      try {
        routeData = (ObjectNode) objectMapper.readTree(routeMetaData);
        // routeData.put("destinationLocation", destinationLocation);
        requestBody.setRouteMetadata(objectMapper.writeValueAsString(routeData));
      } catch (IOException e) {
        logger.error("Exception occurred while updating routeMetaData ", e);
        throw new SftpProcessorException("Exception occurred while updating routeMetaData ", e);
      }
      if (routeEntity.getCreatedDate() != null) {
        requestBody.setCreatedDate(routeEntity.getCreatedDate().getTime());
      }
      if (routeEntity.getModifiedDate() != null) {
        requestBody.setModifiedDate(routeEntity.getModifiedDate().getTime());
      }
      return requestBody;
    }).orElseThrow(() -> new ResourceNotFoundException("routeId " + routeId + " not found")));
  }

  /**
   * This API provides an ability to delete a source.
   */
  @ApiOperation(value = "Deleting an existing route", nickname = "actionBis", notes = "",
      response = Object.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Deleted"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{channelId}/routes/{routeId}", method = RequestMethod.DELETE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<ResponseEntity<Object>> deleteRoutes(
      @ApiParam(value = "Channel id", required = true) @PathVariable(name = "channelId",
          required = true) Long channelId,
      @ApiParam(value = "Route id", required = true) @PathVariable(name = "routeId",
          required = true) Long routeId)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    if (!bisChannelDataRestRepository.existsById(channelId)) {
      throw new ResourceNotFoundException("channelId " + channelId + " not found");
    }

    return ResponseEntity.ok(bisRouteDataRestRepository.findById(routeId).map(route -> {
      logger.info(
          "scheduler uri to update starts here : " + bisSchedulerUrl + scheduleUri + deleteUrl);
      BisScheduleKeys scheduleKeys = new BisScheduleKeys();
      scheduleKeys.setGroupName(String.valueOf(routeId));
      String channelType = channelTypeService
          .findChannelTypeFromChannelId(channelId);
      scheduleKeys.setJobName(channelType + channelId + routeId);
      restTemplate.postForLocation(bisSchedulerUrl + scheduleUri + deleteUrl, scheduleKeys);

      logger.trace("Route deleted :" + route);
      bisRouteDataRestRepository.deleteById(routeId);
      return ResponseEntity.ok().build();
    }).orElseThrow(() -> new ResourceNotFoundException("routeId " + routeId + " not found")));
  }

  /**
   * checks is there a route with given route name.
   * @param channelId channe identifier
   * @param routeId id of the route
   * @return true or false
   */
  @RequestMapping(value = "/channels/{channelId}/routes/{routeId}/deactivate", method
      = RequestMethod.PUT, produces = org.springframework.http.MediaType
      .APPLICATION_JSON_UTF8_VALUE)
  public Map<String,Boolean> deactivateRoute(
      @PathVariable("channelId")  Long channelId,
      @PathVariable("routeId") Long routeId) {

    logger.trace("Inside deactivating route  channelID " + channelId + "routeId: " + routeId);
    BisScheduleKeys scheduleKeys = new BisScheduleKeys();
    scheduleKeys.setGroupName(String.valueOf(routeId));
    String channelType = channelTypeService
        .findChannelTypeFromChannelId(channelId);
    scheduleKeys.setJobName(channelType + channelId + routeId);
    bisRouteService.activateOrDeactivateRoute(channelId, routeId, false);
    Map<String,Boolean> responseMap = new HashMap<String,Boolean>();
    responseMap.put("isDeactivated",Boolean.TRUE);
    return responseMap;
  }

  /**
   * checks is there a route with given route name.
   * @param channelId channe identifier
   * @param routeId id of the route
   * @return true or false
   */
  @RequestMapping(value = "/channels/{channelId}/routes/{routeId}/activate", method
      = RequestMethod.PUT, produces = org.springframework.http.MediaType
      .APPLICATION_JSON_UTF8_VALUE)
  public Map<String,Boolean> activateRoute(
      @PathVariable("channelId")  Long channelId,
      @PathVariable("routeId") Long routeId) {

    logger.trace("Inside activate route  channelID " + channelId + "routeId: " + routeId);
    BisScheduleKeys scheduleKeys = new BisScheduleKeys();
    scheduleKeys.setGroupName(String.valueOf(routeId));
    String channelType = channelTypeService
        .findChannelTypeFromChannelId(channelId);
    scheduleKeys.setJobName(channelType + channelId + routeId);
    bisRouteService.activateOrDeactivateRoute(channelId, routeId, true);
    Map<String,Boolean> responseMap = new HashMap<String,Boolean>();
    responseMap.put("isActivated", Boolean.TRUE);
    return responseMap;
  }


  /**
   * checks is there a route with given route name.
   * @param channelId channe identifier
   * @param routeName name of the route
   * @return true or false
   */
  @RequestMapping(value = "/channels/{channelId}/duplicate-route", method
      = RequestMethod.GET, produces = org.springframework.http.MediaType
      .APPLICATION_JSON_UTF8_VALUE)
  public Map<String,Boolean> isDuplicateRoute(
      @PathVariable("channelId")  Long channelId,
      @RequestParam("routeName") String routeName) {
    Map<String,Boolean> responseMap = new HashMap<String,Boolean>();
    logger.trace("Checking for duplicate route namewith channelId: " + channelId
        + " and routeName: " + routeName);
    Boolean result =  bisRouteService
        .isRouteNameExists(channelId,routeName);
    responseMap.put("isDuplicate", result);
    return responseMap;
  }

}
