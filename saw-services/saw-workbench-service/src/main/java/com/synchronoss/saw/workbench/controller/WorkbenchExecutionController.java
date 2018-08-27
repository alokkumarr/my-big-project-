package com.synchronoss.saw.workbench.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.workbench.service.WorkbenchExecutionService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Base64;
import java.util.Map;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.component.Component;

@RestController
@RequestMapping("/internal/workbench/projects/")
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

  /**
   * Create dataset function.
   * @param project Project ID
   * @param body Body Parameters
   * @param authToken Authentication Token header
   * @return Returns an object node
   * @throws JsonProcessingException When unable to decode the string
   * @throws Exception General Exception
   */
  @RequestMapping(
      value = "{project}/datasets", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ObjectNode create(
      @PathVariable(name = "project", required = true) String project,
      @RequestBody ObjectNode body,
      @RequestHeader("Authorization") String authToken)
      throws JsonProcessingException, Exception {
    log.info("Create dataset: body = {}", body);
    log.debug("Create dataset: project = {}", project);
    log.debug("Auth token = {}", authToken);
    if (authToken.startsWith("Bearer")) {
      authToken = authToken.substring("Bearer ".length());
    }
    /* Extract input parameters */
    final String name = body.path("name").asText();
    final String description = body.path("description").asText();
    String input = body.path("input").asText();

    //TODO: Remove the hardcoded key
    Claims ssoToken = Jwts.parser().setSigningKey("sncrsaw2")
        .parseClaimsJws(authToken).getBody();

    Map<String, Object> ticket =
        ((Map<String, Object>) ssoToken.get("ticket"));
    String userName = (String) ticket.get("userFullName");
    log.info(userName);

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
    xdfOutput.put("name", Component.DATASET.output.name());
    xdfOutput.put("desc", description);

    ObjectNode userData = mapper.createObjectNode();
    userData.put(DataSetProperties.createdBy.toString(), userName);


    xdfOutput.set(DataSetProperties.UserData.toString(), userData);
    /* Invoke XDF component */
    return workbenchExecutionService.execute(
      project, name, component, xdfConfig.toString());
  }

  /**
   * Preview dataset function.
   *
   * @param project Project ID
   * @param body Body Parameters
   * @return Returns a preview
   * @throws JsonProcessingException When not able to get the preview
   * @throws Exception General Exception
   */

  @RequestMapping(value = "{project}/previews", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ObjectNode preview(@PathVariable(name = "project", required = true) String project,
                          @RequestBody ObjectNode body) throws JsonProcessingException, Exception {
    log.debug("Create dataset preview: project = {}", project);
    /* Extract dataset name which is to be previewed */
    String name = body.path("name").asText();
    /* Start asynchronous preview creation */
    return workbenchExecutionService.preview(project, name);
  }

  /**
   * This method is to preview the data.
   * @param project is of type String.
   * @param previewId is of type String.
   * @return ObjectNode is of type Object.
   * @throws JsonProcessingException when this exceptional condition happens.
   * @throws Exception when this exceptional condition happens.
   */
  @RequestMapping(value = "{project}/previews/{previewId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ObjectNode preview(@PathVariable(name = "project", required = true) String project,
                            @PathVariable(name = "previewId", required = true) String previewId)
      throws JsonProcessingException, Exception {
    log.debug("Get dataset preview: project = {}", project);
    /* Get previously created preview */
    ObjectNode body = workbenchExecutionService.getPreview(previewId);
    /*
     * If preview was not found, response to indicate that preview has not been created yet
     */
    if (body == null) {
      throw new NotFoundException();
    }
    /* Otherwise return the preview contents */
    return body;
  }
  /**
   * This method is to preview the data.
   * @param project is of type String.
   * @param previewId is of type String.
   * @return ObjectNode is of type Object.
   * @throws JsonProcessingException when this exceptional condition happens.
   * @throws Exception when this exceptional condition happens.
   */
  @RequestMapping(value = "{project}/{name}/datapath", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public String generatePath(@PathVariable(name = "project", required = true) String project,
                            @PathVariable(name = "name", required = true) String name, @RequestParam Map<String, String> queryMap)
      throws JsonProcessingException, Exception {
    log.debug("Get dataset preview: project = {}", project);
    /* Get previously created preview */
    String catalog = queryMap.get("catalog");
    String body = workbenchExecutionService.createDatasetDirectory(project, catalog, name);
    /*
     * If preview was not found, response to indicate that preview has not been created yet
     */
    if (body == null) {
      throw new NotFoundException();
    }
    /* Otherwise return the preview contents */
    return body;
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Preview does not exist")
  private static class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 412355610432444770L;
  }
}