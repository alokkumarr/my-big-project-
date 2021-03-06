package com.synchronoss.saw.batch.controller;

import com.alibaba.fastjson.JSON;
import com.synchronoss.saw.batch.AsyncConfiguration;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.plugin.SipIngestionPluginFactory;
import com.synchronoss.saw.batch.plugin.service.SftpServiceImpl;
import com.synchronoss.saw.batch.service.ChannelTypeService;
import com.synchronoss.saw.logs.constants.SourceType;

import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.validation.Valid;

import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/ingestion/batch")
public class SawBisPluginController {

  private static final Logger logger = LoggerFactory.getLogger(SawBisPluginController.class);
  @Autowired
  @Qualifier(AsyncConfiguration.TASK_EXECUTOR_CONTROLLER)
  private Executor transactionPostExecutor;

  @Autowired
  private SipIngestionPluginFactory factory;
  
  @Autowired
  ChannelTypeService channelTypeService;
  

  
  /**
   * This end-point to test connectivity for existing route.
   */
  @ApiOperation(value = "To test connectivity for existing route", nickname = "sftpActionBis",
      notes = "", response = HttpStatus.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/routes/{routeId}/status", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public String connectRoute(@ApiParam(value = "Route id to test connectivity",
      required = true) @PathVariable(name = "routeId", required = true) Long routeId) {
    String channelType = channelTypeService.findChannelTypeFromRouteId(routeId);
    SipPluginContract sipConnService = factory.getInstance(channelType);
    return JSON.toJSONString(sipConnService.connectRoute(routeId));
  }


  /**
   * This end-point to test connectivity for route.
   */

  @ApiOperation(value = "To test connectivity for route without an entity present on the system",
      nickname = "sftpActionBis", notes = "", response = HttpStatus.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/routes/test", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public String connectImmediateRoute(
      @ApiParam(value = "Payload to test connectivity",
          required = true) @Valid @RequestBody BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException {
    SipPluginContract sipConnService = factory
        .getInstance(payload.getChannelType().toString());
    return JSON.toJSONString(sipConnService.immediateConnectRoute(payload));
  }

  /**
   * This end-point to test connectivity for channel.
   */
  @ApiOperation(value = "To test connectivity for existing channel", nickname = "sftpActionBis",
      notes = "", response = HttpStatus.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/channels/{channelId}/status", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public String connectChannel(@ApiParam(value = "Channel id to test connectivity",
      required = true) @PathVariable(name = "channelId", required = true) Long channelId) {
    String channelType = channelTypeService.findChannelTypeFromChannelId(Long.valueOf(channelId));
    logger.info("Channel type:: " + channelType);
    SipPluginContract sipConnService = factory.getInstance(channelType);
    return JSON.toJSONString(sipConnService.connectChannel(channelId));
  }

  /** This end-point to test connectivity for channel. */
  @ApiOperation(
      value = "To test connectivity for channel without an entity present on the system",
      nickname = "sftpActionBis",
      notes = "",
      response = HttpStatus.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Request has been succeeded without any error"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")
      })
  @RequestMapping(
      value = "/channels/test",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public String connectImmediateChannel(
      @ApiParam(value = "Payload to test connectivity", required = true) @Valid @RequestBody
          BisConnectionTestPayload payload,
      @QueryParam("channelId") Optional<Long> channelId) {
    logger.debug("Channel ID = ", channelId);

    String channelType = payload.getChannelType().toString();

    SipPluginContract sipConnService = factory.getInstance(payload.getChannelType().toString());

    if (channelId.isPresent()) {
      Long channelIdVal = channelId.get();

      if (channelType.equalsIgnoreCase("sftp")) {
        try {
          SftpServiceImpl sftpService = (SftpServiceImpl) sipConnService;

          return JSON.toJSONString(
              sftpService.immediateConnectChannelWithChannelId(payload, channelIdVal));
        } catch (IOException exception) {
          logger.error("IO Error occurred", exception);
          return "Error occurred: " + exception.getMessage();
        } catch (Exception exception) {
          logger.error("Error occurred", exception);
          return "Error occurred: " + exception.getMessage();
        }
      }
    }
    return JSON.toJSONString(sipConnService.immediateConnectChannel(payload));
  }

  /**
   * This end-point to transfer data from remote channel without logging. i.e. immediate transfer
   * while in design phase.
   */

  @ApiOperation(
      value = "To pull data from remote channel either provide "
          + "(channelId & routeId) or other details",
      nickname = "sftpActionBis", notes = "", response = List.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/channel/transfers", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public List<BisDataMetaInfo> immediateTransfer(@ApiParam(
      value = "Payload structure which " + "to be used to " + "initiate the transfer",
      required = true) @Valid @RequestBody(required = true) BisConnectionTestPayload requestBody) {
    List<BisDataMetaInfo> response = null;
    SipPluginContract sipTransferService = factory
            .getInstance(requestBody.getChannelType().toString());
    try {
      if (requestBody.getBatchSize() > 0) {
        sipTransferService.setBatchSize(requestBody.getBatchSize());
      }
      if (Long.valueOf(requestBody.getChannelId()) > 0L
          && Long.valueOf(requestBody.getRouteId()) > 0L) {
        response = sipTransferService.scanFilesForPattern(Long.valueOf(requestBody.getChannelId()),
            Long.valueOf(requestBody.getRouteId()), null, false, 
            SourceType.REGULAR.name(), Optional.empty(), requestBody.getChannelType().toString());
      } else {
        response = sipTransferService.immediateTransfer(requestBody);
      }
      for (BisDataMetaInfo info : response) {
        String normalizedDestionationPath = SipCommonUtils.normalizePath(info.getDestinationPath());
        if (normalizedDestionationPath != null) {
          File folderWhereFilesDumped = new File(normalizedDestionationPath);
          if (folderWhereFilesDumped.exists() && folderWhereFilesDumped.isDirectory()) {
            logger.trace("Thread with Name :" + Thread.currentThread().getName()
                + "has completed & created folder to put the files in "
                + info.getDestinationPath());
            File[] listOfFiles = folderWhereFilesDumped.listFiles();
            if (listOfFiles != null && listOfFiles.length == 0) {
              folderWhereFilesDumped.delete();
              logger.trace("Thread with Name :" + Thread.currentThread().getName()
                  + "has completed & created folder to put the files in "
                  + info.getDestinationPath()
                  + " & it is empty so it has been deleted after completion of the thread.");
            }
          }
        }
      }
    } catch (Exception e) {
      throw new SftpProcessorException("Exception occured while transferring the file", e);
    }
    return response;
  }

  /**
   * This end-point to transfer data from remote channel without logging. i.e. immediate transfer
   * while in design phase.
   */
  @ApiOperation(
      value = "To pull data from remote channel either provide "
          + "(channelId & routeId) or other details",
      nickname = "sftpActionBis", notes = "", response = List.class)
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator")})
  @RequestMapping(value = "/channel/transfers/data", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public DeferredResult<ResponseEntity<List<BisDataMetaInfo>>> immediateTransferasync(@ApiParam(
      value = "Payload structure which to be used to initiate the transfer",
      required = true) @Valid @RequestBody(required = true) BisConnectionTestPayload requestBody,
      BindingResult result) {
    ZonedDateTime startTime = ZonedDateTime.now();
    logger.trace("Servlet Thread Started -{}", startTime);
    if (result.hasErrors()) {
      throw new SftpProcessorException("Exception occured while transferring the file");
    }
    
    /**
     * Below line is added to support existing schedules
     * which doesnt have channel type stored in job info
     * as part of scheduler.
     */
    String channelType = requestBody.getChannelType() == null 
        ?  "sftp" : requestBody.getChannelType().value();
    
    logger.trace("channel type from job scheduled :: " 
        + requestBody.getChannelType());
    
    SipPluginContract sipTransferService = factory
        .getInstance(channelType);
    if (requestBody.getBatchSize() > 0) {
      sipTransferService.setBatchSize(requestBody.getBatchSize());
    }
    
    DeferredResult<ResponseEntity<List<BisDataMetaInfo>>> deferredResult = new DeferredResult<>();
    if (Long.valueOf(requestBody.getChannelId()) > 0L
        && Long.valueOf(requestBody.getRouteId()) > 0L) {
      CompletableFuture
          .supplyAsync(() -> sipTransferService.scanFilesForPattern(
              Long.valueOf(requestBody.getChannelId()),
              Long.valueOf(requestBody.getRouteId()), null, false,
              SourceType.REGULAR.name(), Optional.empty(), channelType),transactionPostExecutor)
          .whenComplete((p, throwable) -> {
            logger.trace("Current Thread Name :{}", Thread.currentThread().getName());
            if (throwable != null) {
              logger.error("Exception occured while completion of the thread "
                  + Thread.currentThread().getName(), throwable);
            }
            logger.trace("Current Thread Name :{}", Thread.currentThread().getName());
            for (BisDataMetaInfo info : p) {
              if (info.getDestinationPath() != null) {
                File folderWhereFilesDumped = new File(info.getDestinationPath());
                if (folderWhereFilesDumped.exists() && folderWhereFilesDumped.isDirectory()) {
                  logger.trace("Thread with Name :" + Thread.currentThread().getName()
                      + "has completed & created folder to put the files in "
                      + info.getDestinationPath());
                  File[] listOfFiles = folderWhereFilesDumped.listFiles();
                  if (listOfFiles != null && listOfFiles.length == 0) {
                    folderWhereFilesDumped.delete();
                    logger.trace("Thread with Name :" + Thread.currentThread().getName()
                        + "has completed & created folder to put the files in "
                        + info.getDestinationPath()
                        + " & it is empty so it has been deleted after completion of the thread.");
                  }
                }
              }
            }
            deferredResult.setResult(ResponseEntity.ok(p));
          });
    } else {
      CompletableFuture.supplyAsync(() -> sipTransferService.immediateTransfer(requestBody),
          transactionPostExecutor).whenComplete((p, throwable) -> {
            logger.trace("Current Thread Name :{}", Thread.currentThread().getName());
            if (throwable != null) {
              logger.error("Exception occured while completion of the thread "
                  + Thread.currentThread().getName(), throwable);
            }
            for (BisDataMetaInfo info : p) {
              String normalizedDestionationPath =
                  SipCommonUtils.normalizePath(info.getDestinationPath());
              if (normalizedDestionationPath != null) {
                File folderWhereFilesDumped = new File(normalizedDestionationPath);
                if (folderWhereFilesDumped.exists() && folderWhereFilesDumped.isDirectory()) {
                  logger.trace("Thread with Name :" + Thread.currentThread().getName()
                      + "has completed & created folder to put the files in "
                      + info.getDestinationPath());
                  File[] listOfFiles = folderWhereFilesDumped.listFiles();
                  if (listOfFiles != null && listOfFiles.length == 0) {
                    folderWhereFilesDumped.delete();
                    logger.trace("Thread with Name :" + Thread.currentThread().getName()
                        + "has completed & created folder to put the files in "
                        + info.getDestinationPath()
                        + " & it is empty so it has been deleted after completion of the thread.");
                  }
                }
              }
            }
            deferredResult.setResult(ResponseEntity.ok(p));
          });
    }
    ZonedDateTime endTime = ZonedDateTime.now();
    long durationInMillis = Duration.between(startTime, endTime).toMillis();
    logger.trace("Servlet thread released-{}", endTime);
    logger.info("Time taken for the POST Transaction IO to complete(in millis) - {}",
        durationInMillis);

    logger.trace("Number of Active Threads :{}", Thread.activeCount());
    if (durationInMillis > 10000L) {
      logger.info("IO for POST was Blocked for thatn 10 Second-{} ", durationInMillis);
    }

    return deferredResult;
  }
  
  /**
   * This end-point will be used to get the status of the destination.
   * @param requestBody object to send the payload.
   * @return Object {@link Map}
   */
  @RequestMapping(value = "/data/status", method = RequestMethod.POST,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Map<String, Boolean> isDestinationExist(@ApiParam(
      value = "Payload structure which to be used to initiate the transfer",
      required = true) @Valid @RequestBody(required = true) BisConnectionTestPayload requestBody) {
    logger.trace("Checking for data path: " + requestBody.getDestinationLocation());
    boolean result = false;
    SipPluginContract sipTransferService = factory
            .getInstance(requestBody.getChannelType().toString());
    try {
      result = sipTransferService.isDataExists(requestBody.getDestinationLocation());
    } catch (Exception e) {
      logger.trace("Exception occurred while checking the data location" + e);
      throw new SftpProcessorException("Exception occurred while checking the data location", e);
    }
    Map<String, Boolean> responseMap = new HashMap<String, Boolean>();
    responseMap.put("status", result);
    return responseMap;
  }

  
}

