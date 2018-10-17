package com.synchronoss.querybuilder;

import com.synchronoss.querybuilder.model.report.Column;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.List;

public class ReportAggregationBuilder {

    private final static String GROUP_BY_FIELD = "group_by_field";
    private Integer querySize;

    public ReportAggregationBuilder(Integer querySize) {
        this.querySize=querySize;
    }

    public AggregationBuilder reportAggregationBuilder(List<Column> dataFields,
                                                       List<Column> aggregateFields, int fieldCount,
                                                       int aggregatedFieldCount
            , AggregationBuilder aggregationBuilder)
       {
        /**
         * For Report find the list of Aggregate fields.
         */
        if ((fieldCount + aggregateFields.size())< dataFields.size()) {
            Column dataField = dataFields.get(fieldCount+aggregatedFieldCount);
            if(dataField.getAggregate()!=null) {
                aggregatedFieldCount++;
                return reportAggregationBuilder(dataFields, aggregateFields,
                        fieldCount,aggregatedFieldCount, aggregationBuilder);
            }
                if (aggregationBuilder == null) {
                    // initialize the terms aggregation builder.
                    aggregationBuilder = AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                            .field(dataField.getColumnName()).size(querySize);
                    for(Column dataField1 : aggregateFields) {
                        aggregationBuilder.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldReport(
                                dataField1));
                    }
                    return reportAggregationBuilder(dataFields, aggregateFields,
                            fieldCount,aggregatedFieldCount, aggregationBuilder);

                } else {
                    AggregationBuilder aggregationBuilderMain =
                                    AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                                            .field(dataField.getColumnName())
                                            .subAggregation(aggregationBuilder).size(querySize);

                    return reportAggregationBuilder(dataFields, aggregateFields,
                            fieldCount,aggregatedFieldCount, aggregationBuilderMain);
                }
            }
        else {
            return aggregationBuilder;
        }
       }

   public static List<Column> getAggregationField(List<Column> dataFields)
   {
       List<Column> aggregateFields = new ArrayList<>();
       for(Column dataField : dataFields) {
       if (dataField.getAggregate() != null) {
           aggregateFields.add(dataField);
       }
       }
       return aggregateFields;
   }

   public void reportAggregationBuilder(List<Column> dataFields, List<Column> aggregateFields,
                                        SearchSourceBuilder searchSourceBuilder)
    {
        // if only aggregation fields are there.

        if (aggregateFields.size() == dataFields.size()) {
        for (Column dataField1 : aggregateFields) {
            searchSourceBuilder.aggregation(QueryBuilderUtil.aggregationBuilderDataFieldReport(
                    dataField1));
        }
    }
    }
}


