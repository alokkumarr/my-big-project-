package com.synchronoss.saw.workbench.executor;

import org.apache.log4j.Logger;

import com.synchronoss.saw.workbench.executor.service.WorkbenchExecutorCosumerQueue;



public class WorkbenchExecutor {
	
	private final static Logger logger =  
            Logger.getLogger(WorkbenchExecutor.class); 

	public static void main(String[] args) {
		
		logger.debug("Starting...");
		
		SparkConfig sparkConfig = new SparkConfig();
		logger.debug("Loading properties...");
		//sparkConfig.initiProperties();
		logger.debug("Initializing spark config....");
		//sparkConfig.initSparkConf();
		//sparkConfig.initJavaSparkContext();
		WorkbenchExecutorCosumerQueue executorQueue = new WorkbenchExecutorCosumerQueue();
		executorQueue.init();
		executorQueue.lauchConsumer();
	}

}
