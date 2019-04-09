package com.synchronoss.saw.storage.proxy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.storage.proxy.service.ChartResultMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// TODO: 4/4/2019 - This controller is only for testing, need to be removed after the testing
// completed
@RestController
public class ChartController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

  @RequestMapping(
      value = "/internal/proxy/chartResultAnalysis/",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Object retrieveFlattenChartData(@Valid @RequestBody JsonNode jsonNode) {
    LOGGER.debug("Start Chart Controller");
    ChartResultMigration migration = new ChartResultMigration();
    Object obj = migration.parseData(jsonNode);
    LOGGER.debug("Start Chart Controller");
    return obj;
  }
}
