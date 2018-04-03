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

    /**
     * Cached Workbench Livy client to be kept around for next
     * operation to reduce startup time.
     */
    private WorkbenchClient cachedClient;

    @PostConstruct
    private void init() throws Exception {
        /* Initialize the previews MapR-DB table */
        String path = PreviewBuilder.PREVIEWS_TABLE;
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
        /* Cache a Workbench Livy client to reduce startup time for
         * first operation */
        cacheWorkbenchClient();
    }

    /**
     * Get Workbench Livy client to be used for Livy jobs
     */
    private WorkbenchClient getWorkbenchClient() throws Exception {
        /* Synchronize access to the cached client to ensure that the
         * current client is handed out only to a single caller and
         * that a new client is put into place before the next
         * caller */
        synchronized (cachedClient) {
            try {
                if (cachedClient == null) {
                    log.debug("Create Workbench Livy client on demand");
                    cacheWorkbenchClient();
                }
                return cachedClient;
            } finally {
                /* Create a new Workbench Livy client for the next
                 * operation */
                cacheWorkbenchClient();
            }
        }
    }

    private void cacheWorkbenchClient() throws Exception {
        log.debug("Caching Workbench Livy client");
        cachedClient = new WorkbenchClient(livyUri);
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
        WorkbenchClient client = getWorkbenchClient();
        createDatasetDirectory(name);
        client.submit(new WorkbenchExecuteJob(
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
        WorkbenchClient client = getWorkbenchClient();
        String id = UUID.randomUUID().toString();
        client.submit(new WorkbenchPreviewJob(id, location, previewLimit));
        PreviewBuilder preview = new PreviewBuilder(id, "queued");
        preview.insert();
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
        Table table = MapRDB.getTable(PreviewBuilder.PREVIEWS_TABLE);
        Document doc = table.findById(previewId);
        /* Return the preview data */
        if (doc == null) {
            return null;
        }
        JsonNode json = mapper.readTree(doc.toString());
        return (ObjectNode) json;
    }
}
