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
import sncr.xdf.datasets.conf.DataSetProperties;
import sncr.xdf.transformer.system.StructAccumulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_ERRMSG;
import static sncr.xdf.transformer.TransformerComponent.TRANSFORMATION_RESULT;


/**
 * Created by srya0001 on 12/21/2017.
 */
public class JexlExecutor {

    private static final Logger logger = Logger.getLogger(JexlExecutor.class);
    private final String[] refDataSets;
    private final String inDataSet;
    private final String outDataSet;
    private final String rejectedDataSet;
    private final Map<String, Map<String, String>> outputDataSetsDesc;
    private final StructAccumulator structAccumulator;
    private final String tempLoc;
    private JavaSparkContext jsc;
    private SparkSession session_ctx;
    private String script;
    private StructType schema;
    private LongAccumulator successTransformationsCount = null;
    private LongAccumulator failedTransformationsCount = null;


    public JexlExecutor(SparkSession ctx,
                        String script,
                        String inputDataSet,
                        String[] refDataSets,
                        String outDataSet,
                        String rejectedDataSet,
                        String tLoc,
                        Map<String, Map<String, String>> outputDataSets){
        this.script = script;
        session_ctx = ctx;
        jsc = new JavaSparkContext(ctx.sparkContext());
        this.refDataSets = refDataSets;
        inDataSet = inputDataSet;
        this.outDataSet = outDataSet;
        this.rejectedDataSet = rejectedDataSet;
        this.outputDataSetsDesc = outputDataSets;
        tempLoc = tLoc;
        this.successTransformationsCount = ctx.sparkContext().longAccumulator("success");
        this.failedTransformationsCount = ctx.sparkContext().longAccumulator("failed");
        this.structAccumulator = new StructAccumulator();
        ctx.sparkContext().register(structAccumulator, "Struct");
    }


    private JavaRDD     transformation(
            JavaRDD dataRdd,
            Map<String, Broadcast<Dataset>> referenceData
    )  throws Exception {
        String bcFirstRefDataset = (refDataSets != null && refDataSets.length > 0)?refDataSets[0]:"";
        JavaRDD rdd = dataRdd.map(
                new Transform( session_ctx,
                        script,
                        schema,
                        referenceData,
                           successTransformationsCount,
                           failedTransformationsCount,
                           structAccumulator,
                           bcFirstRefDataset)).cache();
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

        logger.trace("Load reference data: " );
        Map<String, Broadcast<Dataset>> mapOfRefData = new HashMap<>();
        if (refDataSets != null && refDataSets.length > 0) {
            for (int i = 0; i < refDataSets.length; i++) {
                mapOfRefData.put(refDataSets[i], jsc.broadcast(dsMap.get(refDataSets[i])));
            }
        }

        JavaRDD transformationResult = transformation(ds.toJavaRDD(), mapOfRefData);
        transformationResult.count();

//        logger.trace("First pass completed: " + transformationResult.count() + " first row/object: " + transformationResult.first().toString());
        logger.trace("Create new schema[" + structAccumulator.value().size() + "]: " + String.join(", ", structAccumulator.value().keySet()));

        StructType newSchema = constructSchema(structAccumulator.value());

        // Using structAccumulator do second pass to align schema
        Dataset<Row> alignedDF = schemaRealignment(transformationResult, newSchema);
        alignedDF.count();
        alignedDF.schema().prettyJson();

        logger.trace("Second pass completed: " + alignedDF.count() + " Schema: " + alignedDF.schema().prettyJson());


        //TODO:: Should we drop RECORD_COUNT as well???
        Column trRes = alignedDF.col(TRANSFORMATION_RESULT);
        Dataset<Row> outputResult = alignedDF.filter( trRes.equalTo(0)).drop(trRes).drop(alignedDF.col(TRANSFORMATION_ERRMSG));
        Dataset<Row> rejectedRecords = alignedDF.filter( trRes.lt(0));

        logger.debug("Final DS: " + outputResult.count() + " Schema: " + outputResult.schema().prettyJson());
        logger.debug("Rejected DS: " + rejectedRecords.count() + " Schema: " + rejectedRecords.schema().prettyJson());

        logger.trace("Save results to temporary location: " );
        writeResults(outputResult, outDataSet, tempLoc);
        writeResults(rejectedRecords, rejectedDataSet, tempLoc);

    }

    private StructType constructSchema(Map<String, StructField> accValues) {
        HashSet<StructField> sf_set = new HashSet();
        accValues.values().forEach (sf_set::add);
        return new StructType(sf_set.toArray(new StructField[0]));
    }

    private Dataset<Row>  schemaRealignment(JavaRDD<Row> rdd, StructType newSchema) {
        JavaRDD<Row> alignedRDD = rdd.map(new SchemaAlignTransform( newSchema)).persist(StorageLevel.MEMORY_AND_DISK());
        return session_ctx.createDataFrame(alignedRDD, newSchema).toDF();
    }



    //    private void writeResults(JavaPairRDD outputResult, String resType) throws IOException {
    private void writeResults(Dataset<Row> outputResult, String resType, String location) throws IOException {

/*
        org.apache.hadoop.conf.Configuration jobConf = new org.apache.hadoop.conf.Configuration();
        Job job = Job.getInstance(jobConf);
        CustomParquetOutputFormat.setCompression(job, CompressionCodecName.SNAPPY);
*/

        Map<String, String> outputDS = outputDataSetsDesc.get(resType);
        String name = outputDS.get(DataSetProperties.Name.name());
        String loc = location + Path.SEPARATOR + name;
        String format = outputDS.get(DataSetProperties.Format.name());
        logger.debug("Write result to location: " + loc + " in format: " + format);

        switch (format.toLowerCase()) {
            case "parquet" :
                outputResult.write().parquet(loc);
/*
                outputResult.saveAsNewAPIHadoopFile(loc,
                        NullWritable.class,
                        Group.class,
                        CustomParquetOutputFormat.class,
                        job.getConfiguration());
*/
                break;
            case "csv" :
                outputResult.write().csv(loc);
            case "json" :
                outputResult.write().json(loc);
//                outputResult.saveAsTextFile(loc);
                break;
        }
    }

}
