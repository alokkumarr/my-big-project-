package com.synchronoss.saw.workbench.executor.service;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.workbench.service.WorkbenchExecutorQueue;
import com.synchronoss.sip.utils.RestUtil;

@Service
public class WorkbenchExecutorImpl implements WorkbenchExecutor {

	private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorImpl.class);
	
	@Value("${workbench.project-root}")
	@NotNull
	private String root;
	
	@Value("${workbench.project-key}")
	@NotNull
	private String project;

	@Autowired
	WorkbenchExecutorQueue queueManager;
	

	@Override
	public ObjectNode executeJob(String project, String name, String component, String cfg) {

		logger.info("execute name = " + name);
		logger.info("execute component = " + component);
		logger.debug("######Tryging deserialize with gson for config::" + cfg);

		String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");


		String recordContent = String.format("%s˜˜%s˜˜%s˜˜%s˜˜%s", WorkbenchExecutionType.EXECUTE_JOB.toString(),
				project, name, component, cfg);
		logger.debug("##### Sending record content " + recordContent);
		

		queueManager.sendWorkbenchMessageToStream(recordContent);

		logger.info("Executing dataset transformation ends here ");

		return null;

	}

	


	
}
