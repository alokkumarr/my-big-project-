package sncr.xdf.parser.spark;

import com.univocity.parsers.common.processor.NoopRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;

import java.text.SimpleDateFormat;
import java.util.List;

public class ConvertToRow implements Function<String, Row> {

    private static final Logger logger = Logger.getLogger(ConvertToRow.class);
    private StructType schema;
    private List<String> tsFormats;

    private String lineSeparator;
    private char delimiter;
    private char quoteChar;
    private char quoteEscapeChar;
    private char charToEscapeQuoteEscaping;

    private static CsvParser parser = null;

    private LongAccumulator recCounter;
    private LongAccumulator errCounter;

    public ConvertToRow(StructType schema,
                        List<String> tsFormats,
                        String lineSeparator,
                        char delimiter,
                        char quoteChar,
                        char quoteEscapeChar,
                        char charToEscapeQuoteEscaping,
                        LongAccumulator recordCounter,
                        LongAccumulator errorCounter) {
        this.schema = schema;
        this.tsFormats = tsFormats;
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

        if( parsed == null || parsed.length != schema.fields().length){
            // Create record with rejected flag
            errCounter.add(1);
            record[schema.length()] = 1;
        } else {
            // TODO: Faster implementation will require automatic Janino code generation
            try {
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
                        SimpleDateFormat df;
                        if (!tsFormats.get(i).isEmpty()) {
                            df = new SimpleDateFormat(tsFormats.get(i));
                        } else {
                            // TODO: pass default timestamp format as a parameter
                            df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                        }
                        record[i] = new java.sql.Timestamp(df.parse(parsed[i]).getTime());
                    }
                    i++;
                }
            } catch(Exception e){
                errCounter.add(1);
                record[schema.length()] = 1;
            }
        }
        recCounter.add(1);
        return  RowFactory.create(record);
    }

        private String codeGen(){

        StringBuffer sb = new StringBuffer();
        Integer i = 0;
        for(StructField sf : schema.fields()) {
            if (sf.dataType().equals(DataTypes.StringType)) {
                sb.append("record[").append(i).append("] = parsed[").append(i).append("];\n");
            }
            if (sf.dataType().equals(DataTypes.LongType)) {
                sb.append("record[").append(i).append("] = Long.parseLong(parsed[").append(i).append("]);\n");
            }
            if (sf.dataType().equals(DataTypes.DoubleType)) {
                sb.append("record[").append(i).append("] = Double.parseDouble(parsed[").append(i).append("]);\n");
            }
            if (sf.dataType().equals(DataTypes.TimestampType)) {
                if(!tsFormats.get(i).isEmpty()){
                    sb.append("SimpleDateFormat df").append(i).append("= new SimpleDateFormat(\"").append(tsFormats.get(i)).append("\");\n");
                } else {
                    // TODO: pass default timestamp format as a parameter
                    sb.append("SimpleDateFormat df").append(i).append("= new SimpleDateFormat(\"dd/MM/yy HH:mm:ss\");\n");
                }
                sb.append("record[").append(i).append("] = new java.sql.Timestamp(df").append(i).append(".parse(parsed[").append(i).append("]).getTime());\n");
            }
            i++;
        }
        return sb.toString();
    }
}