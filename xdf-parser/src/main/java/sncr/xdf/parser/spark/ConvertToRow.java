package sncr.xdf.parser.spark;

import com.univocity.parsers.common.processor.NoopRowProcessor;
import com.univocity.parsers.common.processor.core.NoopProcessor;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.spark.util.LongAccumulator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertToRow implements Function<String, Row> {
    StructType schema;
    Long l = 0L;
    Double d = 1.1;


    private String lineSeparator;
    private char delimiter;
    private char quoteChar;
    private char quoteEscapeChar;
    private char charToEscapeQuoteEscaping;

    private static CsvParser parser = null;

    private LongAccumulator recCounter;
    private LongAccumulator errCounter;

    public ConvertToRow(StructType schema,
                        String lineSeparator,
                        char delimiter,
                        char quoteChar,
                        char quoteEscapeChar,
                        char charToEscapeQuoteEscaping,
                        LongAccumulator recordCounter,
                        LongAccumulator errorCounter) {
        this.schema = schema;
        this.lineSeparator = lineSeparator ;
        this.delimiter = delimiter ;
        this.quoteChar = quoteChar ;
        this.quoteEscapeChar = quoteEscapeChar ;
        this.charToEscapeQuoteEscaping = charToEscapeQuoteEscaping;
        this.errCounter = errorCounter;
        this.recCounter = recordCounter;
    }
    public Row call(String in) throws Exception {

        if(parser == null){
            CsvParserSettings settings = new CsvParserSettings();

            settings.getFormat().setLineSeparator(lineSeparator);
            settings.getFormat().setDelimiter(delimiter);
            settings.getFormat().setQuote(quoteChar);
            settings.getFormat().setQuoteEscape(quoteEscapeChar);
            settings.getFormat().setCharToEscapeQuoteEscaping(charToEscapeQuoteEscaping);

            // skip leading whitespaces
            settings.setIgnoreLeadingWhitespaces(true);
            // skip trailing whitespaces
            settings.setIgnoreTrailingWhitespaces(true);
            settings.setReadInputOnSeparateThread(false);
            settings.setProcessor(NoopRowProcessor.instance);
            parser = new CsvParser(settings);
        }

        Object[] record = new Object[schema.length() + 1];
        record[schema.length()] = 0;

        String[] parsed = parser.parseLine(in);
        Row retval = null;

            if(parsed.length != schema.fields().length){
                // Create record with rejected flag
                errCounter.add(1);
                record[schema.length()] = 1;
            } else {


            // Convert String to array of objects

/*
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss zzz");

        record[0] = parsed[0];
        record[1] = Long.parseLong(parsed[1]);
        record[2] = Double.parseDouble(parsed[2]);
        Date d2 = df2.parse("15/02/2017 15:00:00 UTC");
        record[3] = new java.sql.Timestamp(d2.getTime());

        record[4] = parsed[4];
        record[5] = Long.parseLong(parsed[5]);
        record[6] = Double.parseDouble(parsed[6]);
        record[7] = new java.sql.Timestamp(d2.getTime());

        record[8] = parsed[8];
        record[9] = Long.parseLong(parsed[9]);

*/

                int i = 0;
                for (StructField sf : schema.fields()) {
                    if (sf.dataType().equals(DataTypes.StringType)) {
                        record[i] = parsed[i];
                    }
                    if (sf.dataType().equals(DataTypes.LongType)) {
                        record[i] = Long.parseLong(parsed[i]);
                    }
                    if (sf.dataType().equals(DataTypes.DoubleType)) {
                        record[i] = Double.parseDouble(parsed[i]);
                    }
                    if (sf.dataType().equals(DataTypes.TimestampType)) {
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss zzz");
                        Date d2 = df2.parse("15/02/2017 15:00:00 UTC");
                        record[i] = new java.sql.Timestamp(d2.getTime());
                    }
                    i++;
                }
        }
        recCounter.add(1);
        return  RowFactory.create(record);
    }
}
