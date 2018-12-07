package com.synchronoss.saw.es;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.model.*;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
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
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchQueryBuilder {

  public static final Logger logger = LoggerFactory.getLogger(ElasticSearchQueryBuilder.class);
    private final static String DATE_FORMAT="yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";
    private final static String VALUE = "value";
    private final static String SUM ="_sum";
    String dataSecurityString;

    public String buildDataQuery(SIPDSL sipdsl) throws IOException, ProcessingException {
        SipQuery sipQuery = sipdsl.getSipQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        int size = 1000;

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
                reportAggregationBuilder.reportAggregationBuilder(dataFields
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
