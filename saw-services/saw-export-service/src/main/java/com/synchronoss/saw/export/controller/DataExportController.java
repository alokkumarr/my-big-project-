package com.synchronoss.saw.export.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.export.model.ftp.FTPDetails;
import com.synchronoss.saw.export.model.ftp.FtpCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  @RequestMapping(value = "/{analysisId}/executions/{executionId}/dispatch/{type}", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void dispatchAnalyses(@PathVariable("executionId") String executionId, @PathVariable("analysisId") String analysisId,
                                @PathVariable("type") String analysisType,
                                RequestEntity request, HttpServletResponse response){
    logger.debug("executionId in dispatch {}", executionId);
    if (analysisType.equalsIgnoreCase("report"))
      exportService.reportToBeDispatchedAsync(executionId, request,analysisId);
    else if(analysisType.equalsIgnoreCase("pivot"))
      exportService.pivotToBeDispatchedAsync(executionId, request,analysisId);
    if (analysisType.equalsIgnoreCase("esReport"))
      exportService.reportToBeDispatchedAsync(executionId, request,analysisId);
  }

  @RequestMapping(value = "/listFTP", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void listFTP(RequestEntity request, HttpServletResponse response) {
    try {
      response.setContentType("application/json");
      response.getWriter().write(exportService.listFtpsForCustomer(request).toString());
      response.getWriter().flush();
      response.getWriter().close();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

}
