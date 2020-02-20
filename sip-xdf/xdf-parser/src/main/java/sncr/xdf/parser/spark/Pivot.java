package sncr.xdf.parser.spark;

import org.apache.log4j.Logger;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.XDFReturnCode;
import org.apache.spark.sql.RelationalGroupedDataset;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.spark.sql.functions;
import java.util.Arrays;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.*;
import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.explode;
import sncr.bda.conf.PivotFields;

public class Pivot {
    private static final Logger logger = Logger.getLogger(Pivot.class);
    private String pivotFieldName = null;
    private String aggFieldName = null;
    private static final String SPARK_COLUMN_NAME_DELIMITER = ".";
    private static final String NEW_COLUMN_NAME_DELIMITER = "_";

    public Dataset<Row> applyPivot(Dataset<Row> inputDS, PivotFields pivotFields){
        logger.debug("==> applyPivot()");
        if(pivotFields == null) {
            return inputDS;
        }else{
            String[] groupByColumns = pivotFields.getGroupByColumns();
            String pivotColumn = pivotFields.getPivotColumn();
            String aggregateColumn = pivotFields.getAggregateColumn();
            validatePivotFields(groupByColumns, pivotColumn, aggregateColumn);
            Dataset<Row> pivotExplodeDS = parsePivotField(inputDS, pivotColumn);
            Dataset<Row> aggExplodeDS = parseAggregateField(pivotExplodeDS, pivotColumn, aggregateColumn);
            RelationalGroupedDataset groupByDS = null;
            logger.debug("Number of GroupBy Columns are : "+ groupByColumns.length);
            if(groupByColumns.length == 1){
                groupByDS = aggExplodeDS.groupBy(groupByColumns[0]);
            }else{
                groupByDS = aggExplodeDS.groupBy(groupByColumns[0].trim(), IntStream.range(1, groupByColumns.length)
                    .mapToObj(index -> groupByColumns[index].trim())
                    .toArray(String[]::new));
            }
            logger.debug("groupByDS count : "+ groupByDS.count());
            Dataset<Row> pivotDS = groupByDS.pivot(pivotFieldName).agg(max(aggFieldName));
            logger.debug("pivotDS count : "+ pivotDS.count());
            return pivotDS;
        }
    }

    private Dataset<Row> parsePivotField(Dataset<Row> inputDS, String pivotColumn){
        logger.debug("==> parsePivotField()");
        StructType schema = inputDS.schema();
        logger.debug("inputDS Schema : "+ schema);
        StructField[] fields = schema.fields();
        logger.debug("DS Fields : "+ Arrays.toString(fields));
        String[] pivotColumnSplit = pivotColumn.trim().split(SPARK_COLUMN_NAME_DELIMITER);
        Dataset<Row> explodeDS = inputDS;
        StructField pivotField = null;
        int index = 0;
        int length = pivotColumnSplit.length;
        for(String column : pivotColumnSplit){
            if(pivotFieldName == null){
                pivotFieldName = column;
            }else{
                pivotFieldName = pivotFieldName + SPARK_COLUMN_NAME_DELIMITER + column;
            }
            pivotField = getDSFiled(fields, pivotFieldName);
            if(pivotField.dataType() instanceof StructType){
                if(index == length-1){
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot column - " + pivotFieldName + " - should not be Struct Type.");
                }else{
                    fields = ((StructType)pivotField.dataType()).fields();
                }
            }else if(pivotField.dataType() instanceof ArrayType){
                String newFiledName = pivotFieldName.replace(SPARK_COLUMN_NAME_DELIMITER, NEW_COLUMN_NAME_DELIMITER);
                do{
                    explodeDS = explodeDS.withColumn(newFiledName,explode(explodeDS.col(pivotFieldName)));
                    pivotField = getDSFiled(explodeDS.schema().fields(), newFiledName);
                    pivotFieldName = newFiledName;
                }while(pivotField.dataType() instanceof ArrayType);

                if(pivotField.dataType() instanceof StructType){
                    if(index == length-1){
                        throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot column - " + pivotFieldName + " - should not be Struct Type.");
                    }else{
                        fields = ((StructType)pivotField.dataType()).fields();
                    }
                }else if(pivotField.dataType() instanceof StringType){
                    if(index != length-1){
                        throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot Parent column - " + pivotFieldName + " - should be Struct Type.");
                    }
                }else{
                    if(index == length-1){
                        throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot column - " + pivotFieldName + " - should be String Type.");
                    }else{
                        throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot Parent column - " + pivotFieldName + " - should be Struct Type.");
                    }
                }
            }else if(pivotField.dataType() instanceof StringType){
                if(index != length-1){
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot Parent column - " + pivotFieldName + " - should be Struct or Array Type.");
                }
            }else{
                if(index == length-1){
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot column - " + pivotFieldName + " - should be String Type.");
                }else{
                    throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot Parent column - " + pivotFieldName + " - should be Struct or Array Type.");
                }
            }
            index++;
        }
        return explodeDS;
    }

    private Dataset<Row> parseAggregateField(Dataset<Row> inputDS, String pivotColumn, String aggregateColumn){
        logger.debug("==> parseAggregateField()");
        StructType schema = inputDS.schema();
        logger.debug("inputDS Schema : "+ schema);
        StructField[] fields = schema.fields();
        logger.debug("DS Fields : "+ Arrays.toString(fields));
        String[] aggColSplit = aggregateColumn.trim().split(SPARK_COLUMN_NAME_DELIMITER);
        Dataset<Row> explodeDS = inputDS;
        StructField aggField = null;
        int index = 0;
        int length = aggColSplit.length;
        for(String column : aggColSplit){
            if(aggFieldName == null){
                aggFieldName = column;
            }else{
                aggFieldName = aggFieldName + SPARK_COLUMN_NAME_DELIMITER + column;
            }
            if(!pivotColumn.toUpperCase().startsWith(aggFieldName.toUpperCase())){
                aggField = getDSFiled(fields, aggFieldName);
                if(aggField.dataType() instanceof StructType){
                    fields = ((StructType)aggField.dataType()).fields();
                }else if(aggField.dataType() instanceof ArrayType){
                    String newFiledName = aggFieldName.replace(SPARK_COLUMN_NAME_DELIMITER, NEW_COLUMN_NAME_DELIMITER);
                    do{
                        explodeDS = explodeDS.withColumn(newFiledName,explode(explodeDS.col(aggFieldName)));
                        aggField = getDSFiled(explodeDS.schema().fields(), newFiledName);
                        aggFieldName = newFiledName;
                    }while(aggField.dataType() instanceof ArrayType);

                    if(aggField.dataType() instanceof StructType){
                        fields = ((StructType)aggField.dataType()).fields();
                    }else{
                        if(index != length-1){
                            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Aggregate Parent column - " + aggFieldName + " - should be Struct Type.");
                        }
                    }
                }else{
                    if(index != length-1){
                        throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Aggregate Parent column - " + aggFieldName + " - should be Struct or Array Type.");
                    }
                }
            }
            index++;
        }
        return explodeDS;
    }

    private void validatePivotFields(String[] groupByColumns, String pivotColumn, String aggregateColumn){
        logger.debug("==> validatePivotFields()");
        logger.debug("pivotColumn : " + pivotColumn);
        if(pivotColumn == null || pivotColumn.trim().isEmpty()){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot column is null or empty.");
        }

        logger.debug("aggregateColumn : " + aggregateColumn);
        if(aggregateColumn == null || aggregateColumn.trim().isEmpty()){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Aggregate column is null or empty.");
        }
        if(pivotColumn.trim().equalsIgnoreCase(aggregateColumn.trim())){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot & Aggregate columns should not be same.");
        }

        logger.debug("groupByColumns : " + Arrays.toString(groupByColumns));
        if(groupByColumns == null || groupByColumns.length == 0
            || Arrays.stream(groupByColumns).anyMatch(col -> col == null || col.trim().isEmpty())){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Group By columns array is null or empty or contains null or empty values.");
        }

        if(Arrays.stream(groupByColumns).anyMatch(col -> col.trim().equalsIgnoreCase(pivotColumn) || col.trim().equalsIgnoreCase(aggregateColumn))){
            throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Group By columns array should not contains Pivot & Aggregate columns.");
        }
    }

    private StructField getDSFiled(StructField[] fields, String fieldName){
        logger.trace("==> getDSFiled()");
        for(StructField field : fields){
            if(field.name().equalsIgnoreCase(fieldName)){
                return field;
            }
        }
        throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Pivot Config is not correct. Pivot column - "+fieldName+" - is not exist in Dataset schema.");
    }
}
