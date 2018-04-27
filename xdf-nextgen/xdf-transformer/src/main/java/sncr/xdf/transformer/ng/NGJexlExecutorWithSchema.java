package sncr.xdf.transformer.ng;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.ngcomponent.WithContext;
import sncr.xdf.transformer.TransformWithSchema;

import java.util.List;
import java.util.Map;


/**
 * Created by srya0001 on 12/21/2017.
 */
public class NGJexlExecutorWithSchema extends NGExecutor{

    private static final Logger logger = Logger.getLogger(NGJexlExecutorWithSchema.class);

    public NGJexlExecutorWithSchema(WithContext parent, String script, int threshold, String tLoc, StructType st)  {
        super(parent, script, threshold, tLoc, st);
    }

    protected JavaRDD     transformation(
            JavaRDD dataRdd,
            Map<String, Broadcast<List<Row>>> referenceData,
            Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor) {
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
        Dataset ds = dsMap.get(inDataSetName);
        prepareRefData(dsMap);
        JavaRDD transformationResult = transformation(ds.toJavaRDD(), refData, refDataDescriptor).cache();
        Long c = transformationResult.count();
        // Using structAccumulator do second pass to align schema
        Dataset<Row> df = session_ctx.createDataFrame(transformationResult, schema).toDF();
        //df.schema().prettyJson();
        logger.trace("Transformation completed: " + c + " Schema: " + df.schema().prettyJson());
        createFinalDS(df.cache());
    }


}
