package com.synchronoss.saw.storage.proxy.controller;


import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getDsks;

import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getTicket;
import static com.synchronoss.sip.utils.SipCommonUtils.authValidation;
import static com.synchronoss.sip.utils.SipCommonUtils.checkForPrivateCategory;
import static com.synchronoss.sip.utils.SipCommonUtils.setBadRequest;
import static com.synchronoss.sip.utils.SipCommonUtils.setUnAuthResponse;
import static com.synchronoss.sip.utils.SipCommonUtils.validatePrivilege;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.analysis.modal.Analysis;

import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.DataSecurityKey;

import com.synchronoss.saw.model.SIPDSL;

import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;

import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;

import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class is used to perform all kind of operation by JSON store
 *
 * @author spau0004
 */
@RestController
@Api(
    value =
        "The controller provides operations pertaining to polyglot persistence layer of synchronoss analytics platform ")
@ApiResponses(
    value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Internal server Error. Contact System administrator")
    })
public class StorageProxyController {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyController.class);


  @Autowired
  private StorageProxyService proxyService;

  public static final String AUTHORIZATION = "Authorization";

  /**
   * This method is used to get the data based on the storage type<br> perform conversion based on
   * the specification asynchronously
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
    Boolean isAlert = sipdsl.getType().equalsIgnoreCase("alert");
    Ticket authTicket = request != null && !isAlert ? getTicket(request) : null;
    if (authTicket == null && !isAlert) {
      response.setStatus(401);
      logger.error("Invalid authentication token");
      return Collections.singletonList("Invalid authentication token");
    }
    List<TicketDSKDetails> dskList =
        authTicket != null ? authTicket.getDataSecurityKey() : new ArrayList<>();
    List<Object> responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSecurityKey dataSecurityKey = new DataSecurityKey();
    dataSecurityKey.setDataSecuritykey(getDsks(dskList));
    String analysisType = sipdsl.getType();

    try {
      // proxyNode = StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody),
      // "contents");
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(sipdsl));
      responseObjectFuture =
          proxyService.execute(
              sipdsl.getSipQuery(),
              size,
              ExecutionType.onetime,
              analysisType,
              false,authTicket);
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
  @ResponseBody
  public ExecuteAnalysisResponse executeAnalysis(
      @ApiParam(
          value = "Storage object that needs to be added/updated/deleted to the store",
          required = true)
      @Valid
      @RequestBody
          Analysis analysis,
      @ApiParam(value = "analysis id", required = false)
      @RequestParam(name = "id", required = false)
          String queryId,
      @ApiParam(value = "size of execution", required = false)
      @RequestParam(name = "size", required = false)
          Integer size,
      @ApiParam(value = "page number", required = false)
      @RequestParam(name = "page", required = false)
          Integer page,
      @ApiParam(value = "page size", required = false)
      @RequestParam(name = "pageSize", required = false)
          Integer pageSize,
      @ApiParam(value = "execution type", required = false)
      @RequestParam(name = "executionType", required = false, defaultValue = "onetime")
          ExecutionType executionType,
      @ApiParam(value = "user id", required = false)
      @RequestParam(name = "userId", required = false)
          String userId,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, IllegalAccessException {
    logger.debug("Request Body:{}", analysis);
    String authToken = request.getHeader(AUTHORIZATION);
    if (analysis == null) {
      throw new JSONMissingSAWException("Analysis definition is missing in request body");
    }
    boolean isScheduledExecution = executionType.equals(ExecutionType.scheduled);
    boolean isPublishedExecution = executionType.equals(ExecutionType.publish);

    ExecuteAnalysisResponse executeResponse = new ExecuteAnalysisResponse();
    if (!isScheduledExecution && !authValidation(authToken)) {
      setUnAuthResponse(response);
      return executeResponse;
    }

    Ticket authTicket = request != null && !isScheduledExecution ? getTicket(request) : null;
    if (authTicket == null && !isScheduledExecution) {
      logger.error("Invalid authentication token");
      setUnAuthResponse(response);
      return executeResponse;
    }

    ArrayList<Products> productList = isScheduledExecution ? null : authTicket.getProducts();
    Long category = isScheduledExecution ? null :
        analysis.getCategory() == null
            ? checkForPrivateCategory(authTicket)
            : Long.parseLong(analysis.getCategory());
    logger.debug("Cat " + category);
    if (!isScheduledExecution && category == null) {
      logger.error("BAD REQUEST : category should not be null!!");
      setBadRequest(response);
      return executeResponse;
    }
    if (isPublishedExecution && !validatePrivilege(productList, category, PrivilegeNames.PUBLISH)) {
      logger.error("UNAUTHORIZED ACCESS : User don't have the PUBLISH privilege!!");
      setUnAuthResponse(response);
      return executeResponse;
    } else if (isPublishedExecution && validatePrivilege(productList, category, PrivilegeNames.PUBLISH)
        && SipCommonUtils.haveSystemCategory(productList, category)) {
      logger.error("UNAUTHORIZED ACCESS : System category can't be published.");
      setUnAuthResponse(response);
      return executeResponse;
    } else if (!isScheduledExecution && !isPublishedExecution && !validatePrivilege(productList,
        category,
        PrivilegeNames.EXECUTE)) {
      logger.error("UNAUTHORIZED ACCESS : User don't have the EXECUTE privilege!!");
      setUnAuthResponse(response);
      return executeResponse;
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    try {
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(analysis));
      executeResponse =
          proxyService.executeAnalysis(
              analysis, size, page, pageSize,  executionType, userId, authTicket, queryId);

    } catch (IOException e) {
      logger.error("expected missing on the request body.", e);
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ResponseStatusException responseStatusException) {
      logger.error("Invalid request body.", responseStatusException);
      throw responseStatusException;
    } catch (ReadEntitySAWException ex) {
      logger.error("Problem on the storage while reading data from storage.", ex);
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } catch (ProcessingException e) {
      logger.error("Exception generated while validating incoming json against schema.", e);
      throw new JSONProcessingSAWException(
          "Exception generated while validating incoming json against schema.");
    } catch (SipDslProcessingException sipExeception) {
      throw sipExeception;
    } catch (RuntimeException runTimeExecption) {
      throw runTimeExecption;
    } catch (IllegalAccessException illegalArgumentException) {
      throw illegalArgumentException;
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      throw new RuntimeException("Exception generated while processing incoming json.");
    }
    logger.trace(
        "response data size {}", objectMapper.writeValueAsString(executeResponse.getTotalRows()));
    /**
     * Sip scheduler doesn't requires holding the execution result data while processing the
     * request, which can be minimized by removing the data section for memory optimization *
     */
    return executeResponse;
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
  @ResponseBody
  public List<?> listExecutions(
      @ApiParam(value = "DSL query Id", required = true) @PathVariable(name = "id") String queryId,
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "internalCall", required = false)
      @RequestParam(name = "internalCall", required = false)
      String internal) throws IOException {
    String authToken = request.getHeader(AUTHORIZATION);
    boolean schduledAnalysis = Boolean.valueOf(internal);
    if (!schduledAnalysis && !authValidation(authToken)) {
      setUnAuthResponse(response);
      return Collections.singletonList(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      Ticket authTicket = schduledAnalysis ? null : getTicket(request);
      return proxyService.fetchDslExecutionsList(queryId,authTicket);
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
  @ResponseBody
  public ExecutionResponse executionsData(
      @ApiParam(value = "page number", required = false)
      @RequestParam(name = "page", required = false)
          Integer page,
      @ApiParam(value = "page size", required = false)
      @RequestParam(name = "pageSize", required = false)
          Integer pageSize,
      @ApiParam(value = "execution type", required = false)
      @RequestParam(name = "executionType", required = false)
          ExecutionType executionType,
      @ApiParam(value = "analysis type", required = false)
      @RequestParam(name = "analysisType", required = false)
          String analysisType,
      @ApiParam(value = "List of executions", required = true) @PathVariable(name = "executionId")
          String executionId,
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "internalCall", required = false)
      @RequestParam(name = "internalCall", required = false)
          String internal) throws IOException {
    String authToken = request.getHeader(AUTHORIZATION);
    boolean schduledAnalysis = Boolean.valueOf(internal);
    if (!schduledAnalysis && !authValidation(authToken)) {
      ExecutionResponse executeResponse = new ExecutionResponse();
      setUnAuthResponse(response);
      return executeResponse;
    }
    Ticket authTicket = schduledAnalysis ? null : getTicket(request);
    if (analysisType != null && analysisType.equals("report")) {
      return proxyService
          .fetchDataLakeExecutionData(executionId, page, pageSize, executionType, authTicket);
    }
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService
          .fetchExecutionsData(executionId, executionType, page, pageSize, authTicket);
    } catch (Exception e) {
      logger.error("error occurred while fetching execution data", e);
    }
    return null;
  }

  /**
   * API to fetch the execution Data.
   *
   * @param analysisId
   * @return ExecutionResponse
   */
  @RequestMapping(
      value = "/internal/proxy/storage/{id}/lastExecutions/data",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public ExecutionResponse lastExecutionsData(
      @ApiParam(value = "page number", required = false)
      @RequestParam(name = "page", required = false)
          Integer page,
      @ApiParam(value = "page size", required = false)
      @RequestParam(name = "pageSize", required = false)
          Integer pageSize,
      @ApiParam(value = "execution type", required = false)
      @RequestParam(name = "executionType", required = false)
          ExecutionType executionType,
      @ApiParam(value = "analysis type", required = false)
      @RequestParam(name = "analysisType", required = false)
          String analysisType,
      @ApiParam(value = "List of executions", required = true) @PathVariable(name = "id")
          String analysisId,
      HttpServletRequest request,
      HttpServletResponse response,
      @ApiParam(value = "internalCall", required = false)
      @RequestParam(name = "internalCall", required = false)
          String internal) throws IOException {
    String authToken = request.getHeader(AUTHORIZATION);
    boolean schduledAnalysis = Boolean.valueOf(internal);
    if (!schduledAnalysis && !authValidation(authToken)) {
      ExecutionResponse executeResponse = new ExecutionResponse();
      setUnAuthResponse(response);
      return executeResponse;
    }
    Ticket authTicket = schduledAnalysis ? null : getTicket(request);
    if (analysisType != null && analysisType.equals("report")) {
      return proxyService.fetchLastExecutionsDataForDL(analysisId, page, pageSize,authTicket);
    }
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService
          .fetchLastExecutionsData(analysisId, executionType, page, pageSize, authTicket);
    } catch (Exception e) {
      logger.error("error occurred while fetching execution data", e);
    }
    return null;
  }
}
