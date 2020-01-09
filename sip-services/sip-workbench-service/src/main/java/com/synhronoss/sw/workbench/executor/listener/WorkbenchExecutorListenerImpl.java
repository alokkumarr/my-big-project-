package com.synhronoss.sw.workbench.executor.listener;

import java.io.File;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

import com.google.gson.JsonSyntaxException;
import com.synchronoss.saw.workbench.executor.service.WorkbenchExecutionType;
import com.synchronoss.saw.workbench.service.WorkbenchService;
import com.synchronoss.saw.workbench.service.WorkbenchServiceImpl;


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
	        	  ExecutorService executor = Executors.newFixedThreadPool(10);
	        	  Future<Long> result = executor.submit(new Callable<Long>() {
						@Override
						public Long call() throws Exception {
							try {
				        		  String[] content =   record.value().split(" ");
				        		  
				        		  WorkbenchExecutionType executionType = WorkbenchExecutionType.valueOf(content[0]);
				        		  
				        		  
				        		  switch(executionType) {
				        		  case EXECUTE_JOB:

					        		  if(content.length == 5) {
					        			  	String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");
					        		  
							        		  String project= content[0];
							    			  String name = content[1];
							    			  String component = content[2];
							    			  String cfg = content[3];
							    			  WorkbenchService service = new WorkbenchServiceImpl();
							    			  service.executeJob(root, cfg, project, component, batchID);
							    			  
					        		  }
					        		  break;
					        		  
				        		  case SHOW_PREVIEW:

					        		  if(content.length == 6) {
					        			      String id= content[0];
					        			      String location= content[1];
					        			  	  String previewLimit= content[2];
							        		  String previewsTablePath= content[3];
							    			  String project = content[4];
							    			  String name = content[5];
							    			  WorkbenchService service = new WorkbenchServiceImpl();
							    			  service.createPreview(id, location, previewLimit, previewsTablePath, project, name);
							    			  break;
							    			  
					        		  }
					        		  break;
								default:
									break;
				        		  }
				        		  
				    			  
				    			  
				        	  } catch (JsonSyntaxException exception) {
								logger.error(exception.getMessage());
							} catch (Exception exception) {
								logger.error(exception.getMessage());
							}
							return pollTimeout;
				        	  
				        	  
				          }
	        	  });
						
							
						

					});

	        	  
	        	  
	      consumer.commitAsync();
	    }
	  }

	
}
