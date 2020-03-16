package sncr.xdf.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.google.gson.JsonElement;

import sncr.xdf.adapters.writers.MoveDataDescriptor;

public class InternalContext {

    private static final Logger logger = Logger.getLogger(Context.class);

    //Hadoop FS handlers
    public FileContext fc;
    public FileSystem fs;
    public int defaultPartNumber = 1;

    //Metadata services
    public ArrayList<MoveDataDescriptor> resultDataDesc;

    public Map<String, JsonElement> mdOutputDSMap;
    public Map<String, JsonElement> mdInputDSMap;

    public SparkSession sparkSession;
    public JavaSparkContext javaSparkContext;
    public boolean extSparkCtx = false;
    public int globalFileCount;

    public void registerDataset(String name, Dataset<Row> dobj) {
        datasetRegistry.put(name, dobj);
    }
    private Map<String, Dataset<Row>> datasetRegistry = new HashMap<>();

}
