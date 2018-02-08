package sncr.xdf.transformer;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;
import sncr.xdf.transformer.system.StructAccumulator;

import java.util.*;

import static sncr.xdf.transformer.TransformerComponent.RECORD_COUNTER;
import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_ERRMSG;
import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;


/**
 * Created by srya0001 on 12/21/2017.
 */
public class JexlExecutor extends Executor{

    private static final Logger logger = Logger.getLogger(JexlExecutor.class);
    private StructAccumulator structAccumulator;



    public JexlExecutor(SparkSession ctx,
                        String script,
                        String tLoc,
                        int thr,
                        Map<String, Map<String, String>> inputs,
                        Map<String, Map<String, String>> outputs
                        )  {
        super(ctx,script,null,tLoc,thr,inputs,outputs);
        this.structAccumulator = new StructAccumulator();
        ctx.sparkContext().register(structAccumulator, "Struct");
    }



    private JavaRDD     transformation(
            JavaRDD dataRdd,
            Map<String, Broadcast<List<Row>>> referenceData,
            Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor) {
        JavaRDD rdd = dataRdd.map(
                new Transform(
                        script,
                        schema,
                        referenceData,
                        refDataDescriptor,
                        successTransformationsCount,
                        failedTransformationsCount,
                        structAccumulator,
                        threshold)).cache();
        return rdd;
    }


    public void execute(Map<String, Dataset> dsMap) throws Exception {

        Dataset ds = dsMap.get(inDataSet);
        logger.debug("Initialize structAccumulator: " );
        schema = ds.schema();
        String[] fNames = ds.schema().fieldNames();
        for (int i = 0; i < fNames.length; i++) {
            structAccumulator.add(new Tuple2<>(fNames[i], ds.schema().apply(i)));
            logger.trace("Field: " + fNames[i] + " Type: " + ds.schema().apply(i).toString());
        }
        prepareRefData(dsMap);
        JavaRDD transformationResult = transformation(ds.toJavaRDD(), refData, refDataDescriptor).cache();
        //Long firstPassTrRes = transformationResult.count();
        //logger.debug("First pass completed: " + firstPassTrRes);
        //logger.trace("Create new schema[" + structAccumulator.value().size() + "]: " + String.join(", ", structAccumulator.value().keySet()));
        StructType newSchema = constructSchema(structAccumulator.value());
        // Using structAccumulator do second pass to align schema
        Dataset<Row> alignedDF = schemaRealignment(transformationResult, newSchema);
        //Long c_adf = alignedDF.count();
        //String jschema = alignedDF.schema().prettyJson();
        //logger.debug("Second pass completed: " + c_adf + " Schema: " + jschema);
        createFinalDS(alignedDF);
    }



    private StructType constructSchema(Map<String, StructField> accValues) {
        HashSet<StructField> sf_set = new HashSet();
        accValues.values().forEach (sf_set::add);
        return new StructType(sf_set.toArray(new StructField[0]));
    }

    private Dataset<Row>  schemaRealignment(JavaRDD<Row> rdd, StructType newSchema) {
        JavaRDD<Row> alignedRDD = rdd.map(new SchemaAlignTransform( newSchema)).persist(StorageLevel.MEMORY_AND_DISK());
        return session_ctx.createDataFrame(alignedRDD, newSchema).toDF().cache();
    }

}
