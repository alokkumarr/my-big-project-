package sncr.xdf.ngcomponent.util;

import org.apache.log4j.Logger;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.context.XDFReturnCodes;
import java.util.Optional;
import sncr.bda.conf.ComponentConfiguration;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class NGComponentUtil {

    private static final Logger logger = Logger.getLogger(NGComponentUtil.class);

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

    public static boolean isErrorHandlingEnabled(Optional<ComponentConfiguration> optCfg){
        if(optCfg.isPresent() && optCfg.get() != null && optCfg.get().isErrorHandlingEnabled()){
            return true;
        }
        return false;
    }

    /**
     *
     * @param structType
     * @return
     *
     * getSanitizedStructType() method rename all sub field names like below
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
     * @param arrayType
     * @return
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
     * @param Dataset<Row>
     * @param String
     * @param DataType
     * @return Dataset<Row>
     *
     *changeColumnType() will cast input field with new input DataType.
     */
    public static Dataset<Row> changeColumnType(Dataset<Row> dataset, String colName, DataType dataType) {
        dataset = dataset.withColumn(colName,dataset.col(colName).cast(dataType));
        return dataset;
    }

    /**
     *
     * @param fieldName
     * @return
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
    public static String getSanitizedFieldName(String fieldName) {
        if(fieldName != null && !fieldName.trim().isEmpty()){
            fieldName = sanitizeFieldName(fieldName.trim().replaceAll("\\.", "_"));
        }
        return fieldName;
    }

    /**
     *
     * @param fieldName
     * @return
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
    public static String sanitizeFieldName(String fieldName) {
        String invalidCharRegex = "[^a-zA-Z0-9_ ]";
        if(fieldName != null && !fieldName.trim().isEmpty()){
            fieldName = fieldName.replaceAll(invalidCharRegex, "").trim()
                .replaceAll("\\s+", "_").toUpperCase();
        }
        return fieldName;
    }
}
