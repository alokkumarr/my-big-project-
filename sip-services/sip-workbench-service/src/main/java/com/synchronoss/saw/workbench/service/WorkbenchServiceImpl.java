package com.synchronoss.saw.workbench.service;

import java.util.Iterator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.joda.time.DateTime;
import org.ojai.DocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.synchronoss.saw.workbench.service.PreviewBuilder;
import com.synchronoss.saw.workbench.service.SAWWorkbenchServiceImpl;
import com.synchronoss.saw.workbench.service.WorkbenchService;

import sncr.bda.conf.ComponentConfiguration;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.parser.NGParser;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.sql.ng.NGSQLComponent;
import sncr.xdf.transformer.ng.NGTransformerComponent;

public class WorkbenchServiceImpl implements WorkbenchService {

	  private static final Logger logger = LoggerFactory.getLogger(SAWWorkbenchServiceImpl.class);
	  
	  
	@Override
	public Object executeJob(String root, String cfg, String project, String component, String batchID) {
		
			  
			 // createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);
			    

			    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);


			    //String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

			    NGContextServices contextServices = new NGContextServices(root, config, project, component, batchID);
			    contextServices.initContext();

			    contextServices.registerOutputDataSet();

			    NGContext ngctx = contextServices.getNgctx();

			    ngctx.serviceStatus.put(ComponentServices.InputDSMetadata, true);

			    
			    logger.info("Start execute job");
			    AbstractComponent aac = null;
			    switch (ngctx.componentName) {
			      case "sql":
			        aac = new NGSQLComponent(ngctx);
			        break;
			      case "parser":
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
			    try {
					if (!aac.initComponent(null)) {
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
