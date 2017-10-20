package com.synchronoss.saw.export.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  @RequestMapping(value = "/{executionId}/data", method = RequestMethod.GET)
  public ResponseEntity<?> exportAnalyses (@PathVariable("executionId") String executionId, 
      HttpServletRequest request, HttpServletResponse response){
    logger.info("executionId in export {}", executionId);
    DataResponse dataResponse = new DataResponse();
    List<Object> data = new ArrayList<>();
    data.add("id1");
    data.add("id2");
    data.add("id3");
    dataResponse.setData(data);
    dataResponse.setRowsToExport(100);
    dataResponse.setTotalRows(10);
    //Stream<Integer> stream = Stream.of(1,2,3,4,5,6,7,8,9);
    String jsonString_1 = "{ \"id\" : \"8a1d762e-4976-43a3-8867-e46f2d04f0c7\" }";
    String jsonString_2 = "{ \"id\" : \"8a1d762e-4976-43a3-8867-e46f2d04f0c9\" }";
    Stream<Object> stream = Stream.of(jsonString_1, jsonString_2);
    System.out.println(data.stream());
    return new ResponseEntity<>(data.stream(),HttpStatus.OK);
  }
  
}
