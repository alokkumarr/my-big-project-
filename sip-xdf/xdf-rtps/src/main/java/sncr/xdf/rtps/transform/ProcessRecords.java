package sncr.xdf.rtps.transform;

import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_GENERIC;
import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_SIMPLE;
import static sncr.xdf.rtps.driver.EventProcessingApplicationDriver.DM_SIMPLE_JSON;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.kafka010.CanCommitOffsets;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL;

import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.rtps.driver.RTPSPipelineProcessor;
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
    
    

    public ProcessRecords(
        JavaInputDStream<ConsumerRecord<String, String>> inStream,
        String dataModel, String definitions,
        String esIndex, Map<String, String> esConfig,
        String basePath, String batchPrefix, String outputType, Optional<NGContext> ngctx , Optional<InternalContext> ctx){
    	logger.debug("### Inside process records ####");
        this.inStream = inStream;
        this.itcx = itcx;
        this.dataModel = dataModel;
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
               /* case DM_COUNTLY : {
                    if(schema == null){
                        schema = CountlyModel.createGlobalSchema();
                    }
                    ProcessCountlyRecords(in, tm);
                    break;
                }*/
                case DM_SIMPLE : {
                    // Should be changed
                    ProcessSimpleRecords(in, tm);
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
        SaveSimpleBatch(stringRdd, tm);
    }

    private void ProcessSimpleJsonRecords(JavaRDD<ConsumerRecord<String, String>> in, Time tm){
        JavaRDD<String> stringRdd = in.mapPartitions(new TransformSimpleJsonRecord());
        SaveSimpleBatch(stringRdd, tm);
    }

    private void SaveSimpleBatch(JavaRDD<String> stringRdd, Time tm){
        if(basePath != null && !basePath.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date batchDt = new Date(tm.milliseconds());
            String batchName = batchPrefix + sdf.format(batchDt);
            String path = basePath + File.separator + batchName;
            String strTmpPath = basePath + File.separator + "__TMP-" + batchName;
            // Save data as TEMP
            stringRdd.saveAsTextFile(strTmpPath);
            // Rename and cleanup
            finalizeBatch(strTmpPath, path);
        }
    }

    private void ProcessGenericRecords(JavaRDD<ConsumerRecord<String, String>> in, Time tm, InternalContext ctx){
        JavaRDD<String> jsonRdd = in.mapPartitions(new TransformJsonRecord(definitions));
        SparkConf cnf = in.context().getConf();
        processJsonRecords(jsonRdd, tm, cnf, ctx);
    }

    

    private int processJsonRecords(JavaRDD<String> jsonRdd, Time tm, SparkConf cnf, InternalContext ctx){
    	logger.debug("######## Inside process json records #####" );
    	
       // SparkSession sess = SparkSession.builder().config(cnf).getOrCreate();
    	SparkSession sess = ctx.sparkSession;
        JavaRDD<Row> rowRdd = sess.read().schema(schema).json(jsonRdd).toJavaRDD();
        //rowRdd.foreachPartitionAsync(f)
        
 
        Dataset<Row> df = sess.createDataFrame(rowRdd, schema);
        //List<Row> list =  df.collectAsList();
        //Dataset<Row> data = sess.createDataFrame(list, schema);
  	    NGContext context = this.ngctx;
  	    
		
		/*df.repartition(new Column("EVENT_TYPE")).toJavaRDD().foreachPartitionAsync(new VoidFunction<JavaRDD<Row>>() {

			

			@Override
			public void call(JavaRDD<Row> t) throws Exception {
				JSONObject pipeline = (JSONObject) context.pipelineConfig.get("pipeline");
	            JSONObject pipelineConfigs = (JSONObject) pipeline.get("pipelineConfigs");
				RTPSPipelineProcessor processor = new RTPSPipelineProcessor(sess.createDataFrame(t, schema));
	            processor.processDataWithDataFrame(context.pipelineConfig,context.pipelineConfigParams, null );
				
			}
			
			
		});*/
    	
        logger.debug("Invoking  async ...partion logic ....");
        
       /* List<Row> list =  df.repartition(new Column("EVENT_TYPE")).collectAsList();
        final SparkSession sparkSession = SparkSession.builder().config(cnf).getOrCreate();
		sess.createDataFrame(list, schema).toJavaRDD().foreachPartitionAsync(new ProcessEventType(cnf,
				this.ngctx.pipelineConfig,schema,this.ngctx.pipelineConfigParams,df.sparkSession()));*/
		
		
		
		logger.debug("Invoking async ...partion logic completed....");
        
        if(this.ngctx != null && this.ngctx.runningPipeLine & df != null) {
      	  logger.debug("######## Triggering pipeline as part of RTPS listener pipe line config ##########");
      	  
      	logger.debug("######## Triggering pipeline as part of RTPS listener pipe line config ##########");
    	  logger.debug("####"+ this.ngctx.pipelineConfigParams);
    	  List<Row> eventTypes = df.select("EVENT_TYPE").distinct().collectAsList();
    	  
    	  logger.info("Total event types: "+ eventTypes.size());
    	  
    	  NGContext ct = this.ngctx;
    	  /*ExecutorService executorService = Executors.newFixedThreadPool(10);
    	  for(Row eventType: eventTypes) {
    		  logger.info("Processing for event type: "+ eventType.getString(0));
    		  
    		  
    		  executorService.submit(new Callable<Long>() {
    		        @Override
    		        public Long call() throws Exception {
    		        	
    		        	String type =  (String) eventType.get(0);
    		        	
    		          logger.info("###########Event type::"+ type);
  		    		  String query = "EVENT_TYPE == \'"+ type+ "\'";
  		    		  RTPSPipelineProcessor processor = new RTPSPipelineProcessor(df.filter(query).cache());
  		              processor.processDataWithDataFrame(ct.pipelineConfig, ct.pipelineConfigParams,type );
  					
    					return 1L;
    		        }
    		    });
    		  
    		  
    		  
    		  
    			
    	  }*/
    	  
    	  
    	  
    	  for(Row eventType: eventTypes) {
    		 String type =  (String) eventType.get(0);
    		 String query = "EVENT_TYPE == \'"+ type+ "\'";
    		 logger.debug("Query:: " + query);
    		 RTPSPipelineProcessor processor = new RTPSPipelineProcessor(df.filter(query).cache());
             processor.processDataWithDataFrame(this.ngctx.pipelineConfig, this.ngctx.pipelineConfigParams,type );
    		  
    	  }
    	
    	  logger.debug("COUNT before async::"+ df.count());
    	  //List<Row> list =  df.select("EVENT_TYPE").distinct().toJavaRDD().collect();
    	 /* sess.createDataFrame(list, schema).toJavaRDD().foreachPartitionAsync(
    			  new ProcessEventType(sess , schema,
    					  df.cache(),this.ngctx.pipelineConfig,this.ngctx.pipelineConfigParams, logger));*/
    	  
    	  
    	  
    	  //eventTypes.toJavaRDD().foreachAsync(new ProcessEventType(cachedData,this.ngctx.pipelineConfig,this.ngctx.pipelineConfigParams));
    	  
  		/*eventTypes.toJavaRDD().for((ForeachFunction<Row>) row -> {
  			Dataset<Row> eventTypeData = df.filter("EVENT_TYPE == row.getString(0)");
  			
  			 //eventTypeData.rdd().toJavaRDD().foreachPartitionAsync(f)
  			
            
            JSONObject pipeline = (JSONObject) this.ngctx.pipelineConfig.get("pipeline");
            JSONObject pipelineConfigs = (JSONObject) pipeline.get("pipelineConfigs");
			RTPSPipelineProcessor processor = new RTPSPipelineProcessor(eventTypeData.cache());
             processor.processDataWithDataFrame(this.ngctx.pipelineConfig, this.ngctx.pipelineConfigParams,row.getString(0) );
  			
  		});
      	  
      }*/

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
        }
        //======================================================================
        // Data Lake
        // Store it in file system as separate directory
        if(basePath != null && !basePath.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date batchDt = new Date(tm.milliseconds());
            String batchName = batchPrefix + sdf.format(batchDt);
            String path = basePath + File.separator + batchName;
            String strTmpPath = basePath + File.separator + "__TMP-" + batchName;

            if (outputType.equals("parquet")) {
                df.write().mode(SaveMode.Append).parquet(strTmpPath);
            } else if (outputType.equals("json")) {
                df.write().mode(SaveMode.Append).json(strTmpPath);
            } else {
                logger.error("Invalid output type :" + outputType);
            }
            // Done with writing - safe to rename batch directory
            finalizeBatch(strTmpPath, path);
        } //<-- if(basePath != null && !basePath.isEmpty())

        /* Alert metrics collection */
        if (logger.isTraceEnabled()) {
          logger.debug("Alert metrics to be be collected: " + df.count());
        }
        logger.debug("######## Data frame crated with XDF-RTPS ##########");
        
        
        
        }

    
        
        return 0;
    
}
    

    private int finalizeBatch(String strTmpPath, String finalPath){
        // Done with writing - safe to rename batch directory
        try {
            String defaultFS = "maprfs:///";
            Configuration config = new Configuration();
            config.set("fs.defaultFS", defaultFS);
            FileSystem fs = FileSystem.get(config);

            Path tmpPath = new Path(strTmpPath);

            RemoteIterator<LocatedFileStatus> it = fs.listFiles(tmpPath, false);
            while (it.hasNext()) {
                Path pathToDelete = it.next().getPath();
                // We have to delete all non-parquet files/directories
                // Little bit shaky - how to define parquet files
                if(pathToDelete.getName().startsWith("_S")) {
                    fs.delete(pathToDelete, true);
                }
            }
            // Results are ready - make it final
            // !!!!!!!!!!!Should use another technique for rename
            fs.rename(tmpPath, new Path(finalPath));
        } catch (Exception e) {
            logger.error(e.getMessage());
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
