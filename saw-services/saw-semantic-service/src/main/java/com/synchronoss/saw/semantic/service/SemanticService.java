package com.synchronoss.saw.semantic.service;


import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;

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

  public SemanticNodes search(SemanticNode node, Map<String, String> headers)
      throws JSONValidationSAWException, ReadEntitySAWException;

  public BackCompatibleStructure list(SemanticNode node, Map<String, String> headers)
      throws JSONValidationSAWException, ReadEntitySAWException;

  /**
   * This is the method which generates Id & will be used in CRUD operation.
   * @return String Id for the row
   * @throws JSONValidationSAWException when JSON Parsing fails
   */
  default String generateId(String project, String metricName) throws JSONValidationSAWException {
    String id = project + delimiter + metricName;
    if (StringUtils.containsWhitespace(id)) {
      id = StringUtils.deleteWhitespace(id);
    }
    return id;
  }
}

