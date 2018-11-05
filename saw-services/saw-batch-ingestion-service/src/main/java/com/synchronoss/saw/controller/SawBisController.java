package com.synchronoss.saw.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.Ccode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(
    value = "The controller provides operations pertaining to batch ingestion service of "
        + "synchronoss analytics platform ")
@RequestMapping("/ingestion/batch/internal")
public class SawBisController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisController.class);
  /**
   * This api provides to encrypt password. 
   * @param requestBody String
   * @return encrypted password String
   */
  
  @ApiOperation(value = "Provides an access to functionalities of bis using commmon specification",
      nickname = "actionInternalBis", notes = "", response = Object.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/encrypt", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Object encrypt(
      @ApiParam(value = "String to encrypt",
          required = true) @Valid @RequestBody String requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    logger.debug("Request Body:{}", requestBody);

    if (requestBody == null) {
      throw new NullPointerException("json body is missing in request body");
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String data = "{ \"data\" : \"" + Ccode.cencode(requestBody) + "\"}";
    Object encrypted  = objectMapper.readValue(data, Object.class);
    return encrypted;
  }

  /**
   * This api provides to encrypt password. 
   * @param requestBody String
   * @return decrypted password String
   */
  
  @ApiOperation(value = "Provides an access to functionalities of bis using commmon specification",
      nickname = "actionInternalBis", notes = "", response = Object.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been accepted without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/decrypt", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Object decrypt(
      @ApiParam(value = "String to decrypt",
          required = true) @Valid @RequestBody String requestBody)
      throws NullPointerException, JsonParseException, JsonMappingException, IOException {
    logger.debug("Request Body:{}", requestBody);

    if (requestBody == null) {
      throw new NullPointerException("request body is missing in request body");
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String data = "{ \"data\" : \"" + Ccode.cdecode(requestBody) + "\"}";
    Object decrypted  = objectMapper.readValue(data, Object.class);
    return decrypted;
  }
  
}

