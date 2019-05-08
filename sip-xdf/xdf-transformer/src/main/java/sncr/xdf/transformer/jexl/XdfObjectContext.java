package sncr.xdf.transformer.jexl;

/**
 * Created by alexey.sorokin on 9/21/2015.
 */

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.*;
import scala.Tuple2;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static sncr.xdf.transformer.TransformerComponent.*;

public class XdfObjectContext extends XdfObjectContextBase {

    private static final Logger logger = Logger.getLogger(XdfObjectContext.class);

    public XdfObjectContext(JexlEngine engine, StructType inSchema, Row record) throws Exception {
        super(engine, inSchema, record);
        //Initialize record -- targetRow with values from input Row
        String[] fieldNames = schema.fieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            targetRowTypes.put(fieldNames[i], schema.fields()[i]);
            if (this.record.get(i) != null) {
                Object value = getValue(fieldNames[i], i);
                if (value != null)
                    targetRow.put(fieldNames[i], value);
            }
        }
/*
            StringBuilder sb = new StringBuilder();
            String[] fs = targetRow.keySet().toArray(new String[0]);
            for (int j = 0; j < fs.length; j++) {
                sb.append( ", " + j + " = " +  fs[j].toString());
            }
            System.out.println("Initialized fields from in record: " + sb.toString());
*/
    }

    public Map<String, StructField> getNewOutputSchema(){ return targetRowTypes; }

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
                        updateSchema(nativeName, value, fIndex);
                    } else {
                        //New field -> add it to Schema
                        updateSchema(nativeName, value, -1);
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
     * The method updates schema based on the following:
     *  - New field was received - add it to map
     *  - Known field value is changed from Null to something, NullType will br replaced with new type
     *  - It also checks if field type has been changed (new value assigned in script and passed to class )
     *     - if such case has been detected - throw IllegalArgumentException exception
     * @param nativeName
     * @param value
     * @param fIndex
     * @throws Exception
     */
    private void updateSchema(String nativeName, Object value, int fIndex) throws Exception {
        if ( targetRowTypes.containsKey(nativeName) ) {
            StructField sf = targetRowTypes.get(nativeName);
            //Get value from script with the same data type - ignore it, except if that was NullType
            if (sf.dataType() == DataTypes.NullType && value != null){
                targetRowTypes.put(nativeName, getType(nativeName, value, fIndex));
            }
            //If we have field in type map and coming value is not the same type from set of supported
            // types - throw an exception.
            if (!(
                (sf.dataType() == DataTypes.StringType && value instanceof String) ||
                (sf.dataType() == DataTypes.LongType && value instanceof Long) ||
                (sf.dataType() == DataTypes.IntegerType && ( value instanceof Integer || value instanceof Short)) ||
                (sf.dataType() == DataTypes.TimestampType && value instanceof Timestamp) ||
                (sf.dataType() == DataTypes.DoubleType && ( value instanceof Double || value instanceof Float)) ||
                (sf.dataType() == DataTypes.BooleanType && value instanceof Boolean))
                )
            {
                throw new IllegalArgumentException("Row cannot contain one field with two different data types");
            }
        }else
            targetRowTypes.put(nativeName, getType(nativeName, value, fIndex) );

    }




}

