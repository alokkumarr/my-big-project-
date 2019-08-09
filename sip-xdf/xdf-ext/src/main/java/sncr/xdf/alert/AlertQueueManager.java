package sncr.xdf.alert;

import com.mapr.streams.Admin;
import com.mapr.streams.StreamDescriptor;
import com.mapr.streams.Streams;
import java.io.File;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import sncr.bda.core.file.HFileOperations;

/**
 * Queue manager class to send the notification to queue for DataPod, if Alert is configured for the
 * datapod.
 */
public class AlertQueueManager {

  private static final Logger logger = Logger.getLogger(AlertQueueManager.class);
  private String streamBasePath;

  private String alertStream;
  private String alertTopic;

  public AlertQueueManager(String streamBasePath) {
    this.streamBasePath = streamBasePath;

    this.alertStream = this.streamBasePath + File.separator + "sip-alert-stream";
    this.alertTopic = alertStream + ":alertTopic";
    try {
      createIfNotExists(10);
    } catch (Exception e) {
      logger.error("unable to create path for executor stream : " + this.streamBasePath);
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
        logger.error("unable to create path for executor stream for path : " + streamBasePath);
      }
      Thread.sleep(5 * 1000);
      createIfNotExists(retries - 1);
    }
    Configuration conf = new Configuration();
    Admin streamAdmin = Streams.newAdmin(conf);
    if (!streamAdmin.streamExists(alertStream)) {
      StreamDescriptor streamDescriptor = Streams.newStreamDescriptor();
      try {
        streamAdmin.createStream(alertStream, streamDescriptor);
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
  }

  public boolean sendMessageToStream(String datapod, Long processedTime) {
    Properties properties = new Properties();
    properties.setProperty(
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.setProperty(
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer<String, String> producer = new KafkaProducer(properties);

    String recordContent = String.format("%s,%d", datapod, processedTime);
    logger.debug("Record content = " + recordContent);

    ProducerRecord<String, String> record = new ProducerRecord<>(this.alertTopic, recordContent);

    logger.debug("Writing data to stream " + record);
    producer.send(record);
    producer.flush();
    producer.close();

    return true;
  }
}
