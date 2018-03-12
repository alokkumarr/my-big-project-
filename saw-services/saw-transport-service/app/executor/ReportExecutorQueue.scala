package executor

import java.io.IOException
import java.util.Properties
import scala.collection.JavaConverters._

import com.mapr.streams.Streams
import com.mapr.db.exceptions.TableExistsException
import org.apache.hadoop.conf.Configuration
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.{Logger, LoggerFactory}

import sncr.datalake.engine.Analysis
import sncr.datalake.engine.ExecutionType
import sncr.datalake.engine.ExecutionType.ExecutionType
import files.HFileOperations
import sncr.saw.common.config.SAWServiceConfig

import scala.reflect.io.File

/**
 * Queue for sending and receiving requests to execute report queries.
 * Implemented using MapR streams.
 */
class ReportExecutorQueue(val executorType: String) {
  val MainPath = if (SAWServiceConfig.executorConfig.hasPath("path"))
    SAWServiceConfig.executorConfig.getString("path") else "/main"
  val ExecutorStream = MainPath+File.separator+"saw-transport-executor-" + executorType + "-stream"
  val ExecutorTopic = ExecutorStream + ":executions"

  val RetrySeconds = 5
  val log: Logger = LoggerFactory.getLogger(classOf[ReportExecutorQueue].getName)

  /**
   * Create required MapR streams if they do not exist
   */
  def createIfNotExists(retries: Int = 12) {
    /* Create the parent directory of the stream if it does not exist */
    try {
      HFileOperations.createDirectory(MainPath)
    } catch {
      case e: IOException => {
        log.debug("Failed creating main directory: {}", MainPath)
        /* Retry creating directory for some time, as the MapR-FS connection
         * might be intermittently unavailable at system startup */
        retryCreateIfNotExists(e, retries)
      }
    }
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
        log.debug("Failed creating stream: {}", ExecutorStream)
        /* Retry creating stream for some time, as the MapR-FS connection
         * might be intermittently unavailable at system startup */
        retryCreateIfNotExists(e, retries)
      }
    } finally {
      streamAdmin.close()
    }
  }

  private def retryCreateIfNotExists(exception: Exception, retries: Int) {
    if (retries == 0) {
      throw exception
    }
    log.debug("Waiting for {} seconds and retrying", RetrySeconds)
    Thread.sleep(RetrySeconds * 1000)
    createIfNotExists(retries - 1)
  }

  /**
   * Send request to execute report to queue
   */
  def send(executionType: ExecutionType, analysisId: String, resultId: String, query: String) {
    /* Automatically create queue before sending if it does not exist */
    createIfNotExists()
    log.debug("Starting send: {}", executorType)
    val properties = new Properties()
    properties.setProperty("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    properties.setProperty("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](properties)
    producer.send(new ProducerRecord[String, String](
      ExecutorTopic, executionType + "," + analysisId + "," + resultId + "," + query))
    producer.flush
    producer.close
    log.debug("Finished send")
  }

  /**
   * Process request to execute report from queue
   */
  def receive {
    /* Automatically create queue before receiving if it does not exist */
    createIfNotExists()
    log.debug("Starting receive: {}", executorType)
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
    log.debug("Receive message poll: {}", executorType)
    val pollTimeout = 60 * 60 * 1000
    val records = consumer.poll(pollTimeout)
    val executions = records.asScala.map(record => {
      log.debug("Received message: {} {}", executorType, record: Any);
      val Array(executionType, analysisId, resultId, query) = record.value.split(",", 4)
      val executionTypeEnum = executionType match {
        case "preview" => ExecutionType.preview
        case "onetime" => ExecutionType.onetime
        case "scheduled" => ExecutionType.scheduled
        case obj => throw new RuntimeException("Unknown execution type: " + obj)
      }
      /* Save execution as a function to be invoked only after MapR streams
       * consumer has been closed */
      () => execute(analysisId, resultId, query, executionTypeEnum)
    })
    /* Close MapR streams consumer before starting to execute, to unblock
     * other consumers that need to read messages from the same
     * partition */
    log.debug("Committing consumer offsets")
    consumer.commitSync()
    log.debug("Closing consumer")
    consumer.close()
    /* Start executions */
    log.debug("Executing queries")
    executions.foreach(_())
  }

  private def execute(analysisId: String, resultId: String, query: String, executionType: ExecutionType) {
    try {
      log.debug("Executing analysis {}, result {}", analysisId, resultId: Any)
      val analysis = new Analysis(analysisId)
      analysis.executeAndWait(executionType, query, resultId)
    } catch {
      case e: Exception =>
        log.error("Error while executing analysis " + analysisId, e)
    } finally {
      markExecutionCompleted(resultId)
    }
  }

  private def markExecutionCompleted(resultId: String) {
    val path = MainPath+File.separator+"saw-transport-executor-result-" + resultId
    try {
      val os = HFileOperations.writeToFile(path)
      os.close
    } catch {
      case e: Exception =>
        log.error("Error while writing marker file: " + path, e)
    }
  }
}
