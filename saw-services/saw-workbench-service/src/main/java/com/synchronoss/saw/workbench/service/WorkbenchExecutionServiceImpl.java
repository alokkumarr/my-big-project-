package com.synchronoss.saw.workbench.service;

import java.util.List;

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
        XDFContextProvider ngCtx =
            new XDFContextProvider(root, project, component, config);
        /* createDatasetDirectory(name); */
        WorkbenchExecuteJob  workbenchExecuteJob =
            new WorkbenchExecuteJob(ngCtx.getNGContext());
        List<String> ids = ngCtx.getDataSetIDs();

//        ArrayNode nodes = mapper.createArrayNode();
//        ids.forEach(id -> nodes.add(id));

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseRoot = mapper.createObjectNode();
        responseRoot.put("id", ids.get(0));

        client.submit(livyUri, workbenchExecuteJob);

        return responseRoot;
        //createDatasetDirectory(name);
    }

    private void createDatasetDirectory(String name) throws Exception {
        String path = root + "/" + project + "/dl/fs/data/" + name + "/data";
        if (!HFileOperations.exists(path)) {
            HFileOperations.createDir(path);
        }
    }
}
