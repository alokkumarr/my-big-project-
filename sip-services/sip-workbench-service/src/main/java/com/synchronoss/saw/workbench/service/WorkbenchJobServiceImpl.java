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
			    JavaSparkContext jsCtx = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(this.initSpark().setMaster("local") ));
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
	
	public static SparkConf initSpark(){
		
		/*YarnLocalCluster yarnLocalCluster = new YarnLocalCluster.Builder()
			    .setNumNodeManagers(1)
			    .setNumLocalDirs(Integer.parseInt("1"))
			    .setNumLogDirs(Integer.parseInt("1"))
			    .setResourceManagerAddress("localhost:37001")
			    .setResourceManagerHostname("localhost")
			    .setResourceManagerSchedulerAddress("localhost:37002")
			    .setResourceManagerResourceTrackerAddress("localhost:37003")
			    .setResourceManagerWebappAddress("localhost:37004")
			    .setUseInJvmContainerExecutor(false)
			    .setConfig(new org.apache.hadoop.conf.Configuration()).build();
			   
			try {
				yarnLocalCluster.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
		SparkConf sparkConf = new SparkConf();
		
			   
			    sparkConf.setAppName("sip-workbench-executor");
			    sparkConf.set("spark.master", "local");
			   
			    sparkConf.set("spark.executor.memory", "4G");
			    sparkConf.set("spark.cores.max", "2");
			    // Added to support for Spark core setting with YARN, it will be handled later on with additional
			    // spark parameter. Currently being handled using existing parameter configuration cores.max.
			    sparkConf.set("spark.executor.cores", "2");
			    sparkConf.set("driver.memory", "2G");
			    sparkConf.set("spark.hadoop.fs.defaultFS", "maprfs:///");
			    sparkConf.set("spark.hadoop.yarn.resourcemanager.hostname", "mapr-rd612.eng-sip.dev01.us-west.sncrcloud.net");
			    		//"mapr-rd612.eng-sip.dev01.us-west.sncrcloud.net");
			    /*setIfPathExists(sparkConf, "", cfg, "yarn.resourcemanager.hostname");
			    setIfPathExists(sparkConf, "spark.yarn.jars", cfg, "yarn.spark.jars");
			    setIfPathExists(sparkConf, "spark.yarn.archive", cfg, "yarn.spark.zips");
			    setIfPathExists(sparkConf, "spark.executor.instances", cfg, "");
			    setIfPathExists(sparkConf, "spark.driver.port", cfg, "driver.port");
			    setIfPathExists(sparkConf, "spark.driver.host", cfg, "driver.host");
			    setIfPathExists(sparkConf, "spark.driver.bindAddress", cfg, "driver.bindAddress");
			    setIfPathExists(sparkConf, "spark.driver.blockManager.port", cfg, "driver.blockManager.port");*/
			    
			    sparkConf.set("spark.sql.inMemoryColumnarStorage.compressed", "true");
			    //sparkConf.set("spark.sql.inMemoryColumnarStorage.batchSize", "");
			    sparkConf.set("spark.sql.caseSensitive", "false");
			    /* Disable the UI to avoid port collision with multiple executors */
			    sparkConf.set("spark.ui.enabled", "false");
			    
			    return sparkConf;
			   
			    
		
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
	
	
	public static void main(String args[]) {
		JavaSparkContext jsCtx = JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(initSpark()));
		System.out.print(jsCtx);
	}
	

}
