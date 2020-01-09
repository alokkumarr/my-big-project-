package com.synchronoss.saw.workbench.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WorkbenchService {
	
	Object executeJob(String root, String config, String project, String component, String batchID);
	ObjectNode createPreview(String id, String location, String previewLimit, String previewsTablePath, String project, String name);
	ObjectNode showPreview(String id, String location, String previewLimit, String previewsTablePath, String project, String name);
	

}
