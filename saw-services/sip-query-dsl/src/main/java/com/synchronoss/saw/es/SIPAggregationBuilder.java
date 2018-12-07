package com.synchronoss.saw.es;

import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.util.BuilderUtil;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.List;

public class SIPAggregationBuilder {

    private final static String GROUP_BY_FIELD = "group_by_field";
    private Integer querySize;
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    public SIPAggregationBuilder(Integer querySize) {
        this.querySize=querySize;
    }

    public AggregationBuilder reportAggregationBuilder(List<Field> dataFields,
                                                       List<Field> aggregateFields, int fieldCount,
                                                       int aggregatedFieldCount
            , AggregationBuilder aggregationBuilder)
       {
        /**
         * For Report find the list of Aggregate fields.
         */
        if ((fieldCount + aggregateFields.size())< dataFields.size()) {
            Field dataField = dataFields.get(fieldCount+aggregatedFieldCount);
            if(dataField.getAggregate()!=null) {
                aggregatedFieldCount++;
                return reportAggregationBuilder(dataFields, aggregateFields,
                        fieldCount,aggregatedFieldCount, aggregationBuilder);
            }
                if (aggregationBuilder == null) {
                    // initialize the terms aggregation builder.
                    if (dataField.getType().name().equals(Field.Type.DATE.name())
                        || dataField.getType().name().equals(Field.Type.TIMESTAMP.name()))
                    {
                        if (dataField.getGroupInterval()!=null){
                            aggregationBuilder = AggregationBuilders.
                                dateHistogram(dataField.getDisplayName()).field(dataField.getColumnName()).format(DATE_FORMAT).
                                dateHistogramInterval(groupInterval(dataField.getGroupInterval().value())).order(BucketOrder.key(false));
                        }
                        else {
                            aggregationBuilder =  AggregationBuilders.terms(dataField.getDisplayName()).field(dataField.getColumnName())
                                .format(DATE_FORMAT).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
                        }
                    }
                    else {
                        aggregationBuilder = AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                            .field(dataField.getColumnName()).size(querySize);
                    }
                    for(Field dataField1 : aggregateFields) {
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

   public static List<Field> getAggregationField(List<Field> dataFields)
   {
       List<Field> aggregateFields = new ArrayList<>();
       for(Field dataField : dataFields) {
       if (dataField.getAggregate() != null) {
           aggregateFields.add(dataField);
       }
       }
       return aggregateFields;
   }

   public void reportAggregationBuilder(List<Field> dataFields, List<Field> aggregateFields,
                                        SearchSourceBuilder searchSourceBuilder)
    {
        // if only aggregation fields are there.

        if (aggregateFields.size() == dataFields.size()) {
        for (Field dataField1 : aggregateFields) {
            searchSourceBuilder.aggregation(QueryBuilderUtil.aggregationBuilderDataFieldReport(
                    dataField1));
        }
    }
    }

    public static DateHistogramInterval groupInterval(String groupInterval)
    {
        DateHistogramInterval histogramInterval = null;
        switch (groupInterval)
        {
            case "month" : histogramInterval =  DateHistogramInterval.MONTH; break;
            case "day" : histogramInterval =  DateHistogramInterval.DAY; break;
            case "year" : histogramInterval =  DateHistogramInterval.YEAR; break;
            case "quarter" : histogramInterval =  DateHistogramInterval.QUARTER; break;
            case "hour" : histogramInterval =  DateHistogramInterval.HOUR;break;
            case "week" : histogramInterval =  DateHistogramInterval.WEEK;break;
        }
        return histogramInterval;
    }
}


