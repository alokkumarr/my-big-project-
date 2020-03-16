package com.synchronoss.saw.workbench.executor.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WorkbenchExecutor {


	ObjectNode executeJob(String project, String name, String component, String cfg);

}
