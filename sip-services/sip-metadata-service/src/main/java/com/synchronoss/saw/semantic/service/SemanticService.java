package com.synchronoss.saw.semantic.service;

import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipJsonValidationException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;

import com.synchronoss.saw.semantic.model.request.BackCompatibleStructure;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNodes;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public interface SemanticService {

  String delimiter = "::";
  String SemanticDataSet = "semanticDataSet";
  String nodeCategoryConvention = "SemanticNode";

  SemanticNode addSemantic(SemanticNode node)
      throws SipJsonValidationException, SipCreateEntityException;

  SemanticNode readSemantic(SemanticNode node)
      throws SipJsonValidationException, SipReadEntityException;

  SemanticNode updateSemantic(SemanticNode node, Map<String, String> headers)
      throws SipJsonValidationException, SipUpdateEntityException;

  SemanticNode deleteSemantic(SemanticNode node)
      throws SipJsonValidationException, SipDeleteEntityException;

  SemanticNodes search(SemanticNode node, Map<String, String> headers)
      throws SipJsonValidationException, SipReadEntityException;

  BackCompatibleStructure list(SemanticNode node, Map<String, String> headers)
      throws SipJsonValidationException, SipReadEntityException;

  Boolean addDskToSipSecurity(SemanticNode node, HttpServletRequest request);

  Boolean updateDskInSipSecurity(SemanticNode node, HttpServletRequest request);

  Boolean deleteDskInSipSecurity(String semanticId, HttpServletRequest request);

  /**
   * This is the method which generates Id & will be used in CRUD operation.
   *
   * @return String Id for the row
   * @throws SipJsonValidationException when JSON Parsing fails
   */
  default String generateId(String project, String metricName) throws SipJsonValidationException {
    String id = project + delimiter + metricName;
    if (StringUtils.containsWhitespace(id)) {
      id = StringUtils.deleteWhitespace(id);
    }
    return id;
  }
}
