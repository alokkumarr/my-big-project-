package sncr.xdf.context;

import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.context.ContextMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class NGContext extends ContextMetadata {

    public ComponentConfiguration componentConfiguration;

    public final String xdfDataRootSys;
    public List<String> registeredOutputDSIds = new ArrayList<>();

    public NGContext(String xdfRoot,  ComponentConfiguration componentConfiguration, String applicationID, String componentName, String batchID){
        super(componentName, batchID, applicationID);
        xdfDataRootSys = xdfRoot;
        this.componentConfiguration  = componentConfiguration;
        this.componentName = componentName;
    }


    public Map<String, Map<String, Object>> inputDataSets = new HashMap<>();
    public Map<String, Map<String, Object>> outputDataSets = new HashMap<>();

    public Map<String, Map<String, Object>> inputs = new HashMap<>();
    public Map<String, Map<String, Object>> outputs = new HashMap<>();

    @Override
    public String toString(){

        StringBuilder s = new StringBuilder();
        s.append("Configuration parameters: \n");
        s.append("User: ").append(user).append("\n");
        s.append("Application ").append(applicationID);
        s.append("Component Name ").append(componentName);
        s.append("Batch ID").append(batchID).append("\n");
        s.append("Transformation: ").append(transformationName).append("\n");

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

        s.append("Services:");
        serviceStatus.forEach( (k, v) -> s.append(k.name()).append(" => ").append(v).append("\n"));

        s.append("Pre-registered DS: ");
        registeredOutputDSIds.forEach( id -> s.append(id).append(", "));
        s.append("\n");

        s.append("Input DS maps: ");
        s.append(inputDataSets.toString());
        s.append("\n");

        s.append("Output DS maps: ");
        s.append(outputDataSets.toString());
        s.append("\n");

        return s.toString();
    }

    public Map<ComponentServices, Boolean> serviceStatus = new HashMap<>();

}
