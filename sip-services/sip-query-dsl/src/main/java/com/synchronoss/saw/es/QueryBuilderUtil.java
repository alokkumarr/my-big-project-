package com.synchronoss.saw.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.bda.sip.dsk.BooleanCriteria;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.saw.exceptions.SipDslRuntimeException;
import com.synchronoss.saw.model.Aggregate;

import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.GroupInterval;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Model.Operation;
import com.synchronoss.saw.model.Model.Operator;

import com.synchronoss.saw.util.BuilderUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class QueryBuilderUtil {

  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String SPACE_REGX = "\\s+";
  public static final String EMPTY_STRING = "";
  private static final String HITS = "hits";
  private static final String SOURCE = "_source";
  private static final String MONTH = "month";
  private static final String KEYWORD = ".keyword";

  public static final Map<String, String> dateFormats;

  private QueryBuilderUtil() {}

    static {
    Map<String, String> formats = new HashMap<>();
    formats.put("YYYY", "year");
    formats.put("MMMYYYY", MONTH);
    formats.put("MMYYYY", MONTH);
    formats.put("MMMdYYYY", "day");
    formats.put("MMMMdYYYY,h:mm:ssa", "hour");
    dateFormats = Collections.unmodifiableMap(formats);
  }

  /**
   * @param field
   * @param aggregationName
   * @return
   */
  public static AggregationBuilder aggregationBuilder(Field field, String aggregationName) {

    AggregationBuilder aggregationBuilder = null;

    if (field.getType().name().equals(Field.Type.DATE.name())
        || field.getType().name().equals(Field.Type.TIMESTAMP.name())) {
      if (field.getDateFormat() == null || field.getDateFormat().isEmpty())
        field.setDateFormat(DATE_FORMAT);
      if (field.getGroupInterval() != null) {
        aggregationBuilder =
            AggregationBuilders.dateHistogram(aggregationName)
                .field(field.getColumnName())
                .format(field.getDateFormat())
                .dateHistogramInterval(groupInterval(field.getGroupInterval()))
                .order(BucketOrder.key(false));
      } else {
        aggregationBuilder =
            AggregationBuilders.terms(aggregationName)
                .field(field.getColumnName())
                .format(field.getDateFormat())
                .order(BucketOrder.key(false))
                .size(BuilderUtil.SIZE);
      }
    } else {
      aggregationBuilder =
          AggregationBuilders.terms(aggregationName)
              .field(field.getColumnName())
              .size(BuilderUtil.SIZE);
    }

    return aggregationBuilder;
  }

  /**
   * Group interval for the DateHistogram.
   *
   * @param groupInterval
   * @return
   */
  public static DateHistogramInterval groupInterval(GroupInterval groupInterval) {
    DateHistogramInterval histogramInterval = null;
    switch (groupInterval) {
      case MONTH:
        histogramInterval = DateHistogramInterval.MONTH;
        break;
      case DAY:
      case ALL:
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
      default:
        throw new SipDslRuntimeException(
            String.format("groupInterval %s is not yet supported", groupInterval));
    }
    return histogramInterval;
  }

  /**
   * Aggregation builder for data fields.
   *
   * @param field
   * @return
   */
  public static AggregationBuilder aggregationBuilderDataField(Field field) {
    AggregationBuilder aggregationBuilder = null;
    String dataField = field.getDataField() == null ? field.getColumnName() : field.getDataField();

    switch (field.getAggregate()) {
      case SUM:
        aggregationBuilder = AggregationBuilders.sum(dataField).field(field.getColumnName());
        break;
      case AVG:
        aggregationBuilder = AggregationBuilders.avg(dataField).field(field.getColumnName());
        break;
      case MIN:
        aggregationBuilder = AggregationBuilders.min(dataField).field(field.getColumnName());
        break;
      case MAX:
        aggregationBuilder = AggregationBuilders.max(dataField).field(field.getColumnName());
        break;
      case COUNT:
        aggregationBuilder = AggregationBuilders.count(dataField).field(field.getColumnName());
        break;
      case DISTINCTCOUNT:
        aggregationBuilder =
            AggregationBuilders.cardinality(dataField).field(field.getColumnName());
        break;
      case PERCENTAGE:
        String columnName =
            field.getDataField() == null ? field.getColumnName() : field.getDataField();
        Script script =
            new Script(
                "_value*100/"
                    + field.getAdditionalProperties().get(columnName + "_sum"));
        aggregationBuilder =
            AggregationBuilders.sum(dataField).field(field.getColumnName()).script(script);
        break;
      case PERCENTAGE_BY_ROW:
        aggregationBuilder = AggregationBuilders.sum(dataField).field(field.getColumnName());
        break;
    }
    return aggregationBuilder;
  }

  /**
   * Set Group Interval.
   *
   * @param field
   * @return
   */
  public static Field setGroupInterval(Field field) {
    String interval = dateFormats.get(field.getDateFormat().replaceAll(SPACE_REGX, EMPTY_STRING));
    switch (interval) {
      case "month":
        field.setGroupInterval(Field.GroupInterval.MONTH);
        break;
      case "year":
        field.setGroupInterval(Field.GroupInterval.YEAR);
        break;
      case "day":
        field.setGroupInterval(Field.GroupInterval.DAY);
        break;
      case "hour":
        field.setGroupInterval(Field.GroupInterval.HOUR);
        break;
      default:
        throw new IllegalArgumentException(interval + " not present");
    }
    return field;
  }

  /**
   * Build numeric filter to handle different preset values.
   *
   * @param item
   * @return
   */
  public static QueryBuilder buildNumericFilter(Filter item) {
    Operator operator = item.getModel().getOperator();
    switch (operator) {
      case BTW:
        {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.lte(item.getModel().getValue());
          rangeQueryBuilder.gte(item.getModel().getOtherValue());
          return rangeQueryBuilder;
        }
      case GT:
        {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.gt(item.getModel().getValue());
          return rangeQueryBuilder;
        }
      case GTE:
        {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.gte(item.getModel().getValue());
          return rangeQueryBuilder;
        }
      case LT:
        {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.lt(item.getModel().getValue());
          return rangeQueryBuilder;
        }
      case LTE:
        {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.lte(item.getModel().getValue());
          return rangeQueryBuilder;
        }
      case EQ:
        {
          return new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
        }
      case NEQ:
        {
          BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
          boolQueryBuilderIn.mustNot(
              new TermQueryBuilder(item.getColumnName(), item.getModel().getValue()));
          return boolQueryBuilderIn;
        }
      default:
        throw new SipDslRuntimeException(
            String.format("Operator %s is not yet supported for numeric filter", operator));
    }
  }

  /**
   * Build Aggregation filter to handle different preset values.
   *
   * @param item
   * @return
   */
  public static String prepareAggregationFilter(Filter item, String aggregatedColumnName) {
    Operator operator = item.getModel().getOperator();
    String scriptQuery = null;
    switch (operator) {
      case BTW:
        scriptQuery =
            String.format(
                " (params.%s %s %s && params.%s %s %s) ",
                aggregatedColumnName,
                Operation.LTE,
                item.getModel().getValue(),
                aggregatedColumnName,
                Operation.GTE,
                item.getModel().getOtherValue());
        break;
      case GT:
        scriptQuery =
            String.format(
                " params.%s %s %s ",
                aggregatedColumnName, Operation.GT, item.getModel().getValue());

        break;
      case GTE:
        scriptQuery =
            String.format(
                " params.%s %s %s ",
                aggregatedColumnName, Operation.GTE, item.getModel().getValue());
        break;
      case LT:
        scriptQuery =
            String.format(
                " params.%s %s %s ",
                aggregatedColumnName, Operation.LT, item.getModel().getValue());

        break;
      case LTE:
        scriptQuery =
            String.format(
                " params.%s %s %s ",
                aggregatedColumnName, Operation.LTE, item.getModel().getValue());

        break;
      case EQ:
        scriptQuery =
            String.format(
                " params.%s %s %s ",
                aggregatedColumnName, Operation.EQ, item.getModel().getValue());

        break;
      case NEQ:
        scriptQuery =
            String.format(
                " params.%s %s %s ",
                aggregatedColumnName, Operation.NEQ, item.getModel().getValue());

        break;
      default:
        throw new SipDslRuntimeException(
            String.format("Operator %s is not  supported for Aggregated filter", operator));
    }
    return scriptQuery;
  }

  /**
   * Build String filter to handle case insensitive filter.
   *
   * @param item
   * @return
   */
  public static QueryBuilder stringFilter(Filter item) {
    Operator operator = item.getModel().getOperator();
    switch (operator) {
        /* For equal and IsIn we are build query in same way, So for EQ and ISIN call go to case
        ISIN*/
      case EQ:
      case ISIN:
        {
          TermsQueryBuilder termsQueryBuilder =
              new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
          List<?> modelValues = buildStringTermsfilter(item.getModel().getModelValues());
          TermsQueryBuilder termsQueryBuilder1 =
              new TermsQueryBuilder(buildFilterColumn(item.getColumnName()), modelValues);
          BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
          boolQueryBuilder.should(termsQueryBuilder);
          boolQueryBuilder.should(termsQueryBuilder1);
          return boolQueryBuilder;
        }
        /*For Notequal and IsNotIn we are build query in same way, So for NEQ and ISNOTIN call go
        to ISNOTIN case. */
      case NEQ:
      case ISNOTIN:
        {
          List<?> modelValues = buildStringTermsfilter(item.getModel().getModelValues());
          QueryBuilder qeuryBuilder =
              new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
          BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
          boolQueryBuilder.mustNot(qeuryBuilder);
          QueryBuilder qeuryBuilder1 =
              new TermsQueryBuilder(buildFilterColumn(item.getColumnName()), modelValues);
          boolQueryBuilder.mustNot(qeuryBuilder1);
          return boolQueryBuilder;
        }

        // prefix query builder - not analyzed
      case SW:
        {
          PrefixQueryBuilder pqb =
              new PrefixQueryBuilder(
                  item.getColumnName(), (String) item.getModel().getModelValues().get(0));
          PrefixQueryBuilder pqb1 =
              new PrefixQueryBuilder(
                  buildFilterColumn(item.getColumnName()),
                  (String) ((String) item.getModel().getModelValues().get(0)).toLowerCase());
          BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
          boolQueryBuilder.should(pqb);
          boolQueryBuilder.should(pqb1);
          return boolQueryBuilder;
        }

        // using wildcard as there's no suffix query type provided by
        // elasticsearch
      case EW:
        {
          WildcardQueryBuilder wqb =
              new WildcardQueryBuilder(
                  item.getColumnName(), "*" + item.getModel().getModelValues().get(0));
          WildcardQueryBuilder wqb1 =
              new WildcardQueryBuilder(
                  buildFilterColumn(item.getColumnName()),
                  "*" + (String) ((String) item.getModel().getModelValues().get(0)).toLowerCase());
          BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
          boolQueryBuilder.should(wqb);
          boolQueryBuilder.should(wqb1);
          return boolQueryBuilder;
        }

        // same for contains clause - not analyzed query
      case CONTAINS:
        {
          WildcardQueryBuilder wqb =
              new WildcardQueryBuilder(
                  item.getColumnName(), "*" + item.getModel().getModelValues().get(0) + "*");
          WildcardQueryBuilder wqb1 =
              new WildcardQueryBuilder(
                  buildFilterColumn(item.getColumnName()),
                  "*"
                      + (String) ((String) item.getModel().getModelValues().get(0)).toLowerCase()
                      + "*");
          BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
          boolQueryBuilder.should(wqb);
          boolQueryBuilder.should(wqb1);
          return boolQueryBuilder;
        }
      default:
        throw new SipDslRuntimeException(
            String.format("Operator %s is not yet supported for string filter", operator));
    }
  }
  /**
   * Build the terms values to support case insensitive search options.
   *
   * @param modelValues
   */
  private static List<?> buildStringTermsfilter(List<?> modelValues) {
    List<Object> stringValues = new ArrayList<>();
    modelValues.forEach(
        val -> {
          // Add the lowercase value as terms to lookup based on custom analyser.
          if (val instanceof String) {
            stringValues.add(((String) val).trim().toLowerCase().trim());
          }
        });
    return stringValues;
  }

  /**
   * Build the search column to support case insensitive search options.
   *
   * @param columnName
   */
  private static String buildFilterColumn(String columnName) {
    if (columnName.contains(KEYWORD)) {
      return columnName.replace(KEYWORD, ".filter");
    } else {
      return columnName + ".filter";
    }
  }

  /**
   * To get the aggregation builder for data fields.
   *
   * @param dataFields
   * @param preSearchSourceBuilder
   * @return
   */
  public static void getAggregationBuilder(
      List<?> dataFields, SearchSourceBuilder preSearchSourceBuilder) {

    for (Object dataField : dataFields) {
      if (dataField instanceof com.synchronoss.saw.model.Field) {
        Field field = (Field) dataField;
        if (field.getAggregate() == Aggregate.PERCENTAGE) {
          String aggDataField =
              field.getDataField() == null ? field.getColumnName() : field.getDataField();
          preSearchSourceBuilder.aggregation(
              AggregationBuilders.sum(aggDataField).field(field.getColumnName()));
        }
      }
    }
  }

  /**
   * query builder for DSK node. TODO: Original DSK was supporting only string format, So query
   * builder is in place only for String.
   *
   * @param attribute
   * @return
   */
  public static BoolQueryBuilder queryDSKBuilder(SipDskAttribute attribute) {
    BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    BooleanCriteria booleanCriteria = null;
    if (attribute == null) {
      return boolQuery;
    }
    if (attribute.getBooleanCriteria() == null && attribute.getBooleanQuery() == null) {
      return boolQuery;
    }
    if (attribute.getBooleanCriteria() != null) {
      booleanCriteria = attribute.getBooleanCriteria();
    }
    for (SipDskAttribute dskAttribute : attribute.getBooleanQuery()) {
      if (dskAttribute.getBooleanQuery() != null) {
        BoolQueryBuilder childQuery = queryDSKBuilder(dskAttribute);
        if (childQuery != null) {
          switch (booleanCriteria) {
            case AND:
              boolQuery.must(childQuery);
              break;
            case OR:
              boolQuery.should(childQuery);
              break;
          }
        }
      } else {
        BoolQueryBuilder dataSecurityBuilder = buildFilterObject(dskAttribute);
        switch (booleanCriteria) {
          case AND:
            boolQuery.must(dataSecurityBuilder);
            break;
          case OR:
            boolQuery.should(dataSecurityBuilder);
            break;
        }
      }
    }
    return boolQuery;
  }

  private static BoolQueryBuilder buildFilterObject(SipDskAttribute dskAttribute) {
    String[] col = dskAttribute.getColumnName().trim().split("\\.");
    String dskColName = col.length == 1 ? col[0] : col[1];
    List<String> values = dskAttribute.getModel().getValues().stream().map(String::trim)
        .collect(Collectors.toList());
    TermsQueryBuilder termsQueryBuilder =
        new TermsQueryBuilder(
            dskColName.trim().concat(BuilderUtil.SUFFIX),values);
    List<?> modelValues =
        QueryBuilderUtil.buildStringTermsfilter(dskAttribute.getModel().getValues());
    TermsQueryBuilder termsQueryBuilder1 =
        new TermsQueryBuilder(QueryBuilderUtil.buildFilterColumn(dskColName.trim()), modelValues);
    BoolQueryBuilder dataSecurityBuilder = new BoolQueryBuilder();
    dataSecurityBuilder.should(termsQueryBuilder);
    dataSecurityBuilder.should(termsQueryBuilder1);
    return dataSecurityBuilder;
  }

  /**
   * To build the report data ( Without elasticsearch aggregation).
   *
   * @param jsonNode
   * @return
   */
  public static List<Object> buildReportData(JsonNode jsonNode, List<Field> dataFields) {
    Iterator<JsonNode> recordIterator = jsonNode.get(HITS).get(HITS).iterator();
    List<Object> data = new ArrayList<>();
    while (recordIterator.hasNext()) {
      JsonNode source = recordIterator.next();
      ObjectNode row = source.get(SOURCE).deepCopy();
      // Add the missing columns in response for reports.
      dataFields.forEach(
          field -> {
            // Remove the .keyword if its string fields.
            String fieldName = field.getColumnName().replace(KEYWORD, "");
            if (!row.has(fieldName)) row.put(fieldName, "");
          });
      data.add(row);
    }
    return data;
  }
}
