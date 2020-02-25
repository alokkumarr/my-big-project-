package com.synchronoss.saw.workbench.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WorkbenchJobService {
	
	Object executeJob(String root, String config, String project, String component, String batchID);
	

}
