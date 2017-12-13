package com.synchronoss.saw.export.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.export.generate.interfaces.ExportService;
import com.synchronoss.saw.export.model.DataResponse;

@RestController
@RequestMapping(value = "/exports")
public class DataExportController {

  private static final Logger logger = LoggerFactory.getLogger(DataExportController.class);

  @Autowired
  private ExportService exportService;
  
  @RequestMapping(value = "/{analysisId}/executions/{executionId}/data", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<DataResponse>> exportAnalyses (@PathVariable("executionId") String executionId, @PathVariable("analysisId") String analysisId, 
      HttpServletRequest request, HttpServletResponse response){
    logger.debug("executionId in export {}", executionId);
    logger.debug(request.getHeader("Authorization"));
    logger.debug(request.getHeader("Host"));
    ListenableFuture<ResponseEntity<DataResponse>> responseObjectFuture = null;
    responseObjectFuture = exportService.dataToBeExportedAsync(executionId, request,analysisId);
    return responseObjectFuture;
  }

  @RequestMapping(value = "/{analysisId}/executions/{executionId}/dispatch", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void dispatchAnalyses (@PathVariable("executionId") String executionId, @PathVariable("analysisId") String analysisId,
                                RequestEntity request, HttpServletResponse response){
    logger.debug("executionId in export {}", executionId);
    exportService.dataToBeDispatchedAsync(executionId, request,analysisId);
  }

}
