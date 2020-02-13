package com.synchronoss.saw.workbench.executor.service;

import javax.validation.constraints.NotNull;

import org.apache.hadoop.fs.Path;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.synchronoss.saw.workbench.service.WorkbenchExecutorQueue;
import com.synchronoss.sip.utils.RestUtil;

import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;

@Service
public class WorkbenchExecutorImpl implements WorkbenchExecutor {

	private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorImpl.class);
	
	@Value("${workbench.project-root}")
	@NotNull
	private String root;
	
	@Value("${workbench.project-key}")
	@NotNull
	private String project;

	////@Value("${workbench.executor.streamPath}")
	//@NotNull
	/*private String streamBasePath;

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

	

	

	@Value("${workbench.livy-uri}")
	@NotNull
	private String livyUri;

	@Value("${workbench.preview-limit}")
	@NotNull
	private Integer previewLimit;

	@Value("${workbench.project-root}/services/metadata/previews")
	@NotNull
	private String previewsTablePath;*/

	@Autowired
	private RestUtil restUtil;
	@Autowired
	WorkbenchExecutorQueue queueManager;
	@Autowired
	private ApplicationContext appContext;
	
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public ObjectNode executeJob(String project, String name, String component, String cfg) {

//		createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);
		logger.info("execute name = " + name);
		//logger.info("execute root = " + root);
		logger.info("execute component = " + component);
		logger.debug("######Tryging deserialize with gson for config::" + cfg);
		/*ComponentConfiguration config = null;
		try {
			config = new Gson().fromJson(cfg, ComponentConfiguration.class);
		} catch (Exception e) {
			logger.error("#######Exception during configuration reading as JSON" + e.getMessage());
		}*/

		// log.info("Component Config = " + config);

		String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

		logger.debug("######Instantiating Queue Manager #####");

		logger.debug("######Instantiating completed #####" + queueManager);
		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.EXECUTE_JOB.toString(),
				project, name, component, cfg);
		logger.debug("##### Sending record content " + recordContent);
		logger.info("Application Context" + appContext);
		logger.debug("QueueMnager" + queueManager);
		

		queueManager.sendWorkbenchMessageToStream(recordContent);
		logger.debug("##### Sent record content ");

		/*
		 * NGContextServices contextServices = new NGContextServices(root, config,
		 * project, component, batchID); contextServices.initContext();
		 * 
		 * contextServices.registerOutputDataSet();
		 * 
		 * NGContext workBenchcontext = contextServices.getNgctx();
		 * logger.info("NG context initialing...");
		 * workBenchcontext.serviceStatus.put(ComponentServices.InputDSMetadata, true);
		 * 
		 * logger.debug("##### Sending Complete!!"); ObjectNode root =
		 * mapper.createObjectNode(); ArrayNode ids = root.putArray("outputDatasetIds");
		 * for (String id : workBenchcontext.registeredOutputDSIds) { ids.add(id); }
		 */
		logger.info("Executing dataset transformation ends here ");

		return null;

	}

	/**
	 * Execute a transformation component on a dataset to create a new dataset.
	 */
	@Override
	public String createDatasetDirectory(String project, String catalog, String name) throws Exception {
		logger.trace("generate data system path for starts here :" + project + " : " + name);
		String path = root + Path.SEPARATOR + project + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR + Path.SEPARATOR
				+ MetadataBase.PREDEF_DATA_SOURCE + Path.SEPARATOR + catalog + Path.SEPARATOR + name + Path.SEPARATOR
				+ MetadataBase.PREDEF_DATA_DIR;
		logger.info("createDatasetDirectory path = " + path);
		if (!HFileOperations.exists(path)) {
			HFileOperations.createDir(path);
		}
		logger.trace("generate data system path for starts here " + path);
		return path;
	}

	@Value("${metastore.base}")
	@NotNull
	private String metastoreBase;

	@Override
	public ObjectNode getPreview(String previewId, String name, String component, String cfg) throws Exception {

		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.SHOW_PREVIEW.toString(),
				project, name, component, cfg);
		queueManager.sendWorkbenchMessageToStream(recordContent);
		return null;
	}

	@Override
	public ObjectNode createPreview(String id, String location, int previewLimit, String previewsTablePath,
			String project, String name) throws Exception {
		// String id = UUID.randomUUID().toString();
		// String location = createDatasetDirectory(project,
		// MetadataBase.DEFAULT_CATALOG, name);

		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.CREATE_PREVIEW.toString(),
				id, location, previewLimit, previewsTablePath, project, name);
		queueManager.sendWorkbenchMessageToStream(recordContent);

		return null;
	}

}
