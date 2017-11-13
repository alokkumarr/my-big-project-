package sncr.xdf.parser.spark;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertToRow implements Function<String, Row> {
    StructType schema;
    Long l = 0L;
    Double d = 1.1;

    public ConvertToRow(StructType schema){
        this.schema = schema;
    }
    public Row call(String in) throws Exception {
        Object[] record = new Object[schema.length()];
        // Convert String to array of objects
        int i = 0;
        for(StructField sf : schema.fields()){
            if(sf.dataType().equals(DataTypes.StringType)){
                record[i] = "StringValue" + l;

            }
            if(sf.dataType().equals(DataTypes.LongType)){
                record[i] = l++;

            }
            if(sf.dataType().equals(DataTypes.DoubleType)){
                record[i] = d;
                d += 0.561;

            }
            if(sf.dataType().equals(DataTypes.TimestampType)){
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss zzz");
                Date d2 = df2.parse("15/02/2017 15:00:00 UTC");
                record[i] = new java.sql.Timestamp(d2.getTime());
            }
            i++;
        }

        //return new GenericRowWithSchema(record, schema);
        return  RowFactory.create(record); //, schema);
    }

}
