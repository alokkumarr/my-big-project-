package sncr.xdf.transformer;

import org.apache.log4j.Logger;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.apache.spark.Accumulator;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexey.sorokin on 11/5/2015.
 */
public class OutputRecordToParquetConverter implements
        PairFunction<Map<String, Object>, String, Tuple2<Void, Group>> {
    private static final Logger logger = Logger.getLogger(OutputRecordToParquetConverter.class);

    private static final long serialVersionUID = 8519617592998987032L;
    private final JavaSparkContext jsc;
    private final String datasetName;

    //  private Broadcast<ObjectSchema> objectSchema;
    private MessageType schema = null;
    private SimpleGroupFactory factory = null;
    private String parquetSchemaString;
    private Accumulator<Integer> conversionErrorCount = null;

    public OutputRecordToParquetConverter(
              String dataSetName,
             JavaSparkContext jsc,
             Map<String, Object> firstRow,
            Accumulator<Integer> conversionErrorCount) throws Exception {
//        this.objectSchema = objectSchema;
        datasetName = dataSetName;
        this.jsc = jsc;
        this.conversionErrorCount = conversionErrorCount;
        this.parquetSchemaString = createParquetSchema(dataSetName, firstRow);
        if(factory == null || schema == null){
            schema = MessageTypeParser.parseMessageType(parquetSchemaString);
            factory = new SimpleGroupFactory(schema);
        }
    }

    @Override
    public Tuple2<String, Tuple2<Void, Group>> call(Map<String, Object> values) throws Exception {
        Tuple2<String, Tuple2<Void, Group>> output = null;
        try {
            Group out = factory.newGroup();
            for ( String fn: values.keySet()) {
                // By that time record only contains fields for output
                // all other fields removed in previous stages
                out = convertToParquet(fn, values.get(fn), out);

            }
            Tuple2<Void, Group> tmp = new Tuple2<>(null, out);
            output = new Tuple2<>("success", tmp);
        }catch(Exception e){
            Tuple2<Void, Group> tmp = new Tuple2<>(null, null);
            output = new Tuple2<>("failed, <reason>", tmp);
        }
        return output;
    }

    public static String createParquetSchema(String dsName, Map<String, Object> firstRow) throws Exception {
        StringBuilder schemaBuilder = new StringBuilder();
        schemaBuilder.append("message ").append(dsName).append(" { \n");

        Map<String,String> fields = new HashMap<String,String>();
        for(String fn : firstRow.keySet()){
            Object v = firstRow.get(fn);
            if (v instanceof String ) {
                schemaBuilder.append("  optional binary  ").append(fn).append("(UTF8); \n");
            }else if (v instanceof Integer) {
                schemaBuilder.append("  optional int32   ").append(fn).append("; \n");
            }else if (v instanceof Double){
                schemaBuilder.append("  optional double  ").append(fn).append("; \n");
            }else if (v instanceof Long){
                schemaBuilder.append("  optional int64   ").append(fn).append("; \n");
            }else if (v instanceof Timestamp){

            }else if (v instanceof Date) {

            }else if (v instanceof Boolean) {
                schemaBuilder.append("  optional boolean ").append(fn).append("; \n");
            }else{
                    throw new Exception("Unsupported data type: " + v.getClass().getName());
            }
        }
        schemaBuilder.append("} \n");
//        System.out.println(schemaBuilder.toString());
        return schemaBuilder.toString();
    }

    public static final String EMPTY_STRING = "";
    final static String FMT_yyyyMMdd = "yyyyMMdd";
    final static String FMT_HHmmss = "HHmmss";
    final static String STR_null = "null";
    final static String XDF_DATE = "_xdfDate";
    final static String XDF_TIME = "_xdfTime";

    private static Group convertToParquet(String fieldName,
                                          Object fieldValue,
//                                          String valuePattern,
                                          Group outputParquetFormat ) throws Exception {
        try {
            if (fieldValue instanceof String)
                outputParquetFormat.append(fieldName, (String)fieldValue);
            else if (fieldValue instanceof Long)
                outputParquetFormat.append(fieldName, (Long)fieldValue);
            else if (fieldValue instanceof Integer)
                outputParquetFormat.append(fieldName, (Integer)fieldValue);
            else if (fieldValue instanceof Boolean)
                outputParquetFormat.append(fieldName, (Boolean)fieldValue);
            else if (fieldValue instanceof Double)
                outputParquetFormat.append(fieldName, (Double)fieldValue);
//            else if (fieldValue instanceof Timestamp)
//                outputParquetFormat.append(fieldName, (Timestamp)fieldValue);
//            else if (fieldValue instanceof Date)
//                outputParquetFormat.append(fieldName, (Timestamp)fieldValue);
            else {
                throw new Exception("Invalid data type : " + fieldValue.getClass().getName());
            }
        } catch (Exception e) {
            String err_msg = "Error parsing " + fieldName + ": "+ e.getMessage();
            Exception new_e = new Exception(  err_msg );
            throw new_e;
        }
        return outputParquetFormat;
    }

    private static Tuple2<String, String> convertTimestamp(String value, String pattern) throws Exception {
        String xdfDate = EMPTY_STRING;
        String xdfTime = EMPTY_STRING;


        if(value != null && !(value.trim().isEmpty()) && !(value.trim().equals(STR_null))) {
            SimpleDateFormat srcFormat = new SimpleDateFormat(pattern);
	    srcFormat.setLenient(false);
            SimpleDateFormat dateFormat = new SimpleDateFormat(FMT_yyyyMMdd);
            SimpleDateFormat timeFormat = new SimpleDateFormat(FMT_HHmmss);
            Date tmp = srcFormat.parse(value);
            xdfDate = dateFormat.format(tmp);
            xdfTime = timeFormat.format(tmp);
        }
        return new Tuple2<String, String>(xdfDate, xdfTime);
    }

    private static String convertDate(String value, String pattern) throws Exception {
        String xdfDate = EMPTY_STRING;

        if(value != null && !value.trim().isEmpty() && !value.trim().equals(STR_null)) {
            SimpleDateFormat srcFormat = new SimpleDateFormat(pattern);
	    srcFormat.setLenient(false);
            SimpleDateFormat dateFormat = new SimpleDateFormat(FMT_yyyyMMdd);
            Date tmp = srcFormat.parse(value);
            xdfDate = dateFormat.format(tmp);
        }
        return xdfDate;
    }

}
