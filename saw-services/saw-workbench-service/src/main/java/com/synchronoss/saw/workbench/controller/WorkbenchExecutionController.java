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

import java.util.Base64;
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
        /* Extract input parameters */
        String name = body.path("name").asText();
        String input = body.path("input").asText();
        String component = body.path("component").asText();
        JsonNode configNode = body.path("configuration");
        if (!configNode.isObject()) {
            throw new RuntimeException(
                "Expected config to be an object: " + configNode);
        }
        ObjectNode config = (ObjectNode) configNode;
        /* Build XDF component-specific configuration */
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode xdfConfig = mapper.createObjectNode();
        ObjectNode xdfComponentConfig = xdfConfig.putObject(component);
        if (component.equals("parser")) {
            String rawDirectory = defaultProjectRoot + defaultProjectPath;
            String file = config.path("file").asText();
            config.put("file", rawDirectory + "/" + file);
            xdfConfig.set(component, config);
        } else if (component.equals("sql")) {
            xdfComponentConfig.put("scriptLocation", "inline");
            String script = config.path("script").asText();
            String encoded = Base64.getEncoder()
                .encodeToString(script.getBytes("utf-8"));
            xdfComponentConfig.put("script", encoded);
        } else {
            throw new RuntimeException("Unknown component: " + component);
        }
        /* Build inputs */
        if (!component.equals("parser")) {
            ArrayNode xdfInputs = xdfConfig.putArray("inputs");
            ObjectNode xdfInput = xdfInputs.addObject();
            xdfInput.put("dataSet", input);
        }
        /* Build outputs */
        ArrayNode xdfOutputs = xdfConfig.putArray("outputs");
        ObjectNode xdfOutput = xdfOutputs.addObject();
        xdfOutput.put("dataSet", name);
        /* Invoke XDF component */
        return workbenchExecutionService.execute(
            component, xdfConfig.toString());
    }
}
