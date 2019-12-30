package sncr.xdf.rtps.transform;

import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_GENERIC;
import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_SIMPLE;
import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_SIMPLE_JSON;
import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_COUNTLY;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.kafka010.CanCommitOffsets;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.esotericsoftware.minlog.Log;

import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.rtps.driver.RTPSPipelineProcessor;
import sncr.xdf.rtps.model.CountlyModel;
import sncr.xdf.rtps.model.GenericJsonModel;


/**
 * Created by asor0002 on 5/19/2017.
 *
 */
public class ProcessRecords implements VoidFunction2<JavaRDD<ConsumerRecord<String, String>>, Time> {

	transient Logger logger = Logger.getLogger(ProcessRecords.class);
    private static final long serialVersionUID = -3110740581467891647L;

    private JavaInputDStream<ConsumerRecord<String, String>> inStream;

    private String dataModel;
    private String definitions;

    // Generic model artifacts
    private static Map<String, List<Transform>> transformations;
    private static Set<String> types;
    private StructType schema = null;

    // ES artifacts
    private String esIndex;
    private String esType;
    private Map<String, String> esConfig;
    private static List<Column> esColumns = null;

    // Data Lake artifacts
    private String basePath;
    private String batchPrefix;
    private String outputType;
    private InternalContext itcx;
    private NGContext ngctx ;
    private static final Integer DEFAULT_THREAD_CNT = 10;
    private static final String fileNamePrefix = "part";
    
    private Map<String, StructType> schemaFields = new HashMap<String, StructType>();

    public ProcessRecords(
        JavaInputDStream<ConsumerRecord<String, String>> inStream,
        String dataModel, String definitions,
        String esIndex, Map<String, String> esConfig,
        String basePath, String batchPrefix, String outputType, Optional<NGContext> ngctx , Optional<InternalContext> ctx){
    	logger.debug("### Inside process records ####");
        this.inStream = inStream;
        this.dataModel = dataModel;
        logger.debug("###### Data Model ::"+ this.dataModel);
        this.definitions = definitions;
        if(esIndex != null && !esIndex.isEmpty()) {
            if (esIndex.indexOf("/") > 0) {
                // This means we have Index/Type
                this.esIndex = esIndex.split("/")[0];
                this.esType = "/" + esIndex.split("/")[1];
            } else {
                this.esIndex = esIndex;
                this.esType = "";
            }
        } else {
            this.esIndex = null;
        }

        this.esConfig = esConfig;
        this.basePath = basePath;
        this.batchPrefix = batchPrefix;
        this.outputType = outputType;
        if(ngctx.isPresent()) {
        	this.ngctx = ngctx.get();
        }
        if(ctx.isPresent()) {
        	this.itcx = ctx.get();
        }
        
    }
    public void  call(
        JavaRDD<ConsumerRecord<String, String>> in, Time tm) throws Exception {
        if (!in.isEmpty()) {
            logger.info("Starting new batch processing. Batch time: " + tm);
            logger.debug("#####Data Model::"+ dataModel);
            switch(dataModel.toLowerCase()){
                case DM_GENERIC : {
                    if (transformations == null) {
                        transformations = GenericJsonModel.createTransformationsList(definitions);
                    }
                    logger.info("####transofrmations ::" + transformations);
                    if(types == null){
                        types = GenericJsonModel.getObjectTypeList(definitions);
                    }
                    logger.info("####types ::" + types);
                    if(schema == null){
                        schema = GenericJsonModel.createGlobalSchema(definitions);
                    }
                    logger.info("####schema ::" + schema);
                    ProcessGenericRecords(in, tm ,itcx);
                    break;
                }
                case DM_COUNTLY : {
                    if(schema == null){
                        schema = CountlyModel.createGlobalSchema();
                    }
                    ProcessCountlyRecords(in, tm);
                    break;
                }
                case DM_SIMPLE : {
                    // Should be changed
                    ProcessSimpleRecords(in, tm );
                    break;
                }
                case DM_SIMPLE_JSON : {
                    ProcessSimpleJsonRecords(in, tm);
                    break;
                }
                default:
                    logger.error("Invalid data model configured : " + dataModel.toLowerCase());
                    break;
            }
        } //<-- if(!in.isEmpty())...

        CommitOffsets(in, tm);
    }

    
    private List<Row> getEventTypes(JavaRDD<ConsumerRecord<String, String>> in, SparkSession session){
    	SparkConf cnf = in.context().getConf();
    	JavaRDD<String> stringRdd = in.mapPartitions(new TransformSimpleRecord());
    	Dataset<Row> dataset = session.read().json(stringRdd).toDF();
    	List<Row> eventTypes = dataset.select((String) "EVENT_TYPE").distinct().collectAsList();
    	return eventTypes;
    }
    // We have to support time based indexes
    // Configuration can specify index name with multiple date/time formats in curly brackets
    // In this case we will create index name based on current timestamp
    private static String parseIndexName(String template, Date now) {

        String indexName = template;
        Pattern patternIndexName = Pattern.compile("\\{(.*?)\\}");

        Matcher matchPattern = patternIndexName.matcher(template);
        boolean result = matchPattern.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String format = matchPattern.group(1);
                String replacement;
                DateFormat df = new SimpleDateFormat(format);
                replacement = df.format(now);

                matchPattern.appendReplacement(sb, replacement);
                result = matchPattern.find();
            } while (result);
            matchPattern.appendTail(sb);
            indexName = sb.toString();
        }
        return indexName.toLowerCase();
    }

    private void ProcessSimpleRecords(JavaRDD<ConsumerRecord<String, String>> in, Time tm){
        JavaRDD<String> stringRdd = in.mapPartitions(new TransformSimpleRecord());
        SparkConf cnf = in.context().getConf();
        SparkSession sess = SparkSession.builder().config(cnf).getOrCreate();
        List<Row> types = getEventTypes(in,sess);
        for(Row event: types) {
        	String eventType = (String) event.get(0);
        	SaveSimpleBatch(stringRdd, tm,eventType);
        }
        
        
    }

    private void ProcessSimpleJsonRecords(JavaRDD<ConsumerRecord<String, String>> in, Time tm){
    	logger.debug("Beginning simple-json processing...");
        JavaRDD<String> stringRdd = in.mapPartitions(new TransformSimpleJsonRecord());
        logger.debug("String rdd conversion completed saving batch...");
        SparkConf cnf = in.context().getConf();
        SparkSession sess = SparkSession.builder().config(cnf).getOrCreate();
        List<Row> types = getEventTypes(in,sess);
        for(Row event: types) {
        	String eventType = (String) event.get(0);
        	logger.debug("EVENT TYPE::"+ eventType);
        	String query = "EVENT_TYPE" + "== \'" + eventType + "\'";
        	this.schemaFields.put(eventType, null);
        	/*JavaRDD<String> eventData = stringRdd.filter(new Function<String, Boolean>(){

				@Override
				public Boolean call(String data) throws Exception {
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(data);
					return json.get("EVENT_TYPE").equals(eventType);
					
				}
        		
        	});*/
        	 SaveSimpleBatch(stringRdd, tm,eventType);
        	 
        }
        processJsonRecords(stringRdd, tm, cnf, this.itcx,DM_SIMPLE_JSON);
        logger.debug("retriving spark configuration");
        logger.debug("Invoking json processing");
    }

    /**
     * @param stringRdd
     * @param tm
     * @param eventType
     */
    private void SaveSimpleBatch(JavaRDD<String> stringRdd, Time tm, String eventType){
        if(basePath != null && !basePath.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date batchDt = new Date(tm.milliseconds());
            
            
            String batchName = batchPrefix + sdf.format(batchDt);
            String path = basePath  + File.separator + batchName ;
            String strTmpPath = basePath + File.separator + "__TMP-" + 
            			batchName;
            
            logger.debug("Temp path::"+ strTmpPath);
            
            // Save data as TEMP
            try {
				stringRdd.saveAsTextFile(strTmpPath);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
            // Rename and cleanup
            finalizeBatch(strTmpPath, path, true);
            
        }
    }

    private void ProcessGenericRecords(JavaRDD<ConsumerRecord<String, String>> in, Time tm, InternalContext ctx){
        JavaRDD<String> jsonRdd = in.mapPartitions(new TransformJsonRecord(definitions));
        logger.debug("first below::");
        SparkConf cnf = in.context().getConf();
        processJsonRecords(jsonRdd, tm, cnf, ctx,DM_GENERIC);
    }

    private void ProcessCountlyRecords(JavaRDD<ConsumerRecord<String, String>> in, Time tm){
        JavaRDD<String> jsonRdd = in.mapPartitions(new TransformCountlyRecord());
        SparkConf cnf = in.context().getConf();
       processJsonRecords(jsonRdd, tm, cnf, this.itcx,DM_COUNTLY);

    }
    
    
    
    
    private void extractSchemaForEventTypes(SparkSession session,Dataset<Row> dataset, String model) {
    	if ((model.equals(DM_COUNTLY))) {
			logger.debug("Setting countly schema"+ schema);
			this.schemaFields.put(DM_COUNTLY, schema);
		} else {
    	List<Row> eventTypes = dataset.select((String) "EVENT_TYPE").distinct().collectAsList();
    	for (Row eventType : eventTypes) {
			
			logger.debug("#### Extracting schema for  eventType ::"+ eventType.getString(0));
			
			
				this.schemaFields.put(eventType.getString(0), 
						GenericJsonModel.createSchemaByEventType(definitions, eventType.getString(0)));
			}
			
    	}
    	
    }

    private int processJsonRecords(JavaRDD<String> jsonRdd, Time tm, SparkConf cnf, InternalContext ctx, String model){
    	
    	
    	
    	logger.debug("######## Inside process json records #####" + jsonRdd.count());
    	
        try {
			SparkSession sess = SparkSession.builder().config(cnf).getOrCreate();
			logger.debug("######## Reading through spark session #####");
			if (jsonRdd.count() > 0) {
				logger.debug("######## dataset size more than 0 #####");
				Dataset<Row> dataset = sess.read().json(jsonRdd).toDF();
				logger.debug("######## converted to dataset from rdd #####");
				if ((model.equals(DM_GENERIC) || model.equals(DM_COUNTLY))) {
					this.extractSchemaForEventTypes(sess, dataset, model);
				}

				logger.debug("schema fields::" + this.schemaFields.size());

			}
			if (this.ngctx != null) {

				logger.debug("######## Triggering pipeline as part of RTPS listener pipe line config ##########");

				logger.debug("####" + this.ngctx.pipelineConfigParams);

				JSONObject rtaConfig = null;

				if (this.ngctx != null && this.ngctx.runningPipeLine) {
					Object config = this.ngctx.pipelineConfig.get("pipeline");
					logger.debug("### Pipeline config in consumer ::" + this.ngctx.pipelineConfig);
					if (config instanceof JSONObject) {
						JSONObject jsonConfig = (JSONObject) config;
						rtaConfig = (JSONObject) jsonConfig.get("rta");
						logger.debug("### Pipeline config in RTPS pipeline ::" + rtaConfig);
					}
				}
				String multiColName = null;
				if (rtaConfig == null) {
					multiColName = "EVENT_TYPE";
				} else {

					multiColName = (String) rtaConfig.get("keyColumn");

					if (null == multiColName) {
						logger.error("No multi column defined...ex: EVENT_TYPE");
						return 0;
					}

					logger.debug("Multi column name " + multiColName);

				}

				/**
				 * Multiple event types
				 */

				//List<Row> multiColValues = df.select((String) multiColName).distinct().collectAsList();

				//logger.info("####### Total event types as part of dataset #######: " + multiColValues.size());

				NGContext ct = this.ngctx;
				//Object threadPoolCnt = null;
				//int numThreads = 0;

				final JSONObject rtaConfiguration = rtaConfig;
				final String keyColumn = multiColName;
				logger.debug("schema fields::" + this.schemaFields);
				this.schemaFields.forEach((eventType, eventSchema) -> {

					logger.debug("Processing for event : " + eventType + "with schema : " + eventSchema);
					Dataset<Row> df = null;
					JavaRDD<Row> rowRdd = sess.read().schema(schemaFields.get(eventType)).json(jsonRdd).toJavaRDD();
					logger.debug("######## Reading completed through spark session #####");
					if ((model.equals(DM_GENERIC) || model.equals(DM_COUNTLY))) {
						df = sess.createDataFrame(rowRdd, schemaFields.get(eventType));
					} else {
						df = sess.read().json(jsonRdd).toDF();
					}

					logger.debug("##### Data frame created with specific schema ");
					String isTimeSeries = null;
					Object threadPoolCnt = null;
					int numThreads = DEFAULT_THREAD_CNT;
					if (rtaConfiguration != null) {

						isTimeSeries = (String) rtaConfiguration.get("isTimeSeries");
						threadPoolCnt = rtaConfiguration.get("numberOfThreads");
						numThreads = threadPoolCnt == null ? DEFAULT_THREAD_CNT
								: Integer.valueOf((Integer) threadPoolCnt);
						logger.debug("###### Number of threads used only if non timeseries::" + numThreads);

					}

					logger.debug("########## is Time series data ::" + isTimeSeries);

					String query = keyColumn + "== \'" + eventType + "\'";

					logger.debug("Query:: " + query);
					logger.debug("is pipeline ::" + this.ngctx.runningPipeLine);

					/**
					 * 
					 * Process asynchronously if not time series. Process synchronously if time
					 * 
					 * series
					 * 
					 */

					if ((isTimeSeries == null || !Boolean.valueOf(isTimeSeries)) && this.ngctx.runningPipeLine && !model.equals(DM_COUNTLY)) {
						logger.debug("Starting async pipeline" + this.ngctx.runningPipeLine);
						ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
						final Dataset<Row> dataset = df;
						executorService.submit(new Callable<Long>() {
							@Override
							public Long call() throws Exception {
								try {
									Dataset<Row> data = dataset.filter(query).cache();
									
									logger.debug("Event type :::"+ eventType);
									
									if (data.count() > 0) {
										RTPSPipelineProcessor processor = new RTPSPipelineProcessor(data);
										processor.processDataWithDataFrame(ct.pipelineConfig, ct.pipelineConfigParams,
												eventType);
									} else {
										return 1L;
									}
								} catch (Exception e) {
									logger.error(e.getMessage());
								}
								return 1L;
							}

						});

					} else if (Boolean.valueOf(isTimeSeries) && this.ngctx.runningPipeLine && !model.equals(DM_COUNTLY)) {
						logger.debug("Starting sync pipeline" + this.ngctx.runningPipeLine);
						try {
							Dataset<Row> data = df.filter(query).cache();
							logger.debug("Event type :::"+ eventType);
							if (data.count() > 0) {
								RTPSPipelineProcessor processor = new RTPSPipelineProcessor(data);
								processor.processDataWithDataFrame(this.ngctx.pipelineConfig,
										this.ngctx.pipelineConfigParams, eventType);
							}else {
								return;
							}

						} catch (Exception e) {
							logger.error(e.getMessage());
						}

					}

					// Elastic Search
					if (esIndex != null && !esIndex.isEmpty()) {
						// Store batch in ES
						Dataset<Row> df2;
						if (esColumns != null && esColumns.size() > 0)
							df2 = df.select(scala.collection.JavaConversions.asScalaBuffer(esColumns));
						else
							df2 = df;

						// See configuration files for detailed explanations
						// on how data is written to ES
						// We use ES.Hadoop settings to control this flow
						String currentEsIndex = parseIndexName(esIndex, new Date()) + esType;
						JavaEsSparkSQL.saveToEs(df2, currentEsIndex, esConfig);
					} else {
						logger.debug("ES information not found. Not writing to elastic search");
					}
					//======================================================================
					// Data Lake
					// Store it in file system as separate directory
					if (basePath != null && !basePath.isEmpty() & df != null && 
							(model.equals(DM_GENERIC) || model.equals(DM_COUNTLY)) ) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
						Date batchDt = new Date(tm.milliseconds());
						String batchName = batchPrefix + sdf.format(batchDt);
						String path = basePath + File.separator + batchName ;
						String strTmpPath = basePath + File.separator + "__TMP-" + batchName ;
						logger.debug("Writing to temporary path"+ strTmpPath);
						
						if (outputType.equals("parquet")) {
							
							df.write().mode(SaveMode.Append).parquet(strTmpPath);
						} else if (outputType.equals("json")) {
							df.write().mode(SaveMode.Append).json(strTmpPath);
						} else {
							logger.error("Invalid output type :" + outputType);
						}
						// Done with writing - safe to rename batch directory
						finalizeBatch(strTmpPath, path, false);
						logger.debug("Writing to datalake compled");
					} else {
						logger.debug("base path not found or empty. Hence not writing to data lake");
					}
					
					
					/* Alert metrics collection */
					if (logger.isTraceEnabled()) {
						logger.debug("Alert metrics to be be collected: " + df.count());
					}

				});
				if(this.schemaFields != null) {
					this.schemaFields.clear();
				}

			} 
		} catch (Exception exception) {
			logger.error("Exception while processing RTA message "+ exception.getMessage());
			exception.printStackTrace();
		}
		return 0;
    
}
    

    private int finalizeBatch(String strTmpPath, String finalPath, boolean isSimple){
        // Done with writing - safe to rename batch directory
        try {
            String defaultFS = "maprfs:///";
            Configuration config = new Configuration();
            config.set("fs.defaultFS", defaultFS);
            FileSystem fs = FileSystem.get(config);

            Path tmpPath = new Path(strTmpPath);

            /*RemoteIterator<LocatedFileStatus> it = fs.listFiles(tmpPath, false);
            while (it.hasNext()) {
                Path pathToDelete = it.next().getPath();
                // We have to delete all non-parquet files/directories
                // Little bit shaky - how to define parquet files
                if(pathToDelete.getName().startsWith("_S")) {
                    fs.delete(pathToDelete, true);
                }
            }*/
            // !!!!!!!!!!!Should use another technique for renameh
            logger.debug("renaming "+ tmpPath + " to "+ finalPath);
            fs.rename(tmpPath, new Path(finalPath));
            logger.debug("Is simple??"+ isSimple);
            /**
             * Simple scenario's no suffix such as UUID
             * is not added. Hence renaming file to maintian
             * uniqueness with timestamp as suffix
             */
            if(isSimple ) {
            	FileStatus[] files = fs.globStatus(new Path(finalPath+ 
            			Path.SEPARATOR + fileNamePrefix+ "*"));
            	 String timeStamp = "";
            	 SimpleDateFormat suffixFmt = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
            	 Date date = new Date();
            	
            	
            	 for(FileStatus fileStatus: files) {
            		 logger.debug("******** part file name renaming for simple.... ********"+ fileStatus.getPath().getName());
            		 
            		 
            		fs.rename(new Path(finalPath+ Path.SEPARATOR + fileStatus.getPath().getName()), new Path(finalPath+ 
                    		Path.SEPARATOR + fileNamePrefix + "-" +  suffixFmt.format(date)));
            		
            		logger.info("******** rename completed ********");
            	}
            	
            	
               
                
            }
        } catch (Exception e) {
        	logger.error(e.getLocalizedMessage());
        }
        return 0;
    }

    private void CommitOffsets(JavaRDD<ConsumerRecord<String, String>> rdd, Time tm) {
        if(rdd.rdd() instanceof HasOffsetRanges) {
            OffsetRange[] offsetRanges = ((HasOffsetRanges)rdd.rdd()).offsetRanges();
            boolean commit = false;
            for(OffsetRange o : offsetRanges) {
                if(o.fromOffset() < o.untilOffset()) {
                    logger.info("Batch Time: " + tm + ". Updated offsets : " + o.topic() + ", part. : " + o.partition()
                                       + " [ from: " + o.fromOffset() + "; until: " + o.untilOffset() + "]");
                    commit = true;
                }
            }
            if(commit) {
                logger.info("Committing new offsets.");
                ((CanCommitOffsets)inStream.inputDStream()).commitAsync(offsetRanges);
            }
        }
    }
}
