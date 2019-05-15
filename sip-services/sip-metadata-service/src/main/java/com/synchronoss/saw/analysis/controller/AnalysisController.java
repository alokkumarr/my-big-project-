package com.synchronoss.saw.analysis.controller;

import static com.synchronoss.saw.util.SipMetadataUtils.getTicket;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.response.AnalysisResponse;
import com.synchronoss.saw.analysis.response.TempAnalysisResponse;
import com.synchronoss.saw.analysis.service.AnalysisService;
import com.synchronoss.saw.util.SipMetadataUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Analysis entity controller. */
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

  @Autowired AnalysisService analysisService;

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
      HttpServletRequest request, HttpServletResponse response, @RequestBody Analysis analysis) {
    AnalysisResponse analysisResponse = new AnalysisResponse();
    if (analysis == null) {
      analysisResponse.setMessage("Analysis definition can't be null for create request");
      response.setStatus(400);
      return analysisResponse;
    }

    String id = UUID.randomUUID().toString();
    analysis.setId(id);

    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
      analysisResponse.setMessage("Invalid authentication tol=ken");
    }
    analysis.setCreatedBy(authTicket.getUserFullName());
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
      @PathVariable(name = "id") String id) {
    AnalysisResponse analysisResponse = new AnalysisResponse();

    if (analysis == null) {
      analysisResponse.setMessage("Analysis definition can't be null for update request");
      response.setStatus(400);
      return analysisResponse;
    }
    analysis.setId(id);

    Ticket authTicket = getTicket(request);

    if (authTicket == null) {
      response.setStatus(401);
      analysisResponse.setMessage("Invalid authentication token");
    }
    analysis.setModifiedBy(authTicket.getUserFullName());
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
      @PathVariable(name = "id") String id) {
    AnalysisResponse analysisResponse = new AnalysisResponse();
    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
      analysisResponse.setMessage("Invalid authentication tol=ken");
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
      response = TempAnalysisResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public TempAnalysisResponse getAnalysis(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") String id) {

    TempAnalysisResponse analysisResponse = new TempAnalysisResponse();
    Ticket authTicket = getTicket(request);
    /**if (authTicket == null) {
      response.setStatus(401);
      analysisResponse.setMessage("Invalid authentication token");
    }*/
    analysisResponse.setAnalysis(analysisService.getAnalysis(id, authTicket));
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
      response = AnalysisResponse.class)
  @RequestMapping(
      value = "",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<ObjectNode> getAnalysisByCategory(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(name = "category") String categoryId) {
    AnalysisResponse analysisResponse = new AnalysisResponse();
    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
      analysisResponse.setMessage("Invalid authentication token");

      // TODO: return analysis response here. Will be taken care in the future.
    }
    Long userId = authTicket.getUserId();
    Boolean privateCategory = SipMetadataUtils.checkPrivateCategory(authTicket, categoryId);
    if (privateCategory) {
      return analysisService.getAnalysisByCategoryForUserId(categoryId, userId, authTicket);
    }
    return analysisService.getAnalysisByCategory(categoryId, authTicket);
  }
}
