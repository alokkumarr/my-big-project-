package com.synchronoss.saw.workbench.executor.listener;

import java.io.File;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;
import com.synchronoss.saw.workbench.executor.SparkConfig;

import com.synchronoss.saw.workbench.executor.service.WorkbenchExecutionType;
import com.synchronoss.saw.workbench.executor.service.WorkbenchJobService;
import com.synchronoss.saw.workbench.executor.service.WorkbenchJobServiceImpl;

public class WorkbenchExecutorListenerImpl implements WorkbenchExecutorListener {

	private static final Logger logger = LoggerFactory.getLogger(WorkbenchExecutorListenerImpl.class);
	// @Value("${sip.service.metastore.base}")
	// @NotNull
	// private String basePath="";

	private String root = "";

	private String streamPath = null;
	private String workbenchTopics = null;

	WorkbenchJobService service = new WorkbenchJobServiceImpl();

	private String evaluatorstream;

	/**
	 * Init method for listener.
	 *
	 * @throws Exception if unbale to create the stream.
	 */
	public WorkbenchExecutorListenerImpl() {
		String basePath = "";
		logger.debug("#####Inside constructor of listener...Initializing... #####");

		String sipBasePath = "";
		this.root = "maprfs:///var/sip"; //SparkConfig.configParams.get("workbench.project-root");
		logger.debug("#### Project root ###"+ SparkConfig.configParams.get("workbench.project-root"));
		this.streamPath = sipBasePath + File.separator + "services/workbench/executor";
		this.evaluatorstream = this.streamPath + File.separator + "sip-workbench-executor";
		this.workbenchTopics = evaluatorstream + ":executions";

	}

	@Override
	public void createIfNotExists(int retries) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void runWorkbenchConsumer() throws Exception {

		logger.debug("Starting receive:");
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "sip-workbench");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");

		logger.debug("######### Creating consumer ######");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
		logger.debug("######### Consumer  created ######" + consumer);
		logger.debug("######### Subscribing to topic  ::" + this.workbenchTopics);
		consumer.subscribe(Collections.singletonList(this.workbenchTopics));
		logger.debug("######### Subscribing completed!!  $##########");
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
		logger.debug("Inside recieve messages");
		logger.debug("########Consumer ::" + consumer);
		long pollTimeout = 60 * 60 * 1000;
		while (true) {
			logger.debug("Inside while loop");
			ConsumerRecords<String, String> records = consumer.poll(pollTimeout);
			logger.debug("################Inside polling for  messages");
			logger.debug("#################Number of records recieved in polling" + records.count());
			records.forEach(record -> {
				ExecutorService executor = Executors.newFixedThreadPool(10);
				Future<Long> result = executor.submit(new Callable<Long>() {
					@Override
					public Long call() throws Exception {
						logger.debug("Consumer processing message....");
						try {
							String[] content = record.value().split("˜˜");

							logger.debug("#### content[0]...execution type ::" + content[0]);

							WorkbenchExecutionType executionType = WorkbenchExecutionType.valueOf(content[0]);
							
							logger.debug("checking logic for ::"+ executionType);

							switch (executionType) {
							case EXECUTE_JOB:
								

								if (content.length == 5) {
									logger.debug(" #######  Processing exeucte job type in consumer .... #######");
									String batchID = new DateTime().toString("yyyyMMdd_HHmmssSSS");

									String project = content[1];
									String name = content[2];
									String component = content[3];
									String cfg = content[4];
									service.executeJob(root, cfg, project, component, batchID);

								}
								break;

							case CREATE_PREVIEW:
								
								logger.debug("######## Total number of elements in message  #######"+ content.length);

								if (content.length == 7) {
									logger.debug("####### Processing create preview  type in consumer ....##########");
									String id = content[1];
									String location = content[2];
									String previewLimit = content[3];
									String previewsTablePath = content[4];
									String project = content[5];
									String name = content[6];
									WorkbenchJobService service = new WorkbenchJobServiceImpl();
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
			logger.debug("####End of while loop one iteration");
		}
	}

}
