package com.synchronoss.saw.alert.service.evaluator;

import com.mapr.streams.Admin;
import com.mapr.streams.StreamDescriptor;
import com.mapr.streams.Streams;
import java.io.File;
import java.util.Collections;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.core.file.HFileOperations;

/** This class is used for the listen the alert evaluator mapr-stream. */
@Service
public class EvaluatorListenerImpl implements EvaluatorListener {

  private static final Logger logger = LoggerFactory.getLogger(EvaluatorListenerImpl.class);
  @Autowired AlertEvaluation alertEvaluation;

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  private String streamPath = null;
  private String evaluatorstream = null;
  private String alertTopics = null;

  /**
   * Init method for listener.
   *
   * @throws Exception if unbale to create the stream.
   */
  @PostConstruct
  public void init() {
    streamPath = basePath + File.separator + "services/alert/evaluator";
    evaluatorstream = streamPath + File.separator + "sip-alert-evaluator-stream";
    alertTopics = evaluatorstream + ":alerts";
  }

  /**
   * Create required MapR streams if they do not exist.
   *
   * @param retries number of retries.
   * @throws Exception when unable to create stream path.
   */
  @Override
  public void createIfNotExists(int retries) throws Exception {
    try {
      HFileOperations.createDir(streamPath);
    } catch (Exception e) {
      if (retries == 0) {
        throw e;
      }
      Thread.sleep(5 * 1000);
      createIfNotExists(retries - 1);
    }
    Configuration conf = new Configuration();
    Admin streamAdmin = Streams.newAdmin(conf);
    if (!streamAdmin.streamExists(evaluatorstream)) {
      StreamDescriptor streamDescriptor = Streams.newStreamDescriptor();
      try {
        streamAdmin.createStream(evaluatorstream, streamDescriptor);
        streamAdmin.createTopic(evaluatorstream, "alerts");
      } catch (Exception e) {

        if (retries == 0) {
          logger.error("Error unable to create the evaluator stream no reties left: " + e);
          throw e;
        }
        logger.warn("unable to create the evaluator stream leftover reties : " + retries);
        Thread.sleep(5 * 1000);
        createIfNotExists(retries - 1);
      } finally {
        streamAdmin.close();
      }
    }
  }

  /** This method is listener on messages for evaluators. */
  @Override
  public void runStreamConsumer() {
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
    consumer.subscribe(Collections.singletonList(this.alertTopics));
    receiveMessages(consumer);
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
          record -> {
            logger.debug(
                "AlertMetricsId Record:(%s, %s, %d, %d)",
                record.key(), record.value(), record.partition(), record.offset());
            String[] queueContent = record.value().split("˜˜");
            if (queueContent.length == 2) {
              alertEvaluation.evaluateAlert(queueContent[0], Long.valueOf(queueContent[1]));
            }
          });
      consumer.commitAsync();
    }
  }

  /**
   * Example to Test Send to stream the message for Alert.
   *
   * @return boolean
   */
  public boolean sendMessageToStream() {
    boolean status = true;
    Properties properties = new Properties();
    properties.setProperty(
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.setProperty(
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
    String datapaodId = "workbench::sample-elasticsearch";
    Long createdTime = System.currentTimeMillis();
    String recordContent = String.format("%s˜˜%d", datapaodId, createdTime);

    logger.info("Record content = " + recordContent + alertTopics);

    ProducerRecord<String, String> record = new ProducerRecord<>(alertTopics, recordContent);

    logger.debug("Writing data to stream " + record);
    producer.send(record);
    producer.flush();
    producer.close();

    return true;
  }
}
