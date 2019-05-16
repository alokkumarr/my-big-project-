package com.synchronoss.saw.semantic.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemanticNodes {

  private List<SemanticNode> semanticNodes;

  public List<SemanticNode> getSemanticNodes() {
    return semanticNodes;
  }

  public void setSemanticNodes(List<SemanticNode> semanticNodes) {
    this.semanticNodes = semanticNodes;
  }
}
