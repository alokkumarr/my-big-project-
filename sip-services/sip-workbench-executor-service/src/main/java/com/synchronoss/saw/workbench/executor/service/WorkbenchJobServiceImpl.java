package com.synchronoss.saw.workbench.executor.service;

import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.ojai.DocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.synchronoss.saw.workbench.executor.SparkConfig;
import com.typesafe.config.Config;

import sncr.bda.base.MetadataBase;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.parser.NGParser;
import sncr.xdf.services.NGContextServices;


public class WorkbenchJobServiceImpl implements WorkbenchJobService {
	
	
	private static final long serialVersionUID = 1113799434508676066L;
	
	private static String root = "/var/sip/workbench/";
	  private static final Logger logger = LoggerFactory.getLogger(WorkbenchJobServiceImpl.class);
	  
	  public String createDatasetDirectory(String project, String catalog, String name) throws Exception {
		    logger.trace("generate data system path for starts here :" + project + " : " + name); 
		    String path = root + Path.SEPARATOR + project + Path.SEPARATOR + MetadataBase.PREDEF_DL_DIR
		        + Path.SEPARATOR + MetadataBase.PREDEF_DATA_SOURCE + Path.SEPARATOR
		        + catalog + Path.SEPARATOR + name + Path.SEPARATOR
		        + MetadataBase.PREDEF_DATA_DIR;
		    logger.info("createDatasetDirectory path = " + path);
		    if (!HFileOperations.exists(path)) {
		      HFileOperations.createDir(path);
		    }
		    logger.trace("generate data system path for starts here " + path);
		    return path;
		  }
	  
	@Override
	public Object executeJob(String root, String cfg, String project, String componentName, String batchID) {
		     logger.debug("Inside execute Job!!....######");
			  
			 try {
				createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, "test-workbench");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			    

			    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);


			    //String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

			    NGContextServices contextServices = new NGContextServices(root, config, project, componentName, batchID);
			    contextServices.initContext();
			    logger.debug("Init content done ....######");
			    contextServices.registerOutputDataSet();
			    logger.debug("register output done ....######");
			    NGContext ngctx = contextServices.getNgctx();

			    ngctx.serviceStatus.put(ComponentServices.InputDSMetadata, true);
			  //  ngctx.serviceStatus.put(ComponentServices.Spark, true);
			    
			    logger.info("Start execute job");
			    AbstractComponent component = null;
			    switch (ngctx.componentName) {
			      case "sql":
			       // component = new NGSQLComponent(ngctx);
			        break;
			      case "parser":
			    	  logger.debug("Invoking Parser !!!! ....######");
			    	  try {
			    		  component = new NGParser(ngctx);
			    	  }catch(Exception exception) {
			    		  exception.printStackTrace();
			    	  }
			      
			        break;
			      case "transformer":
			       // component = new NGTransformerComponent(ngctx);
			        break;
			      default:
			        throw new IllegalArgumentException("Unknown component: " + ngctx.componentName);
			    }
			  
			   // SparkConfig.jsc.setLogLevel("DEBUG");
			    logger.debug("#### Setting librarires as class path for spark context ######");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.core-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-core-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-parser-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.meta-api-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-data-profiler-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-preview-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-component-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.bda.xdf-ext-4.jar");
			    SparkConfig.jsc.addJar("/opt/bda/sip-workbench-executor/lib/com.synchronoss.saw.sip-workbench-executor-service-4.jar");
			    logger.debug("#### Manual class path settings completed!! ######");
			    
			    try {
					if (!component.initComponent(SparkConfig.jsc)) {
					  logger.error("Could not initialize component");
					  throw new RuntimeException("Could not initialize component:");
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			    logger.info("Starting Workbench job");
			    int rc = component.run();
			    logger.info("Workbench job completed, result: " + rc + " error: " + component.getError());

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
