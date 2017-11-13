package sncr.xdf.context;

import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import sncr.xdf.conf.ComponentConfiguration;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
public class Context {

    private static final Logger logger = Logger.getLogger(Context.class);

    static final String DATE_FMT = "yyyyMMdd-HHmmss";
    static final SimpleDateFormat format = new SimpleDateFormat(DATE_FMT);
    public String finishedTs;
    static final String defaultFsScheme = "hdfs";
    public final String applicationID;
    public final String user = "A_user";
    public final String transformationName = "A_transformation";


    public ComponentConfiguration componentConfiguration;
    public String batchID;
    public FileContext fc;
    public FileSystem fs = null;

    public SparkSession sparkSession = null;
    public SparkConf sparkConf;

    public String startTs;
    public String componentName;

    {
        this.startTs = new SimpleDateFormat("yyyyMMdd-HHmmss")
                .format(new Timestamp(new java.util.Date().getTime()));
    }

    public Context(String componentName,
                   String batchId,
                   String appId,
                   ComponentConfiguration compConf) throws Exception {

        this.componentName = componentName;
        this.batchID = batchId;
        this.applicationID = appId;
        this.componentConfiguration = compConf;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Component: ").append(componentName).append("\n")
                .append("Application ID: ").append(applicationID).append("\n")
                .append("User: ").append(user).append("\n")
                .append("Transformation: ").append(transformationName).append("\n")
                .append(((sparkSession != null) ? " (Spark application) " : " (Non-Spark application) ")).append("\n")
                .append("Batch ID: ").append(batchID).append("\n")
                .append("Start: ").append(startTs).append("\n");

        s.append("Configuration parameters: \n");
        componentConfiguration.getParameters().forEach( p ->
        { if (p != null) s.append("Name: ").append(p.getName()).append(" Value: ").append(p.getValue()).append("\n"); });
        s.append("Input: \n");
        componentConfiguration.getInputs().forEach(p ->
        { if (p != null) s
                .append(" Object: ").append(p.getDataSet())
                .append(" Name: ").append(p.getName())
                .append(" Format: ").append(p.getFormat())
                .append(" File mask: ").append(p.getFileMask())
                .append(" Project: ").append(p.getProject())
                .append("\n");});
        s.append("Output: \n");
        componentConfiguration.getOutputs().forEach(p ->
        { if (p != null) s
                .append(" Object: ").append(p.getDataSet())
                .append(" Name: ").append(p.getName())
                .append(" Format: ").append(p.getFormat())
                .append(" File mask: ").append(p.getFileMask())
                .append(" Mode: ").append(p.getMode())
                .append("\n");});
        return s.toString();
    }

    protected void finalize() {
        try {
            fs.close();
            sparkSession.close();
        } catch (IOException e) {
            logger.error("Could not close file system: ", e);
        }
        if (sparkSession != null) sparkSession.stop();
    }

    public void setFinishTS() {
        finishedTs = format.format(new Timestamp(new java.util.Date().getTime()));
    }
}
