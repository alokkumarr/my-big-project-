package com.synchronoss.saw.storage.proxy.controller;

import com.synchronoss.saw.storage.proxy.service.executionResultMigrationService.MigrateAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// TODO: 4/4/2019 - This controller is only for testing, need to be removed after the testing
@RestController
public class ChartController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChartController.class);

  @Autowired MigrateAnalysisService migrateAnalysisService;

  @RequestMapping(value = "/internal/proxy/testHbase", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public Object testHbaseConnection() {
    LOGGER.info("Start HBase Connection");

    migrateAnalysisService.convertBinaryStoreToDslJsonStore();

    LOGGER.info("Start HBase Connection");
    return "HBase Connection Response";
  }
}
