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
import com.synchronoss.saw.batch.service.BisChannelService;
import com.synchronoss.sip.utils.Ccode;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



@RestController
@Api(value = "The controller provides operations related Channel Entity "
    + "synchronoss analytics platform ")
@RequestMapping("/ingestion/batch")
public class SawBisChannelController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisChannelController.class);

  @Autowired
  private RetryTemplate retryTemplate;

  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository;

  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Autowired
  private BisChannelService bisChannelService;

  @Value("${bis.encryption-key}")
  private String encryptionKey;

  private static final Long STATUS_ACTIVE = 1L;
  
  private static final Long STATUS_DEACTIVE = 0L;


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
    String channelMetadata = requestBody.getChannelMetadata();
    String sanitizedChannelMetadata = SipCommonUtils.sanitizeJson(channelMetadata);

    nodeEntity = objectMapper.readTree(sanitizedChannelMetadata);
    rootNode = (ObjectNode) nodeEntity;
    String channelName = rootNode.get("channelName").asText();
    if (bisChannelService.isChannelNameExists(channelName)) {
      throw new BisException("Channel Name: " + channelName + " already exists");
    }


    String channelType = requestBody.getChannelType();

    if (channelType.equals(BisChannelType.SFTP.toString())) {
      String secretPhrase = rootNode.get("password").asText();
      String passwordPhrase = Ccode.cencode(secretPhrase, encryptionKey.getBytes());
      rootNode.put("password", passwordPhrase);
      requestBody.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
    }

    BisChannelEntity channelEntity = new BisChannelEntity();
    BeanUtils.copyProperties(requestBody, channelEntity);
    channelEntity.setCreatedDate(new Date());
    channelEntity.setStatus(STATUS_ACTIVE);
    BisChannelEntity channelEntityData =
        retryTemplate.execute((RetryCallback<BisChannelEntity, BisException>) context -> {
          BisChannelEntity retryChannelEntity = bisChannelDataRestRepository.save(channelEntity);
          if (retryChannelEntity == null) {
            throw new BisException("retryChannelEntity must not be null");
          }
          return retryChannelEntity;
        });
    BeanUtils.copyProperties(channelEntityData, requestBody);

    if (channelType.equals(BisChannelType.SFTP.toString())) {
      // Remove password from channel metadata
      String returnChannelMetadata =
          removePasswordFromChannelMetadata(requestBody.getChannelMetadata(), objectMapper);

      requestBody.setChannelMetadata(returnChannelMetadata);
    }

    requestBody.setCreatedDate(channelEntityData.getCreatedDate().getTime());
    return ResponseEntity.ok(requestBody);
  }

  private String removePasswordFromChannelMetadata(
      String channelMetadata, ObjectMapper objectMapper) throws IOException {

    ObjectNode returnChannelMetadataObj = (ObjectNode) objectMapper.readTree(channelMetadata);
    returnChannelMetadataObj.remove("password");

    return returnChannelMetadataObj.toString();
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
    final List<BisChannelDto> channels = new ArrayList<>();
    retryTemplate.execute(context -> channels.addAll(listOfChannels(page, size, sort, column)));
    return ResponseEntity.ok(channels);
  }

  private List<BisChannelDto> listOfChannels(int page, int size, String sort, String column) {
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

        String channelType = rootNode.get("channelType").textValue();

        if (channelType.equals(BisChannelType.SFTP.toString())) {
          rootNode.remove("password");
        }
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
    return channelDtos;
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
    BisChannelEntity channelEntityData = retryTemplate
        .execute((RetryCallback<BisChannelEntity, ResourceNotFoundException>) context -> {
          BisChannelEntity retryChannelEntity = bisChannelDataRestRepository.findById(id).get();
          if (retryChannelEntity == null) {
            throw new ResourceNotFoundException("retryChannelEntity must not be null");
          }
          return retryChannelEntity;
        });
    logger.trace("Channel retrieved :" + channelEntityData);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode nodeEntity = null;
    ObjectNode rootNode = null;
    try {
      nodeEntity = objectMapper.readTree(channelEntityData.getChannelMetadata());
      rootNode = (ObjectNode) nodeEntity;

      String channelType = rootNode.get("channelType").textValue();

      if (channelType.equals(BisChannelType.SFTP.toString())) {
        rootNode.remove("password");
      }
      BeanUtils.copyProperties(channelEntityData, channelDto);
      channelDto.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
      if (channelEntityData.getCreatedDate() != null) {
        channelDto.setCreatedDate(channelEntityData.getCreatedDate().getTime());
      }
      if (channelEntityData.getModifiedDate() != null) {
        channelDto.setModifiedDate(channelEntityData.getModifiedDate().getTime());
      }
    } catch (Exception e) {
      throw new ResourceNotFoundException("channelId " + id + " not found");
    }
    return ResponseEntity.ok(channelDto);
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

    String channelMetadata = requestBody.getChannelMetadata();

    String sanitizedChannelMetadata = SipCommonUtils.sanitizeJson(channelMetadata);

    nodeEntity = objectMapper.readTree(sanitizedChannelMetadata);
    rootNode = (ObjectNode) nodeEntity;

    String channelType = requestBody.getChannelType();

    Optional<BisChannelEntity> optionalChannel = bisChannelDataRestRepository.findById(channelId);
    if (optionalChannel.isPresent()) {
      BisChannelEntity channel = optionalChannel.get();

      if (channelType.equals(BisChannelType.SFTP.toString())) {
        JsonNode passwordNode = rootNode.get("password");

        String secretPhrase = null;

        if (passwordNode == null || passwordNode.isNull()) {
          // If password is not provided, retrieve it from DB
          String savedChannelMetadata = channel.getChannelMetadata();

          ObjectNode savedChannelMetadataObj =
              (ObjectNode) objectMapper.readTree(savedChannelMetadata);

          String savedPassword = savedChannelMetadataObj.get("password").asText();

          rootNode.put("password", savedPassword);
        } else {
          secretPhrase = Ccode.cencode(passwordNode.asText(), encryptionKey.getBytes());
          rootNode.put("password", secretPhrase);
        }

        requestBody.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
      }

      logger.trace("Channel updated :" + channel);
      requestBody.setBisChannelSysId(channelId);
      BeanUtils.copyProperties(requestBody, channel, "modifiedDate", "createdDate");
      channel.setModifiedDate(new Date());
      if (channel.getStatus() == STATUS_DEACTIVE) {
        throw new BisException("Update not allowed on a deactivated channel");
      }
      if (channel.getStatus() == null) {
        channel.setStatus(STATUS_ACTIVE);
      }
      channel = bisChannelDataRestRepository.save(channel);
      BeanUtils.copyProperties(channel, requestBody);

      if (channelType.equals(BisChannelType.SFTP.toString())) {
        // Remove password from channel metadata
        String returnChannelMetadata = requestBody.getChannelMetadata();

        requestBody.setChannelMetadata(
            removePasswordFromChannelMetadata(returnChannelMetadata, objectMapper));
      }

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
  public ResponseEntity<Object> deleteChannel(
      @ApiParam(value = "Entity id needs to be deleted", required = true) @PathVariable Long id)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException,
      BisException {
    Object obj =
        retryTemplate.execute((RetryCallback<Object, ResourceNotFoundException>) context -> {
          List<BisRouteEntity> routeEntities = bisRouteDataRestRepository
              .findByBisChannelSysId(id, PageRequest.of(0, 1, Direction.DESC, "createdDate"))
              .getContent();
          if (routeEntities == null || routeEntities.size() > 0) {
            throw new ResourceNotFoundException("routeEntities must not be null");
          } else {
            Optional<BisChannelEntity> channel = bisChannelDataRestRepository.findById(id);
            if (channel == null || channel.get() == null) {
              throw new ResourceNotFoundException("channel must not be null");
            }
            logger.trace("Channel deleted :" + channel);
            bisChannelDataRestRepository.deleteById(id);
          }
          return ResponseEntity.ok().build();
        });
    return ResponseEntity.ok(obj);

  }

  /**
   * This API provides an ability to delete a source.
   */
  @ApiOperation(value = "check channel Name is duplicate", response = Object.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 201, message = "Deleted"),
          @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
              message = "Unsupported Type." + "Representation not supported for the resource")})
  @RequestMapping(value = "/channels/duplicate", method = RequestMethod.GET,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public ResponseEntity<Boolean> checkDuplicate(@ApiParam(value = "channel Name",
      required = true) @RequestParam("channelName") String channelName) {

    return new ResponseEntity<Boolean>(bisChannelService.isChannelNameExists(channelName),
        HttpStatus.OK);

  }

  /**
   * checks is there a route with given route name.
   *
   * @param channelId channe identifier
   * @return ok
   */
  @RequestMapping(value = "/channels/{channelId}/deactivate", method = RequestMethod.PUT,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Map<String, Boolean> deactivateChannel(@PathVariable("channelId") Long channelId) {
    Map<String, Boolean> responseMap = new HashMap<String, Boolean>();
    logger.trace("Inside deactivating channel");
    bisChannelService.activateOrDeactivateChannel(channelId, false);
    responseMap.put("isDeactivated", Boolean.TRUE);
    return responseMap;
  }

  /**
   * checks is there a route with given route name.
   *
   * @param channelId channe identifier
   * @return ok
   */
  @RequestMapping(value = "/channels/{channelId}/activate", method = RequestMethod.PUT,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Map<String, Boolean> activateChannel(@PathVariable("channelId") Long channelId) {
    Map<String, Boolean> responseMap = new HashMap<String, Boolean>();
    logger.trace("Inside activating channel");
    bisChannelService.activateOrDeactivateChannel(channelId, true);
    responseMap.put("isActivated", Boolean.TRUE);
    return responseMap;
  }

  private String extractAndValidateChannelMetadata(BisChannelDto requestBody) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode nodeEntity = null;
    ObjectNode channelMetadata = null;
    nodeEntity = objectMapper.readTree(requestBody.getChannelMetadata());
    channelMetadata = (ObjectNode) nodeEntity;
    String channelName = channelMetadata.get("channelName").asText();
    if (bisChannelService.isChannelNameExists(channelName)) {
      throw new BisException("Channel Name: " + channelName + " already exists");
    }

    String channelType = requestBody.getChannelType();

    if (channelType.equals(BisChannelType.SFTP.toString())) {
      String secretPhrase = channelMetadata.get("password").asText();
      String passwordPhrase = Ccode.cencode(secretPhrase, encryptionKey.getBytes());
      channelMetadata.put("password", passwordPhrase);
    }

    return objectMapper.writeValueAsString(channelMetadata);
  }
}
