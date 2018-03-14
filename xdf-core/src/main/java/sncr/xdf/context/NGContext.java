package sncr.xdf.context;

import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.context.ContextMetadata;
import sncr.bda.services.AuditLogService;
import sncr.bda.services.DLDataSetService;
import sncr.bda.services.TransformationService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by srya0001 on 9/6/2017.
 *
 * The class provides execution context for processMap component.
 * The contexts includes:
 * - Spark context and configuration
 * - FileSystem context and instance
 * - Component configuration
 * - Aux. values
 * The finalize method is used to close all connections.
 * The context should be used as execution scope of all components
 *
 */
public class NGContext {

    private static final Logger logger = Logger.getLogger(NGContext.class);

    //Hadoop FS handlers
    public FileContext fc;
    public FileSystem fs;


    //Spark session and config
    public SparkSession sparkSession = null;
    public SparkConf sparkConf;


    public NGContext(SparkSession ss){
        sparkSession = ss;
    }

    public NGContext(){
    }

    public Map<String, Map<String, Object>> inputDataSets = new HashMap<>();
    public Map<String, Map<String, Object>> outputDataSets = new HashMap<>();

    public Map<String, Map<String, Object>> inputs = new HashMap<>();
    public Map<String, Map<String, Object>> outputs = new HashMap<>();



}
