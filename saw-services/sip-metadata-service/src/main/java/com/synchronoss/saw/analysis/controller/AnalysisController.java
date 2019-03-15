package com.synchronoss.saw.analysis.controller;

import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.modal.Ticket;
import com.synchronoss.saw.analysis.response.AnalysisResponse;
import com.synchronoss.saw.analysis.service.AnalysisService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Analysis entity controller. */
@RestController
@RequestMapping("/analysis")
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
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param analysis analysis definition
   * @param id analysis ID
   * @return Analysis
   */
  @ApiOperation(
      value = " create Analysis API ",
      nickname = "CreateAnalysis",
      notes = "",
      response = AnalysisResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public AnalysisResponse createAnalysis(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody Analysis analysis,
      @PathVariable(name = "id") String id) {

    AnalysisResponse analysisResponse = new AnalysisResponse();
    Ticket ticket = new Ticket();
    if (analysis == null) {
      analysisResponse.setMessage("Analysis definition can't be null for create request");
      response.setStatus(400);
      return analysisResponse;
    }
    analysis.setId(id);
    analysisResponse.setAnalysis(analysisService.createAnalysis(analysis, ticket));
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
    Ticket ticket = new Ticket();
    AnalysisResponse analysisResponse = new AnalysisResponse();

    if (analysis == null) {
      analysisResponse.setMessage("Analysis definition can't be null for update request");
      response.setStatus(400);
      return analysisResponse;
    }
    analysis.setId(id);
    analysisResponse.setAnalysis(analysisService.updateAnalysis(analysis, ticket));
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
    Ticket ticket = new Ticket();
    AnalysisResponse analysisResponse = new AnalysisResponse();
    analysisService.deleteAnalysis(id, ticket);
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
      @PathVariable(name = "id") String id) {
    Ticket ticket = new Ticket();
    AnalysisResponse analysisResponse = new AnalysisResponse();
    analysisService.getAnalysis(id, ticket);
    analysisResponse.setMessage("Analysis retrieved successfully");
    analysisResponse.setAnalysisId(id);
    return analysisResponse;
  }
}
