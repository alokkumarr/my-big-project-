package sncr.xdf.parser.spark.functions;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import org.apache.commons.lang3.ArrayUtils;
import scala.collection.JavaConversions;

public class AddDateAttributes implements Function<Row, Row> {

    private static final Logger logger = Logger.getLogger(AddDateAttributes.class);
    private static final int NUMBER_OF_DATE_ATTRIBUTES = 7;

    private final StructType newSchema;
    private final List<String> tsfields;
    private final int noOfNewColumns;

    public AddDateAttributes(List<String> tsfields, StructType newSchema)
    {
        this.newSchema = newSchema;
        this.tsfields = tsfields;
        this.noOfNewColumns = tsfields.size() * NUMBER_OF_DATE_ATTRIBUTES;
    }

    /**
     *
     * @param row - Row - Dataset Row
     * @param tsField - List<StructField> -  Timestamp Fields List
     * @return - Object[] - Returns new Row values as Object[]
     *
     * This method calculate Date attribute values of Timestamp fields
     * And add them to Existing Row values
     * And Returns new Row Values
     *
     */
    public Row call(Row row) throws Exception {
        //It will give user current locale
        //This Locale value require to get string value of Month and Day of week.
        Locale currentLocale = Locale.getDefault();

        //Iterating Timestamp field List and calculating New Date Attribute values and adding to Object[]
        Object[] dateAttrValues = new Object[noOfNewColumns];
        final int[] index = new int[1];
        index[0]=0;
        tsfields.forEach(fieldName -> {
            int fieldIndex = row.fieldIndex(fieldName);
            Timestamp ts = row.getTimestamp(fieldIndex);
            if(ts!=null){
                LocalDateTime dateTime = ts.toLocalDateTime();
                dateAttrValues[index[0]]=dateTime.getYear();
                dateAttrValues[index[0]+1]=dateTime.getMonth().getDisplayName(TextStyle.FULL, currentLocale);
                dateAttrValues[index[0]+2]=dateTime.getMonthValue();
                dateAttrValues[index[0]+3]=dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, currentLocale);
                dateAttrValues[index[0]+4]=dateTime.getDayOfMonth();
                dateAttrValues[index[0]+5]=dateTime.getDayOfYear();
                dateAttrValues[index[0]+6]=dateTime.getHour();
            }
            index[0] = index[0]+7;
        });

        //Getting Existing Row Values as Object[]
        Object[] rowValues = JavaConversions.seqAsJavaList(row.toSeq()).toArray(new Object[0]);
        //Adding above date Attribute value array to Existing Row Values
        Object[] newRowAttrValues = ArrayUtils.addAll(rowValues, dateAttrValues);
        //Generate row with new Row values and new schema
        //Return new Row
        return new GenericRowWithSchema(newRowAttrValues, newSchema);
    }

}
