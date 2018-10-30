package com.synchronoss.saw.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.synchronoss.saw.entities.BisChannelEntity;
import com.synchronoss.saw.entities.BisRouteEntity;
import com.synchronoss.saw.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.exception.ResourceNotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(
    value = "The controller provides operations related Route Entity "
        + "synchronoss analytics platform ")
@RequestMapping("/ingestion/batch")
public class SawBisRouteController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisRouteController.class);
  
  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository; 
  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository; 

  
  /**
   * This API provides an ability to add a source. 
   * @param requestBody String
   * @return encrypted password String
   */
  @ApiOperation(value = "Adding a new Route",
      nickname = "actionBis", notes = "", response = BisChannelEntity.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Created"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. "
       + "Representation not supported for the resource")
      })
  @RequestMapping(value = "/channels/{id}/routes", method = RequestMethod.POST,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<@Valid BisRouteEntity> createRoute(
      @ApiParam(value = "Channel Id",
      required = true) @PathVariable Long id,
      @ApiParam(value = "Route related information to store",
          required = true) @Valid @RequestBody BisRouteEntity requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    logger.trace("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    return ResponseEntity.ok(bisChannelDataRestRepository.findById(id).map(channel -> {
      logger.trace("Channel retrieved :" + channel);
      requestBody.setBisChannelSysId(id);
      return bisRouteDataRestRepository.save(requestBody);
    }).orElseThrow(() -> new ResourceNotFoundException("channelId " + id + " not found")));
  }

  /**
   * This API provides an ability to read a routes with pagination by channelId. 
   * @return encrypted password String
   */
  
  @ApiOperation(value = "Reading list of routes & paginate by channel id",
      nickname = "actionBis", notes = "", response = BisRouteEntity.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. "
       + "Representation not supported for the resource")
      })
  @RequestMapping(value = "/channels/{id}/routes", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<Page<BisRouteEntity>> readRoutes(@ApiParam(value = "id",
      required = true)  @PathVariable(name = "id", required = true) Long id,
      @ApiParam(value = "page number",
      required = false)  @RequestParam(name = "page", defaultValue = "0") int page, 
      @ApiParam(value = "number of objects per page",
          required = false) @RequestParam(name = "size", defaultValue = "10") int size,
          @ApiParam(value = "sort order",
          required = false) @RequestParam(name = "sort", defaultValue = "desc") String sort, 
          @ApiParam(value = "column name to be sorted",
          required = false) @RequestParam(name = "column", defaultValue = "createdDate") 
      String column) throws NullPointerException, JsonParseException, 
      JsonMappingException, IOException {
    return ResponseEntity.ok(bisRouteDataRestRepository
    .findByBisChannelSysId(id,PageRequest.of(page, size, 
    Direction.DESC, column)));
  }


  /**
   * This API provides an ability to update a source. 
   * @param requestBody String
   * @return encrypted password String
   */
  @ApiOperation(value = "Updating an existing routes by channel id",
      nickname = "actionBis", notes = "", response = BisRouteEntity.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Updated"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. "
       + "Representation not supported for the resource")
      })
  @RequestMapping(value = "/channels/{channelId}/routes/{routeId}", method = RequestMethod.PUT,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<BisRouteEntity> updateRoutes(@ApiParam
       (value = "Channel id",
          required = true)@PathVariable(name = "channelId", required = true) Long channelId,
       @ApiParam
          (value = "Route id",
          required = true)@PathVariable(name = "routeId", required = true) Long routeId,       
      @ApiParam(value = "Routes related information to update",
          required = true) @Valid @RequestBody BisRouteEntity requestBody)
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
      BeanUtils.copyProperties(requestBody, route, "bisChannelSysId", "bisRouteSysId");
      return bisRouteDataRestRepository.save(route);
    }).orElseThrow(() -> new ResourceNotFoundException("routeId " + routeId + " not found")));
  }

  /**
   * This API provides an ability to delete a source. 
   * @return encrypted password String
   */
  @ApiOperation(value = "Deleting an existing route",
      nickname = "actionBis", notes = "", response = Object.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Deleted"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. "
       + "Representation not supported for the resource")
      })
  @RequestMapping(value = "/channels/{channelId}/routes/{routeId}", method = RequestMethod.DELETE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<ResponseEntity<Object>> deleteRoutes(@ApiParam(value = "Channel id",
      required = true)@PathVariable(name = "channelId", required = true) Long channelId,
      @ApiParam
      (value = "Route id",
      required = true)@PathVariable(name = "routeId", required = true) Long routeId)
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

