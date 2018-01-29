package sncr.xdf.transformer.jexl;

/**
 * Created by alexey.sorokin on 9/21/2015.
 */

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.*;
import scala.Tuple2;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static sncr.xdf.transformer.TransformerComponent.*;

abstract public class XdfObjectContextBase extends ObjectContext<Row> {

    private static final Logger logger = Logger.getLogger(XdfObjectContextBase.class);


    public final static char internalFieldPrefix = '$';
    protected StructType schema;

    protected Row record;
    protected Map<String, Object> targetRow;
    protected Map<String, StructField> targetRowTypes;
    protected Map<String, Object> localVars;
    // Contains Field Name --> Field Number mapping

    protected boolean __SUCCESS__ = true;
    protected final static String __SUCCESS___NAME  = "$__SUCCESS__";

    //TODO:: Output schema???
    public XdfObjectContextBase(JexlEngine engine, StructType inSchema, Row record) throws Exception {
        super(engine, record);
        this.schema = inSchema;
        if(record != null) {
            //Initialize record -- targetRow with values from input Row
            targetRow = new HashMap<>();
            targetRowTypes = new HashMap<>();
            this.record = record;
            this.localVars = new HashMap<>();
        } else {
            throw new Exception("Can't process NULL data record");
        }
    }

    public Boolean isSuccess(){
        return __SUCCESS__;
    }

    public Map<String, StructField> getNewOutputSchema(){ return targetRowTypes; }

    @Override
    public Object get(String name) {
        Object retval = null;
        try {
            if(name.equals(__SUCCESS___NAME)) {
                retval = __SUCCESS__;
            } else {
                if (name.charAt(0) == internalFieldPrefix) {
                    String nativeName = name.substring(1);

                    StringBuilder sb = new StringBuilder();
                    String[] fs = targetRow.keySet().toArray(new String[0]);
 /*
                    for (int j = 0; j < fs.length; j++) {
                        sb.append( ", " + j + " = " +  fs[j].toString());
                    }
                    System.out.println("Getting value from " + nativeName + " fields: " + sb.toString());
*/
                    if ((!nativeName.isEmpty()) && (targetRowTypes != null) && (targetRowTypes.containsKey(nativeName))) {
                        if ( targetRow != null && targetRow.containsKey(nativeName) ) {
                            retval = targetRow.get(nativeName);
                        } // else retval = null;
                    } else {
                        throw new JexlScriptException("Field not found " + nativeName);
                    }
                } else {
                    if (localVars.containsKey(name)) {
                        retval = localVars.get(name);
                    } // else retval = null;
                }
            }
        } catch (JexlScriptException e) {
            throw new JexlScriptException("Exception: Getting value of " + name.toUpperCase() + ":" + e.getMessage(), e);
        }
        return retval;
    }

    @Override
    public boolean has(String name) {
        boolean retval = false;

        if(name.equals(__SUCCESS___NAME)) {
            retval = true;
        } else {

            if (record != null) {
                if (name.charAt(0) == internalFieldPrefix) {
                    String nativeName = name.substring(1);
                    if (record != null && targetRowTypes.containsKey(nativeName)) {
                        retval = true;
                    } else {
                        throw new JexlScriptException("Field not found :" + name.toUpperCase());
                    }
                } else {
                    retval = true;
                }
            }
        }
        return retval;
    }

    public abstract void set(String name, Object value);

    protected static int findField(String[] a, String k) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].equalsIgnoreCase(k)) return i;
        }
        return -1;
    }


    protected StructField getType(String name, Object value, int fIndex) throws Exception {
        StructField sf ;
        if ( fIndex < 0 ){
            DataType dt = null;
            if (value == null)
                dt = DataTypes.NullType;
            else if (value instanceof String)
                dt = DataTypes.StringType;
            else if (value instanceof Double || value instanceof Float)
                dt = DataTypes.DoubleType;
            else if (value instanceof Integer || value instanceof Short )
                dt = DataTypes.IntegerType;
            else if (value instanceof Long)
                dt = DataTypes.LongType;
            else if (value instanceof Timestamp)
                dt = DataTypes.TimestampType;
            else if (value instanceof Boolean)
                dt = DataTypes.BooleanType;
            else{
                throw new Exception("Unsupported data type: " + value.getClass().getName() );
            }
            sf = new StructField(name, dt, true, Metadata.empty());
        }
        else
        {
            sf = schema.fields()[fIndex];
        }
        return sf;
    }

    //TODO:: Make sure typeName returns such values
    protected Object getValue(String fieldName, int i) {
        String type = schema.apply(fieldName).dataType().toString();
        switch (type) {
            case "BooleanType": return this.record.getBoolean(i);
            case "IntegerType":
            case "ShortType":
                return this.record.getInt(i);
            case "LongType":
                return this.record.getLong(i);
            case "DoubleType":
            case "FloatType":
                return this.record.getDouble(i);
            case "StringType":
                return this.record.getString(i);
            case "TimestampType":
                return this.record.getTimestamp(i);
            case "NullType":
                break;
            default:
                throw new JexlScriptException("Unsupported data type: " + type);
        }
        return null;
    }


    /**
     * The function returns new Row generated from Maps:
     * - Map of values and
     * - Map of Types.
     * The actual call creates GenericRowWithSchema that creates Row with schema.
     * NOTE: Function is used to create Row for successful transformation
     * @param errorCode
     * @param errMessage
     * @param rowCounter
     * @return
     */
    public Row createNewRow(int errorCode, String errMessage, Long rowCounter) {

        //Arrays to hold Row values and new schema
        StructField[] fieldArray = new StructField[targetRowTypes.size()+3];
        String[] fieldNames = new String[targetRowTypes.size()+3];
        Object[] newRowVals = new Object[targetRowTypes.size()+3];
        int i = 0;

        for(String fn: targetRowTypes.keySet()) {

            fieldArray[i] = targetRowTypes.get(fn);
            fieldNames[i] = fn;
            if (targetRow.containsKey(fn)) {
                newRowVals[i] = targetRow.get(fn);
            }
            else {
                newRowVals[i] = null;
            }
            i++;
        }

        //Add to new schema new transformations result fields
        int rc_inx = targetRowTypes.size();
        int tr_res_inx = rc_inx + 1;
        int tr_msg_inx = tr_res_inx + 1;
        fieldArray[rc_inx] = new StructField(RECORD_COUNTER, DataTypes.LongType, true, Metadata.empty());
        fieldNames[rc_inx] = RECORD_COUNTER;
        fieldArray[tr_res_inx] = new StructField(TRANSFORMATION_RESULT, DataTypes.IntegerType, true, Metadata.empty());
        fieldNames[tr_res_inx] = TRANSFORMATION_RESULT;
        fieldArray[tr_msg_inx] = new StructField(TRANSFORMATION_ERRMSG, DataTypes.StringType, true, Metadata.empty());
        fieldNames[tr_msg_inx] = TRANSFORMATION_ERRMSG;

//        StringBuilder sb = new StringBuilder();
        targetRowTypes.clear();
        for (int j = 0; j < fieldArray.length; j++) {
            targetRowTypes.put(fieldNames[j], fieldArray[j]);
        }

        //Create new schema
        StructType new_schema = DataTypes.createStructType(fieldArray);
        //Set row transformation result
        newRowVals[rc_inx] = new Long(rowCounter);
        if (errorCode == 0){
            newRowVals[tr_res_inx] =  0;
            newRowVals[tr_msg_inx] = "none";
        }
        else{
            newRowVals[tr_res_inx] = errorCode;
            newRowVals[tr_msg_inx] = errMessage;
        }
        //Generate row with new schema
        return new GenericRowWithSchema(newRowVals, new_schema);
    }

    /**
     * The method create a target row as copy of original row +
     *  fields to describe transformation failure:
     *  - #of failed transformation
     *  - error message
     *  - error code.
     * * NOTE: Function is used to create Row for failed transformation
     * @param existingRow
     * @param errorCode
     * @param message
     * @param rowCounter
     * @return
     *
     */

    public static Tuple2<Row, Map<String, StructField>>
        createNewRow(Row existingRow, int errorCode, String message, Long rowCounter) {

        //Create arrays to hold row values and row schema
        StructType existing_schema = existingRow.schema();
        Object[] row = new Object[existing_schema.length()+3];
        StructField[] fieldArray = new StructField[existingRow.length()+3];
        String[] fieldNames = new String[existingRow.length()+3];
        //Create object array with existing values, including NULL values.
        for (int i = 0; i < existing_schema.fieldNames().length; i++) {
            fieldArray[i] = existing_schema.apply(i);
            fieldNames[i] = existing_schema.fieldNames()[i];
            if (existingRow.get(i) != null) {
                row[i] = getValue(existingRow, existing_schema.fieldNames()[i], i);
            }
            else {
                row[i] = null;
            }
        }

        //Add to new schema new transformations result fields
        int rc_inx = existingRow.length();
        int tr_res_inx = rc_inx + 1;
        int tr_msg_inx = tr_res_inx + 1;
        fieldArray[rc_inx] = new StructField(RECORD_COUNTER, DataTypes.LongType, true, Metadata.empty());
        fieldNames[rc_inx] = RECORD_COUNTER;
        fieldArray[tr_res_inx] = new StructField(TRANSFORMATION_RESULT, DataTypes.IntegerType, true, Metadata.empty());
        fieldNames[tr_res_inx] = TRANSFORMATION_RESULT;
        fieldArray[tr_msg_inx] = new StructField(TRANSFORMATION_ERRMSG, DataTypes.StringType, true, Metadata.empty());
        fieldNames[tr_msg_inx] = TRANSFORMATION_ERRMSG;

        StructType new_schema = DataTypes.createStructType(fieldArray);

        //StringBuilder sb = new StringBuilder();
        Map<String, StructField> new_schema_map = new HashMap(fieldArray.length);
        for (int j = 0; j < fieldArray.length; j++) {
            new_schema_map.put(fieldNames[j], fieldArray[j]);
          //sb.append( ", " + j + " = " +  fieldArray[j]);
        }
        //System.out.println("Initialized struct fields [inv. records]: " + sb.toString());

        //Set row transformation result
        row[rc_inx] = new Long(rowCounter);
        row[tr_res_inx] = errorCode;
        row[tr_msg_inx] = message;

        Tuple2<Row, Map<String, StructField>> rv = new Tuple2(new GenericRowWithSchema(row, new_schema), new_schema_map);
        //Generate row with new schema
        return rv;
    }

    protected static Object getValue(Row row, String fieldName, int i) {
        String type = row.schema().apply(fieldName).dataType().toString();
        switch (type) {
            case "BooleanType": return row.getBoolean(i);
            case "DateType": return row.getDate(i);
            case "IntegerType":
            case "ShortType":
                return row.getInt(i);
            case "LongType":
                return row.getLong(i);
            case "DoubleType":
            case "FloatType":
                return row.getDouble(i);
            case "StringType":
                return row.getString(i);
            case "TimestampType":
                return row.getTimestamp(i);
            case "NullType":
                break;
            default:
                throw new JexlScriptException("Unsupported data type: " + type);
        }
        return null;
    }

}

