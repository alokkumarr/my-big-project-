package com.synchronoss.saw.workbench.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.mapr.db.Admin;
import com.mapr.db.FamilyDescriptor;
import com.mapr.db.MapRDB;
import com.mapr.db.TableDescriptor;
import com.synchronoss.saw.workbench.SparkConfig;
import com.synchronoss.saw.workbench.executor.service.WorkbenchExecutor;
import com.synchronoss.saw.workbench.executor.service.WorkbenchExecutorImpl;

import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.metastore.DataSetStore;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.services.NGContextServices;


@Service
public class WorkbenchExecutionServiceImpl implements WorkbenchExecutionService {
  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  private final ObjectMapper mapper = new ObjectMapper();
  
  

  @Value("${workbench.project-key}")
  @NotNull
  private String project;

  @Value("${workbench.project-root}")
  @NotNull
  private String root;

  @Value("${workbench.preview-limit}")
  @NotNull
  private Integer previewLimit;

  @Value("${workbench.project-root}/services/metadata/previews")
  @NotNull
  private String previewsTablePath;
  
  @Autowired
  WorkbenchExecutor executor;
  
  @Autowired
  SparkConf sparkConf;

 

  @PostConstruct
  private void init() throws Exception {
	  log.info("#### Inside Post Construct ####");
      /* Workaround: If the "/apps/spark" directory does not exist in
       * the data lake, Apache Livy will fail with a file not found
       * error.  So create the "/apps/spark" directory here.  */
      String appsSparkPath = "/apps/spark";
      if (!HFileOperations.exists(appsSparkPath)) {
          HFileOperations.createDir(appsSparkPath);
      }

    /* Initialize the previews MapR-DB table */
    try (Admin admin = MapRDB.newAdmin()) {
      if (!admin.tableExists(previewsTablePath)) {
        log.info("Creating previews table: {}", previewsTablePath);
        TableDescriptor table = MapRDB.newTableDescriptor(previewsTablePath);
        FamilyDescriptor family = MapRDB.newDefaultFamilyDescriptor().setTTL(3600);
        table.addFamily(family);
        admin.createTable(table).close();
      }
    }
    
  }

  

  
  /**
   * Execute a transformation component on a dataset to create a new dataset.
   */
  @Override
  public ObjectNode execute(
    String project, String name, String component, String cfg) throws Exception {
	  
    log.debug("Executing dataset transformation starts here ");
    log.debug("XDF Configuration = " + cfg);
    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
    log.info("Component Config = " + config);
    String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

    NGContextServices contextServices = new NGContextServices(root, config, project, component, batchID);
    contextServices.initContext();

    contextServices.registerOutputDataSet();

    NGContext workBenchcontext = contextServices.getNgctx();

    workBenchcontext.serviceStatus.put(ComponentServices.InputDSMetadata, true);
    executor.executeJob(project, name, component, cfg);
    
    ObjectNode root = mapper.createObjectNode();
    ArrayNode ids = root.putArray("outputDatasetIds");
    for (String id: workBenchcontext.registeredOutputDSIds) {
      ids.add(id);
    }
    log.info("Executing dataset transformation ends here ");
    return root;
  }

  /**
   * Execute a transformation component on a dataset to create a new dataset.
   */
  @Override
  public String createDatasetDirectory(String project, String catalog, String name) throws Exception {
    log.trace("generate data system path for starts here :" + project + " : " + name); 
    String path = root + Path.SEPARATOR + project + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR
        + Path.SEPARATOR + MetadataBase.PREDEF_DATA_SOURCE + Path.SEPARATOR
        + catalog + Path.SEPARATOR + name + Path.SEPARATOR
        + MetadataBase.PREDEF_DATA_DIR;
    log.info("createDatasetDirectory path = " + path);
    if (!HFileOperations.exists(path)) {
      HFileOperations.createDir(path);
    }
    log.trace("generate data system path for starts here " + path);
    return path;
  }



  @Override
  public ObjectNode getPreview(String project, String name) throws Exception {
    log.info("Getting dataset transformation preview");
    log.info("Project ::"+project);
    log.info("Name ::"+name);
    String location = createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);
    log.info("location ::"+ location);
    String jsonData = getDataset(location);
    log.info("#### Extracted DATA ######"+ jsonData);
    ObjectNode root = mapper.createObjectNode();
    root.put("status", "success");
    root.put("rows", jsonData);
    return root;
  }
  
  
	public String getDataset(String location) {
		log.info("Inside getDataset ####");
		SparkConfig sparkConfig = new SparkConfig();
		SparkSession session = SparkSession.builder().config(sparkConf).getOrCreate();

		log.info("Spark config completed ####" + session);

		log.info("Inside try ####");
		FileStatus[] files = null;
		try {
			files = HFileOperations.getFilesStatus(location);
		} catch (Exception exception) {
			log.error("Error while reading hadoop file status"+ exception.getMessage());
		}
		log.info("Retrived hadoop files.... ####");
		String contents = null;
		for (FileStatus file : files) {
			log.info("######### File Path ::" + file.getPath());
			log.info("######### File Name ::" + file.getPath().getName());
			String fileName = file.getPath().getName();

			if (fileName.endsWith(".parquet")) {
				log.info("##### reading file ::" + file.getPath().toString());
				try {

					Dataset<Row> parquetFileDF = session.read().
							parquet(file.getPath().toString());

					List<String> list = parquetFileDF.
							limit(previewLimit).toJSON().collectAsList();
					log.info("############After applying limit dataset count ::"+ list.size());
					List<Map<String, Object>> result = new ArrayList<>();
					Map<String, Object> map = new HashMap<>();
					for (String s : list) {

						map = mapper.readValue(s, 
								new TypeReference<Map<String, String>>() {
						});
						result.add(map);
					}
					contents = mapper.writeValueAsString(result);
					
				} catch (Exception exception) {
					log.error("#######ERROR reading file " 
				        + file.getPath() + exception.getMessage());
				}
				// As we expect only one parquet file break from iteration
				break;
			}

		}
		log.info("#### outside of payload inside try ::");
		return contents;
	}
}
