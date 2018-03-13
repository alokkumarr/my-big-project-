package com.synchronoss.saw.workbench.service;

import sncr.bda.core.file.HFileOperations;

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
    public String execute(String name, String component, String config)
        throws Exception {
        log.info("Workbench is about execute job");
        WorkbenchClient client = new WorkbenchClient();
        createDatasetDirectory(name);
        client.submit(livyUri, root, project, component, config);
        return "{}";
    }

    private void createDatasetDirectory(String name) throws Exception {
        String path = root + "/" + project + "/dl/fs/data/" + name + "/data";
        if (!HFileOperations.exists(path)) {
            HFileOperations.createDir(path);
        }
    }
}
