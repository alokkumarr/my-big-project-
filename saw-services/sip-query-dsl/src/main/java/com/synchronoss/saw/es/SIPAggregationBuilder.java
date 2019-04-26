package com.synchronoss.saw.es;

import static org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders.bucketSort;

import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.util.BuilderUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class SIPAggregationBuilder {

  public static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String GROUP_BY_FIELD = "group_by_field";
  private Integer querySize;

  public SIPAggregationBuilder(Integer querySize) {
    this.querySize = querySize;
  }

  public static List<Field> getAggregationField(List<Field> dataFields) {
    List<Field> aggregateFields = new ArrayList<>();
    for (Field dataField : dataFields) {
      if (dataField.getAggregate() != null) {
        aggregateFields.add(dataField);
      }
    }
    return aggregateFields;
  }

  public static DateHistogramInterval groupInterval(String groupInterval) {
    DateHistogramInterval histogramInterval = null;
    switch (groupInterval) {
      case "month":
        histogramInterval = DateHistogramInterval.MONTH;
        break;
      case "day":
      case "all":
        histogramInterval = DateHistogramInterval.DAY;
        break;
      case "year":
        histogramInterval = DateHistogramInterval.YEAR;
        break;
      case "quarter":
        histogramInterval = DateHistogramInterval.QUARTER;
        break;
      case "hour":
        histogramInterval = DateHistogramInterval.HOUR;
        break;
      case "week":
        histogramInterval = DateHistogramInterval.WEEK;
        break;
    }
    return histogramInterval;
  }

  public AggregationBuilder reportAggregationBuilder(
      List<Field> dataFields,
      List<Field> aggregateFields,
      int fieldCount,
      int aggregatedFieldCount,
      AggregationBuilder aggregationBuilder) {
    /** For Report find the list of Aggregate fields. */
    if ((fieldCount + aggregateFields.size()) < dataFields.size()) {
      Field dataField = dataFields.get(fieldCount + aggregatedFieldCount);
      if (dataField.getAggregate() != null) {
        aggregatedFieldCount++;
        return reportAggregationBuilder(
            dataFields, aggregateFields, fieldCount, aggregatedFieldCount, aggregationBuilder);
      }
      if (aggregationBuilder == null) {
        // initialize the terms aggregation builder.
        if (dataField.getType().name().equals(Field.Type.DATE.name())
            || dataField.getType().name().equals(Field.Type.TIMESTAMP.name())) {
          if (dataField.getDateFormat() == null || dataField.getDateFormat().isEmpty())
            dataField.setDateFormat(DATE_FORMAT);
          if (dataField.getGroupInterval() != null) {
            aggregationBuilder =
                AggregationBuilders.dateHistogram(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .format(dataField.getDateFormat())
                    .dateHistogramInterval(groupInterval(dataField.getGroupInterval().value()))
                    .order(BucketOrder.key(false));
          } else {
            aggregationBuilder =
                AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .size(querySize);
          }
        } else {
          aggregationBuilder =
              AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                  .field(dataField.getColumnName())
                  .size(querySize);
        }
        for (Field dataField1 : aggregateFields) {
          aggregationBuilder.subAggregation(
              QueryBuilderUtil.aggregationBuilderDataField(dataField1));
            SortOrder sortOrder;
            Field.LimitType limitType = dataField1.getLimitType();
            if(limitType!=null) {
                // Default Order will be descending order.
                sortOrder = SortOrder.DESC;
                if (dataField1.getLimitType() == Field.LimitType.BOTTOM)
                    sortOrder = SortOrder.ASC;
                Integer size = new Integer(BuilderUtil.SIZE);
                if (dataField1.getLimitValue() != null && dataField1.getLimitValue() > 0)
                    size = dataField1.getLimitValue();
                aggregationBuilder.subAggregation(bucketSort("bucketSort", Arrays.asList(
                    new FieldSortBuilder(dataField1.getColumnName()).order(sortOrder))).size(size));
            }
        }
        return reportAggregationBuilder(
            dataFields, aggregateFields, fieldCount, aggregatedFieldCount, aggregationBuilder);

      } else {

        AggregationBuilder aggregationBuilderMain = null;
        if (dataField.getType().name().equals(Field.Type.DATE.name())
            || dataField.getType().name().equals(Field.Type.TIMESTAMP.name())) {
          if (dataField.getDateFormat() == null || dataField.getDateFormat().isEmpty())
            dataField.setDateFormat(DATE_FORMAT);
          if (dataField.getGroupInterval() != null) {
            aggregationBuilderMain =
                AggregationBuilders.dateHistogram(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .format(dataField.getDateFormat())
                    .dateHistogramInterval(groupInterval(dataField.getGroupInterval().value()))
                    .order(BucketOrder.key(false))
                    .subAggregation(aggregationBuilder);
          } else {
            aggregationBuilderMain =
                AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .subAggregation(aggregationBuilder)
                    .size(querySize);
          }
        } else {
          aggregationBuilderMain =
              AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                  .field(dataField.getColumnName())
                  .subAggregation(aggregationBuilder)
                  .size(querySize);
        }

        return reportAggregationBuilder(
            dataFields, aggregateFields, fieldCount, aggregatedFieldCount, aggregationBuilderMain);
      }
    } else {
      return aggregationBuilder;
    }
  }

  /**
   * Aggregation builder
   *
   * @param dataFields
   * @param aggregateFields
   * @param searchSourceBuilder
   */
  public void aggregationBuilder(
      List<Field> dataFields,
      List<Field> aggregateFields,
      SearchSourceBuilder searchSourceBuilder) {
    // if only aggregation fields are there.

    if (aggregateFields.size() == dataFields.size()) {
      for (Field dataField1 : aggregateFields) {
        searchSourceBuilder.aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataField1));
      }
    }
  }
}
