package com.synchronoss.saw.semantic.service;


import com.synchronoss.saw.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public interface SemanticService {

  String delimiter = "::";
  String SemanticDataSet = "semanticDataSet";
  String nodeCategoryConvention = "SemanticNode";

  SemanticNode addSemantic(SemanticNode node)
      throws JSONValidationSAWException, CreateEntitySAWException;

  SemanticNode readSemantic(SemanticNode node)
      throws JSONValidationSAWException, ReadEntitySAWException;

  SemanticNode updateSemantic(SemanticNode node)
      throws JSONValidationSAWException, UpdateEntitySAWException;

  SemanticNode deleteSemantic(SemanticNode node)
      throws JSONValidationSAWException, DeleteEntitySAWException;

  SemanticNodes search(SemanticNode node, Map<String, String> headers)
      throws JSONValidationSAWException, ReadEntitySAWException;

  BackCompatibleStructure list(SemanticNode node, Map<String, String> headers)
      throws JSONValidationSAWException, ReadEntitySAWException;

  /**
   * This is the method which generates Id & will be used in CRUD operation.
   *
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
