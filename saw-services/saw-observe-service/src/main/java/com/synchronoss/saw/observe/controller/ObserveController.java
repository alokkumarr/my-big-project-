package com.synchronoss.saw.observe.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/observe")
public class ObserveController {

  private static final Logger logger = LoggerFactory.getLogger(ObserveController.class);

  
  @RequestMapping(value = "/{analysisId}/executions/{executionId}/data", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ListenableFuture<ResponseEntity<?>> exportAnalyses (@PathVariable("executionId") String executionId, @PathVariable("analysisId") String analysisId, 
      HttpServletRequest request, HttpServletResponse response){
    logger.debug("executionId in export {}", executionId);
    logger.debug(request.getHeader("Authorization"));
    logger.debug(request.getHeader("Host"));
    ListenableFuture<ResponseEntity<?>> responseObjectFuture = null;
    responseObjectFuture = null; // TODO: Service invocation
    return responseObjectFuture;
  }
 
  
  
}
