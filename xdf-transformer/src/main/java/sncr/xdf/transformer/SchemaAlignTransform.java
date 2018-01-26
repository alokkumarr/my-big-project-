package sncr.xdf.transformer;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

/**
 * Created by srya0001 on 1/8/2018.
 */
public class SchemaAlignTransform implements Function<Row, Row> {

    private final StructType schema;

    public SchemaAlignTransform(StructType schema) {
        this.schema = schema;
    }

    @Override
    public Row call(Row r1) throws Exception {
        String[] unified_schema = schema.fieldNames();
        StructType row_schema = r1.schema();


        for(int fc = 0;  fc < row_schema.fields().length; fc++ )
            System.out.println(String.format("In row field: %s, value %s ", row_schema.fields()[fc].toString(), ((r1.get(fc) != null)?r1.get(fc).toString():"null")));

        Object[] newRowValues = new Object[schema.length()];
        for( int i = 0; i < unified_schema.length; i++) {
            int fIndex = findField(row_schema.fieldNames(), unified_schema[i]);

            System.out.println(String.format(
                    "Incoming row index: %d, uni schema index: %d, field: %s", fIndex, i, unified_schema[i]));

            //Field is found
            if (fIndex >= 0) {
                //TODO:: Check type again, just in case
                String rowType = row_schema.apply(fIndex).dataType().toString();
                String unifiedSchemaType= schema.apply(i).dataType().toString();
                if (!rowType.equalsIgnoreCase("NullType") && !rowType.equalsIgnoreCase(unifiedSchemaType))
                    throw new Exception(String.format(
                            "Row cannot be converted to aligned dataframe: type of field: %s are different: %s (in Row) and %s (in Dataframe)",
                            row_schema.fieldNames()[fIndex],
                            rowType, unifiedSchemaType));
                Object value = null;
                if (r1.get(fIndex) != null) {
                    switch (rowType) {
                        case "BooleanType":
                            value = r1.getBoolean(fIndex);
                            break;
                        case "IntegerType":
                        case "ShortType":
                            value = r1.getInt(fIndex);
                            break;
                        case "LongType":
                            value = r1.getLong(fIndex);
                            break;
                        case "DoubleType":
                        case "FloatType":
                            value = r1.getDouble(fIndex);
                            break;
                        case "StringType":
                            value = r1.getString(fIndex);
                            break;
                        case "TimestampType":
                            value = r1.getTimestamp(fIndex);
                        case "NullType":
                            break;
                        default:
                            throw new Exception("Unsupported data type: " + unifiedSchemaType);
                    }
                }
                System.out.println(String.format(
                        "set value %s for field: %s and field type: %s", ((value != null)?value.toString():"null"), unified_schema[i], unifiedSchemaType));

                newRowValues[i]= value;
            }
            //Field sfs[i] in R1 not found
            else{
                newRowValues[i] = null;
            }
        }
        return new GenericRowWithSchema(newRowValues, schema);
    }

    private int findField(String[] a, String k) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].equalsIgnoreCase(k)) return i;
        }
        return -1;
    }
}
