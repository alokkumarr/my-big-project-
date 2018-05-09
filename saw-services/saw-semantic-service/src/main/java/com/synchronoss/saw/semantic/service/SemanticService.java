package com.synchronoss.saw.semantic.service;

import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import java.util.UUID;

public interface SemanticService {

  String delimiter = "::";
  String SemanticDataSet = "semanticDataSet";
  String nodeCategoryConvention = "SemanticNode";

  public SemanticNode addSemantic(SemanticNode node)
      throws JSONValidationSAWException, CreateEntitySAWException;

  public SemanticNode readSemantic(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException;

  public SemanticNode updateSemantic(SemanticNode node)
      throws JSONValidationSAWException, UpdateEntitySAWException;

  public SemanticNode deleteSemantic(SemanticNode node)
      throws JSONValidationSAWException, DeleteEntitySAWException;

  public SemanticNodes search(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException;

  public SemanticNodes list(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException;

  /**
   * This is the method which generates Id & will be used in CRUD operation.
   * @return String Id for the row
   * @throws JSONValidationSAWException when JSON Parsing fails
   */
  default String generateId() throws JSONValidationSAWException {
    String id = UUID.randomUUID().toString() + delimiter + SemanticDataSet + delimiter
        + System.currentTimeMillis();
    return id;
  }
}

