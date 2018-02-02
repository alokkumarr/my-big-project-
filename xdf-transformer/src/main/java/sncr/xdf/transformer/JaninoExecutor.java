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
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.LongAccumulator;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IScriptEvaluator;
import sncr.bda.datasets.conf.DataSetProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;

/**
 * Created by srya0001 on 12/21/2017.
 */
public class JaninoExecutor extends Executor{


    public static String PREDEFINED_SCRIPT_RESULT_KEY = "_script_result";
    public static String PREDEFINED_SCRIPT_MESSAGE = "_script_msg";

    private static final Logger logger = Logger.getLogger(JaninoExecutor.class);

    public JaninoExecutor(SparkSession ctx, String script, StructType st, String tLoc, int thr, Map<String, Map<String, String>> inputs, Map<String, Map<String, String>> outputs) {
        super(ctx, script, st, tLoc, thr, inputs, outputs);
    }


    protected JavaRDD transformation(JavaRDD dataRDD, String[] odi) throws Exception {

        JavaRDD rdd = dataRDD.map(
                new JaninoTransform( session_ctx,
                        script,
                        schema,
                        successTransformationsCount,
                        failedTransformationsCount,
                        threshold, odi)).cache();
        return rdd;
    }


    public void execute(Map<String, Dataset> dsMap, String[]  odi) throws Exception {

        Dataset ds = dsMap.get(inDataSet);

        JavaRDD transformationResult = transformation(ds.toJavaRDD(), odi);
        Long c = transformationResult.count();
        logger.debug("Intermediate result, transformation count  = " + c);

        // Using structAccumulator do second pass to align schema
        Dataset<Row> df = session_ctx.createDataFrame(transformationResult, schema).toDF();
        //df.schema().prettyJson();

        logger.trace("Transformation completed: " + df.count() + " Schema: " + df.schema().prettyJson());

        //TODO:: Should we drop RECORD_COUNT as well???
        Column trRes = df.col(TRANSFORMATION_RESULT);
        Dataset<Row> outputResult = df.select("*").where(trRes.equalTo(0)); //.drop(trRes).drop(alignedDF.col(TRANSFORMATION_ERRMSG));
        Dataset<Row> rejectedRecords = df.select("*").where(trRes.lt(0));



        logger.debug("Final DS: " + outputResult.count() + " Schema: " + outputResult.schema().prettyJson());
        logger.debug("Rejected DS: " + rejectedRecords.count() + " Schema: " + rejectedRecords.schema().prettyJson());

        logger.trace("Save results to temporary location: " );
        writeResults(outputResult, outDataSet, tempLoc);
        writeResults(rejectedRecords, rejectedDataSet, tempLoc);

    }

}
