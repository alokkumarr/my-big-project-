package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mapr.streams.Admin;
import com.mapr.streams.StreamDescriptor;
import com.mapr.streams.Streams;

import sncr.bda.core.file.HFileOperations;

@Service
public class WorkbenchExecutorQueue {

	  private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorQueue.class);
	  private String streamBasePath = "";
	  private String project="";
	  private String name="";
	  private String component="";
	  private String cfg="";
	  private String topic="";
	  private String workbenchExecutorStream="";
	  KafkaProducer<String, String> producer=null;
	 
	  @PostConstruct
	  public void init() {
		  logger.debug("#### Post construct of WorkbenchQueue Manager");
	      String sipBasePath = "";
	     
	      this.streamBasePath = sipBasePath + File.separator + "services/workbench/executor";
	      this.workbenchExecutorStream = this.streamBasePath
        + File.separator
        + "sip-workbench-executor";
	      this.topic = workbenchExecutorStream + ":executions";

	      
	      
	    try {
	      createIfNotExists(10);
	    } catch (Exception e) {
	      logger.error("unable to create path for alert stream : " + this.streamBasePath);
	    }
	  }

	  /**
	   * Create required MapR streams if they do not exist.
	   *
	   * @param retries number of retries.
	   * @throws Exception when unable to create stream path.
	   */
	   private void createIfNotExists(int retries) throws Exception {
		   logger.debug("Here");
	    try {
	      HFileOperations.createDir(streamBasePath);
	    } catch (Exception e) {
	      if (retries == 0) {
	        logger.error("unable to create path for alert stream for path : " + streamBasePath);
	        throw e;
	      }
	      Thread.sleep(5 * 1000);
	      createIfNotExists(retries - 1);
	    }
	    Configuration conf = new Configuration();
	    Admin streamAdmin = Streams.newAdmin(conf);
	    if (!streamAdmin.streamExists(workbenchExecutorStream)) {
	      StreamDescriptor streamDescriptor = Streams.newStreamDescriptor();
	      try {
	    	logger.debug("####Stream not exists. Creating stream ####");
	        streamAdmin.createStream(workbenchExecutorStream, streamDescriptor);
	        logger.debug("####Stream created Successfully!! ####");
	      } catch (Exception e) {

	        if (retries == 0) {
	          logger.error("Error unable to create the alert stream no reties left: " + e);
	          throw e;
	        }
	        logger.warn("unable to create the alert stream leftover reties : " + retries);
	        Thread.sleep(5 * 1000);
	        createIfNotExists(retries - 1);
	      } finally {
	        streamAdmin.close();
	      }
	    }
	    
	    createProducer();
	    
	   
	    
	    
	    
	  }
	   
	   public void createProducer() {
		   if(producer == null) {
		    	Properties properties = new Properties();
			    properties.setProperty(
			        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			    properties.setProperty(
			        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			    
			    producer = new KafkaProducer(properties);
		    }
	   }

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
