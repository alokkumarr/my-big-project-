package sncr.xdf.parser.spark;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.DataType;
import static org.apache.spark.sql.functions.explode;
import static org.apache.spark.sql.functions.explode_outer;
import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.size;
import java.util.Arrays;
import java.util.Optional;
import sncr.xdf.context.InternalContext;
import sncr.xdf.services.WithDataSet;
import sncr.bda.core.file.HFileOperations;
import org.apache.hadoop.fs.Path;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.util.NGComponentUtil;

public class Flattener {
    private static final Logger logger = Logger.getLogger(Flattener.class);
    private static final String SPARK_COLUMN_NAME_DELIMITER = ".";
    private static final String NEW_COLUMN_NAME_DELIMITER = "_";

    public Flattener(InternalContext ctx, WithDataSet withDataSet, WithDataSet.DataSetHelper datasetHelper){
        setCheckpointDir(ctx, withDataSet, datasetHelper);
    }

    /**
     *
     * @param ctx
     * @param withDataSet
     * @param datasetHelper
     *
     * setCheckpointDir() gets temp checkpoint directory path and assign as checkpointDir to SparkContext.
     * checkpointDir is require to apply checkpoint on Spark Dataset.
     * checkpoint is require in Flattening because of too many transformations apply if it is nested structure.
     * if there are too many transformations then Spark DAG will break. Checkpoint will help DAG to remember all transformations.
     *
     */
    private void setCheckpointDir(InternalContext ctx, WithDataSet withDataSet, WithDataSet.DataSetHelper datasetHelper) {
        try {
            String checkpointDir = withDataSet.generateCheckpointLocation(datasetHelper, null, null);
            if (ctx.fs.exists(new Path(checkpointDir))) {
                HFileOperations.deleteEnt(checkpointDir);
            }
            HFileOperations.createDir(checkpointDir);
            ctx.sparkSession.sparkContext().setCheckpointDir(checkpointDir);
        }catch (Exception e) {
            logger.error("Exception in creating checkpoint Dir : ", e);
            if (e instanceof XDFException) {
                throw ((XDFException) e);
            } else {
                throw new XDFException(XDFReturnCode.INTERNAL_ERROR, e);
            }
        }
    }

    public Dataset<Row> flattenDataset(Dataset<Row> dataset){
        StructType dsSchema = dataset.schema();
        logger.debug("DS Schema : "+ dsSchema);
        StructField[] dsFields = dsSchema.fields();
        logger.debug("DS Fields : "+ Arrays.toString(dsFields));
        for(StructField field : dsFields){
            String name = field.name();
            logger.debug("Field Name : "+ name);
            DataType datatype = field.dataType();
            logger.debug("Field Type : "+ datatype);
            if(datatype instanceof StructType){
                StructType newStructType = NGComponentUtil.getSanitizedStructType((StructType)datatype);
                dataset = NGComponentUtil.changeColumnType(dataset, name, newStructType);
                dataset = processStructType(dataset, name, newStructType);
            }else if(datatype instanceof ArrayType){
                ArrayType newArrayType = NGComponentUtil.getSanitizedArrayType((ArrayType)datatype);
                dataset = NGComponentUtil.changeColumnType(dataset, name, newArrayType);
                dataset = processArrayType(dataset, name, newArrayType);
            }
        }
        return dataset;
    }

    public Dataset<Row> processStructType(Dataset<Row> dataset, String parentColName, StructType structType){
        logger.debug("Processing StructType Field");
        logger.debug("Parent Column Name : "+ parentColName);
        StructField[] subFields = structType.fields();
        logger.debug("Sub Fields : "+ Arrays.toString(subFields));
        for(StructField field : subFields){
            DataType childType = field.dataType();
            logger.debug("childType: "+ childType);
            String colName = parentColName + SPARK_COLUMN_NAME_DELIMITER + field.name();
            logger.debug("Child Column Name : "+ colName);
            String newColName = colName.replace(SPARK_COLUMN_NAME_DELIMITER, NEW_COLUMN_NAME_DELIMITER);
            logger.debug("Child New Column Name : "+ newColName);
            dataset = dataset.withColumn(newColName, dataset.col(colName));
            if(childType instanceof StructType){
                dataset = processStructType(dataset, newColName, (StructType)childType);
            }else if(childType instanceof ArrayType){
                dataset = processArrayType(dataset, newColName, (ArrayType)childType);
            }
        }
        dataset = dataset.drop(parentColName);
        dataset = dataset.checkpoint(false);
        return dataset;
    }

/*    private Dataset<Row> processArrayType(Dataset<Row> dataset, StructField arrayTypeField){
        logger.debug("Processing ArrayType Field");
        String colName = arrayTypeField.name();
        logger.debug("Array Column Name : "+ colName);
        dataset = dataset.withColumn(colName,explode(dataset.col(colName)));
        return dataset;
    }*/

    public Dataset<Row> processArrayType(Dataset<Row> dataset, String parentColName, ArrayType arrayType){
        logger.debug("Processing ArrayType Field");
        logger.debug("Parent Column Name : "+ parentColName);
        int arrSize = getArrayFieldMaxSize(dataset, parentColName);
        DataType childType = arrayType.elementType();
        logger.debug("childType: "+ childType);
        if(arrSize > 0){
            for(int index = 0; index < arrSize; index++) {
                String newColName = parentColName +  NEW_COLUMN_NAME_DELIMITER + index;
                logger.debug("Child New Column Name : "+ newColName);
                dataset = dataset.withColumn(newColName, dataset.col(parentColName).getItem(index));
                if(childType instanceof StructType){
                    dataset = processStructType(dataset, newColName, (StructType)childType);
                }else if(childType instanceof ArrayType){
                    dataset = processArrayType(dataset, newColName, (ArrayType)childType);
                }
            }
            dataset = dataset.drop(parentColName);
        }else{
            dataset = dataset.withColumn(parentColName,explode_outer(dataset.col(parentColName)));
        }
        dataset = dataset.checkpoint(false);
        return dataset;
    }

    public int getArrayFieldMaxSize(Dataset<Row> dataset, String arrayColName){
        String newSizeColumn = arrayColName + NEW_COLUMN_NAME_DELIMITER + "arrSize";
        Dataset<Row> arraySizeDS = dataset.withColumn(newSizeColumn,size(dataset.col(arrayColName)));
        int arrSize = arraySizeDS.agg(max(arraySizeDS.col(newSizeColumn))).head().getInt(0);
        logger.debug(arrayColName+" Array Column Max Length : "+ arrSize);
        return arrSize;
    }
}
