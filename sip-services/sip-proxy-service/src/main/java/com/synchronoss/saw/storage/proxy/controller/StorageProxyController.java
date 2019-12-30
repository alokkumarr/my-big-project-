package com.synchronoss.saw.storage.proxy.controller;

import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.checkSameColumnAcrossTables;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getArtifactsNames;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getDSKDetailsByUser;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getDsks;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getSipQuery;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synchronoss.bda.sip.jwt.token.DataSecurityKeys;
import com.synchronoss.bda.sip.jwt.token.Products;
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
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
import com.synchronoss.sip.utils.RestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

  @Value("${metadata.service.host}")
  private String metaDataServiceExport;

  @Value("${sip.security.host}")
  private String sipSecurityHost;

  @Autowired
  private RestUtil restUtil;
  @Autowired
  private StorageProxyService proxyService;

  public static final String CUSTOMER_CODE = "customerCode";
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
    } else if (!isScheduledExecution && !isPublishedExecution && !validatePrivilege(productList,
        category,
        PrivilegeNames.EXECUTE)) {
      logger.error("UNAUTHORIZED ACCESS : User don't have the EXECUTE privilege!!");
      setUnAuthResponse(response);
      return executeResponse;
    }

    List<TicketDSKDetails> dskList = new ArrayList<>();
    DataSecurityKeys dataSecurityKeys = null;
    // fetch DSK details for scheduled
    if (isScheduledExecution && userId != null) {
      dataSecurityKeys = getDSKDetailsByUser(sipSecurityHost, userId, restUtil);
      dskList = dataSecurityKeys.getDataSecurityKeys();
    } else {
      dskList = authTicket == null ? dskList : authTicket.getDataSecurityKey();
    }

    SipQuery savedQuery =
        getSipQuery(
            analysis.getSipQuery().getSemanticId(), metaDataServiceExport, request, restUtil);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSecurityKey dataSecurityKey = new DataSecurityKey();
    dataSecurityKey.setDataSecuritykey(getDsks(dskList));
    List<String> sipQueryArts = getArtifactsNames(analysis.getSipQuery());
    String analysisType = analysis.getType();
    Boolean designerEdit =
        analysis.getDesignerEdit() == null ? false : analysis.getDesignerEdit();
    boolean queryMode = analysisType.equalsIgnoreCase("report") && designerEdit;
    String query = queryMode ? analysis.getSipQuery().getQuery().toUpperCase().concat(" ") : null;
    if (checkSameColumnAcrossTables(savedQuery,dataSecurityKey)) {
      response.sendError(HttpStatus.BAD_REQUEST.value(),
          "Column ambiguity error!!"
              + " DSK name should use TableName.columnName if same column present across tables!!");
      return executeResponse;
    }
    DataSecurityKey dataSecurityKeyNode = queryMode ? QueryBuilderUtil
        .checkDSKApplicableAnalysis(getArtifactsNames(savedQuery), query, dataSecurityKey,
        savedQuery) :
        QueryBuilderUtil
            .checkDSKApplicableAnalysis(savedQuery, dataSecurityKey, sipQueryArts);

    // Customer Code filtering SIP-8381, we can make use of existing DSK to filter based on customer
    // code.
    boolean filterDSKByCustomerCode =
        authTicket != null
            && authTicket.getIsJvCustomer() != 1
            && authTicket.getFilterByCustomerCode() == 1;
    boolean scheduledDSKbyCustomerCode =
        authTicket == null
            && isScheduledExecution
            && dataSecurityKeys != null
            && dataSecurityKeys.getIsJvCustomer() != 1
            && dataSecurityKeys.getFilterByCustomerCode() == 1;
    if (filterDSKByCustomerCode || scheduledDSKbyCustomerCode) {
      DataSecurityKeyDef dataSecurityKeyDef = new DataSecurityKeyDef();
      List<String> artsName = getArtifactsNames(savedQuery);
      List<DataSecurityKeyDef> customerFilterDsks = new ArrayList<>();
      String customerCode = dataSecurityKeys!= null ? dataSecurityKeys.getCustomerCode() : authTicket.getCustCode();
      if (analysisType.equalsIgnoreCase("report") && designerEdit) {
        logger.trace("Artifact Name : " + artsName);
        for (String artifact : artsName) {
          if (query.contains(artifact)) {
            dataSecurityKeyDef.setName(artifact + "." + CUSTOMER_CODE);
            dataSecurityKeyDef.setValues(Collections.singletonList(customerCode));
            customerFilterDsks.add(dataSecurityKeyDef);
            dataSecurityKeyDef = new DataSecurityKeyDef();
          }
        }
      } else {
        for (String artifact : sipQueryArts) {
          dataSecurityKeyDef.setName(artifact.toUpperCase() + "." + CUSTOMER_CODE);
          dataSecurityKeyDef.setValues(Collections.singletonList(customerCode));
          customerFilterDsks.add(dataSecurityKeyDef);
          dataSecurityKeyDef = new DataSecurityKeyDef();
        }
      }

      List<DataSecurityKeyDef> dataSecurityKeyDefList;
      if (dataSecurityKeyNode != null && dataSecurityKeyNode.getDataSecuritykey() != null) {
        dataSecurityKeyDefList = dataSecurityKeyNode.getDataSecuritykey();
        dataSecurityKeyDefList.addAll(customerFilterDsks);
        dataSecurityKeyNode.setDataSecuritykey(dataSecurityKeyDefList);
      } else if (dataSecurityKeyNode.getDataSecuritykey() == null) {
        dataSecurityKeyDefList = customerFilterDsks;
        dataSecurityKeyNode.setDataSecuritykey(dataSecurityKeyDefList);
      }
    }

    logger.debug("Final DataSecurity Object : ", dataSecurityKeyNode);
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
                dataSecurityKeyNode,
                (List<Object>) executeResponse.getData());
        proxyService.saveDslExecutionResult(executionResult);
        // For published analysis, update analysis metadata table with the category information.
        analysis = executionResult.getAnalysis();
        Long uid = analysis.getUserId() == null ? authTicket.getUserId() : analysis.getUserId();
        analysis.setUserId(uid);
        analysis.setCreatedTime(analysis.getCreatedTime() == null ? Instant.now().toEpochMilli()
            : analysis.getCreatedTime());
        analysis.setModifiedTime(Instant.now().toEpochMilli());
        analysis.setModifiedBy(
            authTicket != null && !authTicket.getUserFullName().isEmpty() ? authTicket
                .getUserFullName() : analysis.getModifiedBy());
        proxyService.updateAnalysis(analysis);
      }
      if (!analysis.getType().equalsIgnoreCase("report")) {
        logger.info("analysis ." + "not a DL report");
        if (tempExecutionType) {
          ExecutionResult executionResult =
              buildExecutionResult(
                  executeResponse.getExecutionId(),
                  analysis,
                  queryId,
                  startTime,
                  authTicket,
                  executionType,
                  dataSecurityKeyNode,
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
        // return user id with data in execution results
        if (userId != null) {
          executeResponse.setUserId(userId);
        }
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
    } catch (IllegalAccessException illegalArgumentException) {
      throw illegalArgumentException;
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      throw new RuntimeException("Exception generated while processing incoming json.");
    }
    logger.trace(
        "response data size {}", objectMapper.writeValueAsString(executeResponse.getTotalRows()));
    /** Sip scheduler doesn't requires holding the execution result data while processing the
     request, which can be minimized by removing the data section for memory optimization **/
    if (isScheduledExecution) {
      executeResponse.setData(null);
      logger.trace("Response returned back to scheduler {}",
          objectMapper.writeValueAsString(executeResponse));
    }
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
      DataSecurityKey dataSecurityKeyNode,
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
    executionResult.setDataSecurityKey(dataSecurityKeyNode.getDataSecuritykey());
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

      logger.trace("Extracting auth ticket details");
      Ticket authTicket = schduledAnalysis ? null : getTicket(request);

      List<TicketDSKDetails> dskList =
          authTicket == null ? new ArrayList<>() : authTicket.getDataSecurityKey();
      logger.trace("DSK List size = " + dskList);

      if (dskList == null || dskList.size() == 0) {
        return proxyService.fetchDslExecutionsList(queryId);
      } else {
        return new ArrayList<>();
      }

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

    List<TicketDSKDetails> dskList =
        authTicket == null ? new ArrayList<>() : authTicket.getDataSecurityKey();
    logger.trace("DSK List = " + dskList);

    // If user is associated with any datasecurity key, return empty data
    if (dskList != null && dskList.size() != 0 && executionType != null && !executionType
        .equals(ExecutionType.onetime)) {
      return new ExecutionResponse();
    }

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

    List<TicketDSKDetails> dskList =
        authTicket == null ? new ArrayList<>() : authTicket.getDataSecurityKey();
    logger.trace("DSK List = " + dskList);

    if (dskList != null && dskList.size() != 0) {
      return new ExecutionResponse();
    }
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
