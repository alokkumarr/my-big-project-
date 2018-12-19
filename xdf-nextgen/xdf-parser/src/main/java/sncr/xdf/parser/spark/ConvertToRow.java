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

import java.util.Arrays;
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

    private LongAccumulator recCounter;
    private LongAccumulator errCounter;

    public static String DEFAULT_DATE_FORMAT = "dd/MM/yy HH:mm:ss";

    private SimpleDateFormat df;

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

        df = new SimpleDateFormat();
        /*
           Strictly validate the date
           01/01/2016 00:00:00 (January 01 2016) - Valid
           29/02/2015 00:00:00 (February 29 2015) - Invalid
         */
        df.setLenient(false);
    }
    public Row call(String line) throws Exception {

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
            CsvParser  parser = new CsvParser(settings);

        Object[] record = new Object[schema.length() + 2];
        record[schema.length()] = 0;

        logger.debug("Parsing line " + line);

        String[] parsed = parser.parseLine(line);

        if (parsed == null) {
            logger.info("Unable to parse the record");
            errCounter.add(1);
            record = createRejectedRecord(line, "Unable to parse the record");
        }
        else if(parsed.length != schema.fields().length){
            // Create record with rejected flag
            errCounter.add(1);
            record = createRejectedRecord(line, "Invalid number of columns");

        } else {
            // TODO: Faster implementation will require automatic Janino code generation
            try {
                if (Arrays.stream(parsed).filter(val -> val != null).count() == 0) {
                    record = createRejectedRecord(line, "All fields are null");
                }
                int i = 0;
                for (StructField sf : schema.fields()) {
                    //Should accept null values unless mentioned as mandatory
                    //Reject rows with all null fields

                    if (parsed[i] == null) {
                        record[i] = parsed[i];
                    } else {
                        if (sf.dataType().equals(DataTypes.StringType)) {
                            if (validateString(parsed[i])) {
                                record[i] = parsed[i];
                            } else {
                                throw new Exception("Invalid string value " + parsed[i]);
                            }
                        }
                        else if (sf.dataType().equals(DataTypes.LongType)) {
                            record[i] = Long.parseLong(parsed[i]);
                        }
                        else if (sf.dataType().equals(DataTypes.DoubleType)) {
                            record[i] = Double.parseDouble(parsed[i]);
                        }
                        else if (sf.dataType().equals(DataTypes.IntegerType)) {
                            record[i] = Integer.parseInt(parsed[i]);
                        }
                        else if (sf.dataType().equals(DataTypes.TimestampType)) {
                            if (!tsFormats.get(i).isEmpty()) {
                                df.applyPattern(tsFormats.get(i));
                            } else {
                                df.applyPattern(DEFAULT_DATE_FORMAT);
                            }
                            record[i] = new java.sql.Timestamp(df.parse(parsed[i]).getTime());
                        }
                    }
                    i++;
                }
            } catch(Exception e){
                errCounter.add(1);

                //TODO; Not working - Sunil
//                record = new Object[]{line, 1, e.getClass().getCanonicalName() + ": " + e.getMessage()};
                if (e instanceof NumberFormatException){
                    record = createRejectedRecord(line, "Invalid number format " + e.getMessage());
                } else {
                    record = createRejectedRecord(line, e.getMessage());
                }
            }
        }
        recCounter.add(1);
        return  RowFactory.create(record);
    }

    private Object[] createRejectedRecord (String line, String rejectReason) {
        Object []record = new Object[this.schema.length() + 2];

        record[0] = line;
        record[this.schema.length()] = 1;
        record[this.schema.length() + 1] = rejectReason;

        return record;
    }

    private boolean validateString(String inputString) {
        boolean status = true;

        status = validateQuoteBalance(inputString, this.quoteChar);

        return status;
    }

    private boolean validateQuoteBalance(String inputString, char quoteCharacter) {
        boolean status = true;

        int charCount = countChar(inputString, quoteCharacter);

        status = (charCount % 2) == 0 ? true : false;

        return status;
    }

    private int countChar(String inputString, char character) {
        int count = 0;

        for(char c: inputString.toCharArray()) {
            if (c == character) {
                count += 1;
            }
        }

        return count;
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
