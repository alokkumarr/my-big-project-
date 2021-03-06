package sncr.xdf.parser.spark;

import org.apache.commons.lang3.ArrayUtils;
import scala.Tuple2;
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
    private Map<String, Tuple2<Integer, Object>> fieldDefaultValuesMap = null;
    private boolean isSkipFieldsEnabled;

    /**
     *
     * @param schema - StructType - Parser output schema
     * @param tsFormats - List<String>  -Timestamp formats list
     * @param lineSeparator - String - Line separator in Source file
     * @param delimiter - char - Field Delimiter in Source file
     * @param quoteChar - char - Quote Character in Source file
     * @param quoteEscapeChar - char - Quote Escape Character in Source file
     * @param charToEscapeQuoteEscaping - char - Escape Quoting Character in Source file
     * @param recordCounter - LongAccumulator - Record Counter
     * @param errorCounter - LongAccumulator - Error Record counter
     * @param allowInconsistentCol - boolean - Inconsistent record length is valid or not
     * @param fieldDefaultValuesMap - Map<String, Tuple2<Integer, Object>> - Contains all Parser Config Field Names as Keys and Tuple2 as value
     * @param isSkipFieldsEnabled - boolean - Do we have to skip any fields from Input source.
     *
     * ConvertToRow() new constructor added to support ignore few fields from Input File
     * Added 2 additional parameters fieldDefaultValuesMap, isSkipFieldsEnabled to existing constructor
     *
     * fieldDefaultValuesMap - Contains all Parser Config Field Names as Keys and Tuple2 as value
     * Tuple2 contains key as Field index from source.
     * Tuple2 contains value as default value provided in Field config after converting into spark DataType object
     *
     * isSkipFieldsEnabled - Do we have to skip any fields from Input source.
     */
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
                        Map<String, Tuple2<Integer, Object>> fieldDefaultValuesMap,
                        boolean isSkipFieldsEnabled) {
        this(schema,tsFormats,
            lineSeparator,delimiter,quoteChar,
            quoteEscapeChar,charToEscapeQuoteEscaping,
            recordCounter,errorCounter,allowInconsistentCol);
        this.fieldDefaultValuesMap = fieldDefaultValuesMap;
        this.isSkipFieldsEnabled = isSkipFieldsEnabled;
    }

    public ConvertToRow(StructType schema,
                        List<String> tsFormats,
                        String lineSeparator,
                        char delimiter,
                        char quoteChar,
                        char quoteEscapeChar,
                        char charToEscapeQuoteEscaping,
                        LongAccumulator recordCounter,
                        LongAccumulator errorCounter,
                        boolean allowInconsistentCol) {
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

    /**
     *
     * @param line
     * @param record
     * @param parsed
     * @return
     *
     * constructRecord() checks if isSkipFieldsEnabled is true or false
     * If it is true then it will construct output record based on Field Indices
     * If it is false then traditional way, it don't skip any fields from Input source record.
     *
     */
    private Object[] constructRecord(String line, Object[] record, String[] parsed) {
        if(isSkipFieldsEnabled){
            record = constructRecordWithIndices(line, record, parsed);
        }else{
            record = constructRecordFromLine(line, record, parsed);
        }
        return record;
    }

    /**
     *
     * @param line
     * @param record
     * @param parsed
     * @return
     *
     * constructRecordWithIndices() will construct output record based on Field Indices
     * It will get Field Tuple2 (contains field Index and Default value) from fieldDefaultValuesMap
     * Get Value from record with Tuple2 _1 (Index Integer value)
     * If value is Null then it will assign default value from Tuple2 _2 (Default Object).
     *
     */
    private Object[] constructRecordWithIndices(String line, Object[] record, String[] parsed) {
        try {
            final int parsedLength = parsed.length;
            final int schemaLength = schema.length();
            StructField[] structFields = schema.fields();
            final int[] index = new int[1];
            index[0] = 0;
            Object[] recordValues = new Object[schemaLength];
            Arrays.stream(structFields).forEach(structField -> {
                try {
                    Tuple2<Integer, Object> fieldTuple = fieldDefaultValuesMap.get(structField.name());
                    Object fieldValue = null;
                    if(fieldTuple._1 < parsedLength){
                        fieldValue = getFieldValue(parsed[fieldTuple._1], structField, index[0]);
                    }
                    if(fieldValue == null){
                        fieldValue = fieldTuple._2;
                    }
                    recordValues[index[0]] = fieldValue;
                    index[0] = index[0]+1;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            record = ArrayUtils.addAll(recordValues, record[schemaLength], record[schemaLength+1]);
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

    /**
     *
     * @param line
     * @param record
     * @param parsed
     * @return
     *
     * constructRecordFromLine() will construct output record in traditional way.
     * It don't skip any fields from Input source record.
     * It will get all Field values from record.
     * It will get Field Tuple2 (contains field Index and Default value) from fieldDefaultValuesMap
     * If value is Null then it will assign default value from Tuple2 _2 (Default Object).
     *
     */
    private Object[] constructRecordFromLine(String line, Object[] record, String[] parsed) {
        if(parsed.length > schema.fields().length || (!allowInconsistentCol && parsed.length != schema.fields().length)) {
            // Create record with rejected flag
            errCounter.add(1);
            record = createRejectedRecord(line, "Invalid number of columns");
        } else {
            try {
                int parsedLength = parsed.length;
                int schemaLength = schema.length();

                // Don't reject the record if columns are inconsistent (less than the schema length)
                // Copy the input row array and create a valid schema length array with default values
                if(allowInconsistentCol && parsedLength < schemaLength){
                    parsed = Arrays.copyOf(parsed, schemaLength);
                    logger.debug("Column with default values : " + Arrays.toString(parsed));
                }
                if (Arrays.stream(parsed).filter(Objects::nonNull).count() == 0) {
                    record = createRejectedRecord(line, "All fields are null");
                }

                int index = 0;
                for (StructField sf : schema.fields()) {
                    //Should accept null values unless mentioned as mandatory
                    //Reject rows with all null fields
                    Object fieldValue = getFieldValue(parsed[index], sf, index);
                    if(fieldValue == null){
                        Tuple2<Integer, Object> fieldTuple = fieldDefaultValuesMap.get(sf.name());
                        fieldValue = fieldTuple._2;
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
        }
        return record;
    }

    /**
     *
     * @param value
     * @param sf
     * @param sfIndex
     * @return
     * @throws Exception
     *
     * getFieldValue() return Field Value which is Object Type
     * Field Value Object internally it is type of spark DataType
     * This method converts value to specific Spark DataType based on StructField datatype
     *
     */
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
