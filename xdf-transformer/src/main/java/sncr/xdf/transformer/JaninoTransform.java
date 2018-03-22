package sncr.xdf.transformer;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IScriptEvaluator;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static sncr.xdf.transformer.JaninoExecutor.PREDEFINED_SCRIPT_MESSAGE;
import static sncr.xdf.transformer.JaninoExecutor.PREDEFINED_SCRIPT_RESULT_KEY;
import static sncr.xdf.transformer.jexl.XdfObjectContextBase.findField;

public class JaninoTransform implements Function<Row, Row> {

    private final String script;
    private final StructType schema;
    private final SparkSession sparkSession;
    private final LongAccumulator successTransformationsCount;
    private final LongAccumulator failedTransformationsCount;
    private final int threshold;

    private static final Logger logger = Logger.getLogger(JaninoExecutor.class);

    public static  Class thrownExceptions[] =

    {
            NullPointerException.class,
            IllegalArgumentException.class,
            Exception.class
    };
    //private String[] optionalDefaultImports = {"import java.util.Map;", "import java.util.HashMap;" };
    private String[] optionalDefaultImports = { };

    private Map<String, Class> outRowDesc;

    public JaninoTransform(SparkSession ss,
                           String script,
                           StructType inSchema,
                           LongAccumulator successTransformationsCount,
                           LongAccumulator failedTransformationsCount,
                           int threshold,
                           String[] odi) throws Exception {
        this.script = script;
        this.schema = inSchema;
        this.sparkSession = ss;
        this.successTransformationsCount = successTransformationsCount;
        this.failedTransformationsCount = failedTransformationsCount;
        this.threshold = threshold;


        outRowDesc = new HashMap<>(schema.fieldNames().length + 2);

        for (int i = 0; i < schema.fieldNames().length; i++) {
            outRowDesc.put(schema.fieldNames()[i],
                           stringToType(inSchema.apply(i).dataType().typeName()));

        }
        String[] lodi = new String[optionalDefaultImports.length + odi.length];
        for (int i = 0; i < odi.length; i++) lodi[i] = odi[i];
        optionalDefaultImports = lodi;

        outRowDesc.put(PREDEFINED_SCRIPT_RESULT_KEY, int.class);
        outRowDesc.put(PREDEFINED_SCRIPT_MESSAGE, int.class);

    }

    protected Object getValue(Row row, Class inCl, int i) {
        if ( inCl == boolean.class || inCl == Boolean.class)                       return row.getBoolean(i);
        if ( inCl == int.class || inCl == short.class || inCl == Integer.class )   return row.getInt(i);
        if ( inCl == Long.class || inCl == long.class )                            return row.getLong(i);
        if ( inCl == Double.class || inCl == Float.class ||
             inCl == double.class || inCl == float.class )                         return row.getDouble(i);
        if ( inCl == String.class )                                                return row.getString(i);
        if ( inCl == Timestamp.class )                                             return row.getTimestamp(i);
        throw new RuntimeException("Unsupported data type: " + inCl.getName());
    }

    @Override
    public Row call(Row row) throws Exception {

        Row return_value;
        if ( threshold != 0 && failedTransformationsCount.value() > threshold)
            throw new Exception(String.format("Number of invalid records [%d] exceeds threshold value [%d]", failedTransformationsCount.value(), threshold));

        if( row == null) return null;

        Map<String, Object> inRow = new HashMap<>();
        Map<String, Class> inRowDesc = new HashMap<>();

        try {

            for (int i = 0; i < row.schema().fieldNames().length; i++) {
                Class inClass = stringToType(row.schema().apply(i).dataType().typeName());
                inRowDesc.put(row.schema().fieldNames()[i],inClass);
                if (row.get(i) != null){
                    inRow.put(row.schema().fieldNames()[i], getValue(row, inClass, i));
                }
            }


            //TODO:: Add Janino executor
            IScriptEvaluator se = CompilerFactoryFactory.getDefaultCompilerFactory().newScriptEvaluator();
            se.setReturnType(Map.class);
            if (optionalDefaultImports != null && optionalDefaultImports.length >0) {
                String m = "Additional imports: ";
                for (int i = 0; i < optionalDefaultImports.length; i++) m += " " + optionalDefaultImports[i];
                System.out.println(m);
                se.setDefaultImports(optionalDefaultImports);
            }

/*
            System.out.println("In-Row descriptor");
            inRowDesc.forEach((f,c) -> System.out.println("Field: " + f + " Class: " + c.getName()));
            System.out.println("Out-Row descriptor");
            outRowDesc.forEach((f,c) -> System.out.println("Field: " + f + " Class: " + c.getName()));
*/

            String[] inPNames = {"inRowDescriptor", "ROW", "outRowDescriptor"};
            Class[]  inPTypes = {Class.forName("java.util.Map"), Class.forName("java.util.Map"), Class.forName("java.util.Map")};
            se.setParameters(inPNames, inPTypes);
            se.setThrownExceptions(JaninoTransform.thrownExceptions);
            se.cook(script);


            Object[] evalArgs = {inRowDesc, inRow, outRowDesc};
            Map<String, Object> result = (Map<String, Object>) se.evaluate(evalArgs);


/*
            System.out.println("Print result: ");
            for(String kk: result.keySet()){
                System.out.println("Field: " + kk + " value: " + result.get(kk).toString());
            }
*/

            if (result != null && !result.isEmpty()) {
                int src = ((result.containsKey(PREDEFINED_SCRIPT_RESULT_KEY)) ? (int) result.get(PREDEFINED_SCRIPT_RESULT_KEY) : -1);
                if (src >= 0) {
                    successTransformationsCount.add(1);
                    Object[] newRowVals = new Object[schema.length()];

                    for (int i = 0; i < schema.length() - 3; i++) {
                        String fn = schema.fieldNames()[i];
                        newRowVals[i] = null;
                        if (result.containsKey(fn)) {
//                            System.out.println("Set value " + result.get(fn) + " for field " + fn );
                            newRowVals[i] = result.get(fn);
                        } else {
                            if (src > 0) {
                                int fInx = findField(row.schema().fieldNames(), fn);
                                if (fInx >= 0 && row.get(fInx) != null) {
                                    Class inClass = stringToType(row.schema().apply(fInx).dataType().typeName());
                                    newRowVals[i] = getValue(row, inClass, fInx);
                                }
                                // else newRowVals[i] = null;
                            }
                            // else newRowVals[i] = null;
                        }
                        // else newRowVals[i] = null;
//                        System.out.println(String.format("Process field: %s, index: %d, data type: %s", fn, i, newRowVals[i].toString()));

                    }
                    newRowVals[schema.length() - 3] = successTransformationsCount.value();
                    newRowVals[schema.length() - 2] = src;
                    newRowVals[schema.length() - 1] = ((result.get(PREDEFINED_SCRIPT_MESSAGE) != null) ? result.get(PREDEFINED_SCRIPT_MESSAGE) : "n/a");

                    GenericRowWithSchema newrow = new GenericRowWithSchema(newRowVals, schema);
                    System.out.println(schema.mkString("[ ", "; ", "]"));
                    System.out.println((newrow.mkString("[ ", "; ", "]")));

                    return newrow;//new GenericRowWithSchema(newRowVals, schema);

                } else {  // src < 0
                    return createRejectedRecord(row, ((result.get(PREDEFINED_SCRIPT_MESSAGE) != null) ? (String) result.get(PREDEFINED_SCRIPT_MESSAGE) : "n/a"));
                }
            }
            else{
                throw new Exception("Script returns unacceptable (null) result");
            }
        } catch (Exception e) {
            //TODO::Create original record + invalid rec fields
            // //Script result is undefined
            // Mark record with error state
            logger.error(ExceptionUtils.getFullStackTrace(e));
            return createRejectedRecord(row,"Exception has occurred during script execution: " + e.getMessage());

        }
    }


    private Row createRejectedRecord(Row row, String msg ) throws Exception {

        //TODO:: Create invalid record
        failedTransformationsCount.add(1);

        Object[] newRowVals = new Object[schema.length()];
        for (int i = 0; i < schema.length() - 3; i++) {
            //            System.out.println(String.format("Process field: %s, index: %d, data type: %s", fn, i, targetRowTypes.get(fn).toString()));
            String fn = schema.fieldNames()[i];
            newRowVals[i] = null;
            int fInx = findField(row.schema().fieldNames(), fn);
            if (fInx >= 0 && row.get(fInx) != null) {
                Class inClass = stringToType(row.schema().apply(fInx).dataType().typeName());
                newRowVals[i] = getValue(row, inClass, fInx);
            }
            // else newRowVals[i] = null;

        }
        newRowVals[schema.length() - 3] = failedTransformationsCount.value();
        newRowVals[schema.length() - 2] = -1;
        newRowVals[schema.length() - 1] = msg;

        GenericRowWithSchema rejrow = new GenericRowWithSchema(newRowVals, schema);
        System.out.println(schema.mkString("< ", "; ", " >"));
        System.out.println((rejrow.mkString("< ", "; ", " >")));
        return rejrow;
    }


    private static Class stringToType(String inParamType) throws Exception {
        if ("boolean".equals(inParamType)) return Boolean.class;
        if ("Boolean".equals(inParamType)) return Boolean.class;
        if ("int".equals(inParamType))     return Integer.class;
        if ("long".equals(inParamType))    return Long.class;
        if ("double".equals(inParamType))  return Double.class;
        if ("String".equalsIgnoreCase(inParamType) ) return String.class;
        if ("Integer".equalsIgnoreCase(inParamType) ) return Integer.class;
        if ("Short".equalsIgnoreCase(inParamType) ) return Integer.class;
        if ("Double".equalsIgnoreCase(inParamType) ) return Double.class;
        if ("Float".equalsIgnoreCase(inParamType) ) return Double.class;
        if ("Timestamp".equalsIgnoreCase(inParamType) ) return Timestamp.class;
        throw new Exception("Unsupported class occurred: " + inParamType);
    }


}
