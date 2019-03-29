package com.synchronoss.saw.semantic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipJsonMissingException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import com.synchronoss.saw.semantic.service.SemanticService;
import com.synchronoss.saw.util.SipMetadataUtils;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * This class is used to perform CRUD operation for the semantic metadata requests are JSON
 * documents in the following formats.
 *
 * @author spau0004
 */
@RestController
@RequestMapping("/internal/semantic/")
public class SipSemanticController {

  private static final Logger logger = LoggerFactory.getLogger(SipSemanticController.class);

  @Autowired private SemanticService semanticService;

  /** This method is used to create a semantic entity in mapr store with id. */
  @RequestMapping(
      value = "/{projectId}/create",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public SemanticNode addSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @RequestBody SemanticNode requestBody)
      throws SipJsonMissingException {
    if (requestBody == null) {
      throw new SipJsonMissingException("json body is missing in request body");
    }
    SipMetadataUtils.checkSemanticMandatoryFields(requestBody);
    logger.trace("Request Body to create a semantic node:{}", requestBody);
    SemanticNode responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      requestBody.setProjectCode(projectId);
      requestBody.set_id(semanticService.generateId(projectId, requestBody.getMetricName()));
      logger.trace("Invoking service with entity id : {} ", requestBody.get_id());
      responseObjectFuture = semanticService.addSemantic(requestBody);
      logger.trace(
          "Semantic entity created : {}", objectMapper.writeValueAsString(responseObjectFuture));
    } catch (SipCreateEntityException | JsonProcessingException ex) {
      throw new SipCreateEntityException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /** This method is used to read a semantic entity in mapr store with id. */
  @RequestMapping(
      value = "/{projectId}/{id}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SemanticNode readSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @PathVariable(name = "id", required = true) String id)
      throws SipJsonMissingException {
    logger.trace("Request Body to read a semantic node:{}", id);
    SemanticNode responseObjectFuture = null;
    SemanticNode node = new SemanticNode();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      node.set_id(id);
      logger.trace("Invoking service with entity id : {} ", node.get_id());
      responseObjectFuture = semanticService.readSemantic(node);
      logger.trace(
          "Semantic retrieved successfully : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (SipReadEntityException | JsonProcessingException ex) {
      throw new SipReadEntityException("Problem on the storage while creating an entity", ex);
    }
    return responseObjectFuture;
  }

  /** This method is used to update a semantic entity in mapr store with id. */
  @RequestMapping(
      value = "/{projectId}/{id}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SemanticNode updateSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @PathVariable(name = "id", required = true) String id,
      @RequestBody SemanticNode requestBody)
      throws SipJsonMissingException {
    logger.trace("Request Body to update a semantic node:{}", id);
    SemanticNode responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      requestBody.set_id(id);
      logger.trace("Invoking service with entity id : {} ", requestBody.get_id());
      responseObjectFuture = semanticService.updateSemantic(requestBody);
      logger.trace(
          "Semantic updateded successfully : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (SipUpdateEntityException | JsonProcessingException ex) {
      throw new SipUpdateEntityException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /** This method is used to delete a semantic entity in mapr store with id. */
  @RequestMapping(
      value = "/{projectId}/{id}",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SemanticNode deleteSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @PathVariable(name = "id", required = true) String id)
      throws SipJsonMissingException {
    logger.trace("Request Body to delete a semantic node:{}", id);
    SemanticNode responseObjectFuture = null;
    SemanticNode node = new SemanticNode();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      node.set_id(id);
      logger.trace("Invoking service with entity id : {} ", node.get_id());
      responseObjectFuture = semanticService.deleteSemantic(node);
      logger.trace(
          "Semantic deleted successfully : {}",
          objectMapper.writeValueAsString(responseObjectFuture));
    } catch (SipDeleteEntityException | JsonProcessingException ex) {
      throw new SipDeleteEntityException("Problem on the storage while creating an entity");
    }
    return responseObjectFuture;
  }

  /** This method is used to filter a semantic entity in mapr store with id. */
  @RequestMapping(
      value = "/{projectId}/filter",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SemanticNodes searchSemantic(
      @PathVariable(name = "projectId", required = true) String projectId,
      @RequestParam Map<String, String> queryMap,
      @RequestHeader Map<String, String> headers)
      throws SipJsonMissingException {
    SemanticNodes responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      SemanticNode requestBody =
          objectMapper.readValue(objectMapper.writeValueAsString(queryMap), SemanticNode.class);
      logger.trace("Search Request Body : {} ", objectMapper.writeValueAsString(requestBody));
      responseObjectFuture = semanticService.search(requestBody, headers);
      logger.trace(
          "Search Semantic Result : {}", objectMapper.writeValueAsString(responseObjectFuture));
    } catch (SipReadEntityException | JsonProcessingException ex) {
      throw new SipReadEntityException("Problem on the storage while reading an entity", ex);
    } catch (IOException e) {
      throw new SipReadEntityException("Problem on the storage while reading an entity", e);
    }
    return responseObjectFuture;
  }

  /**
   * This method is used to list the semantic entities. This will be deprecated after re-factoring
   * of transport service.
   */
  @RequestMapping(
      value = "/md",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public BackCompatibleStructure searchSemanticWithOutArtifacts(
      @RequestParam Map<String, String> queryMap, @RequestHeader Map<String, String> headers)
      throws SipJsonMissingException {
    BackCompatibleStructure responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      SemanticNode requestBody =
          objectMapper.readValue(objectMapper.writeValueAsString(queryMap), SemanticNode.class);
      logger.trace("Search Request Body : {} ", requestBody);
      responseObjectFuture = semanticService.list(requestBody, headers);
      logger.trace(
          "Search Semantic Result : {}", objectMapper.writeValueAsString(responseObjectFuture));
    } catch (SipReadEntityException | JsonProcessingException ex) {
      throw new SipReadEntityException("Problem on the storage while reading an entity", ex);
    } catch (IOException e) {
      throw new SipReadEntityException("Problem on the storage while reading an entity", e);
    }
    return responseObjectFuture;
  }
}
