package synchronoss.spark.drivers.rt;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.v09.KafkaUtils;
import synchronoss.data.generic.model.GenericJsonModelValidator;
import synchronoss.spark.functions.rt.*;
import synchronoss.spark.rt.common.ConfigurationHelper;
import synchronoss.spark.rt.common.FileUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.apache.spark.api.java.StorageLevels.MEMORY_AND_DISK_SER;

/**
 * Created by asor0002 on 7/25/2016.
 * Generic Event Processing Application Main Driver Class
 */
public class EventProcessingApplicationDriver extends RealTimeApplicationDriver {
    private static final Logger logger = Logger.getLogger(EventProcessingApplicationDriver.class);
    // Supported data models
    private static final String DM_GENERIC = "generic";
    private static final String DM_COUNTLY = "countly";
    private static final String DM_SIMPLE = "simple";

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

        logger.info("Creating new JavaStreamingContext.");
        // Spark application context settings
        SparkConf sparkConf = new SparkConf();
        ConfigurationHelper.initConfig(sparkConf, appConfig, "spark.", false);
        logger.info("Spark settings");
        logger.info(sparkConf);

        // Hardcoded Spark parameters - we don't want them to be overwritten in configuration in any case
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        sparkConf.set("spark.kryo.registrator", "synchronoss.spark.functions.rt.EventProcessingKryoSerdeRegistrator");
        sparkConf.set("spark.app.name", instanceName);

        // Checkpoint directory location
        // Should be inside maprfs/hdfs
        String checkpointDirectory = appConfig.getString("spark.checkpoint.path");

        //Batch Interval
        Integer batchInterval = appConfig.getInt("spark.batch.interval");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(batchInterval));
        jssc.checkpoint(checkpointDirectory);

        // MapR Streams configuration parameters
        HashMap<String, String> kafkaParams = new HashMap<>();
        ConfigurationHelper.initConfig(kafkaParams, appConfig, "streams.", true);
        logger.info("Stream settings");
        logger.info(kafkaParams);

        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        String topic = appConfig.getString("streams.topic");

        // Setup MapR kafka consumer
        HashSet<String> topicsSet = new HashSet<>();
        topicsSet.add(topic);

        // Configuration for ElasticSearch
        Map<String, String> esConfig = new HashMap<>();

        String esIndex = null;
        if(appConfig.hasPath("es")) {
            ConfigurationHelper.initConfig(esConfig, appConfig, "es.", false);
            esIndex = appConfig.getString("es.index");
        }
        logger.info("Elastic Search settings");
        logger.info(esConfig);

        // Create stream
        JavaPairInputDStream<String, String> eventsStream = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                kafkaParams,
                topicsSet
        );

        //Processing pipeline
        logger.info("Starting " + model + " processing model.");
        String outputPath = appConfig.getString("maprfs.path");
        String outputType = appConfig.hasPath("maprfs.outputType") ? appConfig.getString("maprfs.outputType") : "parquet";

        if(outputType.equals("parquet") || outputType.equals("json")) {
            logger.info("Data output type is  " + outputType);
        } else {
            logger.error("Invalid data output type :" + outputType + " . Please set maprfs.outputType configuration parameter to 'parquet' or 'json'");
            System.exit(-1);
        }

        switch(model.toLowerCase()){
            case DM_GENERIC : {
                eventsStream
                    .mapPartitions(new TransformJsonRecord(fieldDefinitions))
                    .persist(MEMORY_AND_DISK_SER)
                    .transform(new ConvertToRows(fieldDefinitions))
                    .transform(new SaveBatchToElasticSearch(esIndex, esConfig, fieldDefinitions))
                    .foreachRDD(new SaveBatchToDataLake(outputPath, outputType, this.instanceName, fieldDefinitions));
                break;
            }
            case DM_COUNTLY : {
                eventsStream
                    .mapPartitions(new TransformCountlyRecord())
                    .persist(MEMORY_AND_DISK_SER)
                    .transform(new ConvertToRows())
                    .transform(new SaveBatchToElasticSearch(esIndex, esConfig))
                    .foreachRDD(new SaveBatchToDataLake(outputPath, outputType, this.instanceName));
                break;
            }
            case DM_SIMPLE : {

            }
            default: {
                logger.error("Invalid data model :" + model);
                System.exit(-1);
            }
        }

        return jssc;
    }

    private String settings[][] = {
            {"maprfs.path", "Output Storage not configured. Please specify value for maprfs.path in configuration file"},
            {"fields.model", "Data model not specified. Please specify value for fields.model in configuration file. Valid values are 'generic' or 'countly'"},
            {"streams.topic", "Input stream/topic not configured. Please specify value for streams.topic in configuration file"},
            {"spark.checkpoint.path", "Spark checkpoint location path not configured."},
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
}
