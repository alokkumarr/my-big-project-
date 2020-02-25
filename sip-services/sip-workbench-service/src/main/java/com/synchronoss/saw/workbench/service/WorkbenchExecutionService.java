package com.synchronoss.saw.workbench.service;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WorkbenchExecutionService {
  /**
   * Start executing transformation component on given dataset asynchronously.
   */
  ObjectNode execute(String project, String name, String component, String config) throws Exception;


  /**
   * Get dataset preview data by dataset name.
   */
  ObjectNode getPreview(String project, String name) throws Exception;
  /**
   * Get dataset preview data by preview ID.
   */
  String createDatasetDirectory(String project, String catalog, String name) throws Exception;
  
}
