package sncr.xdf.transformer;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import sncr.bda.datasets.conf.DataSetProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;

public abstract class Executor {

    protected final StructType schema;
    protected final String script;
    protected final SparkSession session_ctx;
    protected final String tempLoc;
    protected final int threshold;
    protected final HashSet<String> refDataSets;
    protected final JavaSparkContext jsc;
    protected final Map<String, Map<String, String>> outputDataSetsDesc;
    protected final LongAccumulator successTransformationsCount;
    protected final LongAccumulator failedTransformationsCount;
    protected String rejectedDataSet;
    protected String outDataSet;
    protected String inDataSet;

    private static final Logger logger = Logger.getLogger(Executor.class);

    public Executor(SparkSession ctx,
                          String script,
                          StructType st,
                          String tLoc,
                          int thr,
                          Map<String, Map<String, String>> inputs,
                          Map<String, Map<String, String>> outputs){
        this.script = script;
        session_ctx = ctx;
        threshold = thr;
        refDataSets = new HashSet<>();
        jsc = new JavaSparkContext(ctx.sparkContext());
        for( String inpK: inputs.keySet()){
            if (inpK.equalsIgnoreCase(RequiredNamedParameters.Input.toString())){
                inDataSet = inpK;
            }
            else{
                refDataSets.add(inpK);
            }
        }

        for( String outK: outputs.keySet()){
            if (outK.equalsIgnoreCase(RequiredNamedParameters.Output.toString())){
                outDataSet = outK;
            }
            else if(outK.equalsIgnoreCase(RequiredNamedParameters.Rejected.toString())){
                rejectedDataSet = outK;
            }
        }

        this.outputDataSetsDesc = outputs;
        tempLoc = tLoc;
        this.successTransformationsCount = ctx.sparkContext().longAccumulator("success");
        this.failedTransformationsCount = ctx.sparkContext().longAccumulator("failed");
        this.schema = st;
    }

    protected void writeResults(Dataset<Row> outputResult, String resType, String location) throws IOException {

        Map<String, String> outputDS = outputDataSetsDesc.get(resType);
        String name = outputDS.get(DataSetProperties.Name.name());
        String loc = location + Path.SEPARATOR + name;
        String format = outputDS.get(DataSetProperties.Format.name());
        logger.debug("Write result to location: " + loc + " in format: " + format);

        switch (format.toLowerCase()) {
            case "parquet" :
                outputResult.write().parquet(loc);
                break;
            case "csv" :
                outputResult.write().csv(loc);
            case "json" :
                outputResult.write().json(loc);
                break;
        }
    }


}
