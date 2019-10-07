package com.synchronoss.saw.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Filter.Type;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.lang.StringUtils;
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

  public String buildDataQuery(SipQuery sipQuery, Integer size, DataSecurityKey dataSecurityKey)
      throws IOException, ProcessingException {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.from(0);
    DataSecurityKey dataSecurityKeyNode = dataSecurityKey;
    /*
    ToDo: when size is -1 need to remove the hard coded size as 1lakh and we need to send the total
      data  when the size is -1.
     */
    if (size == -1) size = 100000;
    searchSourceBuilder.size(size);
    if (sipQuery.getSorts() == null && sipQuery.getFilters() == null) {
      throw new NullPointerException(
          "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
    }
    // The below call is to build sort
    searchSourceBuilder = buildSortQuery(sipQuery, searchSourceBuilder);

    // The below code to build filters
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    if (sipQuery.getBooleanCriteria() != null) {
      List<Filter> filters = sipQuery.getFilters();
      List<QueryBuilder> builder = new ArrayList<QueryBuilder>();

      builder = QueryBuilderUtil.queryDSKBuilder(dataSecurityKeyNode, builder);
      builder = buildFilters(filters, builder);

      boolQueryBuilder = buildBooleanQuery(sipQuery, builder);
      searchSourceBuilder.query(boolQueryBuilder);
    }

    List<Field> dataFields = sipQuery.getArtifacts().get(0).getFields();
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
            sipQuery.getSorts());

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
      logger.info("rangeQueryBuilder.format(dateFormat.format(date)) : " + dateFormat.format(date));
      rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
    }

    switch (item.getModel().getOperator()) {
      case GT:
        logger.info(
            "Filter for values - Operator: 'GT', Timestamp(asLong) : "
                + item.getModel().getValue().longValue());
        rangeQueryBuilder.gt(dateFormat.format(date));
        break;

      case LT:
        logger.info(
            "Filter for values - Operator: 'LT', Timestamp(asLong) : "
                + item.getModel().getValue().longValue());
        rangeQueryBuilder.lt(dateFormat.format(date));
        break;

      case GTE:
        logger.info(
            "Filter for values - Operator: 'GTE', Timestamp(asLong) : "
                + item.getModel().getValue().longValue());
        rangeQueryBuilder.gte(dateFormat.format(date));
        break;

      case LTE:
        logger.info(
            "Filter for values - Operator: 'LTE', Timestamp(asLong) : "
                + item.getModel().getValue().longValue());
        rangeQueryBuilder.lte(dateFormat.format(date));
        break;
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
   * @param dataSecurityString
   * @return
   * @throws IOException
   */
  public DataSecurityKey buildDsk(String dataSecurityString) throws IOException {
    DataSecurityKey dataSecurityKeyNode;
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode objectNode = objectMapper.readTree(dataSecurityString);
    dataSecurityKeyNode = objectMapper.treeToValue(objectNode, DataSecurityKey.class);
    return dataSecurityKeyNode;
  }

  /**
   * @param sipQuery
   * @param builder
   * @return
   */
  public BoolQueryBuilder buildBooleanQuery(SipQuery sipQuery, List<QueryBuilder> builder) {
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    if (sipQuery.getBooleanCriteria().value().equals(SipQuery.BooleanCriteria.AND.value())) {
      builder.forEach(
          item -> {
            boolQueryBuilder.must(item);
          });
    } else {
      builder.forEach(
          item -> {
            boolQueryBuilder.should(item);
          });
    }
    return boolQueryBuilder;
  }

  /**
   * @param dataFields
   * @param aggregationFields
   * @param searchSourceBuilder
   * @param size
   * @return
   */
  public SearchSourceBuilder buildAggregations(
      List<Field> dataFields,
      List<Field> aggregationFields,
      List<Filter> aggregationFilter,
      SearchSourceBuilder searchSourceBuilder,
      Integer size,
      List<Sort> sorts) {
    SIPAggregationBuilder reportAggregationBuilder = new SIPAggregationBuilder(size);
    AggregationBuilder finalAggregationBuilder = null;
    if (aggregationFields.size() == 0) {
      String[] excludes = null;
      String[] includes = getFieldsInclude(dataFields);
      searchSourceBuilder.fetchSource(includes, excludes);
    } else {
      AggregationBuilder aggregationBuilder = null;
      if (dataFields.size() == aggregationFields.size()) {
        reportAggregationBuilder.aggregationBuilder(
            dataFields, aggregationFields, searchSourceBuilder);
      } else {
        finalAggregationBuilder =
            reportAggregationBuilder.reportAggregationBuilder(
                dataFields, aggregationFields, aggregationFilter, 0, 0, aggregationBuilder, sorts);
        searchSourceBuilder.aggregation(finalAggregationBuilder);
      }
      // set the size zero for aggregation query .
      searchSourceBuilder.size(0);
    }
    return searchSourceBuilder;
  }

  /**
   * @param filters
   * @param builder
   * @return
   */
  public List<QueryBuilder> buildFilters(List<Filter> filters, List<QueryBuilder> builder) {
    for (Filter item : filters) {
      if ((item.getIsRuntimeFilter() == null || !item.getIsRuntimeFilter())
          && (item.getIsGlobalFilter() == null || !item.getIsGlobalFilter())
          // skip the Aggregated filter since it will added based on aggregated data.
          && (item.getAggregationFilter() == null || !item.getAggregationFilter())) {

        if (item.getType().value().equals(Filter.Type.DATE.value())
            || item.getType().value().equals(Filter.Type.TIMESTAMP.value())) {
          if (item.getModel().getPreset() != null
              && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString())) {
            DynamicConvertor dynamicConvertor =
                BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }

            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);
          } else if (item.getModel().getPresetCal() != null) {
            DynamicConvertor dynamicConvertor =
                BuilderUtil.getDynamicConvertForPresetCal(item.getModel().getPresetCal());
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }
            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);
          } else if ((item.getModel().getFormat() != null)
              && ((item.getModel().getFormat().equalsIgnoreCase(EPOCH_MILLIS))
                  || (item.getModel().getFormat().equalsIgnoreCase(EPOCH_SECOND)))) {
            if (item.getModel().getFormat().equalsIgnoreCase(EPOCH_SECOND)) {
              logger.info("Filter format : epoch");
              logger.info("TimeStamp in Epoch(in seconds),Value " + item.getModel().getValue());
              item.getModel().setValue(item.getModel().getValue() * 1000);
              logger.info(
                  "Convert TimeStamp to milliseconds, Value  :" + item.getModel().getValue());
              if (item.getModel().getOtherValue() != null) {
                logger.info(
                    "TimeStamp in Epoch(in seconds), OtherValue "
                        + item.getModel().getOtherValue());
                item.getModel().setOtherValue(item.getModel().getOtherValue() * 1000);
                logger.info(
                    "Convert TimeStamp to milliseconds, OtherValue  :"
                        + item.getModel().getOtherValue());
              }
            }
            logger.debug("recieved value :" + item.getModel().getValue().longValue());
            Date date = new Date(item.getModel().getValue().longValue());
            logger.info("Date object created :" + date);
            DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());

            if ((item.getType().value().equals(Filter.Type.DATE.value()))
                || ((item.getType().value().equals(Type.TIMESTAMP.value())))) {
              logger.info(
                  "rangeQueryBuilder.format(dateFormat.format(date)) : " + dateFormat.format(date));
              rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
            }

            if (item.getModel().getOperator().equals(Model.Operator.EQ)) {
              logger.info("dateFormat (SimpleDateFormat) :" + dateFormat);
              rangeQueryBuilder.lte(dateFormat.format(DateUtils.addMilliseconds(date, 1)));
              rangeQueryBuilder.gte(dateFormat.format(DateUtils.addMilliseconds(date, -1)));
              builder.add(rangeQueryBuilder);
              logger.info("Builder Obj : " + builder);

            } else if (item.getModel().getOperator().equals(Model.Operator.BTW)) {
              logger.info(
                  "Filter for values - Operator: 'BTW', Timestamp(asLong) : "
                      + item.getModel().getValue().longValue());
              rangeQueryBuilder.gte(dateFormat.format(date));
              date = new Date(item.getModel().getOtherValue().longValue());
              rangeQueryBuilder.lte(dateFormat.format(date));
              builder.add(rangeQueryBuilder);
              logger.debug("Builder Obj : " + builder);
            } else {
              if (item.getType().value().equals(Filter.Type.DATE.value())) {
                rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
              }
              rangeQueryBuilder = filterByOperator(item, date);
              builder.add(rangeQueryBuilder);
              logger.debug("Builder Obj : " + builder);
            }
          } else if ((item.getModel().getFormat() != null)
              && (item.getModel().getFormat().equalsIgnoreCase(ONLY_YEAR_FORMAT))) {
            DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            GregorianCalendar startDate =
                new GregorianCalendar(item.getModel().getValue().intValue(), 0, 1, 0, 0, 0);
            GregorianCalendar endDate;

            if ((item.getType().value().equals(Filter.Type.DATE.value()))
                || ((item.getType().value().equals(Type.TIMESTAMP.value())))) {
              rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
            }

            if (item.getModel().getOperator().equals(Model.Operator.EQ)) {
              endDate =
                  new GregorianCalendar(item.getModel().getValue().intValue(), 11, 31, 23, 59, 59);

              logger.info("Start Date :" + startDate.getTime());
              logger.info("End Date :" + endDate.getTime());
              rangeQueryBuilder.gte(dateFormat.format(startDate.getTime()));
              rangeQueryBuilder.lte(dateFormat.format(endDate.getTime()));
              builder.add(rangeQueryBuilder);
              logger.info("Builder Obj : " + builder);
            } else if (item.getModel().getOperator().equals(Model.Operator.BTW)) {
              endDate =
                  new GregorianCalendar(
                      item.getModel().getOtherValue().intValue(), 11, 31, 23, 59, 59);
              logger.info("Start Date :" + startDate.getTime());
              logger.info("End Date :" + endDate.getTime());
              rangeQueryBuilder.gte(dateFormat.format(startDate.getTime()));
              rangeQueryBuilder.lte(dateFormat.format(endDate.getTime()));
              builder.add(rangeQueryBuilder);
              logger.info("Builder Obj : " + builder);
            } else {
              rangeQueryBuilder = filterByOperator(item, startDate.getTime());
              builder.add(rangeQueryBuilder);
              logger.info("Builder Obj : " + builder);
            }

          } else {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if ((item.getType().value().equals(Filter.Type.DATE.value()))
                || ((item.getType().value().equals(Type.TIMESTAMP.value())))) {
              if (item.getModel().getFormat() == null) {
                rangeQueryBuilder.format(DATE_FORMAT);
              } else {
                rangeQueryBuilder.format(item.getModel().getFormat());
              }
            }
            if (item.getModel().getLt() != null) {
              rangeQueryBuilder.lt(item.getModel().getLt());
            }
            if (item.getModel().getGt() != null) {
              /*In yyyy-MM format GT is not working as elastic search is considing the date as yyyy-MM-01,so adding appenderForGTLTE
               * ref to github path for the solution https://github.com/elastic/elasticsearch/issues/24874
               * */
              if ((item.getModel().getFormat() != null)
                  && (item.getModel().getFormat().equalsIgnoreCase("yyyy-MM"))) {
                String date = item.getModel().getGt();
                date = date + appenderForGTLTE;
                rangeQueryBuilder.gt(date);

              } else {
                rangeQueryBuilder.gt(item.getModel().getGt());
              }
            }
            if (item.getModel().getLte() != null) {
              /*In yyyy-MM format LTE is not working as elastic search is considing the date as yyyy-MM-01,so adding appenderForGTLTE
               * ref to github path for the solution https://github.com/elastic/elasticsearch/issues/24874
               * */
              if ((item.getModel().getFormat() != null)
                  && (item.getModel().getFormat().equalsIgnoreCase("yyyy-MM"))) {
                String date = item.getModel().getLte();
                date = date + appenderForGTLTE;
                rangeQueryBuilder.lte(date);
              } else {
                rangeQueryBuilder.lte(item.getModel().getLte());
              }
            }
            if (item.getModel().getGte() != null) {
              rangeQueryBuilder.gte(item.getModel().getGte());
            }
            builder.add(rangeQueryBuilder);
          }
        }
        // make the query based on the filter given
        if (item.getType().value().equals(Filter.Type.STRING.value())) {
          builder = QueryBuilderUtil.stringFilter(item, builder);
        }

        if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase())
                || item.getType().value().toLowerCase().equals(Type.INTEGER.value().toLowerCase()))
            || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
            || item.getType()
                .value()
                .toLowerCase()
                .equals(Filter.Type.LONG.value().toLowerCase())) {
          builder = QueryBuilderUtil.numericFilter(item, builder);
        }
      }
      if (item.getIsRuntimeFilter() != null
          && item.getIsRuntimeFilter()
          && item.getModel() != null) {
        if (item.getType().value().equals(Filter.Type.DATE.value())
            || item.getType().value().equals(Filter.Type.TIMESTAMP.value())) {
          if (item.getModel().getPreset() != null
              && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString())) {
            DynamicConvertor dynamicConvertor =
                BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }
            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);

          } else if (item.getModel().getPresetCal() != null
              && !StringUtils.isEmpty(item.getModel().getPresetCal())) {
            DynamicConvertor dynamicConvertor =
                BuilderUtil.getDynamicConvertForPresetCal(item.getModel().getPresetCal());
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }
            rangeQueryBuilder.lte(dynamicConvertor.getLte());
            rangeQueryBuilder.gte(dynamicConvertor.getGte());
            builder.add(rangeQueryBuilder);
          } else {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            if (item.getType().value().equals(Filter.Type.DATE.value())) {
              rangeQueryBuilder.format(DATE_FORMAT);
            }
            rangeQueryBuilder.lte(item.getModel().getLte());
            rangeQueryBuilder.gte(item.getModel().getGte());
            builder.add(rangeQueryBuilder);
          }
        }
        if (item.getType().value().equals(Filter.Type.STRING.value())) {
          builder = QueryBuilderUtil.stringFilter(item, builder);
        }
        if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase())
                || item.getType().value().toLowerCase().equals(Type.INTEGER.value().toLowerCase()))
            || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
            || item.getType()
                .value()
                .toLowerCase()
                .equals(Filter.Type.LONG.value().toLowerCase())) {
          builder = QueryBuilderUtil.numericFilter(item, builder);
        }
      }
    }
    return builder;
  }

  /**
   * Prepare sum calculated fields required for percentage aggregation.
   *
   * @param sipQuery SIP Query.
   * @return Elasticsearch SearchSourceBuilder
   */
  public SearchSourceBuilder percentagePriorQuery(SipQuery sipQuery) {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(0);
    if (sipQuery.getBooleanCriteria() != null) {
      List<Filter> filters = sipQuery.getFilters();
      List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
      builder = buildFilters(filters, builder);
      // TODO: Future Implementation
      //  builder = QueryBuilderUtil.queryDSKBuilder(dataSecurityKeyNode,builder);
      BoolQueryBuilder boolQueryBuilder = buildBooleanQuery(sipQuery, builder);
      searchSourceBuilder.query(boolQueryBuilder);
    }
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
          String columnName = dataField.getColumnName();
          if (dataField.getAggregate() != null
              && dataField.getAggregate().equals(Aggregate.PERCENTAGE))
            dataField
                .getAdditionalProperties()
                .put(
                    columnName + SUM,
                    String.valueOf(jsonNode.get("aggregations").get(columnName).get(VALUE)));
        });
  }
}
