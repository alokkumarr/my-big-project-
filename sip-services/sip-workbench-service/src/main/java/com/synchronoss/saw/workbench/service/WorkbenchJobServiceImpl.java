package com.synchronoss.saw.workbench.service;

import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import sncr.bda.conf.ComponentConfiguration;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.parser.NGParser;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.sql.ng.NGSQLComponent;
import sncr.xdf.transformer.ng.NGTransformerComponent;

@Service
public class WorkbenchJobServiceImpl implements WorkbenchJobService {
	
	
	private static final long serialVersionUID = 1113799434508676066L;
	
	
	  private static final Logger logger = LoggerFactory.getLogger(WorkbenchJobServiceImpl.class);
	  
	  @Autowired
		JavaSparkContext jsCtx;
	  
	@Override
	public Object executeJob(String root, String cfg, String project, String component, String batchID) {
		     logger.debug("Inside execute Job!!....######");
			  
			    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);

			    NGContextServices contextServices = new NGContextServices(root, config, project, component, batchID);
			    contextServices.initContext();
			    logger.debug("Init content done ....######");
			    contextServices.registerOutputDataSet();
			    logger.debug("register output done ....######");
			    NGContext ngctx = contextServices.getNgctx();

			    ngctx.serviceStatus.put(ComponentServices.InputDSMetadata, true);

			    
			    logger.info("Start execute job");
			    AbstractComponent aac = null;
			    switch (ngctx.componentName) {
			      case "sql":
			        aac = new NGSQLComponent(ngctx);
			        break;
			      case "parser":
			    	  logger.debug("Invoking Parser !!!! ....######");
			        aac = new NGParser(ngctx);
			        break;
			      case "transformer":
			        aac = new NGTransformerComponent(ngctx);
			        break;
			      default:
			        throw new IllegalArgumentException("Unknown component: " + ngctx.componentName);
			    }
			    NGContext workBenchcontext = contextServices.getNgctx();

			    workBenchcontext.serviceStatus.put(ComponentServices.InputDSMetadata, true);
			   // JavaSparkContext jsCtx = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(this.initSpark().setMaster("local") ));
			    jsCtx.setLogLevel("DEBUG");
			    try {
					if (!aac.initComponent(jsCtx)) {
					  logger.error("Could not initialize component");
					  throw new RuntimeException("Could not initialize component:");
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			    logger.info("Starting Workbench job");
			    int rc = aac.run();
			    logger.info("Workbench job completed, result: " + rc + " error: " + aac.getError());

			    if (rc != 0) {
			      throw new RuntimeException("XDF returned non-zero status: " + rc);
			    }
			    
			  
			  
		  
		  
		  return null;
	
	}
	
	

	
	
	
	

}
