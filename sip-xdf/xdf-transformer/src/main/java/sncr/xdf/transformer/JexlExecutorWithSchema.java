package sncr.xdf.transformer;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.List;
import java.util.Map;



/**
 * Created by srya0001 on 12/21/2017.
 */
public class JexlExecutorWithSchema extends Executor{

    private static final Logger logger = Logger.getLogger(JexlExecutorWithSchema.class);

    public JexlExecutorWithSchema(SparkSession ctx, String script, StructType st, String tLoc, int thr,
                                       Map<String, Map<String, Object>> inputs,
                                       Map<String, Map<String, Object>> outputs) {
        super(ctx, script, st, tLoc, thr, inputs, outputs);
        logger.trace("Inside JexlExecutorWithSchema");
    }

/*
    public JexlExecutorWithSchema(AbstractComponent parent, String script, int threshold, String tLoc, StructType st)  {
        super(parent, script, threshold, tLoc, st);
    }
*/

    protected JavaRDD     transformation(
            JavaRDD dataRdd,
            Map<String, Broadcast<List<Row>>> referenceData,
            Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor) {
        logger.trace("Transforming data");
        JavaRDD rdd = dataRdd.map(
                new TransformWithSchema(
                        script,
                        schema,
                        referenceData,
                        refDataDescriptor,
                        successTransformationsCount,
                        failedTransformationsCount,
                        threshold)).cache();
        return rdd;
    }

    public void execute(Map<String, Dataset> dsMap) throws Exception {
        logger.trace("Executing Jexl transformation");
        Dataset ds = dsMap.get(inDataSet);
        prepareRefData(dsMap);
        JavaRDD transformationResult = transformation(ds.toJavaRDD(), refData, refDataDescriptor).cache();
        // Using structAccumulator do second pass to align schema
        Dataset<Row> df = session_ctx.createDataFrame(transformationResult, schema).toDF();
        //df.schema().prettyJson();
        createFinalDS(df.cache());
    }


}
