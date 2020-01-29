package sncr.xdf.parser.parsers;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.InternalContext;
import org.apache.spark.sql.types.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.spark.sql.functions;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    private Dataset<Row> processNestedJsonDS(final Dataset<Row> dataset){
        StructType dsSchema = dataset.schema();
        logger.debug("Root DS Schema : "+ dsSchema);
        StructField[] dsFields = dsSchema.fields();
        logger.debug("Root DS Fields : "+ Arrays.toString(dsFields));
        AtomicBoolean isNested = new AtomicBoolean(false);
        AtomicReference<Dataset<Row>> outputDataset =  new AtomicReference<>();
        Arrays.stream(dsFields).forEach(field -> {
            String name = field.name();
            logger.debug("Field Name : "+ name);
            DataType datatype = field.dataType();
            logger.debug("Field Type : "+ datatype);
            logger.debug("Field Nullable : "+ field.nullable());
            if(datatype instanceof StructType){
                //StructType
                isNested.set(true);
                outputDataset.set(processStructType(dataset, field));
            }else if(datatype instanceof ArrayType){
                //ArrayType
                isNested.set(true);
                outputDataset.set(processArrayType(dataset, field));
            }
        });
        if(isNested.get()){
            outputDataset.set(processNestedJsonDS(dataset));
        }
        return outputDataset.get();
    }

    private Dataset<Row> processStructType(final Dataset<Row> dataset, StructField structTypeField){
        logger.debug("Processing StructType Field");
        final String parentColName = structTypeField.name();
        logger.debug("Parent Column Name : "+ parentColName);
        StructField[] dsFields = ((StructType)structTypeField.dataType()).fields();
        logger.debug("DS Fields : "+ Arrays.toString(dsFields));
        AtomicReference<Dataset<Row>> outputDataset =  new AtomicReference<>();
        Arrays.stream(dsFields).forEach(field -> {
            String name = field.name();
            logger.debug("Sub Field Name : "+ name);
            DataType datatype = field.dataType();
            logger.debug("Sub Field Type : "+ datatype);
            logger.debug("Sub Field Nullable : "+ field.nullable());

            String colName = parentColName + SPARK_COLUMN_NAME_DELIMITER + name;
            logger.debug("Full Column Name : "+ colName);
            String newColName = colName.replace(SPARK_COLUMN_NAME_DELIMITER, NEW_COLUMN_NAME_DELIMITER);
            logger.debug("New Column Name : "+ newColName);

            outputDataset.set(dataset.withColumn(newColName, dataset.col(colName)));
        });
        return outputDataset.get().drop(parentColName);
    }

    private Dataset<Row> processArrayType(final Dataset<Row> dataset, StructField arrayTypeField){
        logger.debug("Processing ArrayType Field");
        final String parentColName = arrayTypeField.name();
        logger.debug("Parent Column Name : "+ parentColName);
        int arrSize = getArrayFieldMaxSize(dataset, parentColName);
        AtomicReference<Dataset<Row>> outputDataset =  new AtomicReference<>();
        IntStream.range(0, arrSize).forEach(index -> {
            String newColName = parentColName +  NEW_COLUMN_NAME_DELIMITER + index;
            logger.debug("New Column Name : "+ newColName);
            outputDataset.set(dataset.withColumn(newColName, dataset.col(parentColName).getItem(index)));
        });
        return outputDataset.get().drop(parentColName);
    }

    private int getArrayFieldMaxSize(Dataset<Row> dataset, String arrayColName){
        String newSizeColumn = "arrSize";
        int arrSize = dataset.withColumn(newSizeColumn,functions.size(dataset.col(arrayColName)))
            .agg(functions.max(dataset.col(newSizeColumn))).head().getInt(0);
        logger.debug(arrayColName+" Array Column Max Length : "+ arrSize);
        dataset = dataset.drop(newSizeColumn);
        return arrSize;
    }
}
