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
    private final String[] odi;

    public JaninoExecutor(SparkSession ctx,
                          String script,
                          StructType st,
                          String tLoc,
                          int thr,
                          Map<String, Map<String, Object>> inputs,
                          Map<String, Map<String, Object>> outputs,
                          String[]  odi) {
        super(ctx, script, st, tLoc, thr, inputs, outputs);
        this.odi = odi;
    }


    protected JavaRDD transformation(JavaRDD dataRDD) throws Exception {

        JavaRDD rdd = dataRDD.map(
                new JaninoTransform( session_ctx,
                        script,
                        schema,
                        successTransformationsCount,
                        failedTransformationsCount,
                        threshold, odi)).cache();
        return rdd;
    }


    public void execute(Map<String, Dataset> dsMap) throws Exception {

        Dataset ds = dsMap.get(inDataSet);
        JavaRDD transformationResult = transformation(ds.toJavaRDD()).cache();
        //Long c = transformationResult.count();
        //logger.trace("Intermediate result, transformation count  = " + c);
        // Using structAccumulator do second pass to align schema
        Dataset<Row> df = session_ctx.createDataFrame(transformationResult, schema).toDF();
        createFinalDS(df.cache());
    }

}
