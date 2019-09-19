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
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.es.QueryBuilderUtil;
import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.ExecuteAnalysisResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResponse;
import com.synchronoss.saw.storage.proxy.model.ExecutionResult;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import com.synchronoss.sip.utils.RestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
 * This class is used to perform all kind of operation by JSON store
 *
 * @author spau0004
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

  @Autowired private RestUtil restUtil;

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
              dataSecurityKey,
              ExecutionType.onetime,
              analysisType,
              false);
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
  @ResponseStatus(HttpStatus.OK)
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
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException {
    logger.debug("Request Body:{}", analysis);
    if (analysis == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }

    ExecuteAnalysisResponse executeResponse = new ExecuteAnalysisResponse();
    boolean isScheduledExecution = executionType.equals(ExecutionType.scheduled);
    Ticket authTicket = request != null ? getTicket(request) : null;
    if (authTicket == null) {
      response.setStatus(401);
      logger.error("Invalid authentication token");
      executeResponse.setData(Collections.singletonList("Invalid authentication token"));
      return executeResponse;
    }
    logger.debug(" is auth is null ?: {}", authTicket == null ? true : false);
    List<TicketDSKDetails> dskList =
        authTicket != null ? authTicket.getDataSecurityKey() : new ArrayList<>();
    List<Object> responseObjectFuture = null;
    SipQuery savedQuery =
        getSipQuery(analysis.getSipQuery(), metaDataServiceExport, request, restUtil);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSecurityKey dataSecurityKey = new DataSecurityKey();
    dataSecurityKey.setDataSecuritykey(getDsks(dskList));
    DataSecurityKey dataSecurityKeyNode =
        QueryBuilderUtil.checkDSKApplicableAnalysis(savedQuery, dataSecurityKey);

      // Customer Code filtering SIP-8381, we can make use of existing DSK to filter based on customer
      // code.
      if (authTicket.getIsJvCustomer() != 1 && authTicket.getFilterByCustomerCode() == 1) {
          DataSecurityKeyDef dataSecurityKeyDef = new DataSecurityKeyDef();
          dataSecurityKeyDef.setName("customerCode");
          List<String> dskValue = new ArrayList<>();
          dskValue.add(authTicket.getCustCode());
          List<String> attFilter = new ArrayList<>(); // For Testing : To be removed later.
          attFilter.add(authTicket.getCustCode());
          dataSecurityKeyDef.setValues(attFilter);
          List<DataSecurityKeyDef> dataSecurityKeyDefList;
          if (dataSecurityKeyNode != null && dataSecurityKeyNode.getDataSecuritykey() != null) {
              dataSecurityKeyDefList = dataSecurityKeyNode.getDataSecuritykey();
              dataSecurityKeyDefList.add(dataSecurityKeyDef);
              dataSecurityKeyNode.setDataSecuritykey(dataSecurityKeyDefList);
          } else if (dataSecurityKeyNode.getDataSecuritykey() == null) {
              dataSecurityKeyDefList = new ArrayList<>();
              dataSecurityKeyDefList.add(dataSecurityKeyDef);
              dataSecurityKeyNode.setDataSecuritykey(dataSecurityKeyDefList);
          }
      }

    try {
      Long startTime = new Date().getTime();
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(analysis));
      executeResponse =
          proxyService.executeAnalysis(
              analysis, size, page, pageSize, dataSecurityKeyNode, executionType);

      // Execution result will one be stored, if execution type is publish or Scheduled.
      boolean validExecutionType =
          executionType.equals(ExecutionType.publish)
              || executionType.equals(ExecutionType.scheduled);

      boolean tempExecutionType =
          executionType.equals(ExecutionType.onetime)
              || executionType.equals(ExecutionType.preview)
              || executionType.equals(ExecutionType.regularExecution);

      if (validExecutionType) {
        ExecutionResult executionResult =
            buildExecutionResult(
                executeResponse.getExecutionId(),
                analysis,
                queryId,
                startTime,
                authTicket,
                executionType,
                (List<Object>) executeResponse.getData());
        proxyService.saveDslExecutionResult(executionResult);
      }
      if (!analysis.getType().equalsIgnoreCase("report")) {
          logger.info("analysis ."+"not a DL report");
        if (tempExecutionType) {
          ExecutionResult executionResult =
              buildExecutionResult(
                  executeResponse.getExecutionId(),
                  analysis,
                  queryId,
                  startTime,
                  authTicket,
                  executionType,
                  (List<Object>) executeResponse.getData());
          proxyService.saveTtlExecutionResult(executionResult);
        }
        // return only requested data based on page no and page size, only for FE
        List<Object> pagingData =
            proxyService.pagingData(page, pageSize, (List<Object>) executeResponse.getData());
        /* If FE is sending the page no and page size then we are setting only   data that
         * corresponds to page no and page size in response instead of  total data.
         * For DL reports skipping this step as response from DL is already paginated.
         * */
        executeResponse.setData(
            pagingData != null && pagingData.size() > 0 ? pagingData : executeResponse.getData());
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
    } catch (SipDslProcessingException sipExeception) {
      throw sipExeception;
    } catch (RuntimeException runTimeExecption) {
      throw runTimeExecption;
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      throw new RuntimeException("Exception generated while processing incoming json.");
    }
    logger.trace("response data size {}", objectMapper.writeValueAsString(executeResponse.getTotalRows()));
    return executeResponse;
  }

  /**
   * Build execution result bean.
   *
   * @param executionId
   * @param analysis
   * @param queryId
   * @param startTime
   * @param authTicket
   * @param executionType
   * @param data
   * @return execution
   */
  private ExecutionResult buildExecutionResult(
      String executionId,
      Analysis analysis,
      String queryId,
      Long startTime,
      Ticket authTicket,
      ExecutionType executionType,
      List<Object> data) {
    ExecutionResult executionResult = new ExecutionResult();
    String type = analysis.getType();
    executionResult.setExecutionId(executionId);
    executionResult.setDslQueryId(queryId);
    executionResult.setAnalysis(analysis);
    executionResult.setStartTime(startTime);
    executionResult.setFinishedTime(new Date().getTime());
    executionResult.setExecutionType(executionType);
    executionResult.setData(!type.equalsIgnoreCase("report") ? data : null);
    executionResult.setStatus("success");
    executionResult.setExecutedBy(authTicket != null ? authTicket.getMasterLoginId() : "scheduled");
    executionResult.setRecordCount(data.size());
    return executionResult;
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
          String executionId) {
    if (analysisType != null && analysisType.equals("report")) {
      return proxyService.fetchDataLakeExecutionData(executionId, page, pageSize, executionType);
    }

    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService.fetchExecutionsData(executionId, executionType, page, pageSize);
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
  @ResponseStatus(HttpStatus.OK)
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
          String analysisId) {

    if (analysisType != null && analysisType.equals("report")) {
      return proxyService.fetchLastExecutionsDataForDL(analysisId, page, pageSize);
    }
    try {
      logger.info("Storage Proxy request to fetch list of executions");
      return proxyService.fetchLastExecutionsData(analysisId, executionType, page, pageSize);
    } catch (Exception e) {
      logger.error("error occurred while fetching execution data", e);
    }
    return null;
  }
}
