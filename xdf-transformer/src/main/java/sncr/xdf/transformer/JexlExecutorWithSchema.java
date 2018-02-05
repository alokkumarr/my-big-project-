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
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.transformer.system.StructAccumulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_ERRMSG;
import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;


/**
 * Created by srya0001 on 12/21/2017.
 */
public class JexlExecutorWithSchema extends Executor{

    private static final Logger logger = Logger.getLogger(JexlExecutorWithSchema.class);

    public JexlExecutorWithSchema(SparkSession ctx, String script, StructType st, String tLoc, int thr, Map<String, Map<String, String>> inputs, Map<String, Map<String, String>> outputs) {
        super(ctx, script, st, tLoc, thr, inputs, outputs);
    }


    protected JavaRDD     transformation(
            JavaRDD dataRdd,
            Map<String, Broadcast<Dataset<Row>>> referenceData
    )  throws Exception {
        String bcFirstRefDataset = (refDataSets != null && refDataSets.size() > 0)?refDataSets.toArray(new String[0])[0]:"";
        JavaRDD rdd = dataRdd.map(
                new TransformWithSchema( session_ctx,
                        script,
                        schema,
                        referenceData,
                           successTransformationsCount,
                           failedTransformationsCount,
                           threshold)).cache();
        return rdd;
    }

    public void execute(Map<String, Dataset> dsMap) throws Exception {

        Dataset ds = dsMap.get(inDataSet);

        logger.trace("Load reference data: " );
        Map<String, Broadcast<Dataset<Row>>> mapOfRefData = new HashMap<>();
        if (refDataSets != null && refDataSets.size() > 0) {
            for (String refDataSetName: refDataSets) {
                mapOfRefData.put(refDataSetName, jsc.broadcast(dsMap.get(refDataSetName)));
            }
        }

        JavaRDD transformationResult = transformation(ds.toJavaRDD(), mapOfRefData);
        Long c = transformationResult.count();

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
