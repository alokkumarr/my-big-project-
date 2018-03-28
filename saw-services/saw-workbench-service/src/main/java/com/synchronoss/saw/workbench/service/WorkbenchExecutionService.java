package com.synchronoss.saw.workbench.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WorkbenchExecutionService {
    ObjectNode execute(String name, String component, String config)
        throws Exception;
}
