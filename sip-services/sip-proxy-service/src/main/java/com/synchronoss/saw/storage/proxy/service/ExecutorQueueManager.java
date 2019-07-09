package com.synchronoss.saw.storage.proxy.service;

import com.synchronoss.saw.storage.proxy.model.ExecutionType;
import java.io.File;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ExecutorQueueManager {
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

    ProducerRecord<String, String> record = new ProducerRecord<>(this.executorTopic, recordContent);

    producer.send(record);
    producer.flush();
    producer.close();

    return true;
  }
}
