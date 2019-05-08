package sncr.xdf.transformer.ng;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.ngcomponent.WithContext;
import sncr.xdf.transformer.JaninoTransform;

import java.util.Map;

/**
 * Created by srya0001 on 12/21/2017.
 */
public class NGJaninoExecutor extends NGExecutor{


    public static String PREDEFINED_SCRIPT_RESULT_KEY = "_script_result";
    public static String PREDEFINED_SCRIPT_MESSAGE = "_script_msg";

    private static final Logger logger = Logger.getLogger(NGJaninoExecutor.class);
    private final String[] odi;


    public NGJaninoExecutor(WithContext parent, String script, int threshold, String tLoc, StructType st, String[]  odi)  {
        super(parent, script, threshold, tLoc, st);
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

        Dataset ds = dsMap.get(inDataSetName);
        JavaRDD transformationResult = transformation(ds.toJavaRDD()).cache();
        logger.debug("Intermediate result, transformation count  = " + transformationResult.count());

        // Using structAccumulator do second pass to align schema
        Dataset<Row> df = session_ctx.createDataFrame(transformationResult, schema).toDF();
        createFinalDS(df.cache());
    }

}
