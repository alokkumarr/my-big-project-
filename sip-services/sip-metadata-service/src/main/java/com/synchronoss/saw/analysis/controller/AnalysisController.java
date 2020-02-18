package com.synchronoss.saw.analysis.controller;

import static com.synchronoss.saw.util.SipMetadataUtils.getTicket;
import static com.synchronoss.saw.util.SipMetadataUtils.validateTicket;
import static com.synchronoss.sip.utils.SipCommonUtils.authValidation;
import static com.synchronoss.sip.utils.SipCommonUtils.checkForPrivateCategory;
import static com.synchronoss.sip.utils.SipCommonUtils.setBadRequest;
import static com.synchronoss.sip.utils.SipCommonUtils.setUnAuthResponse;
import static com.synchronoss.sip.utils.SipCommonUtils.validatePrivilege;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.modal.AnalysisPrivileges;
import com.synchronoss.saw.analysis.response.AnalysisResponse;
import com.synchronoss.saw.analysis.service.AnalysisService;
import com.synchronoss.saw.util.SipMetadataUtils;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Analysis entity controller.
 */
@RestController
@RequestMapping("/dslanalysis")
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
public class AnalysisController {

  private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);

  @Autowired
  AnalysisService analysisService;

  /**
   * create Analysis API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param analysis analysis definition
   * @return Analysis
   */
  @ApiOperation(
      value = " create Analysis API ",
      nickname = "CreateAnalysis",
      notes = "",
      response = AnalysisResponse.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AnalysisResponse createAnalysis(
      HttpServletRequest request, HttpServletResponse response, @RequestBody Analysis analysis,
      @RequestHeader("Authorization") String authToken) throws IOException {
    AnalysisResponse analysisResponse = new AnalysisResponse();
    if (analysis == null || analysis.getCategory() == null) {
      analysisResponse.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
      setBadRequest(response);
      logger.error(
          String.format("Analysis body and category can't be null or empty : %s ", analysis));
      return analysisResponse;
    }
    String id = UUID.randomUUID().toString();
    analysis.setId(id);

    if (!authValidation(authToken)) {
      setUnAuthResponse(response);
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    Ticket authTicket = getTicket(request);
    response = validateTicket(authTicket, PrivilegeNames.CREATE, analysis, response);
    if (response != null && response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    analysis.setCreatedBy(authTicket.getUserFullName());
    analysis.setCreatedTime(Instant.now().toEpochMilli());
    analysis.setUserId(authTicket.getUserId());
    analysisResponse.setAnalysis(analysisService.createAnalysis(analysis, authTicket));
    analysisResponse.setAnalysisId(id);
    return analysisResponse;
  }

  /**
   * Update Analysis API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param analysis Analysis definition
   * @param id Analysis id
   * @return Analysis
   */
  @ApiOperation(
      value = "Update Analysis API ",
      nickname = "UpdateAnalysis",
      notes = "",
      response = AnalysisResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AnalysisResponse updateAnalysis(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody Analysis analysis,
      @PathVariable(name = "id") String id,
      @RequestHeader("Authorization") String authToken) throws IOException {
    AnalysisResponse analysisResponse = new AnalysisResponse();

    if (analysis == null || analysis.getCategory() == null) {
      analysisResponse.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
      setBadRequest(response);
      logger.error(
          String.format("Analysis body and category can't be null or empty : %s ", analysis));
      return analysisResponse;
    }
    analysis.setId(id);

    if (!authValidation(authToken)) {
      setUnAuthResponse(response);
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    Ticket authTicket = getTicket(request);

    response = validateTicket(authTicket, PrivilegeNames.EDIT, analysis, response);
    if (response != null && response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    analysis.setModifiedBy(authTicket.getUserFullName());
    analysis.setModifiedTime(Instant.now().toEpochMilli());
    analysis.setUserId(authTicket.getUserId());
    analysisResponse.setAnalysis(analysisService.updateAnalysis(analysis, authTicket));
    analysisResponse.setAnalysisId(id);
    return analysisResponse;
  }

  /**
   * Delete Analysis API.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param id Analysis id
   * @return AnalysisResponse
   */
  @ApiOperation(
      value = "Delete Analysis API",
      nickname = "DeleteAnalysis",
      notes = "",
      response = AnalysisResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AnalysisResponse deleteAnalysis(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") String id,
      @RequestHeader(name = "Authorization", required = false) String authToken,
      @ApiParam(value = "internalCall", required = false)
          @RequestParam(name = "internalCall", required = false)
          String internalCall)
      throws IOException {
    boolean isInternalCall = Boolean.parseBoolean(internalCall);
    AnalysisResponse analysisResponse = new AnalysisResponse();
    if (!isInternalCall && !authValidation(authToken)) {
      setUnAuthResponse(response);
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }
    Ticket authTicket = isInternalCall ? null : getTicket(request);
    Analysis analysis = analysisService.getAnalysis(id, authTicket);
    if (analysis == null) {
      response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
      analysisResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
      return analysisResponse;
    }
    response =
        isInternalCall
            ? null
            : validateTicket(authTicket, PrivilegeNames.DELETE, analysis, response);
    if (response != null && response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    Long categoryId = analysis.getCategory() != null ? Long.valueOf(analysis.getCategory()) : 0L;
    if (response != null
        && response.getStatus() != HttpStatus.UNAUTHORIZED.value()
        && SipCommonUtils.haveSystemCategory(authTicket.getProducts(), categoryId)) {
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    analysisService.deleteAnalysis(id, authTicket);
    analysisResponse.setMessage("Analysis deleted successfully");
    analysisResponse.setAnalysisId(id);
    return analysisResponse;
  }

  /**
   * Fetch Analysis definition.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param id Analysis id
   * @return Analysis
   */
  @ApiOperation(
      value = "Fetch Analysis definition API",
      nickname = "FetchAnalysis",
      notes = "",
      response = AnalysisResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AnalysisResponse getAnalysis(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") String id,
      @ApiParam(value = "internalCall", required = false)
      @RequestParam(name = "internalCall", required = false)
          String internal) throws IOException {
    String authToken = request.getHeader("Authorization");
    AnalysisResponse analysisResponse = new AnalysisResponse();
    boolean schduledAnalysis = Boolean.valueOf(internal);
    if (!schduledAnalysis && !authValidation(authToken)) {
      setUnAuthResponse(response);
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }
    Ticket authTicket = schduledAnalysis ? null : getTicket(request);
    Analysis analysis = analysisService.getAnalysis(id, authTicket);
    if (analysis == null) {
      response.sendError(HttpStatus.NOT_FOUND.value(),HttpStatus.NOT_FOUND.getReasonPhrase());
      analysisResponse.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
      return analysisResponse;
    }
    response = schduledAnalysis ? null
        : validateTicket(authTicket, PrivilegeNames.ACCESS, analysis, response);
    if (response != null && response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
      analysisResponse.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisResponse;
    }

    analysisResponse.setAnalysis(analysis);
    analysisResponse.setMessage("Analysis retrieved successfully");
    analysisResponse.setAnalysisId(id);
    return analysisResponse;
  }

  /**
   * Fetch Analysis definition by Category Id.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param categoryId Category id
   * @return List of Analysis
   */
  @ApiOperation(
      value = "Fetch Analysis definition API",
      nickname = "FetchAnalysis",
      notes = "",
      response = List.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Analysis> getAnalysisByCategory(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "category") String categoryId) throws Exception {
    AnalysisResponse analysisResponse = new AnalysisResponse();
    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      setUnAuthResponse(response);
      analysisResponse.setMessage("Invalid authentication token");

      // TODO: return analysis response here. Will be taken care in the future.
    }
    Boolean isCatgryAuthorized = SipMetadataUtils.checkCategoryAccessible(authTicket, categoryId);
    if (isCatgryAuthorized) {
      Long userId = authTicket.getUserId();
      SipMetadataUtils.checkCategoryAccessible(authTicket, categoryId);
      Long privateCat = checkForPrivateCategory(authTicket);
      if (privateCat != null && categoryId.equalsIgnoreCase(String.valueOf(privateCat))) {
        return analysisService.getAnalysisByCategoryForUserId(categoryId, userId, authTicket);
      }
      return analysisService.getAnalysisByCategory(categoryId, authTicket);
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.sendError(HttpStatus.UNAUTHORIZED.value(),
          "You are not authorized to view this Category");
      return new ArrayList<>();
    }
  }

  /**
   * Fetch Analysis list with Privilege.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param analysisList List of analysis id
   * @return AnalysisPrivilege list
   * @throws Exception exception
   */
  @ApiOperation(
      value = "Fetch Analysis With privileges API",
      nickname = "FetchAnalysis list",
      notes = "",
      response = List.class)
  @RequestMapping(
      value = "/analysisPrivileges",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<AnalysisPrivileges> getAnalysisListWithPrivilege(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody List<String> analysisList) throws Exception {
    String authToken = request.getHeader("Authorization");
    List<AnalysisPrivileges> analysisPrivilegesList = new ArrayList<>();
    if (!authValidation(authToken)) {
      response
          .sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
      return analysisPrivilegesList;
    }
    Ticket authTicket = getTicket(request);
    analysisList.forEach(analysisId -> {
      Analysis analysis = analysisService.getAnalysis(analysisId, authTicket);
      AnalysisPrivileges analysisPrivileges = new AnalysisPrivileges();
      if (analysis == null) {
        analysisPrivileges.setAnalysisId(analysisId);
        analysisPrivileges.setAccessPermission(false);
        analysisPrivileges.setExecutePermission(false);
        analysisPrivileges.setMessage("Analysis is not present in the store");
        analysisPrivilegesList.add(analysisPrivileges);
      } else {
        analysisPrivileges.setAnalysisId(analysisId);
        analysisPrivileges.setCategory(analysis.getCategory());
        analysisPrivileges.setAccessPermission(
            validatePrivilege(authTicket.getProducts(), Long.parseLong(analysis.getCategory()),
                PrivilegeNames.ACCESS));
        analysisPrivileges.setExecutePermission(
            validatePrivilege(authTicket.getProducts(), Long.parseLong(analysis.getCategory()),
                PrivilegeNames.EXECUTE));
        analysisPrivilegesList.add(analysisPrivileges);
      }

    });
    // List<AnalysisPrivileges> analysisPriv = analysisService
    //     .getAnalysisListWithPrivilege(analysisList, authTicket);

    return analysisPrivilegesList;
  }
}
