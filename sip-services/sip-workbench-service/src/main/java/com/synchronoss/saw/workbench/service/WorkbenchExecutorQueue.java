package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mapr.streams.Admin;
import com.mapr.streams.StreamDescriptor;
import com.mapr.streams.Streams;
import com.synchronoss.saw.workbench.executor.listener.WorkbenchExecutorListener;

import sncr.bda.core.file.HFileOperations;

@Service
/**
 * Workbench add dataset request is submitted to the queue.
 * Workbench executor consumer recieves and processes the messages
 * from the queue. This is to control backpressure.
 */
public class WorkbenchExecutorQueue {

	  private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorQueue.class);
	  
	  @Value("${workbench.stream.base-path}")
	  @NotNull
	  private String streamBasePath;
	  
	  private String workbenchStream;
	  
	  private String topic="";
	  KafkaProducer<String, String> producer=null;
	  @Autowired
	  WorkbenchExecutorListener listener;

	  @PostConstruct
	  public void init() {
		  logger.info("#### Post construct of WorkbenchQueue Manager");
	     
	      //this.streamPath = workbenchExecutorStream + File.separator + "services/workbench/executor";
		  this.workbenchStream = this.streamBasePath
	    		  + File.separator
	    		  + "sip-workbench-executor";
	      this.topic = workbenchStream + ":executions";

	      
	      
	    try {
	      createIfNotExists(10);
	    } catch (Exception e) {
	      logger.error("unable to create path for workbench executor stream : " + this.streamBasePath);
	    }
	  }

	  /**
	   * Create required MapR streams if they do not exist.
	   *
	   * @param retries number of retries.
	   * @throws Exception when unable to create stream path.
	   */
	   private void createIfNotExists(int retries) throws Exception {
		
		 
	    try {
	      HFileOperations.createDir(streamBasePath);
	    } catch (Exception e) {
	      if (retries == 0) {
	        logger.error("unable to create path for workbench executor stream for path : " + streamBasePath);
	        throw e;
	      }
	      Thread.sleep(5 * 1000);
	      createIfNotExists(retries - 1);
	    }
	    Configuration conf = new Configuration();
	    Admin streamAdmin = Streams.newAdmin(conf);
	    if (!streamAdmin.streamExists(workbenchStream)) {
	      StreamDescriptor streamDescriptor = Streams.newStreamDescriptor();
	      try {
	    	logger.debug("####Stream not exists. Creating stream ####");
	        streamAdmin.createStream(workbenchStream, streamDescriptor);
	        logger.debug("####Stream created Successfully!! ####");
	      } catch (Exception e) {

	        if (retries == 0) {
	          logger.error("Error unable to create the workbench stream no reties left: " + e);
	          throw e;
	        }
	        logger.warn("unable to create the workbench stream leftover reties : " + retries);
	        Thread.sleep(5 * 1000);
	        createIfNotExists(retries - 1);
	      } finally {
	        streamAdmin.close();
	      }
	    }
	    logger.info("####### Starting workbench consumer thread....");
	    Runnable r =
		        () -> {
		          try {
		        	  
		      	    listener.runWorkbenchConsumer();
		          } catch (Exception e) {
		            logger.error("Error occurred while running the stream consumer : " + e);
		          }
		        };
		    new Thread(r).start();
		    logger.info("#######Workbench consumer thread started");
		    
		    
	    
	    if(producer == null) {
	    	Properties properties = new Properties();
		    properties.setProperty(
		        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		    properties.setProperty(
		        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		    
		    producer = new KafkaProducer(properties);
	    }
	    
	    
	    
	  }
	   
	  /**
	   * Senindg messages to the queue.
	   * 
	   * @param recordContent content of record
	   * @return record sent or not.
	   */
	  public boolean sendWorkbenchMessageToStream(String recordContent) {
	   

	    logger.debug("######## Sending record content to kafka Queue ######" );

	    ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, recordContent);
	    logger.debug("#### Sending to topic ####"+ this.topic);

	    logger.debug("Writing data to stream " + record);
	    producer.send(record);
	    producer.flush();
	    logger.debug("######## Sent record content to kafka Queue ######" );
	    return true;
	  }


}
