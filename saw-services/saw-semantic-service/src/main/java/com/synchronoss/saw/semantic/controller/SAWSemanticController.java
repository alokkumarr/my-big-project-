package com.synchronoss.saw.semantic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.service.SemanticService;

/**
 * @author spau0004
 * This class is used to perform CRUD operation for the semantic metadata
 * The requests are JSON documents in the following formats
 */
@RestController
@RequestMapping("/internal/workbench/projects/")
public class SAWSemanticController {

  private static final Logger logger = LoggerFactory.getLogger(SAWSemanticController.class);

  @Autowired
  private SemanticService semanticService;
  
  /**
   * This method is used to create a semantic entity in mapr store with id
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  // TODO : Validation of schema is pending
  @RequestMapping(value = "/{projectId}/semantic/create", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public SemanticNode addSemantic(@PathVariable(name = "projectId", required = true) String projectId, @RequestBody SemanticNode requestBody) 
      throws JSONMissingSAWException {
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    logger.trace("Request Body to create a semantic node:{}", requestBody);
    SemanticNode responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
   try {
     requestBody.setProjectCode(projectId);
     requestBody.set_id(semanticService.generateId());
      logger.trace("Invoking service with entity id : {} ", requestBody.get_id());
      responseObjectFuture = semanticService.addSemantic(requestBody);
      logger.trace("Semantic entity created : {}", objectMapper.writeValueAsString(responseObjectFuture)); 
    }  catch (CreateEntitySAWException | JsonProcessingException ex) {
      throw new CreateEntitySAWException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

}
