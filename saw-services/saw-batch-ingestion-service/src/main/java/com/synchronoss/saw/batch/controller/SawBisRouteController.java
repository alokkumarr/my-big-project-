package com.synchronoss.saw.batch.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.dto.BisRouteDto;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.ResourceNotFoundException;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisSchedulerRequest;
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
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*")
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

  private String insertUrl = "/schedule";
  private String updateUrl = "/update";

  @Value("${bis.default-data-drop-location}")
  private String dropLocation;

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
  public ResponseEntity<@Valid BisRouteDto> createRoute(
      @ApiParam(value = "Channel Id", required = true) @PathVariable Long channelId,
      @ApiParam(value = "Route related information to store",
          required = true) @Valid @RequestBody BisRouteDto requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
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
      ObjectNode routeData = null;
      // String destinationLocation = null;
      // String sentDestinationLocation = null;
      try {
        requestBody.setBisChannelSysId(channelId);
        BeanUtils.copyProperties(requestBody, routeEntity);
        routeEntity.setCreatedDate(new Date());
        routeData = (ObjectNode) objectMapper.readTree(routeMetaData);
        /*
         * sentDestinationLocation = routeData.get("destinationLocation").asText() != null &&
         * !routeData.get("destinationLocation").asText().equals("") ?
         * routeData.get("destinationLocation").asText() : dropLocation; destinationLocation =
         * routeData.get("destinationLocation").asText() != null &&
         * !routeData.get("destinationLocation").asText().equals("") ? dropLocation + File.separator
         * + routeData.get("destinationLocation").asText() : dropLocation;
         * routeData.put("destinationLocation", destinationLocation);
         */
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
        // String schedulerDetails = routeData.get("schedulerExpression").toString();
        BisSchedulerRequest schedulerRequest = new BisSchedulerRequest();
        schedulerRequest.setChannelId(String.valueOf(channelId.toString()));
        schedulerRequest.setRouteId(String.valueOf(routeEntity.getBisRouteSysId()));
        schedulerRequest.setJobName(BisChannelType.SFTP.name() + routeEntity.getBisChannelSysId()
            + routeEntity.getBisRouteSysId().toString());
        schedulerRequest.setJobGroup(String.valueOf(requestBody.getBisRouteSysId()));
        // JsonNode schedulerData = null;
        /*
         * try { schedulerData = objectMapper.readTree(schedulerDetails); } catch (IOException e) {
         * logger.error("Exception occurred while reading schedulerExpression ", e); throw new
         * SftpProcessorException("Exception occurred while reading schedulerExpression ", e); }
         */

        // If schedule the route while creating
        /*
         * if (!schedulerExpn.toString().equals("")) { JsonNode cronExp =
         * schedulerExpn.get("cronexp"); JsonNode startDate = schedulerExpn.get("startDate");
         * JsonNode endDate = schedulerExpn.get("endDate");
         */



        // If activeTab is immediate the its immediate job.
        // irrespecitve of request set expression to empty
        // so that scheduler treats as immediate
        JsonNode activeTab = schedulerExpn.get("activeTab");
        if (activeTab != null && activeTab.asText().equals("immediate")) {
          schedulerRequest.setCronExpression("");
        } else {
          JsonNode cronExp = schedulerExpn.get("cronexp");
          JsonNode startDate = schedulerExpn.get("startDate");
          JsonNode endDate = schedulerExpn.get("endDate");
          if (cronExp != null) {
            schedulerRequest.setCronExpression(cronExp.asText());
          }
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          try {
            if (startDate != null) {
              schedulerRequest.setJobScheduleTime(dateFormat.parse(startDate.asText()));
            }
            if (endDate != null) {
              schedulerRequest.setEndDate(dateFormat.parse(endDate.asText()));
            }
          } catch (ParseException e) {
            logger.error(e.getMessage());
          }
        }
        // }
        RestTemplate restTemplate = new RestTemplate();
        logger.info("posting scheduler inserting uri starts here: " + bisSchedulerUrl + insertUrl);
        restTemplate.postForLocation(bisSchedulerUrl + insertUrl, schedulerRequest);
        logger.info("posting scheduler inserting uri ends here: " + bisSchedulerUrl + insertUrl);
      }
      /*
       * try { routeData = (ObjectNode) objectMapper.readTree(routeEntity.getRouteMetadata());
       * routeData.put("destinationLocation", sentDestinationLocation);
       * routeEntity.setRouteMetadata(objectMapper.writeValueAsString(routeData)); } catch
       * (IOException e) {
       * logger.error("Exception occurred while writing back routeMetaData to request entity ", e);
       * throw new SftpProcessorException(
       * "Exception occurred while writing back routeMetaData to request entity ", e); }
       */
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
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
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
    List<BisRouteEntity> routeEntities = bisRouteDataRestRepository
        .findByBisChannelSysId(id, PageRequest.of(page, size, Direction.DESC, column)).getContent();
    List<BisRouteDto> routeDtos = new ArrayList<>();
    routeEntities.forEach(route -> {
      BisRouteDto routeDto = new BisRouteDto();
      BeanUtils.copyProperties(route, routeDto);
      /*
       * ObjectMapper objectMapper = new ObjectMapper();
       * objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
       * objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY); ObjectNode
       * routeData = null; String sentDestinationLocation = null; String destinationLocation = null;
       * try { routeData = (ObjectNode) objectMapper.readTree(routeDto.getRouteMetadata());
       * destinationLocation = routeData.get("destinationLocation").asText(); int locationLength =
       * dropLocation.length(); sentDestinationLocation =
       * destinationLocation.startsWith(dropLocation) ?
       * destinationLocation.substring(locationLength) : destinationLocation;
       * sentDestinationLocation = sentDestinationLocation.equals("") ? dropLocation :
       * sentDestinationLocation; routeData.put("destinationLocation", sentDestinationLocation);
       * routeDto.setRouteMetadata(objectMapper.writeValueAsString(routeData)); } catch (IOException
       * e) { logger.error("Exception occurred while reading routeMetaData ", e); throw new
       * SftpProcessorException("Exception occurred while reading routeMetaData ", e); }
       */
      if (route.getCreatedDate() != null) {
        routeDto.setCreatedDate(route.getCreatedDate().getTime());
      }
      if (route.getModifiedDate() != null) {
        routeDto.setModifiedDate(route.getModifiedDate().getTime());
      }
      routeDtos.add(routeDto);
    });
    return ResponseEntity.ok(routeDtos);
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
      // String routeMetaDataFromStore = routeEntity.getRouteMetadata();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      ObjectNode routeData = null;
      // ObjectNode routeDataFromStore = null;
      // String destinationLocation = null;
      try {
        routeData = (ObjectNode) objectMapper.readTree(routeMetaData);
        // routeDataFromStore = (ObjectNode) objectMapper.readTree(routeMetaDataFromStore);
        // destinationLocation = routeData.get("destinationLocation").asText();
        // routeData.put("destinationLocation", dropLocation + destinationLocation);
        requestBody.setRouteMetadata(objectMapper.writeValueAsString(routeData));
      } catch (IOException e) {
        logger.error("Exception occurred while updating routeMetaData ", e);
        throw new SftpProcessorException("Exception occurred while updating routeMetaData ", e);
      }
      String schedulerDetails = routeData.get("schedulerExpression").toString();
      // String schedulerDetailsFromStore =
      // routeDataFromStore.get("schedulerExpression").toString();
      JsonNode schedulerExpn = routeData.get("schedulerExpression");
      if (schedulerExpn != null) {
        logger.trace("schedulerExpression  is not null ", schedulerExpn);
        BisSchedulerRequest schedulerRequest = new BisSchedulerRequest();
        schedulerRequest.setChannelId(String.valueOf(channelId.toString()));
        schedulerRequest.setRouteId(String.valueOf(routeId.toString()));
        schedulerRequest.setJobName(BisChannelType.SFTP.name() + channelId + routeId);
        schedulerRequest.setJobGroup(String.valueOf(routeId));
        JsonNode schedulerData = null;
        try {
          schedulerData = objectMapper.readTree(schedulerDetails);
          logger.trace("schedulerData  is not null ", schedulerData);
        } catch (IOException e) {
          logger.error("Exception occurred while updating schedulerExpression ", e);
          throw new SftpProcessorException("Exception occurred while updating schedulerExpression ",
              e);
        }

        // If schedule the route while creating
        /*
         * if (!schedulerData.toString().equals("")) { JsonNode cronExp =
         * schedulerData.get("cronexp"); JsonNode startDate = schedulerData.get("startDate");
         * JsonNode endDate = schedulerData.get("endDate");
         */

        // If activeTab is immediate the its immediate job.
        // irrespective of request set expression to empty
        // so that scheduler treats as immediate
        JsonNode activeTab = schedulerData.get("activeTab");
        if (activeTab != null && activeTab.asText().equals("immediate")) {
          logger.trace("schedulerData on activeTab", activeTab);
          schedulerRequest.setCronExpression("");
        } else {
          logger.trace("schedulerData on cronTab", schedulerData);
          JsonNode cronExp = schedulerData.get("cronexp");
          JsonNode startDate = schedulerData.get("startDate");
          JsonNode endDate = schedulerData.get("endDate");
          if (cronExp != null) {
            schedulerRequest.setCronExpression(cronExp.asText());
          }
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          try {
            if (startDate != null) {
              schedulerRequest.setJobScheduleTime(dateFormat.parse(startDate.asText()));
            }
            if (endDate != null) {
              schedulerRequest.setEndDate(dateFormat.parse(endDate.asText()));
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
        routeEntity = bisRouteDataRestRepository.save(routeEntity);
        RestTemplate restTemplate = new RestTemplate();
        logger.info("scheduler uri to update starts here : " + bisSchedulerUrl + updateUrl);
        logger.trace("Sending the content to " + bisSchedulerUrl + updateUrl + " : " + schedulerRequest);
        restTemplate.postForLocation(bisSchedulerUrl + updateUrl, schedulerRequest);
        logger.trace("scheduler uri to update ends here : " + bisSchedulerUrl + updateUrl);
      }
      BeanUtils.copyProperties(routeEntity, requestBody, "routeMetadata");
      try {
        routeData = (ObjectNode) objectMapper.readTree(requestBody.getRouteMetadata());
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
      logger.trace("Route deleted :" + route);
      bisRouteDataRestRepository.deleteById(routeId);
      return ResponseEntity.ok().build();
    }).orElseThrow(() -> new ResourceNotFoundException("routeId " + routeId + " not found")));
  }
}
