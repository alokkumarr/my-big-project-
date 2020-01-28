package com.synchronoss.saw.workbench.service;

import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.ojai.DocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.typesafe.config.Config;

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

	  private static final Logger logger = LoggerFactory.getLogger(WorkbenchJobServiceImpl.class);
	  
	  @Autowired
		JavaSparkContext jsCtx;
	  
	@Override
	public Object executeJob(String root, String cfg, String project, String component, String batchID) {
		     logger.debug("Inside execute Job!!....######");
			  
			 // createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);
			    

			    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);


			    //String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

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
	
	private void setIfPathExists( SparkConf sparkConf,  String sparkProperty, Config cfg , String path) {
	    logger.debug("Checking if configuration path exists: {}", path);
	    if (cfg.hasPath(path)) {
	      logger.debug("Configuration path found, so setting Spark property: {}", sparkProperty);
	      sparkConf.set(sparkProperty, cfg.getString(path));
	    } else {
	      logger.debug("Configuration path not found");
	    }
	  }
	

	@Override
	public ObjectNode createPreview(String id, String location, String previewLimit, String previewsTablePath, String project, String name) {
	    Logger log = LoggerFactory.getLogger(getClass().getName());
	    log.info("Starting preview job");
	    PreviewBuilder preview = new PreviewBuilder(previewsTablePath, id, "success");
	    DocumentBuilder document = preview.getDocumentBuilder();
	    document.putNewArray("rows");
	   //// SparkSession session = jobContext.sparkSession();
	    Dataset<Row> dataset = getDataset(null, location); //replace spark session in place of null  @@Naresh
	    if (dataset != null) {
	      StructField[] fields = dataset.schema().fields();
	      Iterator<Row> rows = dataset.limit(Integer.valueOf(previewLimit)).toLocalIterator();
	      rows.forEachRemaining(
	          (Row row) -> {
	            document.addNewMap();
	            for (int i = 0; i < row.size(); i++) {
	              if (row.isNullAt(i)) {
	                continue;
	              }
	              //String name = fields[i].name();
	              DataType dataType = fields[i].dataType();
	              if (dataType.equals(DataTypes.StringType)) {
	                document.put(name, row.getString(i));
	              } else if (dataType.equals(DataTypes.IntegerType)) {
	                document.put(name, row.getInt(i));
	              } else if (dataType.equals(DataTypes.LongType)) {
	                document.put(name, row.getLong(i));
	              } else if (dataType.equals(DataTypes.FloatType)) {
	                document.put(name, row.getFloat(i));
	              } else if (dataType.equals(DataTypes.DoubleType)) {
	                document.put(name, row.getDouble(i));
	              } else if (dataType.equals(DataTypes.TimestampType)) {
	                document.put(name, row.getTimestamp(i).toString());
	              } else {
	                log.warn("Unhandled Spark data type: {}", dataType);
	                document.put(name, row.get(i).toString());
	              }
	            }
	            document.endMap();
	          });
	    }
	    document.endArray();
	    preview.insert();
	    log.info("Finished preview job");
	    return null;
	  }
	

	private Dataset<Row> getDataset(SparkSession session, String location) {
	    Logger log = LoggerFactory.getLogger(getClass().getName());
	    try {
	      return session.read().load(location);
	    } catch (Exception e) {
	      /*
	       * Handle exception thrown by Spark for example when dataset is empty
	       */
	      log.debug("Error while loading dataset, returning no rows");
	      return null;
	    }
	  }

	@Override
	public ObjectNode showPreview(String id, String location, String previewLimit, String previewsTablePath,
			String project, String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
