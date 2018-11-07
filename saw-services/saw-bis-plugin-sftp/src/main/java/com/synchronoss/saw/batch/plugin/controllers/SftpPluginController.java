package com.synchronoss.saw.batch.plugin.controllers;

import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisIngestionPayload;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ingestion/batch/sftp")
public class SftpPluginController {

  @Autowired
  @Qualifier("sftpService")
  private SipPluginContract sftpServiceImpl;
  
  /**
   * This endpoint to test connectivity for route.
   */
  @ApiOperation(value = "To test connectivity for route",
      nickname = "sftpActionBis", notes = "", response = HttpStatus.class)
  @ApiResponses(value = { @ApiResponse(code = 200, 
      message = "Request has been accepted without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator") })
  @RequestMapping(value = "/routes/connect/{id}", method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public HttpStatus connectRoute(@ApiParam(value = "Route id to test connectivity",
          required = true) @PathVariable(name = "id",required = true) Long id) {
    return sftpServiceImpl.connectRoute(id);
  }

  /**
   * This endpoint to test connectivity for route.
   */
  @ApiOperation(value = "To test connectivity for source",
      nickname = "sftpActionBis", notes = "", response = HttpStatus.class)
  @ApiResponses(value = { @ApiResponse(code = 200, 
      message = "Request has been accepted without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator") })
  @RequestMapping(value = "/channels/connect/{id}", method = RequestMethod.GET, 
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public HttpStatus connectSource(@ApiParam(value = "Source id to test connectivity",
          required = true) @PathVariable(name = "id",required = true) Long id) {
    return sftpServiceImpl.connectChannel(id);
  }
  /**
   * This endpoint to transfer data from remote channel.
   */
  
  @ApiOperation(value = "To pull data from remote channel",
      nickname = "sftpActionBis", notes = "", response = HttpStatus.class)
  @ApiResponses(value = { @ApiResponse(code = 200, 
      message = "Request has been accepted without any error"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator") })
  //  @RequestMapping(value = "/channel/transfer", method = RequestMethod.POST, 
  //   produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Object pullData(@ApiParam(value = "Payload structure which to be used to "
      + "initiate the transfer",
          required = true) @Valid @RequestBody(required = true) BisIngestionPayload requestBody) {
    Object response = null;
    try {
      response = sftpServiceImpl.transferData(requestBody);
    } catch (Exception e) {
      response = HttpStatus.BAD_REQUEST;
      throw new SftpProcessorException("Exception occured while transferring the file");
    }
    return response;
  }
}
