package com.synchronoss.saw.workbench.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sncr.bda.core.file.HFileOperations;


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
        WorkbenchClient client =
            new WorkbenchClient(component, root, project, config);
        //createDatasetDirectory(name);
        List<String> ids = client.getDataSetIDs();
        client.submit(livyUri);
        StringBuilder s = new StringBuilder();
        ids.forEach(id -> s.append((s.length() == 0) ? "[\"" : ",\"")
                            .append(id)
                            .append("\"")
        );
        s.append("]");
        return s.toString();
    }

    private void createDatasetDirectory(String name) throws Exception {
        String path = root + "/" + project + "/dl/fs/data/" + name + "/data";
        if (!HFileOperations.exists(path)) {
            HFileOperations.createDir(path);
        }
    }
}
