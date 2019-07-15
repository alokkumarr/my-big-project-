package com.synchronoss.sip.datalake.executors

import java.io.IOException
import java.util.Properties

import com.mapr.db.exceptions.TableExistsException
import com.mapr.streams.Streams
import com.synchronoss.sip.datalake.{DLConfiguration, SipExecutorsConfig}
import com.synchronoss.sip.datalake.engine.{DataLakeExecution, ExecutionType}
import com.synchronoss.sip.datalake.engine.ExecutionType.ExecutionType
import org.apache.hadoop.conf.Configuration
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}
import sncr.bda.core.file.HFileOperations

import scala.collection.JavaConverters._
import scala.reflect.io.File

/**
 * Queue receiving requests to execute DL queries.
 * Implemented using MapR streams.
 */
class ReportExecutorQueue(val executorType: String) {
  val MainPath = if (SipExecutorsConfig.executorConfig.hasPath("path"))
    SipExecutorsConfig.executorConfig.getString("path") else "/main"
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
      HFileOperations.createDir(MainPath)
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
      val Array(executionType, semanticId, resultId , limit, query) = record.value.split(",",5)
      val executionTypeEnum = executionType match {
        case "preview" => ExecutionType.preview
        case "onetime" => ExecutionType.onetime
        case "scheduled" => ExecutionType.scheduled
        case "regularExecution" => ExecutionType.regularExecution
        case "publish" => ExecutionType.publish
        case obj => throw new RuntimeException("Unknown execution type: " + obj)
      }
      /* Save execution as a function to be invoked only after MapR streams
       * consumer has been closed */
      () => execute(semanticId, resultId, query, executionTypeEnum,Integer.parseInt(limit))
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

  private def execute(semanticId: String, resultId: String, query: String, executionType: ExecutionType,limit : Integer ) {
    try {
      log.debug("Executing {}, result {}", semanticId, resultId: Any)
      val dataLakeExecution : DataLakeExecution = new DataLakeExecution(executionType, semanticId,resultId )
      dataLakeExecution.executeDLQuery(query,limit)
    } catch {
      case e: Exception =>
        log.error("Error while executing " + semanticId, e)
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
