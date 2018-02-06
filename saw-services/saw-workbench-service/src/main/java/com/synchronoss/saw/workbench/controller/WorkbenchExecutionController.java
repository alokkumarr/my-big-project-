package com.synchronoss.saw.workbench.controller;

import com.synchronoss.saw.workbench.service.WorkbenchExecutionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/workbench/")
public class WorkbenchExecutionController {
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private WorkbenchExecutionService workbenchExecutionService;
  
    @RequestMapping(value = "datasets", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String create(@RequestBody ObjectNode conf)
        throws JsonProcessingException, Exception {
        log.debug("Create dataset");
        return workbenchExecutionService.execute(conf.toString());
    }
}
