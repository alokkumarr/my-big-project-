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

public class Flattener {
    private static final Logger logger = Logger.getLogger(Flattener.class);
    private static final String SPARK_COLUMN_NAME_DELIMITER = ".";
    private static final String NEW_COLUMN_NAME_DELIMITER = "_";

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
                dataset = processStructType(dataset, field.name(), (StructType)datatype);
            }else if(datatype instanceof ArrayType){
                dataset = processArrayType(dataset, field.name(), (ArrayType)datatype);
            }
        }
        return dataset;
    }

    private Dataset<Row> processStructType(Dataset<Row> dataset, String parentColName, StructType structType){
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

    private Dataset<Row> processArrayType(Dataset<Row> dataset, String parentColName, ArrayType arrayType){
        logger.debug("Processing ArrayType Field");
        logger.debug("Parent Column Name : "+ parentColName);
        int arrSize = getArrayFieldMaxSize(dataset, parentColName);
        DataType childType = arrayType.elementType();
        logger.debug("childType: "+ childType);
        if(arrSize != 0){
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

    private int getArrayFieldMaxSize(Dataset<Row> dataset, String arrayColName){
        String newSizeColumn = arrayColName + NEW_COLUMN_NAME_DELIMITER + "arrSize";
        Dataset<Row> arraySizeDS = dataset.withColumn(newSizeColumn,size(dataset.col(arrayColName)));
        int arrSize = arraySizeDS.agg(max(arraySizeDS.col(newSizeColumn))).head().getInt(0);
        logger.debug(arrayColName+" Array Column Max Length : "+ arrSize);
        return arrSize;
    }
}
