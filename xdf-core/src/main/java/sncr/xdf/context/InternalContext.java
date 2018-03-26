package sncr.xdf.context;

import com.google.gson.JsonElement;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.context.ContextMetadata;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.adapters.writers.MoveDataDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class InternalContext extends ContextMetadata {

    private static final Logger logger = Logger.getLogger(Context.class);


    //Metadata services
    public ArrayList<MoveDataDescriptor> resultDataDesc;

    public Map<String, JsonElement> mdOutputDSMap;
    public Map<String, JsonElement> mdInputDSMap;

    public String transformationID;
    public final String xdfDataRootSys;
    public SparkSession sparkSession;
    public boolean extSparkCtx = false;
    public int globalFileCount;


    public InternalContext(String componentName,
                   String batchId,
                   String appId,
                   ComponentConfiguration compConf,
                           String xdfRoot ) throws Exception {
        super(componentName, batchId, appId, compConf);
        xdfDataRootSys = xdfRoot;

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Component: ").append(componentName).append("\n")
                .append("Application ID: ").append(applicationID).append("\n")
                .append("User: ").append(user).append("\n")
                .append("Transformation: ").append(transformationName).append("\n")
                .append("Start: ").append(startTs).append("\n");

        s.append("Configuration parameters: \n");
        componentConfiguration.getParameters().forEach(p ->
        {
            if (p != null) s.append("Name: ").append(p.getName()).append(" Value: ").append(p.getValue()).append("\n");
        });
        s.append("Input: \n");
        componentConfiguration.getInputs().forEach(p ->
        {
            if (p != null) s
                    .append(" Object: ").append(p.getDataSet())
                    .append(" Name: ").append(p.getName())
                    .append(" Format: ").append(p.getFormat())
                    .append(" File mask: ").append(p.getFileMask())
                    .append(" Project: ").append(p.getProject())
                    .append("\n");
        });
        s.append("Output: \n");
        componentConfiguration.getOutputs().forEach(p ->
        {
            if (p != null) s
                    .append(" Object: ").append(p.getDataSet())
                    .append(" Name: ").append(p.getName())
                    .append(" Format: ").append(p.getFormat())
                    .append(" Mode: ").append(p.getMode())
                    .append("\n");
        });
        return s.toString();
    }

    protected void finalize() {
        if (!extSparkCtx) {
            try {
                HFileOperations.fs.close();
                sparkSession.close();

            } catch (IOException e) {
                logger.error("Could not close file system: ", e);
            }
            if (sparkSession != null) sparkSession.stop();
        }
    }

}
