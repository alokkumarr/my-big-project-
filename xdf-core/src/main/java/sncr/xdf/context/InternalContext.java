package sncr.xdf.context;

import com.google.gson.JsonElement;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.context.ContextMetadata;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.adapters.writers.MoveDataDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public boolean extSparkCtx = false;
    public int globalFileCount;

    public void registerDataset(String name, Dataset<Row> dobj) {
        datasetRegistry.put(name, dobj);
    }
    private Map<String, Dataset<Row>> datasetRegistry = new HashMap<>();


    protected void finalize() {
        if (!extSparkCtx) {
            try {
                fs.close();
                sparkSession.close();

            } catch (IOException e) {
                logger.error("Could not close file system: ", e);
            }
            if (sparkSession != null) sparkSession.stop();
        }
    }

}
