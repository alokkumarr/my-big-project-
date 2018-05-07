package com.synchronoss.saw.semantic.service;

import java.util.UUID;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;

public interface SemanticService {

  String delimiter = "::";
  String SemanticDataSet = "semanticDataSet";
  String nodeCategoryConvention = "SemanticNode";
  public SemanticNode addSemantic(SemanticNode node) throws JSONValidationSAWException, CreateEntitySAWException;
  public SemanticNode getDashboardbyCriteria(SemanticNode node) throws JSONValidationSAWException, ReadEntitySAWException;
  public SemanticNode updateSemantic(SemanticNode node) throws JSONValidationSAWException, UpdateEntitySAWException;
  public SemanticNode deleteSemantic(SemanticNode node) throws JSONValidationSAWException, DeleteEntitySAWException;
  public SemanticNodes search(SemanticNode node) throws JSONValidationSAWException, ReadEntitySAWException;
  default String generateId() throws JSONValidationSAWException{
    String id = UUID.randomUUID().toString() + delimiter + SemanticDataSet + delimiter
        + System.currentTimeMillis();
    return id;
  }
}

