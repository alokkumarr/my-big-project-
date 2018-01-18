package synchronoss.spark.drivers.rt;


import com.mapr.db.Admin;
import com.mapr.streams.Streams;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka09.*;
import org.ojai.store.DocumentStore;
import synchronoss.data.generic.model.GenericJsonModelValidator;
import synchronoss.spark.functions.rt.*;
import synchronoss.spark.rt.common.ConfigurationHelper;
import synchronoss.spark.rt.common.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by asor0002 on 7/25/2016.
 * Generic Event Processing Application Main Driver Class
 */
public class EventProcessingApplicationDriver extends RealTimeApplicationDriver {
    private static final Logger logger = Logger.getLogger(EventProcessingApplicationDriver.class);
    // Supported data models
    public static final String DM_GENERIC = "generic";
    public static final String DM_COUNTLY = "countly";
    public static final String DM_SIMPLE = "simple";
    public static final String DM_SIMPLE_JSON = "simple-json";

    public static void main(String[] args){
        EventProcessingApplicationDriver driver = new EventProcessingApplicationDriver();
        driver.run(args[0]);
    }

    /**
     * Create new Streaming context and define application steps
     * @param   appConfig configuration object
     * @return  new Java Streaming context initialized with provided configuration
     */
    protected JavaStreamingContext createContext(String instanceName, com.typesafe.config.Config appConfig){


        // Validate configuration and settings
        if(!checkConfiguration(appConfig)){
            System.exit(-1);
        }

        // Get field definition
        String model = appConfig.getString("fields.model");
        String fieldDefinitions = null;

        if(model.toLowerCase().equals(DM_GENERIC)) {
            if(!appConfig.hasPath("fields.definition.file")) {
                logger.error("Definition file path is missing in configuration (fields.definition.file)");
                System.exit(-1);
            }
            String fieldDefinitionPath = appConfig.getString("fields.definition.file");
            try {
                fieldDefinitions = FileUtils.readFile(fieldDefinitionPath);
                logger.debug(fieldDefinitions);
                // Validate
                if (!GenericJsonModelValidator.validate(fieldDefinitions)) {
                    throw new Exception("Incorrect object definition file");
                }
            } catch (Exception e) {
                logger.error("Can't process fields definition file : " + fieldDefinitionPath);
                logger.error(e.getMessage());
                System.exit(-1);
            }
        }

        // Spark application context settings
        SparkConf sparkConf = new SparkConf();
        ConfigurationHelper.initConfig(sparkConf, appConfig, "spark.", false);
        logger.info("Spark settings");
        logger.info(sparkConf);

        // Hardcoded Spark parameters - we don't want them to be overwritten in configuration in any case
        sparkConf.set("spark.app.name", instanceName);

        //Processing pipeline
        logger.info("Starting " + model + " processing model.");
        String outputPath = appConfig.getString("maprfs.path");
        String outputType = appConfig.hasPath("maprfs.outputType") ? appConfig.getString("maprfs.outputType") : "parquet";

        if (outputType.equals("parquet") || outputType.equals("json")) {
            logger.info("Data output type is  " + outputType);
        } else {
            logger.error("Invalid data output type :" + outputType + " . Please set maprfs.outputType configuration parameter to 'parquet' or 'json'");
            System.exit(-1);
        }
        if(checkOutputPath(outputPath) != 0){
            logger.error("Can't create output directory " + outputPath);
            System.exit(-1);
        }
        logger.info("Output directory " + outputPath);


        // MapR Streams configuration parameters
        HashMap<String, Object> kafkaParams = new HashMap<>();
        // !!!!!!!!!!!!! NEED CLEANUP !!!!!!!!!!!!!!!!!!!
        ConfigurationHelper.initConfig2(kafkaParams, appConfig, "streams.", true);
        // Setup MapR kafka consumer
        String configuredSource = appConfig.getString("streams.topic");

        // Validate if stream/topic exists
        if(validateStream(configuredSource) != 0){
            logger.error("Can't connect to the stream " + configuredSource);
            System.exit(-1);
        }

        // We have to create list with single string
        List<String> src = new ArrayList<>();
        src.add(configuredSource);

        logger.info("Connection to stream : " + src);
        // We have to modify/enforce some kafka parameters
        fixKafkaParams(kafkaParams);

        logger.info("Modified Kafka parameters:" + kafkaParams);

        //Batch Interval
        Integer batchInterval = appConfig.getInt("spark.batch.interval");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(batchInterval));

        // Create stream
        JavaInputDStream<ConsumerRecord<String, String>> eventsStream
            = KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(),
                                            ConsumerStrategies.<String, String>Subscribe(src, kafkaParams));
        // Configuration for ElasticSearch
        Map<String, String> esConfig = new HashMap<>();

        String esIndex = null;
        if (appConfig.hasPath("es")) {
            ConfigurationHelper.initConfig(esConfig, appConfig, "es.", false);
            esIndex = appConfig.getString("es.index");
        }
        logger.info("Elastic Search settings");
        logger.info(esConfig);

        eventsStream
            .foreachRDD(new ProcessRecords(eventsStream,
                                          model, fieldDefinitions,
                                          esIndex, esConfig,
                                          outputPath, this.appName, outputType));
        return jssc;
    }

    private String settings[][] = {
            {"maprfs.path", "Output Storage not configured. Please specify value for maprfs.path in configuration file"},
            {"fields.model", "Data model not specified. Please specify value for fields.model in configuration file. Valid values are 'generic' or 'countly'"},
            {"streams.topic", "Input stream/topic not configured. Please specify value for streams.topic in configuration file"},
            {"spark.batch.interval", "Spark bath interval not configured"}
    };

    private boolean checkConfiguration(com.typesafe.config.Config appConfig){

        for(int i = 0; i < settings.length; i++){
            if(!appConfig.hasPath(settings[i][0])){
                logger.error(settings[i][1]);
                return false;
            }
        }
        return true;
    }

    private void fixKafkaParams(Map<String, Object> kafkaParams) {

        // Mandatory parameters for Kafka queues
        // without them streams will not be able to get data out of queue
        // even if application tself is working
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.STREAMS_ZEROOFFSET_RECORD_ON_EOF_CONFIG, false);
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        kafkaParams.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 65536);

        String originalGroupId = (String)kafkaParams.get(ConsumerConfig.GROUP_ID_CONFIG);
        if (null == originalGroupId) {
            logger.error("${ConsumerConfig.GROUP_ID_CONFIG} is null, you should probably set it");
        }
        String groupId = "spark-executor-" + originalGroupId;
        logger.info("overriding executor ${ConsumerConfig.GROUP_ID_CONFIG} to ${groupId}");
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //========================================================
    }

    private int checkOutputPath(String p) {
        // Check if output path exists
        String defaultFS = "maprfs:///";
        Configuration config = new Configuration();
        config.set("fs.defaultFS", defaultFS);
        try {
            Path path = new Path(p);
            FileSystem fs = FileSystem.get(config);
            if(!fs.exists(path)){
                // directory doesn't exists - try to create
                fs.mkdirs(path);
            }
        } catch(Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private int validateStream(String p) {
        int retval = -1;
        try {
            String[] t = p.split(":");
            if(t.length != 2){
                logger.error("Invalid stream/topic configuration value, please correct configuration file. Must be defined as <stream path>:<topic> .");
            } else {
                DocumentStore ds = Streams.getMessageStore(t[0], t[1]);
                if (ds == null) {
                    logger.error("getMessageStore(<stream path>, <topic>) returned NULL.");
                } else {
                    retval = 0;
                }
            }
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return retval ;
    }
}
