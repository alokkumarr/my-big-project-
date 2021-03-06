package sncr.xdf.transformer;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.Script;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;
import sncr.xdf.transformer.jexl.DataManipulationUtil;
import sncr.xdf.transformer.jexl.XdfObjectContext;
import sncr.xdf.transformer.jexl.XdfObjectContextWithStaticSchema;
import sncr.xdf.transformer.system.StructAccumulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srya0001 on 1/5/2018.
 */
public class TransformWithSchema implements Function<Row, Row> {

    private static final Logger logger = Logger.getLogger(TransformWithSchema.class);

    private final LongAccumulator successTransformationsCount;
    private final LongAccumulator failedTransformationsCount;
    private final StructType schema;
    private final int threshold;
    private final Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor;
    private Map<String, Broadcast<List<Row>>> mapRefData = null;

    private Map<String, Object> extFunctions;
    private JexlEngine jexlEngine = null;
    private String script;
    private Script jexlScript;

    public TransformWithSchema(String scr,
                               StructType inSchema,
                               Map<String, Broadcast<List<Row>>> mapRefData,
                               Map<String, Broadcast<List<Tuple2<String, String>>>> refDataDescriptor,
                               LongAccumulator successTransformationsCount,
                               LongAccumulator failedTransformationsCount,
                               int threshold)
    {
        script = scr;
        schema = inSchema;
        this.successTransformationsCount = successTransformationsCount;
        this.failedTransformationsCount = failedTransformationsCount;
        this.mapRefData = mapRefData;
        this.refDataDescriptor = refDataDescriptor;
        this.threshold = threshold;

    }

    public Row call(Row arg0) throws Exception {
        Row return_value;
        if ( threshold != 0 && failedTransformationsCount.value() > threshold)
            throw new Exception(String.format("Number of invalid records [%d] exceeds threshold value [%d]", failedTransformationsCount.value(), threshold));

        try {
            if (jexlEngine == null) {
                // Initialize
                jexlEngine = new JexlEngine();
                extFunctions = new HashMap<String, Object>();
                extFunctions.put("Integer", new Integer(0));
                extFunctions.put("Double", new Double(0.0));
                extFunctions.put("String", new String(""));
                extFunctions.put("extFunctions", new DataManipulationUtil());
                logger.debug("Jexl script: " + script);
                jexlEngine.setLenient(true);
                if (this.mapRefData != null && !this.mapRefData.isEmpty()) {
                       extFunctions.put("ref", new sncr.xdf.transformer.jexl.DataScanner(mapRefData, refDataDescriptor));
                }
                jexlEngine.setFunctions(extFunctions);
                jexlScript = jexlEngine.createScript(script);

            }

            XdfObjectContextWithStaticSchema sc1 = new XdfObjectContextWithStaticSchema( jexlEngine, schema, arg0);
                jexlScript.execute(sc1);
            if(sc1.isSuccess()) {
                successTransformationsCount.add(1);
                // Transformation finished - create resulting Tuple
                return_value = sc1.createNewRow(0, null, successTransformationsCount.value());
                Map<String, StructField> newSchema = sc1.getNewOutputSchema();

            } else {
                // Error flag has been set inside the JEXL transformation
                // Mark record with error state
                failedTransformationsCount.add(1);
                return_value = sc1.createNewRow(-3, "Record has been invalidated inside the JEXL script", failedTransformationsCount.value());
                Map<String, StructField> newSchema = sc1.getNewOutputSchema();

            }
        } catch (Exception e) {
            logger.error("Jexl script execution exception: ", e);
            failedTransformationsCount.add(1);
            Row  rv = XdfObjectContextWithStaticSchema.createNewRow2(schema, arg0, -2, e.getMessage(), failedTransformationsCount.value());

            return_value = rv;
        }
        if (threshold > 0 && threshold < failedTransformationsCount.value()){
            throw new RuntimeException("# of failed records exceeded given threshold, cancel processing");
        }

        return return_value;
    }

}
