package com.synchronoss.saw.storage.proxy.controller;

import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getDsks;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getSipQuery;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getTicket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.es.QueryBuilderUtil;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author spau0004 This class is used to perform CRUD operation by storage The requests are JSON
 *     documents in the following formats "{ "contents": { "proxy" : [ { "storage" : "ES", "action"
 *     : "search", "query" : "", "requestBy" :"admin@sycnchrnoss.com", "objectType" : "",
 *     "indexName": "", "tableName": "", "objectName":"", "requestedTime":"", "productCode": "",
 *     "moduleName":"", "dataSecurityKey":[], "resultFormat":"", "data": [] } ] } }"
 */
@RestController
@Api(
    value =
        "The controller provides operations pertaining to polyglot persistence layer of synchronoss analytics platform ")
public class StorageProxyController {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyController.class);

  @Autowired private StorageProxyService proxyService;

  @Value("${metadata.service.host}")
  private String metaDataServiceExport;

  /**
   * This method is used to get the data based on the storage type<br>
   * perform conversion based on the specification asynchronously.
   *
   * @param requestBody
   * @return
   */
  // @Async(AsyncConfiguration.TASK_EXECUTOR_CONTROLLER)
  // @RequestMapping(value = "/internal/proxy/storage/async", method = RequestMethod.POST, produces=
  // MediaType.APPLICATION_JSON_UTF8_VALUE)
  // @ResponseStatus(HttpStatus.ACCEPTED)
  public CompletableFuture<StorageProxy> retrieveStorageDataAsync(
      @RequestBody StorageProxy requestBody) {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    CompletableFuture<StorageProxy> responseObjectFuture = null;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      // StorageProxyNode proxyNode =
      // StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody), "contents");
      logger.trace(
          "Storage Proxy async request object : {} ", objectMapper.writeValueAsString(requestBody));
      responseObjectFuture =
          CompletableFuture.supplyAsync(
                  () -> {
                    StorageProxy proxyResponseData = null;
                    try {
                      proxyResponseData = proxyService.execute(requestBody);
                    } catch (IOException e) {
                      logger.error("While retrieving data there is an exception.", e);
                      proxyResponseData =
                          StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
                    } catch (ProcessingException e) {
                      logger.error(
                          "Exception generated while validating incoming json against schema.", e);
                      proxyResponseData =
                          StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
                    } catch (Exception e) {
                      logger.error("Exception generated while processing incoming json.", e);
                      proxyResponseData =
                          StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
                    }
                    return proxyResponseData;
                  })
              .handle(
                  (res, ex) -> {
                    if (ex != null) {
                      logger.error("While retrieving data there is an exception.", ex);
                      res.setStatusMessage(ex.getCause().toString());
                      return res;
                    }
                    return res;
                  });
    } catch (IOException e) {
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to get the data based on the storage type<br>
   * perform conversion based on the specification asynchronously
   *
   * @param requestBody
   * @return
   * @throws JsonProcessingException
   */
  @ApiOperation(
      value = "Provides an access to persistence using commmon specification",
      nickname = "actionStorage",
      notes = "",
      response = StorageProxy.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
      })
  @RequestMapping(
      value = "/internal/proxy/storage",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public StorageProxy retrieveStorageDataSync(
      @ApiParam(
              value = "Storage object that needs to be added/updated/deleted to the store",
              required = true)
          @Valid
          @RequestBody
          StorageProxy requestBody)
      throws JsonProcessingException {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    StorageProxy responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    try {
      // proxyNode = StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody),
      // "contents");
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(requestBody));
      responseObjectFuture = proxyService.execute(requestBody);
    } catch (IOException e) {
      logger.error("expected missing on the request body.", e.getMessage());
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex) {
      logger.error("Problem on the storage while reading data from storage.", ex);
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } catch (ProcessingException e) {
      logger.error("Exception generated while validating incoming json against schema.", e);
      responseObjectFuture =
          StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      responseObjectFuture =
          StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
    }
    logger.trace("response data {}", objectMapper.writeValueAsString(responseObjectFuture));
    return responseObjectFuture;
  }

  @RequestMapping(
      value = "/internal/proxy/storage/fetch",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public List<?> retrieveStorageDataSync(
      @ApiParam(
              value = "Storage object that needs to be added/updated/deleted to the store",
              required = true)
          @Valid
          @RequestBody
          SIPDSL sipdsl,
      @RequestParam(name = "size", required = false) Integer size,
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException {
    logger.debug("Request Body:{}", sipdsl);
    if (sipdsl == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
      logger.error("Invalid authentication token");
      return Collections.singletonList("Invalid authentication token");
    }
    List<TicketDSKDetails> dskList = authTicket.getDataSecurityKey();
    List<Object> responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSecurityKey dataSecurityKey = new DataSecurityKey();
    dataSecurityKey.setDataSecuritykey(getDsks(dskList));
    try {
      // proxyNode = StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody),
      // "contents");
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(sipdsl));
      responseObjectFuture = proxyService.execute(sipdsl.getSipQuery(), size, dataSecurityKey);
    } catch (IOException e) {
      logger.error("expected missing on the request body.", e);
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex) {
      logger.error("Problem on the storage while reading data from storage.", ex);
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } catch (ProcessingException e) {
      logger.error("Exception generated while validating incoming json against schema.", e);
      throw new JSONProcessingSAWException(
          "Exception generated while validating incoming json against schema.");
      // responseObjectFuture = StorageProxyUtils.prepareResponse(sipdsl, e.getCause().toString());
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      throw new RuntimeException("Exception generated while processing incoming json.");
      //  responseObjectFuture= StorageProxyUtils.prepareResponse(sipdsl, e.getCause().toString());
    }
    logger.trace("response data {}", objectMapper.writeValueAsString(responseObjectFuture));
    return responseObjectFuture;
  }

  @RequestMapping(
      value = "/internal/proxy/storage/execute",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public List<?> executeAnalysis(
      @ApiParam(
              value = "Storage object that needs to be added/updated/deleted to the store",
              required = true)
          @Valid
          @RequestBody
          SipQuery sipQuery,
      @RequestParam(name = "id", required = false) String queryId,
      @RequestParam(name = "size", required = false) Integer size,
      @RequestParam(name = "ExecutionType", required = false, defaultValue = "onetime")
          ExecutionType executionType,
      @RequestParam(name = "executedBy", required = false) String executedBy,
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException {
    logger.debug("Request Body:{}", sipQuery);
    if (sipQuery == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    Ticket authTicket = getTicket(request);
    if (authTicket == null && !executionType.equals(ExecutionType.scheduled)) {
      response.setStatus(401);
      logger.error("Invalid authentication token");
      return Collections.singletonList("Invalid authentication token");
    }
    List<TicketDSKDetails> dskList =
        authTicket != null ? authTicket.getDataSecurityKey() : new ArrayList<>();
    List<Object> responseObjectFuture = null;
    SipQuery savedQuery = getSipQuery(sipQuery.getSemanticId(), metaDataServiceExport, request);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSecurityKey dataSecurityKey = new DataSecurityKey();
    dataSecurityKey.setDataSecuritykey(getDsks(dskList));
    DataSecurityKey dataSecurityKeyNode =
        QueryBuilderUtil.checkDSKApplicableAnalysis(savedQuery.getArtifacts(), dataSecurityKey);

    try {
      // proxyNode = StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody),
      // "contents");
      Long startTime = new Date().getTime();
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(sipQuery));
      responseObjectFuture = proxyService.execute(sipQuery, size, dataSecurityKeyNode);
      // Execution result will one be stored, if execution type is publish or Scheduled.
      if (executionType.equals(ExecutionType.publish)
          || executionType.equals(ExecutionType.scheduled)) {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setExecutionId(UUID.randomUUID().toString());
        executionResult.setDslQueryId(queryId);
        executionResult.setSipQuery(sipQuery);
        executionResult.setStartTime(startTime);
        executionResult.setFinishedTime(new Date().getTime());
        executionResult.setData(responseObjectFuture);
        executionResult.setExecutionType(executionType);
        executionResult.setStatus("success");
        executionResult.setExecutedBy(executedBy);
        proxyService.saveDslExecutionResult(executionResult);
      }
    } catch (IOException e) {
      logger.error("expected missing on the request body.", e);
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex) {
      logger.error("Problem on the storage while reading data from storage.", ex);
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } catch (ProcessingException e) {
      logger.error("Exception generated while validating incoming json against schema.", e);
      throw new JSONProcessingSAWException(
          "Exception generated while validating incoming json against schema.");
      // responseObjectFuture = StorageProxyUtils.prepareResponse(sipdsl, e.getCause().toString());
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      throw new RuntimeException("Exception generated while processing incoming json.");
      //  responseObjectFuture= StorageProxyUtils.prepareResponse(sipdsl, e.getCause().toString());
    }
    logger.trace("response data {}", objectMapper.writeValueAsString(responseObjectFuture));
    return responseObjectFuture;
  }

  /**
   * API to fetch the list of saved executions.
   *
   * @param queryId DSL Query ID
   * @return List
   */
  @RequestMapping(
      value = "/internal/proxy/storage/{id}/executions",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public List<?> listExecutions(
      @ApiParam(value = "DSL query Id", required = true) @PathVariable(name = "id")
          String queryId) {
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService.fetchDslExecutionsList(queryId);
    } catch (Exception e) {
      logger.error("error occurred while fetching list of executions ", e);
    }
    return null;
  }

  /**
   * API to fetch the execution Data.
   *
   * @param executionId
   * @return ExecutionResponse
   */
  @RequestMapping(
      value = "/internal/proxy/storage/{executionId}/executions/data",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ExecutionResponse executionsData(
      @ApiParam(value = "List of executions", required = true) @PathVariable(name = "executionId")
          String executionId) {
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService.fetchExecutionsData(executionId);
    } catch (Exception e) {
      logger.error("error occurred while fetching execution data", e);
    }
    return null;
  }

  /**
   * API to fetch the execution Data.
   *
   * @param executionId
   * @return ExecutionResponse
   */
  @RequestMapping(
      value = "/internal/proxy/storage/{id}/lastExecutions/data",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ExecutionResponse lastExecutionsData(
      @ApiParam(value = "List of executions", required = true) @PathVariable(name = "id")
          String executionId) {
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService.fetchLastExecutionsData(executionId);
    } catch (Exception e) {
      logger.error("error occurred while fetching execution data", e);
    }
    return null;
  }
}
