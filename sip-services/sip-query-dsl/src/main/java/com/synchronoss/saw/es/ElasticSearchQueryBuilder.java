package com.synchronoss.saw.es;

import static com.synchronoss.saw.es.QueryBuilderUtil.queryDSKBuilder;
import static com.synchronoss.saw.util.BuilderUtil.buildNestedFilter;

import com.fasterxml.jackson.databind.JsonNode;

import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.saw.exceptions.SipDslRuntimeException;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Filter.Type;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.SipQuery.BooleanCriteria;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class ElasticSearchQueryBuilder {

  public static final Logger logger = LoggerFactory.getLogger(ElasticSearchQueryBuilder.class);
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";
  private static final String EPOCH_TO_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String ONLY_YEAR_FORMAT = "YYYY";
  private static final String EPOCH_SECOND = "epoch_second";
  private static final String EPOCH_MILLIS = "epoch_millis";
  private static final String VALUE = "value";
  private static final String SUM = "_sum";
  private static String appenderForGTLTE = "||/M";
  private String[] groupByFields;

  public String[] getGroupByFields() {
    return groupByFields;
  }

  public void setGroupByFields(String[] groupByFields) {
    this.groupByFields = groupByFields;
  }

  public String buildDataQuery(SipQuery sipQuery, Integer size, SipDskAttribute dskAttribute) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.from(0);
    /*
    ToDo: when size is -1 need to remove the hard coded size as 1lakh and we need to send the total
      data  when the size is -1.
     */
    if (size == null || size == -1) size = 100000;
    searchSourceBuilder.size(size);
    if (sipQuery.getSorts() == null && sipQuery.getFilters() == null) {
      throw new NullPointerException(
          "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
    }
    // The below call is to build sort
    searchSourceBuilder = buildSortQuery(sipQuery, searchSourceBuilder);
    BoolQueryBuilder boolQueryBuilderDsk;
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    if (dskAttribute != null && dskAttribute.getBooleanCriteria() != null && !CollectionUtils
        .isEmpty(dskAttribute.getBooleanQuery())) {
      boolQueryBuilderDsk = queryDSKBuilder(dskAttribute);
      boolQueryBuilder.must(boolQueryBuilderDsk);
    }
    // The below code to build filters

    if (!CollectionUtils.isEmpty(sipQuery.getFilters())) {
      List<Filter> filters = sipQuery.getFilters();
      BoolQueryBuilder newBooleanQuery =
          buildFilterQuery(buildNestedFilter(filters, sipQuery.getBooleanCriteria()));
      boolQueryBuilder.must(newBooleanQuery);
    }
    searchSourceBuilder.query(boolQueryBuilder);

    List<Field> dataFields = sipQuery.getArtifacts().get(0).getFields();
    // rearrange data field based upon sort
    boolean haveAggregate = dataFields.stream().anyMatch(field -> field.getAggregate() != null
        && !field.getAggregate().value().isEmpty());
    if (haveAggregate) {
      dataFields = BuilderUtil.buildFieldBySort(dataFields, sipQuery.getSorts());
    }

    List<Field> aggregationFields = SIPAggregationBuilder.getAggregationField(dataFields);
    List<Filter> aggregationFilter =
        SIPAggregationBuilder.getAggregationFilter(sipQuery.getFilters());

    // Generated Query
    searchSourceBuilder =
        buildAggregations(
            dataFields,
            aggregationFields,
            aggregationFilter,
            searchSourceBuilder,
            size,
            sipQuery.getSorts(),
            sipQuery.getBooleanCriteria());

    return searchSourceBuilder.toString();
  }

  /**
   * This method will return all the list of columns which required for ES report.
   *
   * @param dataFields
   */
  public String[] getFieldsInclude(List<Field> dataFields) {
    String[] fieldsIncludes = new String[dataFields.size()];
    int count = 0;
    /** Iterate the Data fields to include */
    for (Field dataField : dataFields) {
      String columnName = dataField.getColumnName();
      /**
       * .keyword may present in the es-mapping fields take out form the columnName to get actual
       * column name if present
       */
      String[] split = columnName.split("\\.");
      if (split.length >= 2) fieldsIncludes[count++] = split[0];
      else fieldsIncludes[count++] = columnName;
    }
    return fieldsIncludes;
  }

  /**
   * @param item
   * @param date
   * @return
   */
  public RangeQueryBuilder filterByOperator(Filter item, Date date) {

    RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
    DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);

    if (item.getType().value().equals(Filter.Type.DATE.value())) {
      logger.trace("date format is  :{} ", EPOCH_TO_DATE_FORMAT);
      rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
    }

    switch (item.getModel().getOperator()) {
      case GT:
        logger.trace(
            "Filter for values - Operator: 'GT', Timestamp(asLong) : {}",
            item.getModel().getValue().longValue());
        rangeQueryBuilder.gt(dateFormat.format(date));
        break;

      case LT:
        logger.trace(
            "Filter for values - Operator: 'LT', Timestamp(asLong) :{} ",
            item.getModel().getValue().longValue());
        rangeQueryBuilder.lt(dateFormat.format(date));
        break;

      case GTE:
        logger.trace(
            "Filter for values - Operator: 'GTE', Timestamp(asLong) : {}",
            item.getModel().getValue().longValue());
        rangeQueryBuilder.gte(dateFormat.format(date));
        break;

      case LTE:
        logger.trace(
            "Filter for values - Operator: 'LTE', Timestamp(asLong) :{} ",
            item.getModel().getValue().longValue());
        rangeQueryBuilder.lte(dateFormat.format(date));
        break;
      default:
        throw new SipDslRuntimeException(
            String.format(
                "Operator %s is not  supported for date filter", item.getModel().getOperator()));
    }
    return rangeQueryBuilder;
  }

  /**
   * @param sipQuery
   * @param searchSourceBuilder
   * @return
   */
  public SearchSourceBuilder buildSortQuery(
      SipQuery sipQuery, SearchSourceBuilder searchSourceBuilder) {
    List<Sort> sortNode = sipQuery.getSorts();
    for (Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }
    return searchSourceBuilder;
  }


  /**
   * @param dataFields
   * @param aggregationFields
   * @param searchSourceBuilder
   * @param size
   * @param booleanCriteria
   * @return
   */
  public SearchSourceBuilder buildAggregations(
      List<Field> dataFields,
      List<Field> aggregationFields,
      List<Filter> aggregationFilter,
      SearchSourceBuilder searchSourceBuilder,
      Integer size,
      List<Sort> sorts,
      BooleanCriteria booleanCriteria) {
    SIPAggregationBuilder reportAggregationBuilder = new SIPAggregationBuilder(size);
    AggregationBuilder finalAggregationBuilder = null;
    if (aggregationFields.isEmpty()) {
      String[] excludes = null;
      String[] includes = getFieldsInclude(dataFields);
      searchSourceBuilder.fetchSource(includes, excludes);
    } else {
      AggregationBuilder aggregationBuilder = null;
      if (dataFields.size() == aggregationFields.size()) {
        reportAggregationBuilder.aggregationBuilder(
            dataFields, aggregationFields, searchSourceBuilder, aggregationFilter,booleanCriteria);
      } else {
        this.groupByFields = new String[dataFields.size() - aggregationFields.size()];
        finalAggregationBuilder =
            reportAggregationBuilder.reportAggregationBuilder(
                dataFields,
                aggregationFields,
                aggregationFilter,
                0,
                0,
                aggregationBuilder,
                sorts,
                groupByFields,
                booleanCriteria);
        searchSourceBuilder.aggregation(finalAggregationBuilder);
      }
      // set the size zero for aggregation query .
      searchSourceBuilder.size(0);
    }
    return searchSourceBuilder;
  }

  /**
   * Prepare sum calculated fields required for percentage aggregation.
   *
   * @param sipQuery SIP Query.
   * @return Elasticsearch SearchSourceBuilder
   */
  public SearchSourceBuilder percentagePriorQuery(SipQuery sipQuery,SipDskAttribute dskAttribute) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(0);
      BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    if (dskAttribute != null && dskAttribute.getBooleanCriteria() != null && !CollectionUtils
        .isEmpty(dskAttribute.getBooleanQuery())) {
      // The below code to build filters
      BoolQueryBuilder boolQueryBuilderDsk = queryDSKBuilder(dskAttribute);
      boolQueryBuilder.must(boolQueryBuilderDsk);
      }
    if (sipQuery.getFilters() != null && sipQuery.getFilters().size() > 0) {
      List<Filter> filters = sipQuery.getFilters();
      BoolQueryBuilder newBooleanQuery =
          buildFilterQuery(buildNestedFilter(filters, sipQuery.getBooleanCriteria()));
      boolQueryBuilder.must(newBooleanQuery);
    }
      searchSourceBuilder.query(boolQueryBuilder);
    QueryBuilderUtil.getAggregationBuilder(
        sipQuery.getArtifacts().get(0).getFields(), searchSourceBuilder);
    return searchSourceBuilder;
  }

  /**
   * Set the pre percentage sum calculated data to dataFields.
   *
   * @param fields List of the fields.
   * @param jsonNode Sum calculated json response.
   */
  public void setPriorPercentages(List<Field> fields, JsonNode jsonNode) {
    fields.forEach(
        dataField -> {
          String columnName =
              dataField.getDataField() == null
                  ? dataField.getColumnName()
                  : dataField.getDataField();
          if (dataField.getAggregate() != null
              && dataField.getAggregate().equals(Aggregate.PERCENTAGE))
            dataField
                .getAdditionalProperties()
                .put(
                    columnName + SUM,
                    String.valueOf(jsonNode.get("aggregations").get(columnName).get(VALUE)));
        });
  }

  /**
   * This method builds filter query for the nested filter structure.
   *
   * @param filter nested filter
   * @return querybuilder
   */
  public BoolQueryBuilder buildFilterQuery(Filter filter) {
    BoolQueryBuilder boolQuery = new BoolQueryBuilder();
    BooleanCriteria booleanCriteria = null;
    if (filter == null) {
      return boolQuery;
    }
    if (filter.getBooleanCriteria() == null && filter.getFilters() == null) {
      return boolQuery;
    }
    if (filter.getBooleanCriteria() != null) {
      booleanCriteria = filter.getBooleanCriteria();
    }
    for (Filter sipFilter : filter.getFilters()) {
      if (sipFilter.getFilters() != null && !sipFilter.getFilters().isEmpty()) {
        QueryBuilder childQuery = buildFilterQuery(sipFilter);
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
        Optional<QueryBuilder> queryBuilder = buildFilter(sipFilter);
        if (queryBuilder.isPresent()) {
          switch (booleanCriteria) {
            case AND:
              boolQuery.must(queryBuilder.get());
              break;
            case OR:
              boolQuery.should(queryBuilder.get());
              break;
          }
        }
      }
    }
    return boolQuery;
  }

  /**
   * This method builds query for each filter.
   *
   * @param filter filter
   * @return optional querybuilder
   */
  public Optional<QueryBuilder> buildFilter(Filter filter) {
    if ((filter.getIsRuntimeFilter() == null || !filter.getIsRuntimeFilter())
        && (filter.getIsGlobalFilter() == null || !filter.getIsGlobalFilter())
        // skip the Aggregated filter since it will added based on aggregated data.
        && (filter.getAggregationFilter() == null || !filter.getAggregationFilter())) {

      if (filter.getType() == Filter.Type.DATE || filter.getType() == Filter.Type.TIMESTAMP) {
        return Optional.of(buildDateFilter(filter));
      }
      // make the query based on the filter given
      if (filter.getType() == Filter.Type.STRING) {
        return Optional.of(QueryBuilderUtil.stringFilter(filter));
      }

      if ((filter.getType() == Filter.Type.DOUBLE || filter.getType() == Type.INTEGER)
          || filter.getType() == Filter.Type.FLOAT
          || filter.getType() == Filter.Type.LONG) {
        return Optional.of(QueryBuilderUtil.buildNumericFilter(filter));
      }
    }
    if (filter.getIsRuntimeFilter() != null
        && filter.getIsRuntimeFilter()
        && filter.getModel() != null
        && (filter.getAggregationFilter() == null || !filter.getAggregationFilter())) {
      if (filter.getType() == Filter.Type.DATE || filter.getType() == Filter.Type.TIMESTAMP)
        return Optional.of(buildDateFilter(filter));
      if (filter.getType() == Filter.Type.STRING) {
        return Optional.of(QueryBuilderUtil.stringFilter(filter));
      }
      if ((filter.getType() == Filter.Type.DOUBLE || filter.getType() == Type.INTEGER)
          || filter.getType() == Filter.Type.FLOAT
          || filter.getType() == Filter.Type.LONG) {
        return Optional.of(QueryBuilderUtil.buildNumericFilter(filter));
      }
    }
    return Optional.empty();
  }

  /**
   * This method builds filter query for date type fields .
   *
   * @param filter filter
   * @return querybuilder
   */
  private QueryBuilder buildDateFilter(Filter filter) {
    Optional<String> formatForDate = Optional.empty();
    if (filter.getModel().getPreset() != null
        && (filter.getModel().getPreset() != Model.Preset.NA)) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.dynamicDecipher(filter.getModel().getPreset().value());
      if (filter.getType() == Type.DATE) {
        formatForDate = Optional.of(DATE_FORMAT);
      }
      return buildRangeQueryBuilder(
          filter.getColumnName(),
          formatForDate,
          dynamicConvertor.getLte(),
          dynamicConvertor.getGte());
    } else if (filter.getModel().getPresetCal() != null) {
      DynamicConvertor dynamicConvertor =
          BuilderUtil.getDynamicConvertForPresetCal(filter.getModel().getPresetCal());
      if (filter.getType().value().equals(Filter.Type.DATE.value())) {
        formatForDate = Optional.of(DATE_FORMAT);
      }
      return buildRangeQueryBuilder(
          filter.getColumnName(),
          formatForDate,
          dynamicConvertor.getLte(),
          dynamicConvertor.getGte());
    } else if ((filter.getModel().getFormat() != null)
        && ((filter.getModel().getFormat().equalsIgnoreCase(EPOCH_MILLIS))
            || (filter.getModel().getFormat().equalsIgnoreCase(EPOCH_SECOND)))) {
      if (filter.getModel().getFormat().equalsIgnoreCase(EPOCH_SECOND)) {
        logger.debug("TimeStamp in Epoch(in seconds),Value :{}" ,filter.getModel().getValue());
        filter.getModel().setValue(filter.getModel().getValue() * 1000);
        if (filter.getModel().getOtherValue() != null) {
          filter.getModel().setOtherValue(filter.getModel().getOtherValue() * 1000);
          logger.trace(
              "Convert TimeStamp to milliseconds, OtherValue  :{}",
              filter.getModel().getOtherValue());
        }
      }
      Date date = new Date(filter.getModel().getValue().longValue());
      logger.trace("Date object created :{}", date);
      DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);
      if ((filter.getType() == Filter.Type.DATE) || (filter.getType() == Type.TIMESTAMP)) {
        formatForDate = Optional.of(EPOCH_TO_DATE_FORMAT);
      }

      if (filter.getModel().getOperator() == Model.Operator.EQ) {
        logger.info("dateFormat (SimpleDateFormat) :{}", dateFormat);
        return buildRangeQueryBuilder(
            filter.getColumnName(),
            formatForDate,
            dateFormat.format(DateUtils.addMilliseconds(date, 1)),
            dateFormat.format(DateUtils.addMilliseconds(date, -1)));

      } else if (filter.getModel().getOperator() == Model.Operator.BTW) {
        logger.trace(
            "Filter for values - Operator: 'BTW', Timestamp(asLong) : {}",
            filter.getModel().getValue().longValue());
        Date toDate = new Date(filter.getModel().getOtherValue().longValue());
        return buildRangeQueryBuilder(
            filter.getColumnName(),
            formatForDate,
            dateFormat.format(toDate),
            dateFormat.format(date));
      } else {
        return filterByOperator(filter, date);
      }
    } else if ((filter.getModel().getFormat() != null)
        && (filter.getModel().getFormat().equalsIgnoreCase(ONLY_YEAR_FORMAT))) {
      return buildFilterQueryWithYearFormat(filter);
    } else {
      RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(filter.getColumnName());
      if ((filter.getType().value().equals(Filter.Type.DATE.value()))
          || (filter.getType().value().equals(Type.TIMESTAMP.value()))) {
        if (filter.getModel().getFormat() == null) {
          rangeQueryBuilder.format(DATE_FORMAT);
        } else {
          rangeQueryBuilder.format(filter.getModel().getFormat());
        }
      }
      if (filter.getModel().getLt() != null) {
        rangeQueryBuilder.lt(filter.getModel().getLt());
      }
      if (filter.getModel().getGt() != null) {
        /*In yyyy-MM format GT is not working as elastic search is considing the date as yyyy-MM-01,so adding appenderForGTLTE
         * ref to github path for the solution https://github.com/elastic/elasticsearch/issues/24874
         * */
        if ((filter.getModel().getFormat() != null)
            && (filter.getModel().getFormat().equalsIgnoreCase("yyyy-MM"))) {
          String date = filter.getModel().getGt();
          date = date + appenderForGTLTE;
          rangeQueryBuilder.gt(date);

        } else {
          rangeQueryBuilder.gt(filter.getModel().getGt());
        }
      }
      if (filter.getModel().getLte() != null) {
        /*In yyyy-MM format LTE is not working as elastic search is considing the date as yyyy-MM-01,so adding appenderForGTLTE
         * ref to github path for the solution https://github.com/elastic/elasticsearch/issues/24874
         * */
        if ((filter.getModel().getFormat() != null)
            && (filter.getModel().getFormat().equalsIgnoreCase("yyyy-MM"))) {
          String date = filter.getModel().getLte();
          date = date + appenderForGTLTE;
          rangeQueryBuilder.lte(date);
        } else {
          rangeQueryBuilder.lte(filter.getModel().getLte());
        }
      }
      if (filter.getModel().getGte() != null) {
        rangeQueryBuilder.gte(filter.getModel().getGte());
      }
      return rangeQueryBuilder;
    }
  }

  /**
   * This method builds filter query for date type fields with Year format.
   *
   * @param filter filter
   * @return querybuilder
   */
  private QueryBuilder buildFilterQueryWithYearFormat(Filter filter) {
    logger.info("Format select is year format");
    DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);
    Optional<String> formatForDate = Optional.empty();
    GregorianCalendar startDate =
        new GregorianCalendar(filter.getModel().getValue().intValue(), 0, 1, 0, 0, 0);
    GregorianCalendar endDate;

    if ((filter.getType() == Filter.Type.DATE) || (filter.getType() == Type.TIMESTAMP)) {
      formatForDate = Optional.of(EPOCH_TO_DATE_FORMAT);
    }

    if (filter.getModel().getOperator() == Model.Operator.EQ) {
      endDate = new GregorianCalendar(filter.getModel().getValue().intValue(), 11, 31, 23, 59, 59);
      logger.debug("Start Date :{}" , startDate.getTime());
      logger.debug("End Date :{}", endDate.getTime());
      return buildRangeQueryBuilder(
          filter.getColumnName(),
          formatForDate,
          dateFormat.format(endDate.getTime()),
          dateFormat.format(startDate.getTime()));
    } else if (filter.getModel().getOperator() == Model.Operator.BTW) {
      endDate =
          new GregorianCalendar(filter.getModel().getOtherValue().intValue(), 11, 31, 23, 59, 59);
      logger.debug("Start Date :{}", startDate.getTime());
      logger.debug("End Date :{}", endDate.getTime());
      return buildRangeQueryBuilder(
          filter.getColumnName(),
          formatForDate,
          dateFormat.format(endDate.getTime()),
          dateFormat.format(startDate.getTime()));
    } else {
      return filterByOperator(filter, startDate.getTime());
    }
  }

  /**
   * This method builds the range query.
   *
   * @param columnName columnName
   * @param dateFormat dateFormat
   * @param lte lte
   * @param gte gte
   * @return querybuilder
   */
  private QueryBuilder buildRangeQueryBuilder(
      String columnName, Optional<String> dateFormat, String lte, String gte) {
    RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(columnName);
    if (dateFormat.isPresent()) {
      rangeQueryBuilder.format(dateFormat.get());
    }
    rangeQueryBuilder.lte(lte);
    rangeQueryBuilder.gte(gte);
    return rangeQueryBuilder;
  }
}
