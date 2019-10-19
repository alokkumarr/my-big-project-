package com.synchronoss.saw.es;

import static org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders.bucketSort;

import com.google.gson.Gson;
import com.synchronoss.saw.exceptions.SipDslProcessingException;
import com.synchronoss.saw.model.Expression;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Field.GroupInterval;
import com.synchronoss.saw.model.Operand;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.model.Sort.Order;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.util.BuilderUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.bucketscript.BucketScriptPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketselector.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class SIPAggregationBuilder {

  private Random random;
  private Integer querySize;
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String GROUP_BY_FIELD = "group_by_field";

  int groupFieldCount = 0;

  public SIPAggregationBuilder(Integer querySize) {
    this.querySize = querySize;
    random = new Random();
  }

  public static List<Field> getAggregationField(List<Field> dataFields) {
    return dataFields.stream().filter(
        dataField -> dataField.getAggregate() != null || dataField.getFormula() != null)
        .collect(Collectors.toList());
  }

  public static DateHistogramInterval groupInterval(GroupInterval groupInterval) {
    DateHistogramInterval histogramInterval = null;
    switch (groupInterval) {
      case ALL:
        // For group interval ALL, no need to set any value. Refer line: 87.
        break;
      case MONTH:
        histogramInterval = DateHistogramInterval.MONTH;
        break;
      case DAY:
        histogramInterval = DateHistogramInterval.DAY;
        break;
      case YEAR:
        histogramInterval = DateHistogramInterval.YEAR;
        break;
      case QUARTER:
        histogramInterval = DateHistogramInterval.QUARTER;
        break;
      case HOUR:
        histogramInterval = DateHistogramInterval.HOUR;
        break;
      case WEEK:
        histogramInterval = DateHistogramInterval.WEEK;
        break;
      case MINUTE:
        histogramInterval = DateHistogramInterval.MINUTE;
        break;
      case SECOND:
        histogramInterval = DateHistogramInterval.SECOND;
        break;
    }
    return histogramInterval;
  }

  public AggregationBuilder reportAggregationBuilder(
      List<Field> dataFields,
      List<Field> aggregateFields,
      List<Filter> aggregationFilter,
      int fieldCount,
      int aggregatedFieldCount,
      AggregationBuilder aggregationBuilder,
      List<Sort> sorts,
      String[] groupByFields) {

    /** For Report find the list of Aggregate fields. */
    if ((fieldCount + aggregateFields.size()) < dataFields.size()) {
      Field dataField = dataFields.get(fieldCount + aggregatedFieldCount);
      if (dataField.getAggregate() != null || dataField.getFormula() != null) {
        aggregatedFieldCount++;
        return reportAggregationBuilder(
            dataFields,
            aggregateFields,
            aggregationFilter,
            fieldCount,
            aggregatedFieldCount,
            aggregationBuilder,
            sorts,
            groupByFields);
      }
      if (aggregationBuilder == null) {
        // initialize the terms aggregation builder.
        if (dataField.getType().name().equals(Field.Type.DATE.name())
            || dataField.getType().name().equals(Field.Type.TIMESTAMP.name())) {
          boolean order = checkSortOrder(sorts, dataField.getColumnName()) ? false : true;
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
                    .dateHistogramInterval(groupInterval(dataField.getGroupInterval()))
                    .order(BucketOrder.key(order));
            if (dataField.getAggregate() == null && dataField.getFormula() == null)
              groupByFields[groupFieldCount++] = dataField.getColumnName();
          } else {
            aggregationBuilder =
                AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .format(dataField.getDateFormat())
                    .field(dataField.getColumnName())
                    .order(BucketOrder.key(order))
                    .size(querySize);
            if (dataField.getAggregate() == null && dataField.getFormula() == null)
              groupByFields[groupFieldCount++] = dataField.getColumnName();
          }
        } else {
          boolean order = checkSortOrder(sorts, dataField.getColumnName()) ? false : true;
          aggregationBuilder =
              AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                  .field(dataField.getColumnName())
                  .order(BucketOrder.key(order))
                  .size(querySize);
          if (dataField.getAggregate() == null && dataField.getFormula() == null)
            groupByFields[groupFieldCount++] = dataField.getColumnName();
        }
        for (Field dataField1 : aggregateFields) {
          if (dataField1.getExpression() != null) {

            /**
             * Evaluate every operand in the expression as a separate datafield, store them in a
             * custom field and then evaluate the entire expression and store it in the user
             * provided field.
             */
            String expressionStr = dataField1.getExpression();
            Expression expression = null;
            Gson gson = new Gson();
            if (expressionStr != null && expressionStr.length() != 0) {
              expression = gson.fromJson(expressionStr, Expression.class);
              String dataFieldName = dataField1.getDataField();
              expressionEvaluator(dataFieldName, expression, aggregationBuilder);
            }
          } else {
            aggregationBuilder.subAggregation(
                QueryBuilderUtil.aggregationBuilderDataField(dataField1));
          }
          SortOrder sortOrder;

          Integer size = new Integer(BuilderUtil.SIZE);
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
        for (Filter filter : aggregationFilter) {
          Map<String, String> bucketsPathsMap = new HashMap<>();
          bucketsPathsMap.put(filter.getColumnName(), filter.getColumnName() + ".value");
          Script script = QueryBuilderUtil.prepareAggregationFilter(filter);
          BucketSelectorPipelineAggregationBuilder bs =
              PipelineAggregatorBuilders.bucketSelector("bucket_filter", bucketsPathsMap, script);
          aggregationBuilder.subAggregation(bs);
        }
        return reportAggregationBuilder(
            dataFields,
            aggregateFields,
            aggregationFilter,
            fieldCount,
            aggregatedFieldCount,
            aggregationBuilder,
            sorts,
            groupByFields);

      } else {
        boolean order = checkSortOrder(sorts, dataField.getColumnName()) ? false : true;
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
                    .dateHistogramInterval(groupInterval(dataField.getGroupInterval()))
                    .order(BucketOrder.key(order))
                    .subAggregation(aggregationBuilder);
            if (dataField.getAggregate() == null && dataField.getFormula() == null)
              groupByFields[groupFieldCount++] = dataField.getColumnName();

          } else {
            aggregationBuilderMain =
                AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                    .field(dataField.getColumnName())
                    .format(dataField.getDateFormat())
                    .subAggregation(aggregationBuilder)
                    .order(BucketOrder.key(order))
                    .size(querySize);
            if (dataField.getAggregate() == null && dataField.getFormula() == null)
              groupByFields[groupFieldCount++] = dataField.getColumnName();
          }
        } else {
          aggregationBuilderMain =
              AggregationBuilders.terms(GROUP_BY_FIELD + "_" + ++fieldCount)
                  .field(dataField.getColumnName())
                  .subAggregation(aggregationBuilder)
                  .order(BucketOrder.key(order))
                  .size(querySize);
          if (dataField.getAggregate() == null && dataField.getFormula() == null)
            groupByFields[groupFieldCount++] = dataField.getColumnName();
        }
        return reportAggregationBuilder(
            dataFields,
            aggregateFields,
            aggregationFilter,
            fieldCount,
            aggregatedFieldCount,
            aggregationBuilderMain,
            sorts, groupByFields);
      }
    } else {
      return aggregationBuilder;
    }
  }

  /**
   * Collect the valid filter from the filter lists
   *
   * @param filters
   * @return
   */
  public static List<Filter> getAggregationFilter(List<Filter> filters) {
    return filters.stream().filter(filter -> filter.getAggregationFilter() != null
        && filter.getAggregationFilter()).collect(Collectors.toList());
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
      for (Field field : aggregateFields) {
        searchSourceBuilder.aggregation(QueryBuilderUtil.aggregationBuilderDataField(field));
      }
    }
  }

  /**
   * Check the desc sort order while building the ES query
   *
   * @param sorts
   * @param columnName
   * @return true if order matched else return false
   */
  public Boolean checkSortOrder(List<Sort> sorts, String columnName) {
    return sorts.stream().anyMatch(s -> s.getColumnName().equalsIgnoreCase(columnName)
        && s.getOrder().equals(Order.DESC));
  }

  private String expressionEvaluator(
      String dataFieldName, Expression expression, AggregationBuilder aggregationBuilder)
      throws SipDslProcessingException {
    StringBuilder expressionBuilder = new StringBuilder();
    Map<String, String> bucketsPathsMap = new HashMap<>();

    if (expression != null) {
      // if expression has operator and operand(s)
      String operator = expression.getOperator();
      if (operator != null) {
        Operand operand1 = expression.getOperand1();
        String operand1Exp = operandEvaluator(operand1, aggregationBuilder, bucketsPathsMap);
        expressionBuilder.append(operand1Exp);
        if (expression.getOperand2() != null) {
          Operand operand2 = expression.getOperand2();
          String operand2Exp = operandEvaluator(operand2, aggregationBuilder, bucketsPathsMap);
          expressionBuilder.append(expression.getOperator());
          expressionBuilder.append(operand2Exp);
        }
      } else {
        Operand operand = new Operand();
        if (expression.getValue() != null) {
          operand.setValue(expression.getValue());
        } else if (expression.getAggregate() != null && expression.getColumn() != null) {
          operand.setColumn(expression.getColumn());
          operand.setAggregate(expression.getAggregate());
        } else {
          throw new SipDslProcessingException("Invalid expression");
        }
        String operandExp = operandEvaluator(operand, aggregationBuilder, bucketsPathsMap);
        expressionBuilder.append(operandExp);
      }
    }

    Script script1 = new Script(expressionBuilder.toString());
    BucketScriptPipelineAggregationBuilder bs =
        PipelineAggregatorBuilders.bucketScript(dataFieldName, bucketsPathsMap, script1);
    bs.gapPolicy(GapPolicy.INSERT_ZEROS);
    aggregationBuilder.subAggregation(bs);

    return expressionBuilder.toString();
  }

  private String operandEvaluator(
      Operand operand, AggregationBuilder aggregationBuilder, Map bucketPathsMap) {
    StringBuilder operandBuilder = new StringBuilder();
    if (operand.getAggregate() != null) {
      Field aggField = new Field();
      aggField.setColumnName(operand.getColumn());
      aggField.setAggregate(Aggregate.fromValue(operand.getAggregate().toUpperCase()));
      String fieldName = operand.getAggregate() + "_" + operand.getColumn() + "_formula_" + random.nextInt(10000);
      aggField.setDataField(fieldName);
      aggregationBuilder.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(aggField));
      bucketPathsMap.put(fieldName, fieldName + ".value");
      operandBuilder.append("params.").append(fieldName);
    } else if (operand.getOperand1() != null && operand.getOperator() != null) {
      operandBuilder.append("(");
      String operator = operand.getOperator();
      Operand operand1 = operand.getOperand1();
      String operand1Exp = operandEvaluator(operand1, aggregationBuilder, bucketPathsMap);
      operandBuilder.append(operand1Exp);
      Operand operand2 = operand.getOperand2();

      if (operand2 != null) {
        operandBuilder.append(operator);
        String operand2Exp = operandEvaluator(operand2, aggregationBuilder, bucketPathsMap);
        operandBuilder.append(operand2Exp);
      }
      operandBuilder.append(")");
    } else if (operand.getValue() != null) {
      operandBuilder.append(operand.getValue());
    }
    return operandBuilder.toString();
  }
}
