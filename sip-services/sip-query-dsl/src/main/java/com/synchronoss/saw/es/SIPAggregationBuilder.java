package com.synchronoss.saw.es;

import static org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders.bucketSort;

import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.GroupInterval;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.model.Sort.Order;
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
      case "all":
        // For groupinterval ALL, no need to set any value. Refer line: 87.
        break;
      case "month":
        histogramInterval = DateHistogramInterval.MONTH;
        break;
      case "day":
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
      AggregationBuilder aggregationBuilder,
      List<Sort> sorts) {
    /** For Report find the list of Aggregate fields. */
    if ((fieldCount + aggregateFields.size()) < dataFields.size()) {
      Field dataField = dataFields.get(fieldCount + aggregatedFieldCount);
      if (dataField.getAggregate() != null) {
        aggregatedFieldCount++;
        return reportAggregationBuilder(
            dataFields,
            aggregateFields,
            fieldCount,
            aggregatedFieldCount,
            aggregationBuilder,
            sorts);
      }
      if (aggregationBuilder == null) {
        // initialize the terms aggregation builder.
        if (dataField.getType().name().equals(Field.Type.DATE.name())
            || dataField.getType().name().equals(Field.Type.TIMESTAMP.name())) {
          boolean order = false;
          if (isSortColumnPresent(sorts, dataField.getColumnName())) {
            order = getSortOrder(sorts, dataField.getColumnName());
          }
          if (dataField.getDateFormat() == null || dataField.getDateFormat().isEmpty())
            dataField.setDateFormat(DATE_FORMAT);
          if (dataField.getGroupInterval() != null
              && !dataField
                  .getGroupInterval()
                  .value()
                  .equalsIgnoreCase(GroupInterval.ALL.value())) {
            if (dataField.getMinDocCount() == null) {
              dataField.setMinDocCount(1);
            }
            aggregationBuilder =
                AggregationBuilders.dateHistogram(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .format(dataField.getDateFormat())
                    .minDocCount(dataField.getMinDocCount())
                    .dateHistogramInterval(groupInterval(dataField.getGroupInterval().value()))
                    .order(BucketOrder.key(order));
          } else {
            aggregationBuilder =
                AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .format(dataField.getDateFormat())
                    .field(dataField.getColumnName())
                    .order(BucketOrder.key(order))
                    .size(querySize);
          }
        } else {
          boolean order = false;
          if (isSortColumnPresent(sorts, dataField.getColumnName())) {
            order = getSortOrder(sorts, dataField.getColumnName());
          }
          aggregationBuilder =
              AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                  .field(dataField.getColumnName())
                  .order(BucketOrder.key(order))
                  .size(querySize);
        }
        for (Field dataField1 : aggregateFields) {
          aggregationBuilder.subAggregation(
              QueryBuilderUtil.aggregationBuilderDataField(dataField1));
          SortOrder sortOrder;
          Boolean isSortReq = isSortColumnPresent(sorts, dataField1.getColumnName());
          Integer size = new Integer(BuilderUtil.SIZE);
          if (isSortReq) {
            Boolean sortField = getSortOrder(sorts, dataField1.getColumnName());
            sortOrder = sortField == true ? SortOrder.ASC : SortOrder.DESC;
            aggregationBuilder.subAggregation(
                bucketSort(
                        "bucketSort",
                        Arrays.asList(
                            new FieldSortBuilder(dataField1.getColumnName()).order(sortOrder)))
                    .size(size));
          }

          Field.LimitType limitType = dataField1.getLimitType();
          if (limitType != null) {
            // Default Order will be descending order.
            sortOrder = SortOrder.DESC;
            if (dataField1.getLimitType() == Field.LimitType.BOTTOM) sortOrder = SortOrder.ASC;
            if (dataField1.getLimitValue() != null && dataField1.getLimitValue() > 0)
              size = dataField1.getLimitValue();
            aggregationBuilder.subAggregation(
                bucketSort(
                        "bucketSort",
                        Arrays.asList(
                            new FieldSortBuilder(dataField1.getColumnName()).order(sortOrder)))
                    .size(size));
          }
        }
        return reportAggregationBuilder(
            dataFields,
            aggregateFields,
            fieldCount,
            aggregatedFieldCount,
            aggregationBuilder,
            sorts);

      } else {
        boolean order = false;
        if (isSortColumnPresent(sorts, dataField.getColumnName())) {
          order = getSortOrder(sorts, dataField.getColumnName());
        }

        AggregationBuilder aggregationBuilderMain = null;
        if (dataField.getType().name().equals(Field.Type.DATE.name())
            || dataField.getType().name().equals(Field.Type.TIMESTAMP.name())) {
          if (dataField.getDateFormat() == null || dataField.getDateFormat().isEmpty())
            dataField.setDateFormat(DATE_FORMAT);
          if (dataField.getGroupInterval() != null
              && !dataField
                  .getGroupInterval()
                  .value()
                  .equalsIgnoreCase(GroupInterval.ALL.value())) {
            if (dataField.getMinDocCount() == null) {
              dataField.setMinDocCount(1);
            }
            aggregationBuilderMain =
                AggregationBuilders.dateHistogram(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .format(dataField.getDateFormat())
                    .minDocCount(dataField.getMinDocCount())
                    .dateHistogramInterval(groupInterval(dataField.getGroupInterval().value()))
                    .order(BucketOrder.key(order))
                    .subAggregation(aggregationBuilder);
          } else {
            aggregationBuilderMain =
                AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .format(dataField.getDateFormat())
                    .subAggregation(aggregationBuilder)
                    .order(BucketOrder.key(order))
                    .size(querySize);
          }
        } else {
          aggregationBuilderMain =
              AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                  .field(dataField.getColumnName())
                  .subAggregation(aggregationBuilder)
                  .order(BucketOrder.key(order))
                  .size(querySize);
        }

        return reportAggregationBuilder(
            dataFields,
            aggregateFields,
            fieldCount,
            aggregatedFieldCount,
            aggregationBuilderMain,
            sorts);
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

  public Boolean getSortOrder(List<Sort> sorts, String column) {
    Boolean sortField = null;
    for (Sort s : sorts) {
      if (s.getColumnName().equalsIgnoreCase(column)) {
        sortField = s.getOrder() == Order.ASC;
      }
    }
    return sortField;
  }

  public Boolean isSortColumnPresent(List<Sort> sorts, String columnName) {
    Boolean isSortReq = false;
    for (Sort sort : sorts) {
      isSortReq = sort.getColumnName().equalsIgnoreCase(columnName);
      if (isSortReq == true) {
        return true;
      }
    }
    return isSortReq;
  }
}
