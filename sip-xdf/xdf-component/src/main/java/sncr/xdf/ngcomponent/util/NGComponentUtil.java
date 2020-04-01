package sncr.xdf.ngcomponent.util;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.context.XDFReturnCodes;
import java.util.Optional;
import sncr.bda.conf.ComponentConfiguration;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NGComponentUtil {

    private static final Logger logger = Logger.getLogger(NGComponentUtil.class);

    /**
     *
     * @param optComponent - Optional<AbstractComponent>
     * @param optCfg - Optional<ComponentConfiguration>
     * @param rc - int - Return Code from Component
     * @param e - Exception - Exception from Component
     * @return - int - Returns final return code of component.
     *
     * This method checks for non-zero return code and Exception.
     * Checks for isErrorHandlingEnabled Flag
     * If above flag is true then returns XDF Custom Error Code.
     * If above flag is false then return -1
     */
    public static int handleErrors(Optional<AbstractComponent> optComponent, Optional<ComponentConfiguration> optCfg, int rc, Exception e) {
        logger.info("handleErrors() : Return Code: " + rc +", Exception : ", e);
        AbstractComponent component = null;
        ComponentConfiguration config = null;
        if(optComponent.isPresent() &&  optComponent.get() != null){
            component = optComponent.get();
            config = component.getNgctx().componentConfiguration;
        }else if(optCfg.isPresent() &&  optCfg.get() != null){
            config = optCfg.get();
        }
        boolean isErrorHandlingEnabled = isErrorHandlingEnabled(Optional.ofNullable(config));
        logger.info("isErrorHandlingEnabled : "+ isErrorHandlingEnabled);
        if(isErrorHandlingEnabled) {
            try {
                logger.debug("handleErrors() Arg rc :" + rc);
                if (rc != 0 && e == null && !XDFReturnCodes.getMap().containsKey(rc)) {
                    logger.error("Non XDF Return Code :" + rc);
                    e = new XDFException(XDFReturnCode.INTERNAL_ERROR);
                }
                if (e != null) {
                    String description = e.getMessage();
                    if (e instanceof XDFException) {
                        rc = ((XDFException) e).getReturnCode().getCode();
                    } else {
                        rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                    }
                    if (component != null) {
                        component.getErrors().put(rc, description);
                        try {
                            component.finalize(rc);
                        } catch (Exception ex) {
                            if (ex instanceof XDFException) {
                                rc = ((XDFException) ex).getReturnCode().getCode();
                            } else {
                                rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                            }
                        }
                    }
                    logger.debug("Error Return Code :" + rc);
                } else {
                    rc = 0;
                }
            } catch (Exception ex) {
                if (ex instanceof XDFException) {
                    rc = ((XDFException) ex).getReturnCode().getCode();
                } else {
                    rc = XDFReturnCode.INTERNAL_ERROR.getCode();
                }
            }
            logger.info("Component Return Code :" + rc);
        }else{
            rc = (rc == 0 && e == null) ? 0 : -1;
        }
        return rc;
    }

    /**
     *
     * @param optCfg - Optional<ComponentConfiguration>
     * @return - boolean - returns true if isErrorHandlingEnabled else false.
     *
     * This method returns true if isErrorHandlingEnabled else false.
     */
    public static boolean isErrorHandlingEnabled(Optional<ComponentConfiguration> optCfg){
        if(optCfg.isPresent() && optCfg.get() != null && optCfg.get().isErrorHandlingEnabled()){
            return true;
        }
        return false;
    }

    /**
     *
     * @param zipIndexRdd - JavaPairRDD<String, Long> - after ZipWithIndex on rdd
     * @param headerSize - int - File Header Size.
     * @param fieldDefRowNumber - int - Filed Names Line Number in Header
     * @return - String - Returns Header record string
     *
     * This method takes JavaPairRDD, headerSize and fieldDefRowNumber
     * And Returns Header Record String from JavaPairRDD (CSV File RDD).
     * We are using this method to get Header from CSV File RDD.
     */
    public static String getHeaderRecordFromRdd(JavaPairRDD<String, Long> zipIndexRdd, int headerSize, int fieldDefRowNumber){
        if(headerSize < 1){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "headerSize should not be less than 1.");
        }else if(fieldDefRowNumber < 0){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "fieldDefRowNumber should not be less than 0.");
        }else if(headerSize < fieldDefRowNumber){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "fieldDefRowNumber should not be greater than headerSize");
        }else if(headerSize == 1){
            fieldDefRowNumber = 1;
        }
        return getRecordFromRdd(zipIndexRdd, fieldDefRowNumber);
    }

    /**
     *
     * @param zipIndexRdd - JavaPairRDD<String, Long> - after ZipWithIndex on rdd
     * @param lineNumber - int - Line Number.
     * @return - String - Returns Line Number Record string
     *
     * This method takes JavaPairRDD, lineNumber
     * And Returns Line Number Record String from JavaPairRDD (CSV File RDD).
     *
     */
    public static String getRecordFromRdd(JavaPairRDD<String, Long> zipIndexRdd, int lineNumber){
        final int index = lineNumber-1;
        String line  = null;
        if(zipIndexRdd != null && index >= 0){
            try{
                //JavaPairRDD contains key and value pair
                //zipIndexRdd below contains Entire Row from File as Key, Record Index as value.
                //_1 is to access key from JavaPairRDD
                //_2 is to access value from JavaPairRDD
                line = zipIndexRdd.filter(row->row._2==index).map(row->row._1).first();
            } catch (Exception e) {
                logger.error("Exception occurred while getting line from rdd :" + e);
            }
        }
        logger.debug("Line :: :" + line);
        return line;
    }

    /**
     * Validate the column string with the quote char
     *
     * @param inputString - String - Input String
     * @quoteChar - char - Quote Character
     * @return - boolean - returns true if valid else false
     */
    public static boolean validateString(String inputString, char quoteChar) {
        boolean status = validateQuoteBalance(inputString, quoteChar);
        logger.debug("Have valid quote balanced string : " + status);
        return status;
    }

    /**
     * Validate string column with balance quote
     *
     * @param inputString - String - Input String
     * @param quoteCharacter - char - Quote Character
     * @return - boolean - returns true if the column value valid else false
     */
    public static boolean validateQuoteBalance(String inputString, char quoteCharacter) {
        int charCount = countChar(inputString, quoteCharacter);
        return  (charCount % 2) == 0;
    }

    /**
     * Count the number of char for balance character
     *
     * @param inputString - String - Input String
     * @param character - char - Character to Count
     * @return - int - returns no of balance char
     */
    public static int countChar(String inputString, char character) {
        int count = 0;
        for(char c: inputString.toCharArray()) {
            if (c == character) {
                count += 1;
            }
        }
        return count;
    }
    /**
     *
     * @param structType - StructType - Input StructType or Schema
     * @return - StructType - Returns Sanitized Field Names StructType
     *
     * This method renames all sub field names like below
     *
     * example: If sub field name is " Test col.name * "
     * If we observe sub field name contains special charcters, spaces, and "."
     * 1) Trim Filed Name. It becomes "Test col.name *"
     * 2) Replace "." with "_". It becomes "Test col_name *"
     * 3) Remove all special characters. It becomes "Test col_name "
     * 4) Trim Name. It becomes "Test col_name"
     * 5) Replace spaces with "_". It becomes "Test_col_name"
     * 6) Uppercase name. It becomes "TEST_COL_NAME"
     *
     * It repeat this logic for all sub fields in given StructType
     *
     * It calls recursively if subfields again StructType or ArrayType.
     *
     * And returns new StructType with Sanitized field names.
     *
     */
    public static StructType getSanitizedStructType(StructType structType) {
        StructType sanitizedStructType = new StructType();
        for(StructField field : structType.fields()){
            DataType childType = field.dataType();
            if(childType instanceof StructType){
                childType = getSanitizedStructType((StructType)childType);
            }else if(childType instanceof ArrayType){
                childType = getSanitizedArrayType((ArrayType)childType);
            }
            sanitizedStructType = sanitizedStructType.add(StructField.apply(getSanitizedFieldName(field.name()),childType,field.nullable(),field.metadata()));
        }
        return sanitizedStructType;
    }

    /**
     *
     * @param arrayType - ArrayType - Input ArrayType
     * @return - ArrayType - Returns Sanitized Field Names ArrayType
     *
     * getSanitizedArrayType() method rename all nested field names like below
     *
     * example: If nested field name is " Test col.name * "
     * If we observe sub field name contains special charcters, spaces, and "."
     * 1) Trim Filed Name. It becomes "Test col.name *"
     * 2) Replace "." with "_". It becomes "Test col_name *"
     * 3) Remove all special characters. It becomes "Test col_name "
     * 4) Trim Name. It becomes "Test col_name"
     * 5) Replace spaces with "_". It becomes "Test_col_name"
     * 6) Uppercase name. It becomes "TEST_COL_NAME"
     *
     * It repeat this logic for all nested fields in given ArrayType
     *
     * It calls recursively if subfields again StructType or ArrayType.
     *
     * And returns new ArrayType with Sanitized field names.
     *
     */
    public static ArrayType getSanitizedArrayType(ArrayType arrayType) {
        DataType childType = arrayType.elementType();
        if(childType instanceof StructType){
            childType = getSanitizedStructType((StructType)childType);
        }else if(childType instanceof ArrayType){
            childType = getSanitizedArrayType((ArrayType)childType);
        }
        ArrayType sanitizedArrayType = ArrayType.apply(childType);
        return sanitizedArrayType;
    }

    /**
     *
     * @param dataset - Dataset<Row> - Input Dataset
     * @param colName - String - Column Name which type has to convert
     * @param dataType - DataType - Target DataType to be applied
     * @return
     *
     *changeColumnType() will cast input field with new input DataType.
     */
    public static Dataset<Row> changeColumnType(Dataset<Row> dataset, String colName, DataType dataType) {
        dataset = dataset.withColumn(colName,dataset.col(colName).cast(dataType));
        return dataset;
    }

    /**
     *
     * @param inputString - String - Input String
     * @return - String - Returns Sanitized String
     *
     * getSanitizedFieldName() method rename name like below
     *
     * example: If name is " Test col.name * "
     * If we observe sub field name contains special charcters, spaces, and "."
     * 1) Trim Filed Name. It becomes "Test col.name *"
     * 2) Replace "." with "_". It becomes "Test col_name *"
     * 3) Remove all special characters. It becomes "Test col_name "
     * 4) Trim Name. It becomes "Test col_name"
     * 5) Replace spaces with "_". It becomes "Test_col_name"
     * 6) Uppercase name. It becomes "TEST_COL_NAME"
     *
     */
    public static String getSanitizedFieldName(String inputString) {
        if(inputString != null && !inputString.trim().isEmpty()){
            inputString = sanitizeFieldName(inputString.trim().replaceAll("\\.", "_"));
        }
        return inputString;
    }

    /**
     *
     * @param inputString - String - Input String
     * @return - String - Returns Sanitized String
     *
     * sanitizeFieldName() method rename name like below
     *
     * example: If is " Test col_name * "
     * If we observe sub field name contains special charcters, spaces
     * 1) Remove all special characters. It becomes " Test col_name "
     * 2) Trim Name. It becomes "Test col_name"
     * 3) Replace spaces with "_". It becomes "Test_col_name"
     * 4) Uppercase name. It becomes "TEST_COL_NAME"
     *
     */
    public static String sanitizeFieldName(String inputString) {
        String invalidCharRegex = "[^a-zA-Z0-9_ ]";
        if(inputString != null && !inputString.trim().isEmpty()){
            inputString = inputString.replaceAll(invalidCharRegex, "").trim()
                .replaceAll("\\s+", "_").toUpperCase();
        }
        return inputString;
    }
}
