package com.synchronoss.saw.es;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.model.*;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ElasticSearchQueryBuilder {

  public static final Logger logger = LoggerFactory.getLogger(ElasticSearchQueryBuilder.class);
    private final static String DATE_FORMAT="yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";
    private final static String EPOCH_TO_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
    private final static String ONLY_YEAR_FORMAT="YYYY";
    private final static String EPOCH_SECOND="epoch_second";
    private final static String EPOCH_MILLIS="epoch_millis";
    String dataSecurityString;

    public String buildDataQuery(SIPDSL sipdsl, Integer size) throws IOException, ProcessingException {
        SipQuery sipQuery = sipdsl.getSipQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        if (size==null || size.equals(0))
            size=1000;
        searchSourceBuilder.size(size);
        if (sipQuery.getSorts() == null && sipQuery.getFilters() == null) {
            throw new NullPointerException(
                "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
        }
        // The below block adding the sort block
        List<Sort> sortNode = sipQuery.getSorts();
        for (Sort item : sortNode) {
            SortOrder sortOrder =
                item.getOrder().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
            FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
            searchSourceBuilder.sort(sortBuilder);
        }
        DataSecurityKey dataSecurityKeyNode = null;
        ObjectMapper objectMapper = null;

        if (dataSecurityString !=null && !dataSecurityString.trim().equals("")){
            objectMapper= new ObjectMapper();
            objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
            JsonNode objectNode = objectMapper.readTree(dataSecurityString);
            dataSecurityKeyNode = objectMapper.treeToValue(objectNode, DataSecurityKey.class);
        }

        // The below block adding filter block
        // The below block adding filter block
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();;
        if (sipQuery.getBooleanCriteria() !=null ){
            List<Filter> filters = sipQuery.getFilters();
            List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
          //  builder = QueryBuilderUtil.queryDSKBuilder(dataSecurityKeyNode,builder);
            for (Filter item : filters)
            {
                if (!item.getIsRuntimeFilter() && item.getIsGlobalFilter()!=null
                    && !item.getIsGlobalFilter()){

                    if (item.getType().value().equals(Filter.Type.DATE.value()) || item.getType().value().equals(Filter.Type.TIMESTAMP.value())) {
                        if (item.getModel().getPreset()!=null && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
                        {
                            DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
                            if(item.getType().value().equals(Filter.Type.DATE.value())) {
                                rangeQueryBuilder.format(DATE_FORMAT);
                            }

                            rangeQueryBuilder.lte(dynamicConvertor.getLte());
                            rangeQueryBuilder.gte(dynamicConvertor.getGte());
                            builder.add(rangeQueryBuilder);
                        }
                        else if ((item.getModel().getFormat().equalsIgnoreCase(EPOCH_MILLIS)) || (item.getModel().getFormat().equalsIgnoreCase("epoch"))) {
                            if (item.getModel().getFormat().equalsIgnoreCase(EPOCH_SECOND)) {
                                logger.info("Filter format : epoch");
                                logger.info("TimeStamp in Epoch(in seconds),Value "+item.getModel().getValue());
                                item.getModel().setValue(item.getModel().getValue()*1000);
                                logger.info("Convert TimeStamp to milliseconds, Value  :"+item.getModel().getValue());
                                if (item.getModel().getOtherValue() != null) {
                                    logger.info("TimeStamp in Epoch(in seconds), OtherValue "+item.getModel().getOtherValue());
                                    item.getModel().setOtherValue(item.getModel().getOtherValue()*1000);
                                    logger.info("Convert TimeStamp to milliseconds, OtherValue  :"+item.getModel().getOtherValue());
                                }
                            }
                            logger.debug("recieved value :" + item.getModel().getValue().longValue());
                            Date date = new Date(item.getModel().getValue().longValue());
                            logger.info("Date object created :"+date);
                            DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());

                            switch (item.getModel().getOperator()) {
                                case EQ:
                                    logger.info("dateFormat (SimpleDateFormat) :" + dateFormat);
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        logger.info("rangeQueryBuilder.format(dateFormat.format(date)) : " + dateFormat.format(date));
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    rangeQueryBuilder.lte(dateFormat.format(DateUtils.addMilliseconds(date, 1)));
                                    rangeQueryBuilder.gte(dateFormat.format(DateUtils.addMilliseconds(date, -1)));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case GT:
                                    logger.info("Filter for values - Operator: 'GT', Timestamp(asLong) : " + item.getModel().getValue().longValue());

                                    logger.info("Date object created :" + date);
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    rangeQueryBuilder.gt(dateFormat.format(date));
                                    builder.add(rangeQueryBuilder);
                                    logger.debug("Builder Obj : " + builder);
                                    break;

                                case LT:
                                    logger.info("Filter for values - Operator: 'LT', Timestamp(asLong) : " + item.getModel().getValue().longValue());

                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    rangeQueryBuilder.lt(dateFormat.format(date));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case BTW:
                                    logger.info("Filter for values - Operator: 'BTW', Timestamp(asLong) : " + item.getModel().getValue().longValue());

                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    rangeQueryBuilder.lte(dateFormat.format(date));
                                    date = new Date(item.getModel().getOtherValue().longValue());
                                    rangeQueryBuilder.gte(dateFormat.format(date));
                                    builder.add(rangeQueryBuilder);
                                    logger.debug("Builder Obj : " + builder);
                                    break;

                                case GTE:
                                    logger.info("Filter for values - Operator: 'GTE', Timestamp(asLong) : " + item.getModel().getValue().longValue());

                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    rangeQueryBuilder.gte(dateFormat.format(date));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case LTE:
                                    logger.info("Filter for values - Operator: 'LTE', Timestamp(asLong) : " + item.getModel().getValue().longValue());

                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    rangeQueryBuilder.lte(dateFormat.format(date));
                                    builder.add(rangeQueryBuilder);
                                    logger.debug("Builder Obj : " + builder);
                                    break;
                            }

                        }
                        else if (item.getModel().getFormat().equalsIgnoreCase(ONLY_YEAR_FORMAT)) {
                            DateFormat dateFormat = new SimpleDateFormat(EPOCH_TO_DATE_FORMAT);
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
                            GregorianCalendar startDate = new GregorianCalendar(item.getModel().getValue().intValue(),0,1,0,0,0);
                            GregorianCalendar endDate;

                            switch (item.getModel().getOperator()) {
                                case EQ:
                                    endDate = new GregorianCalendar(item.getModel().getValue().intValue() ,11,31,23,59,59);
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    logger.info("Start Date :"+ startDate.getTime());
                                    logger.info("End Date :"+ endDate.getTime());
                                    rangeQueryBuilder.gte(dateFormat.format(startDate.getTime()));
                                    rangeQueryBuilder.lte(dateFormat.format(endDate.getTime()));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case LT:
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    logger.debug("Retriving date < "+startDate.getTime());
                                    rangeQueryBuilder.lt(dateFormat.format(startDate.getTime()));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case GT:
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    logger.debug("Retriving date > "+startDate.getTime());
                                    rangeQueryBuilder.gt(dateFormat.format(startDate.getTime()));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case LTE:
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    logger.debug("Retriving date <= (less than or equal to) "+startDate.getTime());
                                    rangeQueryBuilder.lte(dateFormat.format(startDate.getTime()));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case GTE:
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    logger.debug("Retriving date >= "+startDate.getTime());
                                    rangeQueryBuilder.gte(dateFormat.format(startDate.getTime()));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;

                                case BTW:
                                    endDate = new GregorianCalendar(item.getModel().getOtherValue().intValue() ,11,31,23,59,59);
                                    if (item.getType().value().equals(Filter.Type.DATE.value())) {
                                        rangeQueryBuilder.format(EPOCH_TO_DATE_FORMAT);
                                    }
                                    logger.info("Start Date :"+ startDate.getTime());
                                    logger.info("End Date :"+ endDate.getTime());
                                    rangeQueryBuilder.gte(dateFormat.format(startDate.getTime()));
                                    rangeQueryBuilder.lte(dateFormat.format(endDate.getTime()));
                                    builder.add(rangeQueryBuilder);
                                    logger.info("Builder Obj : " + builder);
                                    break;
                            }
                        }
                        else {
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
                            if(item.getType().value().equals(Filter.Type.DATE.value())) {
                                rangeQueryBuilder.format(DATE_FORMAT);
                            }
                            rangeQueryBuilder.lte(item.getModel().getLte());
                            rangeQueryBuilder.gte(item.getModel().getGte());
                            builder.add(rangeQueryBuilder);
                        }
                    }
                    // make the query based on the filter given
                    if (item.getType().value().equals(Filter.Type.STRING.value())) {
                        builder = QueryBuilderUtil.stringFilter(item, builder);
                    }

                    if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase()) || item
                        .getType().value().toLowerCase().equals(Filter.Type.INT.value().toLowerCase()))
                        || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
                        || item.getType().value().toLowerCase().equals(Filter.Type.LONG.value().toLowerCase())) {
                        builder = QueryBuilderUtil.numericFilter(item, builder);
                    }
                }
                if (item.getIsRuntimeFilter() && item.getModel()!=null)
                {
                    if (item.getType().value().equals(Filter.Type.DATE.value()) || item.getType().value().equals(Filter.Type.TIMESTAMP.value())) {
                        if (item.getModel().getPreset()!=null && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
                        {
                            DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
                            if(item.getType().value().equals(Filter.Type.DATE.value())) {
                                rangeQueryBuilder.format(DATE_FORMAT);
                            }
                            rangeQueryBuilder.lte(dynamicConvertor.getLte());
                            rangeQueryBuilder.gte(dynamicConvertor.getGte());
                            builder.add(rangeQueryBuilder);

                        }
                        else {
                            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
                            if(item.getType().value().equals(Filter.Type.DATE.value())) {
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
                    if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase()) || item
                        .getType().value().toLowerCase().equals(Filter.Type.INT.value().toLowerCase()))
                        || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
                        || item.getType().value().toLowerCase().equals(Filter.Type.LONG.value().toLowerCase())) {
                        builder = QueryBuilderUtil.numericFilter(item, builder);
                    }
                }
            }
            if (sipQuery.getBooleanCriteria().value().equals(SipQuery.BooleanCriteria.AND.value())) {
                builder.forEach(item -> {
                    boolQueryBuilder.must(item);
                });
            } else {
                builder.forEach(item -> {
                    boolQueryBuilder.should(item);
                });
            }
            searchSourceBuilder.query(boolQueryBuilder);
        }

        List<Field> dataFields =
            sipQuery.getArtifacts().get(0).getFields();
        SIPAggregationBuilder reportAggregationBuilder = new SIPAggregationBuilder(size);
        List<Field> aggregationFields = SIPAggregationBuilder.getAggregationField(dataFields);
        AggregationBuilder finalAggregationBuilder =null;

        boolean isPercentage = dataFields.stream().anyMatch(dataField ->
            dataField.getAggregate()!=null && dataField.getAggregate().value().equalsIgnoreCase(Field.Aggregate.PERCENTAGE.value()));

      /*  //pre-calculation for percentage.
        if(isPercentage)
        {
            SearchSourceBuilder preSearchSourceBuilder = new SearchSourceBuilder();
            preSearchSourceBuilder.size(0);
            preSearchSourceBuilder.query(boolQueryBuilder);
            QueryBuilderUtil.getAggregationBuilder(dataFields, preSearchSourceBuilder);
            String result = SAWElasticTransportService.executeReturnAsString(preSearchSourceBuilder.toString(),jsonString,"dummy",
                "system","analyse",timeOut);
            // Set total sum for dataFields will be used for percentage calculation.
            objectMapper = new ObjectMapper();
            JsonNode objectNode = objectMapper.readTree(result);
            dataFields.forEach (dataField -> {
                String columnName = dataField.getColumnName();
                if(dataField.getAggregate()!=null && dataField.getAggregate().equals(Column.Aggregate.PERCENTAGE))
                    dataField.getAdditionalProperties()
                        .put(columnName+SUM, String.valueOf(objectNode.get(columnName
                        ).get(VALUE)));
            });

        }*/
        // Generated Query
        if (aggregationFields.size()==0)
        {
            String[] excludes = null;
            String[] includes = getFieldsInclude(dataFields);
            searchSourceBuilder.fetchSource(includes,excludes);
        }
        else {
            AggregationBuilder aggregationBuilder = null;
            if (dataFields.size()==aggregationFields.size())
            {
                reportAggregationBuilder.aggregationBuilder(dataFields
                    ,aggregationFields,searchSourceBuilder);
            }
            else {
                finalAggregationBuilder = reportAggregationBuilder.reportAggregationBuilder(
                    dataFields, aggregationFields, 0, 0, aggregationBuilder);
                searchSourceBuilder.aggregation(finalAggregationBuilder);
            }
            // set the size zero for aggregation query .
            searchSourceBuilder.size(0);
        }
        return searchSourceBuilder.toString();
    }

    /**
     * This method will return all the list of columns which required for ES report.
     * @param dataFields
     */
    private String [] getFieldsInclude( List<Field> dataFields)
    {
        String [] fieldsIncludes = new String[dataFields.size()];
        int count =0;
        /** Iterate the Data fields to include */
        for (Field dataField : dataFields)
        {
            String columnName = dataField.getColumnName();
            /** .keyword may present in the es-mapping fields
             take out form the columnName to get actual column name
             if present */
            String [] split = columnName.split("\\.");
            if (split.length>=2)
                fieldsIncludes[count++]= split[0];
            else
                fieldsIncludes[count++]= columnName;
        }
        return fieldsIncludes;
    }
}
