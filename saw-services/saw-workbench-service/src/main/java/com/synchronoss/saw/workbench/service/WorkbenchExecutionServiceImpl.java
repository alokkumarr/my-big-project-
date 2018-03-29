package com.synchronoss.saw.workbench.service;

import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mapr.db.Admin;
import com.mapr.db.FamilyDescriptor;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import com.mapr.db.TableDescriptor;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sncr.bda.core.file.HFileOperations;
import sncr.bda.metastore.DataSetStore;

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

    @Value("${workbench.preview-limit}")
    @NotNull
    private Integer previewLimit;

    private static final String PREVIEWS_TABLE = "/previews";

    @PostConstruct
    private void init() throws Exception {
        String path = PREVIEWS_TABLE;
        try (Admin admin = MapRDB.newAdmin()) {
            if (!admin.tableExists(path)) {
                log.info("Creating previews table: {}", path);
                TableDescriptor table =
                    MapRDB.newTableDescriptor(path);
                FamilyDescriptor family =
                    MapRDB.newDefaultFamilyDescriptor().setTTL(3600);
                table.addFamily(family);
                admin.createTable(table).close();
            }
        }
    }

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
        client.submit(livyUri, new WorkbenchExecuteJob(
                          root, project, component, config));
        ObjectNode root = mapper.createObjectNode();
        return root;
    }

    private void createDatasetDirectory(String name) throws Exception {
        String path = root + "/" + project + "/dl/fs/data/" + name + "/data";
        if (!HFileOperations.exists(path)) {
            HFileOperations.createDir(path);
        }
    }

    @Value("${metastore.base}")
    @NotNull
    private String metastoreBase;

    /**
     * Preview the output of a executing a transformation component on
     * a dataset
     * 
     * Also used for simply viewing the contents of an existing
     * dataset.
     */
    @Override
    public ObjectNode preview(String project, String name) throws Exception {
        log.info("Create dataset transformation preview");
        /* Get physical location of dataset */
        DataSetStore dss = new DataSetStore(metastoreBase);
        String json = dss.readDataSet(project, name);
        log.debug("Dataset metadata: {}", json);
        JsonNode dataset = mapper.readTree(json);
        String location = dataset.path("system")
            .path("physicalLocation").asText();
        /* Submit job to Livy for reading out preview data */
        WorkbenchClient client = new WorkbenchClient();
        String id = UUID.randomUUID().toString();
        client.submit(
            livyUri, new WorkbenchPreviewJob(id, location, previewLimit));
        /* Return generated preview ID to client to be used for
         * retrieving preview data */
        ObjectNode root = mapper.createObjectNode();
        root.put("id", id);
        return root;
    }

    @Override
    public ObjectNode getPreview(String previewId) throws Exception {
        log.info("Get dataset transformation preview");
        /* Locate the preview data in MapR-DB */
        Table table = MapRDB.getTable(PREVIEWS_TABLE);
        Document doc = table.findById(previewId);
        /* Return the preview data */
        if (doc == null) {
            return null;
        }
        JsonNode json = mapper.readTree(doc.toString());
        return (ObjectNode) json;
    }
}
