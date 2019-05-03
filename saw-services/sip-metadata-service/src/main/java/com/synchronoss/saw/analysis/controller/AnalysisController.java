package com.synchronoss.saw.analysis.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.analysis.modal.Ticket;
import com.synchronoss.saw.analysis.response.AnalysisResponse;
import com.synchronoss.saw.analysis.response.TempAnalysisResponse;
import com.synchronoss.saw.analysis.service.AnalysisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody Analysis analysis,
      @RequestHeader("Authorization") String authToken) {

    logger.debug("Auth token = " + authToken);
    if (authToken.startsWith("Bearer")) {
      authToken = authToken.substring("Bearer ".length());
    }

    // TODO: Remove the hardcoded key
    Claims ssoToken = Jwts.parser().setSigningKey("sncrsaw2").parseClaimsJws(authToken).getBody();

    Map<String, Object> authTicket = ((Map<String, Object>) ssoToken.get("ticket"));
    logger.debug("Auth ticket = " + authTicket);
    String userName = (String) authTicket.get("userFullName");
    logger.debug("Username = " + userName);

    AnalysisResponse analysisResponse = new AnalysisResponse();
    if (analysis == null) {
      analysisResponse.setMessage("Analysis definition can't be null for create request");
      response.setStatus(400);
      return analysisResponse;
    }

    String id = UUID.randomUUID().toString();
    analysis.setId(id);
    analysis.setCreatedBy(userName);
    Ticket ticket = new Ticket();
    analysisResponse.setAnalysis(analysisService.createAnalysis(analysis, ticket));
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
      @RequestHeader("Authorization") String authToken) {

    logger.debug("Auth token = " + authToken);
    if (authToken.startsWith("Bearer")) {
      authToken = authToken.substring("Bearer ".length());
    }

    // TODO: Remove the hardcoded key
    Claims ssoToken = Jwts.parser().setSigningKey("sncrsaw2").parseClaimsJws(authToken).getBody();

    Map<String, Object> authTicket = ((Map<String, Object>) ssoToken.get("ticket"));
    logger.debug("Auth ticket = " + authTicket);
    String userName = (String) authTicket.get("userFullName");
    logger.debug("Username = " + userName);
    AnalysisResponse analysisResponse = new AnalysisResponse();

    if (analysis == null) {
      analysisResponse.setMessage("Analysis definition can't be null for update request");
      response.setStatus(400);
      return analysisResponse;
    }
    analysis.setId(id);
    analysis.setModifiedBy(userName);
    Ticket ticket = new Ticket();
    analysisResponse.setAnalysis(analysisService.updateAnalysis(analysis, ticket));
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
      response = TempAnalysisResponse.class)
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public TempAnalysisResponse getAnalysis(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable(name = "id") String id) {
    Ticket ticket = new Ticket();
    TempAnalysisResponse analysisResponse = new TempAnalysisResponse();
    analysisResponse.setAnalysis(analysisService.getAnalysis(id, ticket));
    analysisResponse.setMessage("Analysis retrieved successfully");
    analysisResponse.setAnalysisId(id);
    return analysisResponse;
  }

  /**
   * Fetch Analysis definition by Category Id.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param id Category id
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
      @RequestParam(name = "category") String id) {
    Ticket ticket = new Ticket();
    return analysisService.getAnalysisByCategory(id, ticket);
  }
}
