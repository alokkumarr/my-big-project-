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
import java.util.Objects;

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
    private boolean inconsistentCol;

    public ConvertToRow(StructType schema,
                        List<String> tsFormats,
                        String lineSeparator,
                        char delimiter,
                        char quoteChar,
                        char quoteEscapeChar,
                        char charToEscapeQuoteEscaping,
                        LongAccumulator recordCounter,
                        LongAccumulator errorCounter,
                        boolean inconsistentCol) {
        this.schema = schema;
        this.tsFormats = tsFormats;
        this.lineSeparator = lineSeparator ;
        this.delimiter = delimiter ;
        this.quoteChar = quoteChar ;
        this.quoteEscapeChar = quoteEscapeChar ;
        this.charToEscapeQuoteEscaping = charToEscapeQuoteEscaping;
        this.errCounter = errorCounter;
        this.recCounter = recordCounter;
        this.inconsistentCol = inconsistentCol;

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
        } else if(inconsistentCol && parsed.length > schema.fields().length){
            // Create record with rejected flag, if column are more than schema
            logger.debug("Rejected : No of column are more than the defined schema : "
                + Arrays.toString(parsed));
            errCounter.add(1);
            record = createRejectedRecord(line, "Invalid number of columns");
        } else if (!inconsistentCol && parsed.length != schema.fields().length){
            errCounter.add(1);
            record = createRejectedRecord(line, "Invalid number of columns");
        } else {
            try {
                int parsedLength = parsed.length;
                int validSchemaLength = schema.fields().length;

                // Don't reject the record if columns are inconsistent (less than the schema length)
                // Copy the input row array and create a valid schema length array with default values
                if(inconsistentCol && parsedLength < validSchemaLength){
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

                    if (parsed[i] == null) {
                        record[i] = parsed[i];
                    } else {
                        if (sf.dataType().equals(DataTypes.StringType)) {
                            if (validateString(parsed[i])) {
                                record[i] = parsed[i];
                            } else {
                                throw new Exception("Invalid string value " + parsed[i]);
                            }
                        } else if (sf.dataType().equals(DataTypes.LongType)) {
                            record[i] = Long.parseLong(parsed[i]);
                        } else if (sf.dataType().equals(DataTypes.DoubleType)) {
                            record[i] = Double.parseDouble(parsed[i]);
                        } else if (sf.dataType().equals(DataTypes.IntegerType)) {
                            record[i] = Integer.parseInt(parsed[i]);
                        } else if (sf.dataType().equals(DataTypes.TimestampType)) {
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
            } catch (NumberFormatException ex){
              errCounter.add(1);
              record = createRejectedRecord(line, "Invalid number format " + ex.getMessage());
            } catch(Exception e){
               errCounter.add(1);
               record = createRejectedRecord(line, e.getMessage());
            }
        }
        recCounter.add(1);
        return  RowFactory.create(record);
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
