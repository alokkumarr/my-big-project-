package synchronoss.spark.functions.rt;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import synchronoss.data.countly.model.CountlyModel;
import synchronoss.data.generic.model.GenericJsonModel;
import synchronoss.spark.drivers.rt.EventProcessingApplicationDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asor0002 on 7/26/2016.
 *
 */
public class SaveBatchToDataLake implements VoidFunction2<JavaRDD<Row>, Time> {

    private static final Logger logger = Logger.getLogger(SaveBatchToDataLake.class);
    private static final long serialVersionUID = -1074162051977105430L;


    private String definitions;
    private String basePath;
    private StructType eventSchema = null;
    private String batchPrefix;
    private String outputType;

    public SaveBatchToDataLake(String basePath, String outputType, String batchPrefix, String definitions) {
        this.definitions = definitions;
        this.basePath = basePath;
        this.batchPrefix = batchPrefix + ".";
        this.outputType = outputType;
    }

    public SaveBatchToDataLake(String basePath, String outputType, String batchPrefix) {
        this.definitions = null;
        this.basePath = basePath;
        this.batchPrefix = batchPrefix + ".";
        this.outputType = outputType;
    }

    public void call(JavaRDD<Row> rdd, org.apache.spark.streaming.Time time) {

        if (eventSchema == null) {
            if(definitions == null)
                eventSchema = CountlyModel.createGlobalSchema();
            else
                eventSchema = GenericJsonModel.createGlobalSchema(definitions);
        }

        // For non-empty batch...
        if (!rdd.isEmpty()) {
            // Store it in file system as separate directory
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date batchDt = new Date(time.milliseconds());
            String batchName = batchPrefix + sdf.format(batchDt);
            SQLContext sqlContext = SQLContext.getOrCreate(rdd.context());
            String path = basePath + File.separator + batchName;
            String strTmpPath = basePath + File.separator + "_TMP - " + batchName;
            Dataset<Row> eventsDataFrame = sqlContext.createDataFrame(rdd, eventSchema);
            if(outputType.equals("parquet")) {
                eventsDataFrame
                        .write()
                        .mode(SaveMode.Append)
                        .parquet(strTmpPath);
            } else if(outputType.equals("json")) {
                eventsDataFrame
                        .write()
                        .mode(SaveMode.Append)
                        .json(strTmpPath);
            } else {
                logger.error("Invalid output type :" + outputType);
            }
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
                    if(!pathToDelete.getName().endsWith("." + outputType)) {
                        //fs.delete(pathToDelete, true);
                    }
                }
                // Results are ready - make it final
                fs.rename(tmpPath, new Path(path));
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }
}
