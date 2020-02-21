package sncr.xdf.parser.spark;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.DataType;
import static org.apache.spark.sql.functions.explode;
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
        boolean isNested = false;
        for(StructField field : dsFields){
            String name = field.name();
            logger.debug("Field Name : "+ name);
            DataType datatype = field.dataType();
            logger.debug("Field Type : "+ datatype);
            logger.debug("Field Nullable : "+ field.nullable());
            if(datatype instanceof StructType){
                //StructType
                isNested=true;
                dataset = processStructType(dataset, field);
            }else if(datatype instanceof ArrayType){
                //ArrayType
                isNested=true;
                dataset = processArrayType(dataset, field);
            }
        }
        if(isNested){
            dataset = flattenDataset(dataset);
        }
        return dataset;
    }

    private Dataset<Row> processStructType(Dataset<Row> dataset, StructField structTypeField){
        logger.debug("Processing StructType Field");
        String parentColName = structTypeField.name();
        logger.debug("Parent Column Name : "+ parentColName);
        StructField[] subFields = ((StructType)structTypeField.dataType()).fields();
        logger.debug("Sub Fields : "+ Arrays.toString(subFields));
        for(StructField field : subFields){
            String name = field.name();
            logger.debug("Sub Field Name : "+ name);
            DataType datatype = field.dataType();
            logger.debug("Sub Field Type : "+ datatype);
            logger.debug("Sub Field Nullable : "+ field.nullable());

            String colName = parentColName + SPARK_COLUMN_NAME_DELIMITER + name;
            logger.debug("Full Column Name : "+ colName);
            String newColName = colName.replace(SPARK_COLUMN_NAME_DELIMITER, NEW_COLUMN_NAME_DELIMITER);
            logger.debug("New Column Name : "+ newColName);

            dataset = dataset.withColumn(newColName, dataset.col(colName));
        }
        dataset = dataset.drop(parentColName);
        return dataset;
    }

    private Dataset<Row> processArrayType(Dataset<Row> dataset, StructField arrayTypeField){
        logger.debug("Processing ArrayType Field");
        String colName = arrayTypeField.name();
        logger.debug("Array Column Name : "+ colName);
        dataset = dataset.withColumn(colName,explode(dataset.col(colName)));
        return dataset;
    }
}
