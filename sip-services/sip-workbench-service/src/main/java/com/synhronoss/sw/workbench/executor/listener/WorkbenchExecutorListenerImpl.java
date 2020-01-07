package com.synhronoss.sw.workbench.executor.listener;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import sncr.bda.conf.ComponentConfiguration;
import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.parser.NGParser;
import sncr.xdf.services.NGContextServices;
import sncr.xdf.sql.ng.NGSQLComponent;
import sncr.xdf.transformer.ng.NGTransformerComponent;


public class WorkbenchExecutorListenerImpl implements  WorkbenchExecutorListener{
	
	 private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorListenerImpl.class);
	 @Value("${sip.service.metastore.base}")
	  @NotNull
	  private String basePath;
	 
	 @Value("${workbench.project-root}")
	  @NotNull
	  private String root;

	  private String streamPath = null;
	  private String evaluatorstream = null;
	  private String workbenchTopics= null;
	  
	  
	  /**
	   * Init method for listener.
	   *
	   * @throws Exception if unbale to create the stream.
	   */
	  @PostConstruct
	  public void init() {
	    streamPath = basePath + File.separator + "services/workbench/executor";
	    evaluatorstream = streamPath + File.separator + "sip-workbench-executor-stream";
	    workbenchTopics = evaluatorstream + ":executions";
	  }
	  
	  

	@Override
	public void createIfNotExists(int retries) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runWorkbenchConsumer() throws Exception {
	    try {
	        createIfNotExists(10);
	      } catch (Exception e) {
	        logger.error("Error occurred while initializing the AlertEvaluator stream ", e);
	      }

	      logger.debug("Starting receive:");
	      Properties properties = new Properties();
	      properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "sip-evaluator");
	      properties.setProperty(
	          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	      properties.setProperty(
	          ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	      properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");

	      KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
	      consumer.subscribe(Collections.singletonList(this.workbenchTopics));
	      receiveMessages(consumer);
	    }

	@Override
	public boolean sendMessageToStream() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	   * Method to receive consumer messages.
	   *
	   * @param consumer consumer
	   * @throws Exception when unable to process the messages.
	   */
	  private void receiveMessages(KafkaConsumer<String, String> consumer) {
	    long pollTimeout = 60 * 60 * 1000;
	    while (true) {
	      ConsumerRecords<String, String> records = consumer.poll(pollTimeout);
	      records.forEach(
	          record-> {
	        	  try {
					String[] content =   record.value().split(" ", 4);
					  if(content.length == 4) {
						  String project= content[0];
						  String name = content[1];
						  String component = content[2];
						  String cfg = content[3];
						  
						 // createDatasetDirectory(project, MetadataBase.DEFAULT_CATALOG, name);
						    

						    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);


						    String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

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
						    if (!aac.initComponent(null)) {
						      logger.error("Could not initialize component");
						      throw new RuntimeException("Could not initialize component:");
						    }
						    logger.info("Starting Workbench job");
						    int rc = aac.run();
						    logger.info("Workbench job completed, result: " + rc + " error: " + aac.getError());

						    if (rc != 0) {
						      throw new RuntimeException("XDF returned non-zero status: " + rc);
						    }
						    
						  
						  
					  }
				} catch (JsonSyntaxException exception) {
					logger.error(exception.getMessage());
				} catch (Exception exception) {
					logger.error(exception.getMessage());
				}
	        	  
	        	  
	          });
	      consumer.commitAsync();
	    }
	  }

	
}
