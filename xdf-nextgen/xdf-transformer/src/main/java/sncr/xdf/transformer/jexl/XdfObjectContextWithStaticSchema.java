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

public class XdfObjectContextWithStaticSchema extends XdfObjectContextBase {

    private static final Logger logger = Logger.getLogger(XdfObjectContextWithStaticSchema.class);


    //TODO:: Output schema???
    public XdfObjectContextWithStaticSchema(JexlEngine engine, StructType inSchema, Row record) throws Exception {
        super(engine, inSchema, record);
        String[] fieldNames = schema.fieldNames();
        String[] recordFieldNames = record.schema().fieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            targetRowTypes.put(fieldNames[i], schema.fields()[i]);
            int fInx = findField(recordFieldNames, fieldNames[i]);
            if(fInx >= 0) {
                if (!schema.fields()[i].dataType().sameType(record.schema().fields()[fInx].dataType())) {
                    throw new Exception("Explicit data type conversion is required " +
                            "if output schema is given. Do not use existing field " +
                            "name in output schema with different data type");
                }
                if (this.record.get(fInx) != null) {
                    Object value = getValue(fieldNames[i], fInx);
                    if (value != null)
                        targetRow.put(fieldNames[i], value);

                }
            }
            else{
                // Field not found
            }
        }
    }



    @Override
    public void set(String name, Object value) {
        try {
            if (record == null) throw new JexlScriptException("Input record is null");
            if(name.equals(__SUCCESS___NAME)) {
                if (value instanceof Boolean) {
                    __SUCCESS__ = (Boolean) value;
                } else if (value instanceof String){
                    __SUCCESS__ = Boolean.parseBoolean((String) value);
                } else if(value instanceof Integer) {
                    __SUCCESS__ = ((Integer) value) == 0;
                }
            } else {
                if (name.charAt(0) == internalFieldPrefix) {
                    String nativeName = name.substring(1);
                    int fIndex = findField(schema.fieldNames(), nativeName);
                    if (fIndex >= 0) {
                        validateSchema(nativeName, value, fIndex);
                    } else {
                        throw new JexlScriptException("Transformer with static schema cannot accept new fields");
                    }
                    if(value != null)
                        if (value instanceof String
                                || value instanceof Integer
                                || value instanceof Long
                                || value instanceof Short
                                || value instanceof Float
                                || value instanceof Double
                                || value instanceof Timestamp
                                || value instanceof Boolean) {
                            targetRow.put(nativeName, value);
                        } else {
                            // Exception
                            throw new JexlScriptException("Unsupported object type : " + value.getClass().toString());
                        }
                }else {
                    localVars.put(name, value);
                }
            }
        } catch (Exception e) {
            throw new JexlScriptException("Exception: Setting value of : " + name.toUpperCase() + ":" + e.getMessage(), e);
        }
    }


    /**
     * The method validate schema as the following:
     *  - New field was received - add it to map
     *  - Known field value is changed from Null to something, NullType will br replaced with new type
     *  - It also checks if field type has been changed (new value assigned in script and passed to class )
     *     - if such case has been detected - throw IllegalArgumentException exception
     * @param nativeName
     * @param value
     * @param fIndex
     * @throws Exception
     */
    private void validateSchema(String nativeName, Object value, int fIndex) {
        if ( targetRowTypes.containsKey(nativeName) ) {
            StructField sf = targetRowTypes.get(nativeName);
            //Get value from script with the same data type - ignore it, except if that was NullType
            if (sf.dataType() == DataTypes.NullType && value != null) {
                throw new IllegalArgumentException("NullType data type cannot contain not null value");
            }
            //If we have field in type map and coming value is not the same type from set of supported
            // types - throw an exception.
  //          System.out.printf("Field: %s, Schema type: %s, actual type: %s \n", nativeName, sf.dataType().toString(), value.getClass().getName());
            if (!(
                (sf.dataType() == DataTypes.StringType && value instanceof String) ||
                    (sf.dataType() == DataTypes.LongType && value instanceof Long) ||
                    (sf.dataType() == DataTypes.IntegerType && (value instanceof Integer || value instanceof Short)) ||
                    (sf.dataType() == DataTypes.TimestampType && value instanceof Timestamp) ||
                    (sf.dataType() == DataTypes.DoubleType && (value instanceof Double || value instanceof Float)) ||
                    (sf.dataType() == DataTypes.BooleanType && value instanceof Boolean))
                ) {
                throw new IllegalArgumentException("Row cannot contain one field with two different data types");
            }

        }
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
    @Override
    public Row  createNewRow(int errorCode, String errMessage, Long rowCounter) {
        Object[] newRowVals = new Object[targetRowTypes.size()];

        for (int i = 0; i < schema.length()-3 ; i++)
        {
//            System.out.println(String.format("Process field: %s, index: %d, data type: %s", fn, i, targetRowTypes.get(fn).toString()));
            String fn = schema.fieldNames()[i];
            if (targetRow.containsKey(fn)) {
                newRowVals[i] = targetRow.get(fn);
            }
            else {
                newRowVals[i] = null;
            }
        }

        //Add to new schema new transformations result fields
        int rc_inx = targetRowTypes.size()     -3;
        int tr_res_inx = targetRowTypes.size() -2;
        int tr_msg_inx = targetRowTypes.size() -1;


        //Set row transformation result
        newRowVals[rc_inx] = rowCounter;
        if (errorCode == 0){
            newRowVals[tr_res_inx] =  0;
            newRowVals[tr_msg_inx] = "none";
        }
        else{
            newRowVals[tr_res_inx] = errorCode;
            newRowVals[tr_msg_inx] = errMessage;
        }
        //Generate row with new schema
        return new GenericRowWithSchema(newRowVals, schema);
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

    public static Row createNewRow2(StructType schema, Row existingRow, int errorCode, String message, Long rowCounter) throws Exception {

        //Create arrays to hold row values and row schema

        StructType existing_schema = existingRow.schema();

        Object[] row =             new Object[schema.length()];
        StructField[] fieldArray = new StructField[schema.length()];
        String[] fieldNames =      new String[schema.length()];

        //Create object array with existing values, including NULL values.

        for (int i = 0; i < schema.fieldNames().length; i++) {
            fieldArray[i] = schema.apply(i);
            fieldNames[i] = schema.fieldNames()[i];
            int fInxExt = findField(existing_schema.fieldNames(), fieldNames[i]);
            if (fInxExt >= 0) {
                if (existingRow.get(fInxExt) != null) {
                    if (!schema.fields()[i].dataType().sameType(existingRow.schema().fields()[fInxExt].dataType())) {
                        throw new Exception("Explicit data type conversion is required " +
                                "if output schema is given. Do not use existing field " +
                                "name in output schema with different data type");
                    }
                    row[i] = XdfObjectContextBase.getValue(existingRow, fieldNames[i], fInxExt);

                } else {
                    row[i] = null;
                }
            }else{
                row[i] = null;
            }
        }

        //Add to new schema new transformations result fields
        int rc_inx =     schema.length() -3;
        int tr_res_inx = schema.length() -2;
        int tr_msg_inx = schema.length() -1;


        //Set row transformation result
        row[rc_inx] = new Long(rowCounter);
        row[tr_res_inx] = errorCode;
        row[tr_msg_inx] = message;

        Row rv = new GenericRowWithSchema(row, schema);
        return rv;
    }

}

