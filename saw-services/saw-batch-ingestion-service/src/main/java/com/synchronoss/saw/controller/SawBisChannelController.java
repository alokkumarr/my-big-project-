package com.synchronoss.saw.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.synchronoss.saw.entities.BisChannelEntity;
import com.synchronoss.saw.entities.repositories.BisChannelDataRestRepository;
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
    value = "The controller provides operations related Channel Entity "
        + "synchronoss analytics platform ")
@RequestMapping("/ingestion/batch")
public class SawBisChannelController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisChannelController.class);
  
  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository; 

  
  /**
   * This API provides an ability to add a source. 
   */
  @ApiOperation(value = "Add a new channel",
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
  @RequestMapping(value = "/channels", method = RequestMethod.POST,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<@Valid BisChannelEntity> createChannel(
      @ApiParam(value = "Channel related information to store",
          required = true) @Valid @RequestBody BisChannelEntity requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    logger.trace("Request Body:{}", requestBody);

    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    return ResponseEntity.ok(bisChannelDataRestRepository.save(requestBody));
  }

  /**
   * This API provides an ability to read a source with pagination. 
   */
  
  @ApiOperation(value = "Reading list of channels & paginate",
      nickname = "actionBis", notes = "", response = BisChannelEntity.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. "
       + "Representation not supported for the resource")
      })
  @RequestMapping(value = "/channels", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<Page<BisChannelEntity>> readChannel(
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
    return ResponseEntity.ok(bisChannelDataRestRepository.findAll(PageRequest.of(page, size, 
    Direction.DESC, column)));
  }

  /**
   * This API provides an ability to read a source by id. 
   */
  
  @ApiOperation(value = "Reading channel by id",
      nickname = "actionBis", notes = "", response = BisChannelEntity.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"),
          @ApiResponse(code = 415, message = "Unsupported Type. "
       + "Representation not supported for the resource")
      })
  @RequestMapping(value = "/channels/{id}", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<BisChannelEntity> readChannelById(
      @PathVariable(name = "id",required = true) 
      Long id) throws NullPointerException, JsonParseException, 
      JsonMappingException, IOException {
    return ResponseEntity.ok(bisChannelDataRestRepository.findById(id).map(channel -> {
      logger.trace("Channel retrieved :" + channel);
      return channel;
    }).orElseThrow(() -> new ResourceNotFoundException("channelId " + id + " not found")));
  }

  /**
   * This API provides an ability to update a source. 
   */
  @ApiOperation(value = "Updating an existing channel",
      nickname = "actionBis", notes = "", response = BisChannelEntity.class)
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
  @RequestMapping(value = "/channels/{id}", method = RequestMethod.PUT,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<@Valid BisChannelEntity> updateChannel(@ApiParam
       (value = "Entity id needs to be updated",
          required = true)@PathVariable Long id,
      @ApiParam(value = "Channel related information to update",
          required = true) @Valid @RequestBody BisChannelEntity requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    return ResponseEntity.ok(bisChannelDataRestRepository.findById(id).map(channel -> {
      logger.trace("Channel updated :" + channel);
      BeanUtils.copyProperties(requestBody, channel, "bisChannelSysId");
      return bisChannelDataRestRepository.save(channel);
    }).orElseThrow(() -> new ResourceNotFoundException("channelId " + id + " not found")));
  }

  /**
   * This API provides an ability to delete a source. 
   */
  @ApiOperation(value = "Deleting an existing channel",
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
  @RequestMapping(value = "/channels/{id}", method = RequestMethod.DELETE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<Object> deleteChannel(@ApiParam
       (value = "Entity id needs to be deleted",
          required = true)@PathVariable Long id)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    return ResponseEntity.ok(bisChannelDataRestRepository.findById(id).map(channel -> {
      bisChannelDataRestRepository.deleteById(id);
      logger.trace("Channel deleted :" + channel);
      return ResponseEntity.ok().build();
    }).orElseThrow(() -> new ResourceNotFoundException("channelId " + id + " not found")));
  }
}
