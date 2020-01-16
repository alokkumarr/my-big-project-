package com.synchronoss.saw.workbench.executor.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WorkbenchExecutorService {


	ObjectNode executeJob(String project, String name, String component, String cfg);

	String createDatasetDirectory(String project, String catalog, String name) throws Exception;
	
	/**
	   * Start creating preview of given dataset asynchronously.
	   */
	  ObjectNode createPreview(String id, String location, int previewLimit, String previewsTablePath, String project, String name) throws Exception;

	  /**
	   * Get dataset preview data by preview ID.
	   */
	  ObjectNode getPreview(String previewId, String name, String component, String cfg) throws Exception;
}
