package com.synchronoss.saw.workbench.service;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sncr.bda.core.file.HFileOperations;

@Service
public class WorkbenchExecutionServiceImpl
    implements WorkbenchExecutionService {
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();
  
    @Value("${workbench.project-key}")
    @NotNull
    private String project;

    @Value("${workbench.project-root}")
    @NotNull
    private String root;
  
    @Value("${workbench.livy-uri}")
    @NotNull
    private String livyUri;

    /**
     * Execute a transformation component on a dataset to create a new
     * dataset
     */
    @Override
    public ObjectNode execute(
        String project, String name, String component, String config)
        throws Exception {
        log.info("Execute dataset transformation");
        WorkbenchClient client = new WorkbenchClient();
        createDatasetDirectory(name);
        client.submitExecute(livyUri, root, project, component, config);
        ObjectNode root = mapper.createObjectNode();
        return root;
    }

    private void createDatasetDirectory(String name) throws Exception {
        String path = root + "/" + project + "/dl/fs/data/" + name + "/data";
        if (!HFileOperations.exists(path)) {
            HFileOperations.createDir(path);
        }
    }
}
