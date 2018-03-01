package com.synchronoss.saw.workbench.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class WorkbenchExecutionServiceImpl
    implements WorkbenchExecutionService {
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
  
    @Value("${workbench.project-key}")
    @NotNull
    private String project;

    @Value("${workbench.project-root}")
    @NotNull
    private String root;
  
    @Value("${workbench.livy-uri}")
    @NotNull
    private String livyUri;

    @Override
    public String execute(String component, String config) throws Exception {
        WorkbenchClient client = new WorkbenchClient();
        client.submit(livyUri, root, project, component, config);
        return "{}";
    }
}
