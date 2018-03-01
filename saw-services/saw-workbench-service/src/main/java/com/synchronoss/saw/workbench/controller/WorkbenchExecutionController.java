package com.synchronoss.saw.workbench.controller;

import com.synchronoss.saw.workbench.service.WorkbenchExecutionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/internal/workbench/")
public class WorkbenchExecutionController {
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Value("${workbench.project-root}")
    @NotNull
    private String defaultProjectRoot;

    @Value("${workbench.project-path}")
    @NotNull
    private String defaultProjectPath;

    @Autowired
    private WorkbenchExecutionService workbenchExecutionService;
  
    @RequestMapping(value = "datasets", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String create(@RequestBody ObjectNode body)
        throws JsonProcessingException, Exception {
        log.debug("Create dataset");
        String component = body.path("component").asText();
        String rawDirectory = defaultProjectRoot + defaultProjectPath;
        JsonNode configNode = body.path("configuration");
        if (!configNode.isObject()) {
            throw new RuntimeException(
                "Expected config to be an object: " + configNode);
        }
        ObjectNode config = (ObjectNode) configNode;
        String file = config.path("file").asText();
        config.put("file", rawDirectory + "/" + file);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode xdfConfig = mapper.createObjectNode();
        xdfConfig.put("parser", config);
        ArrayNode outputs = xdfConfig.putArray("outputs");
        ObjectNode dataset = outputs.addObject();
        dataset.put("dataSet", "test");
        return workbenchExecutionService.execute(
            component, xdfConfig.toString());
    }
}
