package executor

import java.util.Properties
import scala.collection.JavaConverters._

import com.mapr.streams.Streams
import com.mapr.db.exceptions.TableExistsException
import org.apache.hadoop.conf.Configuration;
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.{Logger, LoggerFactory}

import sncr.datalake.engine.Analysis
import sncr.datalake.engine.ExecutionType
import files.HFileOperations

/**
 * Queue for sending and receiving requests to execute report queries.
 * Implemented using MapR streams.
 */
class ReportExecutorQueue {
  val ExecutorStream = "/main/saw-transport-executor-stream"
  val ExecutorTopic = ExecutorStream + ":executions"
  val log: Logger = LoggerFactory.getLogger(classOf[ReportExecutorQueue].getName)

  /* Automatically create queue upon startup if it does not exist */
  createIfNotExists()

  /**
   * Create required MapR streams if they do not exist
   */
  def createIfNotExists(retries: Int = 12) {
    /* Create the parent directory of the stream if it does not exist */
    HFileOperations.createDirectory("/main")
    /* Create the queue stream */
    log.debug("Creating stream for executor queue: {}", ExecutorStream)
    val conf = new Configuration()
    val streamAdmin = Streams.newAdmin(conf)
    val desc = Streams.newStreamDescriptor()
    try {
      streamAdmin.createStream(ExecutorStream, desc)
      log.info("Stream created: {}", ExecutorStream)
    } catch {
      case e: TableExistsException =>
        log.debug("Stream already exists, so not creating: {}", ExecutorStream)
      case e: Exception => {
        /* Retry creating stream for some time, as the MapR-FS connection
         * might be intermittently unavailable at system startup */
        if (retries == 0) {
          log.debug("Failed creating stream: {}", ExecutorStream)
          throw e
        }
        val seconds = 5
        log.debug("Creating stream failed, waiting for {} seconds "
          + "and retrying", seconds)
        Thread.sleep(seconds * 1000)
        createIfNotExists(retries - 1)
      }
    } finally {
      streamAdmin.close()
    }
  }

  /**
   * Send request to execute report to queue
   */
  def send(analysisId: String, resultId: String, query: String) {
    log.debug("Starting send")
    val properties = new Properties()
    properties.setProperty("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    properties.setProperty("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](properties)
    producer.send(new ProducerRecord[String, String](
      ExecutorTopic, analysisId + "," + resultId + "," + query))
    producer.flush
    producer.close
    log.debug("Finished send")
  }

  /**
   * Process request to execute report from queue
   */
  def receive {
    log.debug("Starting receive")
    val properties = new Properties()
    properties.setProperty("group.id", "saw-transport-executor")
    properties.setProperty("key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("max.poll.records", "1")
    val consumer = new KafkaConsumer[String, String](properties)
    consumer.subscribe(List(ExecutorTopic).asJava);
    receiveMessage(consumer)
    log.debug("Finished receive")
    /* After processing one message, exit and let system service restart
     * to process next message */
  }

  private def receiveMessage(consumer: KafkaConsumer[String, String]) {
    log.debug("Receive message")
    val pollTimeout = 60 * 60 * 1000
    val records = consumer.poll(pollTimeout)
    records.asScala.foreach(record => {
      log.debug("Received queue record: {}", record);
      val Array(analysisId, resultId, query) = record.value.split(",", 3)
      execute(analysisId, resultId, query)
    })
  }

  private def execute(analysisId: String, resultId: String, query: String) {
    val analysis = new Analysis(analysisId)
    analysis.executeAndWait(ExecutionType.onetime, query, resultId)
  }
}
