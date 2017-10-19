package com.synchronoss.saw.export.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.export.model.DataResponse;

@RestController
@RequestMapping(value = "/export")
public class DataExportController {

  private static final Logger logger = LoggerFactory.getLogger(DataExportController.class);

  @Value("${analysis.service.host}")
  private String apiExportOtherProperties;

  @RequestMapping(value = "/{executionId}/data", method = RequestMethod.GET)
  public ResponseEntity<DataResponse> exportAnalyses (@PathVariable("executionId") String executionId, 
      HttpServletRequest request, HttpServletResponse response){
    logger.info("executionId in export {}", executionId);
    logger.info("URI to execute {}", apiExportOtherProperties);
    DataResponse dataResponse = new DataResponse();
    List<Object> data = new ArrayList<>();
    data.add("id");
    dataResponse.setData(data);
    dataResponse.setRowsToExport(100);
    dataResponse.setTotalRows(10);
    return new ResponseEntity<>(dataResponse,HttpStatus.OK);
  }
  
}
