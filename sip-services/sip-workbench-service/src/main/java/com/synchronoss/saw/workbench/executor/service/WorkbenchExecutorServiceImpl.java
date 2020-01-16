package com.synchronoss.saw.workbench.executor.service;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.synchronoss.sip.utils.RestUtil;

import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.services.NGContextServices;

public class WorkbenchExecutorServiceImpl implements WorkbenchExecutorService {


	  private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorServiceImpl.class);

	  @Value("${workbench.executor.streamPath}")
	  @NotNull
	  private String streamBasePath;

	  @Value("${workbench.executor.wait-time}")
	  private Integer dlReportWaitTime;

	  @Value("${workbench.executor.preview-output-location}")
	  @NotNull
	  private String previewOutputLocation;

	  @Value("${workbench.executor.publish-schedule-output-location}")
	  @NotNull
	  private String pubSchOutputLocation;

	  @Value("${workbench.execution.preview-rows-limit}")
	  private Integer dlPreviewRowLimit;

	  @Value("${workbench.metadata.service.host}")
	  private String metaDataServiceExport;
	  
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

		@Value("${workbench.project-root}/services/metadata/previews")
		@NotNull
		private String previewsTablePath;

	  @Autowired private RestUtil restUtil;
	  
	  private final Logger log = LoggerFactory.getLogger(getClass().getName());
	  private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public ObjectNode executeJob(String project, String name, String component, String cfg) {
	
//		createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);
		log.info("execute name = " + name);
		log.info("execute root = " + root);
		log.info("execute component = " + component);

		ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);

		log.info("Component Config = " + config);

		String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

		NGContextServices contextServices = new NGContextServices(root, config, project, component, batchID);
		contextServices.initContext();

		contextServices.registerOutputDataSet();

		NGContext workBenchcontext = contextServices.getNgctx();

		workBenchcontext.serviceStatus.put(ComponentServices.InputDSMetadata, true);
		WorkbenchExecutorQueuManager queueManager = new WorkbenchExecutorQueuManager(streamBasePath);
		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.EXECUTE_JOB.toString(), 
				project, name,component,cfg);
	    queueManager.sendWorkbenchMessageToStream(recordContent);
		ObjectNode root = mapper.createObjectNode();
		ArrayNode ids = root.putArray("outputDatasetIds");
		for (String id : workBenchcontext.registeredOutputDSIds) {
			ids.add(id);
		}
		log.info("Executing dataset transformation ends here ");
		
		return null;
		
	}

	
	/**
	 * Execute a transformation component on a dataset to create a new dataset.
	 */
	@Override
	public String createDatasetDirectory(String project, String catalog, String name) throws Exception {
		log.trace("generate data system path for starts here :" + project + " : " + name);
		String path = root + Path.SEPARATOR + project + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR + Path.SEPARATOR
				+ MetadataBase.PREDEF_DATA_SOURCE + Path.SEPARATOR + catalog + Path.SEPARATOR + name + Path.SEPARATOR
				+ MetadataBase.PREDEF_DATA_DIR;
		log.info("createDatasetDirectory path = " + path);
		if (!HFileOperations.exists(path)) {
			HFileOperations.createDir(path);
		}
		log.trace("generate data system path for starts here " + path);
		return path;
	}

	@Value("${metastore.base}")
	@NotNull
	private String metastoreBase;

	


	@Override
	public ObjectNode getPreview(String previewId, String name, String component, String cfg) throws Exception {
		WorkbenchExecutorQueuManager queueManager = new WorkbenchExecutorQueuManager(streamBasePath);

		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.SHOW_PREVIEW.toString(), project, name,
				component, cfg);
	    queueManager.sendWorkbenchMessageToStream(recordContent);
		return null;
	}


	@Override
	public ObjectNode createPreview(String id, String location, int previewLimit, String previewsTablePath, String project,
			String name) throws Exception {
		WorkbenchExecutorQueuManager queueManager = new WorkbenchExecutorQueuManager(streamBasePath);
		//String id = UUID.randomUUID().toString();
		//String location = createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);

		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.CREATE_PREVIEW.toString(), id, location, previewLimit, previewsTablePath, 
				project, name);
	    queueManager.sendWorkbenchMessageToStream(recordContent);
		
		return null;
	}
	
	  
}
