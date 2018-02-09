package sncr.xdf.transformer;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;

import java.util.*;

import static sncr.xdf.transformer.TransformerComponent.RECORD_COUNTER;
import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_ERRMSG;
import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;

public abstract class Executor {

    protected StructType schema;
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
    protected Map<String, Broadcast<List<Row>>> refData;
    protected Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor;
    protected Dataset<Row> outputResult;
    protected Dataset<Row> rejectedRecords;

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
        refData = new HashMap<>();
        refDataDescriptor = new HashMap<>();
    }

    protected void writeResults(Dataset<Row> outputResult, String resType, String location) throws Exception {

        Map<String, String> outputDS = outputDataSetsDesc.get(resType);
        String name = outputDS.get(DataSetProperties.Name.name());
        String loc = location + Path.SEPARATOR + name;
        String format = outputDS.get(DataSetProperties.Format.name());
        logger.debug("Write result to location: " + loc + " in format: " + format + ". if dataset already exists - remove it first");

        if (HFileOperations.exists(loc)) HFileOperations.deleteEnt(loc);

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

    public abstract void execute(Map<String, Dataset> dsMap) throws Exception;


    protected void prepareRefData(Map<String, Dataset> dsMap){

        if (refDataSets != null && refDataSets.size() > 0) {
            for (String refDataSetName: refDataSets) {
                logger.debug("Load reference data: " + refDataSetName);
                Dataset ds = dsMap.get(refDataSetName);
                StructField[] ds_schema = ds.schema().fields();
                List<Tuple2<String, String>> list = new ArrayList<>();
                for (int i = 0; i < ds_schema.length; i++) {
                    list.add( new Tuple2(ds_schema[i].name(), ds_schema[i].dataType().toString()));
                }
                Broadcast<List<Row>> rows = jsc.broadcast(ds.collectAsList());
                refData.put(refDataSetName, rows);
                Broadcast<List<Tuple2<String, String>>> blist = jsc.broadcast(list);
                refDataDescriptor.put(refDataSetName, blist);
            }
        }

    }

    protected void createFinalDS(Dataset<Row> ds) throws Exception {
        ds.schema();
        Column trRes = ds.col(TRANSFORMATION_RESULT);
        Column trMsg = ds.col(TRANSFORMATION_ERRMSG);
        Column trRC = ds.col(RECORD_COUNTER);
        outputResult = ds.filter( trRes.equalTo(0))
                .drop(trRes)
                .drop(trMsg)
                .drop(trRC);
        if (rejectedDataSet != null && !rejectedDataSet.isEmpty())
            rejectedRecords = ds.filter( trRes.lt(0));

        logger.trace("Final DS: " + outputResult.count() + " Schema: " + outputResult.schema().prettyJson());
        logger.trace("Rejected DS: " + rejectedRecords.count() + " Schema: " + rejectedRecords.schema().prettyJson());

        writeResults(outputResult, outDataSet, tempLoc);
        writeResults(rejectedRecords, rejectedDataSet, tempLoc);

    }

}
