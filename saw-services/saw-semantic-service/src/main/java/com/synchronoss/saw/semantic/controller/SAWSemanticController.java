package com.synchronoss.saw.semantic.controller;

import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.semantic.SAWSemanticUtils;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import com.synchronoss.saw.semantic.service.SemanticService;


/**
 * @author spau0004 This class is used to perform CRUD operation for the semantic metadata The
 *         requests are JSON documents in the following formats
 */
@RestController
@RequestMapping("/internal/semantic/")
public class SAWSemanticController {

  private static final Logger logger = LoggerFactory.getLogger(SAWSemanticController.class);

  @Autowired
  private SemanticService semanticService;

  /**
   * This method is used to create a semantic entity in mapr store with id
   * 
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/{projectId}/create", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public SemanticNode addSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @RequestBody SemanticNode requestBody) throws JSONMissingSAWException {
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    SAWSemanticUtils.checkMandatoryFields(requestBody);
    logger.trace("Request Body to create a semantic node:{}", requestBody);
    SemanticNode responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      requestBody.setProjectCode(projectId);
      requestBody.set_id(semanticService.generateId());
      logger.trace("Invoking service with entity id : {} ", requestBody.get_id());
      responseObjectFuture = semanticService.addSemantic(requestBody);
      logger.trace("Semantic entity created : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (CreateEntitySAWException | JsonProcessingException ex) {
      throw new CreateEntitySAWException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to read a semantic entity in mapr store with id
   * 
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/{projectId}/{Id}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SemanticNode readSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @PathVariable(name = "Id", required = true) String Id) throws JSONMissingSAWException {
    logger.trace("Request Body to read a semantic node:{}", Id);
    SemanticNode responseObjectFuture = null;
    SemanticNode node = new SemanticNode();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      node.set_id(Id);
      logger.trace("Invoking service with entity id : {} ", node.get_id());
      responseObjectFuture = semanticService.readSemantic(node);
      logger.trace("Semantic retrieved successfully : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (ReadEntitySAWException | JsonProcessingException ex) {
      throw new ReadEntitySAWException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to update a semantic entity in mapr store with id
   * 
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/{projectId}/{Id}", method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.FOUND)
  public SemanticNode updateSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @PathVariable(name = "Id", required = true) String Id, @RequestBody SemanticNode requestBody) throws JSONMissingSAWException {
    logger.trace("Request Body to update a semantic node:{}", Id);
    SemanticNode responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      requestBody.set_id(Id);
      logger.trace("Invoking service with entity id : {} ", requestBody.get_id());
      responseObjectFuture = semanticService.updateSemantic(requestBody);
      logger.trace("Semantic updateded successfully : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (UpdateEntitySAWException | JsonProcessingException ex) {
      throw new UpdateEntitySAWException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to delete a semantic entity in mapr store with id
   * 
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/{projectId}/{Id}", method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public SemanticNode deleteSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @PathVariable(name = "Id", required = true) String Id) throws JSONMissingSAWException {
    logger.trace("Request Body to delete a semantic node:{}", Id);
    SemanticNode responseObjectFuture = null;
    SemanticNode node = new SemanticNode();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      node.set_id(Id);
      logger.trace("Invoking service with entity id : {} ", node.get_id());
      responseObjectFuture = semanticService.deleteSemantic(node);
      logger.trace("Semantic deleted successfully : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (DeleteEntitySAWException | JsonProcessingException ex) {
      throw new DeleteEntitySAWException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to filter a semantic entity in mapr store with id
   * 
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/{projectId}/filter", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public SemanticNodes searchSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @RequestParam Map<String, String> queryMap) throws JSONMissingSAWException {
    SemanticNodes responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      SemanticNode requestBody =
          objectMapper.readValue(objectMapper.writeValueAsString(queryMap), SemanticNode.class);
      logger.trace("Search Request Body : {} ", objectMapper.writeValueAsString(requestBody));
      responseObjectFuture = semanticService.search(requestBody);
      logger.trace("Search Semantic Result : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (ReadEntitySAWException | JsonProcessingException ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", ex);
    } catch (IOException e) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", e);
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to list the semantic entities.
   * This will be deprecated after re-factoring of transport service
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @RequestMapping(value = "/md", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public BackCompatibleStructure searchSemanticWithOutArtifacts(
      @PathVariable(name = "projectId", required = true) String projectId,
      @RequestParam Map<String, String> queryMap) throws JSONMissingSAWException {
    BackCompatibleStructure responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      SemanticNode requestBody =
          objectMapper.readValue(objectMapper.writeValueAsString(queryMap), SemanticNode.class);
      logger.trace("Search Request Body : {} ", requestBody);
      responseObjectFuture = semanticService.list(requestBody);
      logger.trace("Search Semantic Result : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (ReadEntitySAWException | JsonProcessingException ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", ex);
    } catch (IOException e) {
      throw new ReadEntitySAWException("Problem on the storage while reading an entity", e);
    }
    return responseObjectFuture;
  }

}
