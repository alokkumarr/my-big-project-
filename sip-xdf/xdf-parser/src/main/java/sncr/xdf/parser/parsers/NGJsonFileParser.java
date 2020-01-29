package sncr.xdf.parser.parsers;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.InternalContext;
import org.apache.spark.sql.types.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.spark.sql.functions;

public class NGJsonFileParser implements FileParser {
    private InternalContext iCtx;
    private static final Logger logger = Logger.getLogger(NGJsonFileParser.class);
    private static final String SPARK_COLUMN_NAME_DELIMITER = ".";
    private static final String NEW_COLUMN_NAME_DELIMITER = "_";

    public NGJsonFileParser(InternalContext iCtx) {
        this.iCtx = iCtx;
    }

    public Dataset<Row> parseInput(String inputLocation,boolean multiLine) {
        logger.debug("Is multiLine : " + multiLine + "\n");
        Dataset<Row> inputDataset = iCtx.sparkSession.read().option("multiline", multiLine).json(inputLocation);
        Dataset<Row> outputDS = processNestedJsonDS(inputDataset);
        return outputDS;
    }

    @Override
    public Dataset<Row> parseInput(String inputLocation){
        Dataset<Row> inputDataset =iCtx.sparkSession.read().json(inputLocation);
        Dataset<Row> outputDS = processNestedJsonDS(inputDataset);
        return outputDS;
    }

    private Dataset<Row> processNestedJsonDS(Dataset<Row> dataset){
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
                break;
            }else if(datatype instanceof ArrayType){
                //ArrayType
                isNested=true;
                dataset = processArrayType(dataset, field);
                break;
            }
        }
        if(isNested){
            dataset = processNestedJsonDS(dataset);
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
        return dataset.drop(parentColName);
    }

    private Dataset<Row> processArrayType(Dataset<Row> dataset, StructField arrayTypeField){
        logger.debug("Processing ArrayType Field");
        String parentColName = arrayTypeField.name();
        logger.debug("Parent Column Name : "+ parentColName);
        int arrSize = getArrayFieldMaxSize(dataset, parentColName);
        for(int index = 0; index < arrSize; index++) {
            String newColName = parentColName +  NEW_COLUMN_NAME_DELIMITER + index;
            logger.debug("New Column Name : "+ newColName);
            dataset = dataset.withColumn(newColName, dataset.col(parentColName).getItem(index));
        }
        return dataset.drop(parentColName);
    }

    private int getArrayFieldMaxSize(Dataset<Row> dataset, String arrayColName){
        String newSizeColumn = arrayColName + NEW_COLUMN_NAME_DELIMITER + "arrSize";
        Dataset<Row> arraySizeDS = dataset.withColumn(newSizeColumn,functions.size(dataset.col(arrayColName)));
        int arrSize = arraySizeDS.agg(functions.max(arraySizeDS.col(newSizeColumn))).head().getInt(0);
        logger.debug(arrayColName+" Array Column Max Length : "+ arrSize);
        return arrSize;
    }
}
