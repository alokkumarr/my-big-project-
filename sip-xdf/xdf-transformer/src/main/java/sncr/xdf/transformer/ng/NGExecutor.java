package sncr.xdf.transformer.ng;

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
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.context.DSMapKey;
import sncr.xdf.ngcomponent.WithContext;
import sncr.xdf.ngcomponent.WithDLBatchWriter;
import sncr.xdf.context.RequiredNamedParameters;
import sncr.xdf.context.NGContext;

import java.util.*;

import static sncr.xdf.transformer.ng.NGTransformerComponent.*;

public abstract class NGExecutor {

    protected StructType schema;
    protected final String script;
    protected final SparkSession session_ctx;
    protected final String tempLoc;
    protected final int threshold;
    protected final HashSet<String> refDataSets;
    protected final LongAccumulator successTransformationsCount;
    protected final LongAccumulator failedTransformationsCount;
    protected String rejectedDataSetName;
    protected String outDataSetName;
    protected String inDataSetName;
    protected Map<String, Broadcast<List<Row>>> refData;
    protected Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor;
    protected Dataset<Row> outputResult;
    protected Dataset<Row> rejectedRecords;

    protected WithContext parent;


    private static final Logger logger = Logger.getLogger(NGExecutor.class);


    public NGExecutor(WithContext parent, String script, int threshold, String tLoc, StructType st){
        logger.trace("Inside Executor");
        this.script = script;
        session_ctx = parent.getICtx().sparkSession;
        this.threshold = threshold;
        refDataSets = new HashSet<>();
        for( String inpK: parent.getNgctx().inputs.keySet()){
            if (inpK.equalsIgnoreCase(RequiredNamedParameters.Input.toString())){
                inDataSetName = inpK;
            }
            else{
                refDataSets.add(inpK);
            }
        }

        for( String outK: parent.getNgctx().outputs.keySet()){
            if (outK.equalsIgnoreCase(RequiredNamedParameters.Output.toString())){
                outDataSetName = outK;
            }
            else if(outK.equalsIgnoreCase(RequiredNamedParameters.Rejected.toString())){
                rejectedDataSetName = outK;
            }
        }

        tempLoc = tLoc;
        this.successTransformationsCount = session_ctx.sparkContext().longAccumulator("success");
        this.failedTransformationsCount = session_ctx.sparkContext().longAccumulator("failed");
        this.schema = st;
        refData = new HashMap<>();
        refDataDescriptor = new HashMap<>();
        this.parent = parent;
    }

    protected void writeResults(Dataset<Row> outputResult, String resType, String location) throws Exception {

        WithDLBatchWriter pres = (WithDLBatchWriter) parent;

        Map<String, Object> outputDS = parent.getNgctx().outputs.get(resType);
        String name = (String) outputDS.get(DataSetProperties.Name.name());
        String loc = location + Path.SEPARATOR + name;
        logger.debug("writeResults keySet is : " + outputDS.keySet());
        logger.debug("writeResults name is : " + name);
        logger.debug("writeResults location is : " + loc);
        pres.commitDataSetFromOutputMap(parent.getNgctx(), outputResult, resType, loc, "replace");
    }

    public abstract void execute(Map<String, Dataset> dsMap) throws Exception;


    protected void prepareRefData(Map<String, Dataset> dsMap){
        if (refDataSets != null && refDataSets.size() > 0) {
            JavaSparkContext jsc = new JavaSparkContext(session_ctx.sparkContext());
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
        outputResult = ds.filter( trRes.geq(0))
            .drop(trRes)
            .drop(trMsg)
            .drop(trRC);
        if (rejectedDataSetName != null && !rejectedDataSetName.isEmpty())
            rejectedRecords = ds.filter( trRes.lt(0));

        logger.debug("Final DS: " + successTransformationsCount.value());
        logger.debug("Rejected DS: " + failedTransformationsCount.value());

        writeResults(outputResult, outDataSetName, tempLoc);
        
        logger.debug("createFinalDS :: Rejected record exists? "+ rejectedDataSetName );
        if(rejectedDataSetName != null && !rejectedDataSetName.isEmpty()) {
        	writeResults(rejectedRecords, rejectedDataSetName, tempLoc);
        }
        

    }


    protected void createFinalDS(Dataset<Row> ds, NGContext ngctx) throws Exception {

        ds.schema();
        //getOutputDatasetDetails();
        Column trRes = ds.col(TRANSFORMATION_RESULT);
        Column trMsg = ds.col(TRANSFORMATION_ERRMSG);
        Column trRC = ds.col(RECORD_COUNTER);
        outputResult = ds.filter( trRes.geq(0))
            .drop(trRes)
            .drop(trMsg)
            .drop(trRC);

        ds.toDF().show(4);

        String transOutKey =  ngctx.componentConfiguration.getOutputs().get(0).getDataSet().toString();
        String transInKey =  ngctx.componentConfiguration.getInputs().get(0).getDataSet().toString();

        ngctx.datafileDFmap.put(transOutKey,outputResult);

        if (rejectedDataSetName != null && !rejectedDataSetName.isEmpty())
            rejectedRecords = ds.filter( trRes.lt(0));

        logger.trace("Final DS: " + outputResult.count() + " Schema: " + outputResult.schema().prettyJson());
        //logger.trace("Rejected DS: " + rejectedRecords.count() + " Schema: " + rejectedRecords.schema().prettyJson());

        writeResults(outputResult, outDataSetName, tempLoc);
        logger.debug("createFinalDS :: Rejected record exists? "+ rejectedDataSetName );
        if(rejectedDataSetName != null && !rejectedDataSetName.isEmpty()) {
        	writeResults(rejectedRecords, rejectedDataSetName, tempLoc);
        }
    }

}
