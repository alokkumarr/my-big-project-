package com.synchronoss.saw.batch.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.dto.BisChannelDto;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;
import com.synchronoss.saw.batch.exception.BisException;
import com.synchronoss.saw.batch.exception.ResourceNotFoundException;
import com.synchronoss.saw.batch.model.BisChannelType;
import com.synchronoss.saw.batch.model.BisScheduleKeys;
import com.synchronoss.saw.batch.service.BisChannelService;
import com.synchronoss.saw.batch.utils.IntegrationUtils;
import com.synchronoss.saw.batch.utils.SipObfuscation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.client.ResourceAccessException;

@CrossOrigin(origins = "*")
@RestController
@Api(value = "The controller provides operations related Channel Entity "
    + "synchronoss analytics platform ")
@RequestMapping("/ingestion/batch")
public class SawBisChannelController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisChannelController.class);

  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository;
  
  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;
  
  @Autowired
  private BisChannelService bisChannelService;


  /**
   * This API provides an ability to add a source.
   */
  @ApiOperation(value = "Add a new channel", nickname = "actionBis", notes = "",
      response = BisChannelDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Created"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels", method = RequestMethod.POST,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<@Valid BisChannelDto> createChannel(
      @ApiParam(value = "Channel related information to store",
          required = true) @Valid @RequestBody BisChannelDto requestBody)
      throws Exception {
    logger.trace("Request Body:{}", requestBody);

    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode nodeEntity = null;
    ObjectNode rootNode = null;
    nodeEntity = objectMapper.readTree(requestBody.getChannelMetadata());
    rootNode = (ObjectNode) nodeEntity;
    String channelName =  rootNode.get("channelName").asText();
    if (bisChannelService.isChannelNameExists(channelName)) {
      throw new BisException("Channel Name: " + channelName + " already exists");
    }
    SipObfuscation obfuscator = new SipObfuscation(IntegrationUtils.secretKey);
    String secretPhrase = rootNode.get("password").asText();
    String passwordPhrase = obfuscator.encrypt(secretPhrase);
    rootNode.put("password", passwordPhrase);
    requestBody.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
    BisChannelEntity channelEntity = new BisChannelEntity();
    BeanUtils.copyProperties(requestBody, channelEntity);
    channelEntity.setCreatedDate(new Date());
    channelEntity = bisChannelDataRestRepository.save(channelEntity);
    BeanUtils.copyProperties(channelEntity, requestBody);
    requestBody.setCreatedDate(channelEntity.getCreatedDate().getTime());
    return ResponseEntity.ok(requestBody);
  }

  /**
   * This API provides an ability to read a source with pagination.
   */

  @ApiOperation(value = "Reading list of channels & paginate", nickname = "actionBis", notes = "",
      response = BisChannelDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<List<BisChannelDto>> readChannel(
      @ApiParam(value = "page number", required = false) @RequestParam(name = "page",
          defaultValue = "0") int page,
      @ApiParam(value = "number of objects per page", required = false) @RequestParam(name = "size",
          defaultValue = "10") int size,
      @ApiParam(value = "sort order", required = false) @RequestParam(name = "sort",
          defaultValue = "desc") String sort,
      @ApiParam(value = "column name to be sorted", required = false) @RequestParam(name = "column",
          defaultValue = "createdDate") String column)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    List<BisChannelEntity> entities = bisChannelDataRestRepository
        .findAll(PageRequest.of(page, size, Direction.DESC, column)).getContent();
    List<BisChannelDto> channelDtos = new ArrayList<>();
    entities.forEach(entity -> {
      BisChannelDto bisChannelDto = null;
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      JsonNode nodeEntity = null;
      ObjectNode rootNode = null;
      try {
        nodeEntity = objectMapper.readTree(entity.getChannelMetadata());
        rootNode = (ObjectNode) nodeEntity;
        SipObfuscation obfuscator = new SipObfuscation(IntegrationUtils.secretKey);
        String secretPhrase = rootNode.get("password").asText();
        secretPhrase = obfuscator.decrypt(secretPhrase);
        rootNode.put("password", secretPhrase);
        bisChannelDto = new BisChannelDto();
        BeanUtils.copyProperties(entity, bisChannelDto);
        bisChannelDto.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
        if (entity.getCreatedDate() != null) {
          bisChannelDto.setCreatedDate(entity.getCreatedDate().getTime());
        }
        if (entity.getModifiedDate() != null) {
          bisChannelDto.setModifiedDate(entity.getModifiedDate().getTime());
        }
        channelDtos.add(bisChannelDto);
      } catch (Exception e) {
        logger.error("Exception while reading the list :", e);
        throw new ResourceNotFoundException(
            "Exception occurred while " + "reading the list of channels");
      }
    });
    return ResponseEntity.ok(channelDtos);
  }

  /**
   * This API provides an ability to read a source by id.
   */

  @ApiOperation(value = "Reading channel by id", nickname = "actionBis", notes = "",
      response = BisChannelDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{id}", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<BisChannelDto> readChannelById(
      @PathVariable(name = "id", required = true) Long id) throws Exception {
    BisChannelDto channelDto = new BisChannelDto();
    return ResponseEntity.ok(bisChannelDataRestRepository.findById(id).map(channel -> {
      logger.trace("Channel retrieved :" + channel);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      JsonNode nodeEntity = null;
      ObjectNode rootNode = null;
      try {
        nodeEntity = objectMapper.readTree(channel.getChannelMetadata());
        rootNode = (ObjectNode) nodeEntity;
        SipObfuscation obfuscator = new SipObfuscation(IntegrationUtils.secretKey);
        String secretPhrase = rootNode.get("password").asText();
        secretPhrase = obfuscator.decrypt(secretPhrase);
        rootNode.put("password", secretPhrase);
        BeanUtils.copyProperties(channel, channelDto);
        channelDto.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
        if (channel.getCreatedDate() != null) {
          channelDto.setCreatedDate(channel.getCreatedDate().getTime());
        }
        if (channel.getModifiedDate() != null) {
          channelDto.setModifiedDate(channel.getModifiedDate().getTime());
        }
      } catch (Exception e) {
        throw new ResourceNotFoundException("channelId " + id + " not found");
      }
      return channelDto;
    }).orElseThrow(() -> new ResourceNotFoundException("channelId " + id + " not found")));
  }

  /**
   * This API provides an ability to update a source.
   */
  @ApiOperation(value = "Updating an existing channel", nickname = "actionBis", notes = "",
      response = BisChannelDto.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Updated"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{channelId}", method = RequestMethod.PUT,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<@Valid BisChannelDto> updateChannel(
      @ApiParam(value = "Entity id needs to be updated",
          required = true) @PathVariable Long channelId,
      @ApiParam(value = "Channel related information to update",
          required = true) @Valid @RequestBody BisChannelDto requestBody)
      throws Exception {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode nodeEntity = null;
    ObjectNode rootNode = null;
    nodeEntity = objectMapper.readTree(requestBody.getChannelMetadata());
    rootNode = (ObjectNode) nodeEntity;
    SipObfuscation obfuscator = new SipObfuscation(IntegrationUtils.secretKey);
    String secretPhrase = rootNode.get("password").asText();
    secretPhrase = obfuscator.encrypt(secretPhrase);
    rootNode.put("password", secretPhrase);
    requestBody.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
    Optional<BisChannelEntity> optionalChannel = bisChannelDataRestRepository.findById(channelId);
    if (optionalChannel.isPresent()) {
      BisChannelEntity channel = optionalChannel.get();
      logger.trace("Channel updated :" + channel);
      requestBody.setBisChannelSysId(channelId);
      BeanUtils.copyProperties(requestBody, channel, "modifiedDate", "createdDate");
      channel.setModifiedDate(new Date());
      channel = bisChannelDataRestRepository.save(channel);
      channel = bisChannelDataRestRepository.findById(channelId).get();
      BeanUtils.copyProperties(channel, requestBody);
      requestBody.setCreatedDate(channel.getCreatedDate().getTime());
      requestBody.setModifiedDate(channel.getModifiedDate().getTime());
    } else {
      throw new ResourceNotFoundException("channelId " + channelId + " not found");
    }
    return ResponseEntity.ok(requestBody);
  }

  /**
   * This API provides an ability to delete a source.
   */
  @ApiOperation(value = "Deleting an existing channel", nickname = "actionBis", notes = "",
      response = Object.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Deleted"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/{id}", method = RequestMethod.DELETE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<Object> deleteChannel(
      @ApiParam(value = "Entity id needs to be deleted", required = true) @PathVariable Long id)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException, 
          BisException {
    
    List<BisRouteEntity> routeEntities = bisRouteDataRestRepository
              .findByBisChannelSysId(id, PageRequest.of(0, 1, Direction.DESC, "createdDate"))
                .getContent();
    if (routeEntities.size() > 0) {
      throw new BisException("Can not delete a channel until all routes associated are deleted");
    } else {
      return ResponseEntity.ok(bisChannelDataRestRepository.findById(id).map(channel -> {
           
        bisChannelDataRestRepository.deleteById(id);
        logger.trace("Channel deleted :" + channel);
        return ResponseEntity.ok().build();
      }).orElseThrow(() -> new ResourceNotFoundException("channelId " + id + " not found")));
    }
    
    
  }
  
  
  /**
   * This API provides an ability to delete a source.
   */
  @ApiOperation(value = "check channel Name is duplciate", response = Object.class)
  @ApiResponses(value = { @ApiResponse(code = 200, 
      message = "Request has been succeeded without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
      @ApiResponse(code = 400, message = "Bad request"), 
      @ApiResponse(code = 201, message = "Deleted"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 415, message = "Unsupported Type."
          + "Representation not supported for the resource") })
  @RequestMapping(value = "/channels/duplicate", 
      method = RequestMethod.GET, produces = org.springframework.http
          .MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<Boolean> checkDuplicate(
      @ApiParam(value = "channel Name", required = true) 
      @RequestParam("channelName") String channelName) {

    return new ResponseEntity<Boolean>(bisChannelService
        .isChannelNameExists(channelName), HttpStatus.OK);

  }
  
  /**
   * checks is there a route with given route name.
   * 
   * @param channelId channe identifier
   * @return  ok
   */
  @RequestMapping(value = "/channels/{channelId}/deactivate", method 
      = RequestMethod.GET, produces = org.springframework.http.MediaType
      .APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Object>  deactivateChannel(
      @RequestParam("channelId")  Long channelId) {
    bisChannelService.activateOrDeactivateChannel(channelId, false);
    return new ResponseEntity<Object>(HttpStatus.OK);
  }
  
  /**
   * checks is there a route with given route name.
   * 
   * @param channelId channe identifier
   * @return ok
   */
  @RequestMapping(value = "/channels/{channelId}/activate", method 
      = RequestMethod.GET, produces = org.springframework.http.MediaType
      .APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Object>  activateChannel(
      @RequestParam("channelId")  Long channelId,
      @RequestParam("routeId") Long routeId) {
    bisChannelService.activateOrDeactivateChannel(channelId, true);
    return new ResponseEntity<Object>(HttpStatus.OK);
  }

  
}

