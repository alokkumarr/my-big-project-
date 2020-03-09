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
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;

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

    public static Dataset<Row> changeDatasetSchema(Dataset<Row> dataset, StructType newSchema) {
        ExpressionEncoder<Row> encoder = RowEncoder.apply(newSchema);
        dataset = dataset.as(encoder);
        return dataset;
    }

    public static Dataset<Row> changeColumnStructType(Dataset<Row> dataset, String colName, StructType newStructType) {
        dataset = dataset.withColumn(colName,dataset.col(colName).cast(newStructType));
        return dataset;
    }

    public static String getSanitizedFieldName(String fieldName) {
        if(fieldName != null && !fieldName.trim().isEmpty()){
            fieldName = sanitizeFieldName(fieldName.replaceAll("\\.", "_"));
        }
        return fieldName;
    }

    public static String sanitizeFieldName(String fieldName) {
        String invalidCharRegex = "[^a-zA-Z0-9_ ]";
        if(fieldName != null && !fieldName.trim().isEmpty()){
            fieldName = fieldName.replaceAll(invalidCharRegex, "").trim()
                .replaceAll("\\s+", "_").toUpperCase();
        }
        return fieldName;
    }
}
