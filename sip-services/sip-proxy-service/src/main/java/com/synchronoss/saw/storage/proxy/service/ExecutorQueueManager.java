package com.synchronoss.saw.storage.proxy.service;

import com.mapr.streams.Admin;
import com.mapr.streams.StreamDescriptor;
import com.mapr.streams.Streams;
import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import java.io.File;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.core.file.HFileOperations;

public class ExecutorQueueManager {

  private static final Logger logger = LoggerFactory.getLogger(ExecutorQueueManager.class);
  private ExecutionType executionType;
  private String streamBasePath;

  private String executorQueue;

  private String executorStream;
  private String executorTopic;

  ExecutorQueueManager(ExecutionType executionType, String streamBasePath) {
    this.executionType = executionType;
    this.streamBasePath = streamBasePath;

    if (this.executionType == ExecutionType.onetime
        || this.executionType == ExecutionType.preview) {
      executorQueue = "fast";
    } else if (this.executionType == ExecutionType.scheduled
        || this.executionType == ExecutionType.regularExecution) {
      executorQueue = "regular";
    }

    this.executorStream =
        this.streamBasePath
            + File.separator
            + "saw-transport-executor-"
            + this.executorQueue
            + "-stream";
    this.executorTopic = executorStream + ":executions";
      try {
          createIfNotExists(10);
      } catch (Exception e) {
          logger.error("unable to create path for executor stream : " +this.streamBasePath);
      }
  }

    /**
     * Create required MapR streams if they do not exist.
     *
     * @param retries number of retries.
     * @throws Exception when unable to create stream path.
     */
    public void createIfNotExists(int retries) throws Exception {
        try {
            HFileOperations.createDir(streamBasePath);
        } catch (Exception e) {
            if (retries == 0) {
                logger.error("unable to create path for executor stream for path : "+streamBasePath);

            }
            Thread.sleep(5 * 1000);
            createIfNotExists(retries - 1);
        }
        Configuration conf = new Configuration();
        Admin streamAdmin = Streams.newAdmin(conf);
        if (!streamAdmin.streamExists(executorStream)) {
            StreamDescriptor streamDescriptor = Streams.newStreamDescriptor();
            try {
                streamAdmin.createStream(executorStream, streamDescriptor);
            } catch (Exception e) {

                if (retries == 0) {
                    logger.error("Error unable to create the executor stream no reties left: " + e);
                    throw e;
                }
                logger.warn("unable to create the executor stream leftover reties : " + retries);
                Thread.sleep(5 * 1000);
                createIfNotExists(retries - 1);
            } finally {
                streamAdmin.close();
            }
        }
    }

  public boolean createStream() {
    boolean status = true;

    return status;
  }

  public boolean sendMessageToStream(
      String semanticId, String executionId, int limit, String query) {
    boolean status = true;
    Properties properties = new Properties();
    properties.setProperty(
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.setProperty(
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

    String recordContent =
        String.format("%s,%s,%s,%d,%s", this.executionType, semanticId, executionId, limit, query);

    logger.debug("Record content = " + recordContent);

    ProducerRecord<String, String> record = new ProducerRecord<>(this.executorTopic, recordContent);

    logger.debug("Writing data to stream " + record);
    producer.send(record);
    producer.flush();
    producer.close();

    return true;
  }
}
