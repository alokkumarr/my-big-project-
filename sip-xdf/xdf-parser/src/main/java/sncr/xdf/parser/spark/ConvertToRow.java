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

import java.util.*;
import sncr.xdf.ngcomponent.util.NGComponentUtil;

/**
 * This class build and validate the every column of the row while collecting in RDD. Mark the accepted/rejected record with the addition schema column.
 */
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

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yy HH:mm:ss";

    private SimpleDateFormat df;

    private CsvParser parser = null;
    private boolean allowInconsistentCol;
    LinkedHashMap<Integer, Object> fieldsAddlConfigMap = null;

    public ConvertToRow(StructType schema,
                        List<String> tsFormats,
                        String lineSeparator,
                        char delimiter,
                        char quoteChar,
                        char quoteEscapeChar,
                        char charToEscapeQuoteEscaping,
                        LongAccumulator recordCounter,
                        LongAccumulator errorCounter,
                        boolean allowInconsistentCol,
                        Optional<LinkedHashMap<Integer, Object>> optFieldsAddlConfigMap) {
        this.schema = schema;
        this.tsFormats = tsFormats;
        this.lineSeparator = lineSeparator ;
        this.delimiter = delimiter ;
        this.quoteChar = quoteChar ;
        this.quoteEscapeChar = quoteEscapeChar ;
        this.charToEscapeQuoteEscaping = charToEscapeQuoteEscaping;
        this.errCounter = errorCounter;
        this.recCounter = recordCounter;
        this.allowInconsistentCol = allowInconsistentCol;
        if(optFieldsAddlConfigMap.isPresent()) this.fieldsAddlConfigMap = optFieldsAddlConfigMap.get();

        df = new SimpleDateFormat();
        /*
           Strictly validate the date
           01/01/2016 00:00:00 (January 01 2016) - Valid
           29/02/2015 00:00:00 (February 29 2015) - Invalid
         */
        df.setLenient(false);
    }

    public Row call(String line) throws Exception {

        if (parser == null) {
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

        Object[] record = new Object[schema.length() + 2];
        record[schema.length()] = 0;
        logger.debug("Parsing line " + line);
        String[] parsed = parser.parseLine(line);
        if (parsed == null) {
            logger.info("Unable to parse the record");
            errCounter.add(1);
            record = createRejectedRecord(line, "Unable to parse the record");
        }else {
            record = constructRecord(line, record, parsed);
        }
        recCounter.add(1);
        return  RowFactory.create(record);
    }

    private Object[] constructRecord(String line, Object[] record, String[] parsed) {
        if(fieldsAddlConfigMap == null || fieldsAddlConfigMap.isEmpty()){
            record = constructRecordFromLine(line, record, parsed);
        }else{
            record = constructRecordWithIndices(line, record, parsed);
        }
        return record;
    }

    private Object[] constructRecordWithIndices(String line, Object[] record, String[] parsed) {
        try {
            int parsedLength = parsed.length;
            StructField[] structFields = schema.fields();
            int index = 0;
            for (Map.Entry<Integer, Object> entry : fieldsAddlConfigMap.entrySet()) {
                Object fieldValue = null;
                if(entry.getKey() < parsedLength){
                    fieldValue = getFieldValue(parsed[entry.getKey()], structFields[index], index);
                }
                if(fieldValue == null){
                    fieldValue = entry.getValue();
                }
                record[index] = fieldValue;
                index++;
            }
        } catch(Exception ex){
            errCounter.add(1);
            if(ex instanceof  NumberFormatException){
                record = createRejectedRecord(line, "Invalid number format " + ex.getMessage());
            }else{
                record = createRejectedRecord(line, ex.getMessage());
            }
        }
        return record;
    }

    private Object[] constructRecordFromLine(String line, Object[] record, String[] parsed) {
        if(parsed.length > schema.fields().length || (!allowInconsistentCol && parsed.length != schema.fields().length)) {
            // Create record with rejected flag
            errCounter.add(1);
            record = createRejectedRecord(line, "Invalid number of columns");
        } else {
            try {
                int parsedLength = parsed.length;
                int validSchemaLength = schema.fields().length;

                // Don't reject the record if columns are inconsistent (less than the schema length)
                // Copy the input row array and create a valid schema length array with default values
                if(allowInconsistentCol && parsedLength < validSchemaLength){
                    parsed = Arrays.copyOf(parsed, validSchemaLength);
                    logger.debug("Column with default values : " + Arrays.toString(parsed));
                }
                if (Arrays.stream(parsed).filter(Objects::nonNull).count() == 0) {
                    record = createRejectedRecord(line, "All fields are null");
                }
                int i = 0;
                for (StructField sf : schema.fields()) {
                    //Should accept null values unless mentioned as mandatory
                    //Reject rows with all null fields
                    record[i] = getFieldValue(parsed[i], sf, i);
                    i++;
                }
            } catch(Exception ex){
                errCounter.add(1);
                if(ex instanceof  NumberFormatException){
                    record = createRejectedRecord(line, "Invalid number format " + ex.getMessage());
                }else{
                    record = createRejectedRecord(line, ex.getMessage());
                }
            }
        }
        return record;
    }

    private Object getFieldValue(String value, StructField sf, int sfIndex) throws Exception {
        if(value != null && (!value.trim().isEmpty() || sf.dataType().equals(DataTypes.StringType))){
            value = value.trim();
            if (sf.dataType().equals(DataTypes.StringType)) {
                if (validateString(value)) {
                    return value;
                } else {
                    throw new Exception("Invalid string value " + value);
                }
            } else if (sf.dataType().equals(DataTypes.LongType)) {
                return Long.parseLong(value);
            } else if (sf.dataType().equals(DataTypes.DoubleType)) {
                return Double.parseDouble(value);
            } else if (sf.dataType().equals(DataTypes.IntegerType)) {
                return Integer.parseInt(value);
            } else if (sf.dataType().equals(DataTypes.TimestampType)) {
                SimpleDateFormat df = new SimpleDateFormat();
                df.setLenient(false);
                if (!tsFormats.get(sfIndex).isEmpty()) {
                    df.applyPattern(tsFormats.get(sfIndex));
                } else {
                    df.applyPattern(DEFAULT_DATE_FORMAT);
                }
                return new java.sql.Timestamp(df.parse(value).getTime());
            }
        }
        return null;
    }

    /**
   * Create the rejected records row with the reason of rejection
   *
   * @param line
   * @param rejectReason
   * @return object array with value and reason of rejection
   */
    private Object[] createRejectedRecord (String line, String rejectReason) {
        Object []record = new Object[this.schema.length() + 2];
        record[0] = line;
        record[this.schema.length()] = 1;
        record[this.schema.length() + 1] = rejectReason;
        return record;
    }

    /**
     * Validate the column string with the quote char
     *
     * @param inputString
     * @return true if valid else false
     */
    private boolean validateString(String inputString) {
        boolean status = validateQuoteBalance(inputString, this.quoteChar);
        logger.debug("Have valid quote balanced string : " + status);
        return status;
    }

    /**
     * Validate string column with balance quote
     *
     * @param inputString
     * @param quoteCharacter
     * @return true if the column value valid else false
     */
    private boolean validateQuoteBalance(String inputString, char quoteCharacter) {
        int charCount = countChar(inputString, quoteCharacter);
        return  (charCount % 2) == 0;
    }

    /**
     * Count the number of char for balance character
     *
     * @param inputString
     * @param character
     * @return no of balance char
     */
    private int countChar(String inputString, char character) {
        int count = 0;
        for(char c: inputString.toCharArray()) {
            if (c == character) {
                count += 1;
            }
        }
        return count;
    }
}
