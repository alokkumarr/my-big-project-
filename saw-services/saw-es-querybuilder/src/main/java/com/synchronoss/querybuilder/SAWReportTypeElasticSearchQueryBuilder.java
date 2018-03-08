package com.synchronoss.querybuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.DynamicConvertor;
import com.synchronoss.SAWElasticTransportService;
import com.synchronoss.querybuilder.model.report.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SAWReportTypeElasticSearchQueryBuilder {

    private final static String DATE_FORMAT="yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";
    private final static String VALUE = "value";
    private final static String SUM ="_sum";
    String jsonString;
    String dataSecurityString;
    SearchSourceBuilder searchSourceBuilder;

    public SAWReportTypeElasticSearchQueryBuilder(String jsonString) {
        super();
        this.jsonString = jsonString;
    }

    public SAWReportTypeElasticSearchQueryBuilder(String jsonString, String dataSecurityKey) {
        super();
        this.dataSecurityString = dataSecurityKey;
        this.jsonString = jsonString;
    }

    public String getDataSecurityString() {
        return dataSecurityString;
    }

    public String getJsonString() {
        return jsonString;
    }

    /**
     *  This method will return the data required to display for data.
     * @return
     * @throws IOException
     * @throws ProcessingException
     */

    public String buildDataQuery(Integer size) throws IOException, ProcessingException {
        SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTreeReport(getJsonString(), "sqlBuilder");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        if (sqlBuilderNode.getSorts() == null && sqlBuilderNode.getFilters() == null) {
            throw new NullPointerException(
                    "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
        }
        // The below block adding the sort block
        List<Sort> sortNode = sqlBuilderNode.getSorts();
        for (Sort item : sortNode) {
            SortOrder sortOrder =
                    item.getOrder().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
            FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
            searchSourceBuilder.sort(sortBuilder);
        }
        DataSecurityKey dataSecurityKeyNode = null;
        ObjectMapper objectMapper = null;
        if (getDataSecurityString()!=null && !getDataSecurityString().trim().equals("")){
            objectMapper= new ObjectMapper();
            objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
            JsonNode objectNode = objectMapper.readTree(getDataSecurityString());
            dataSecurityKeyNode = objectMapper.treeToValue(objectNode, DataSecurityKey.class);
        }

        // The below block adding filter block
        // The below block adding filter block
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();;
        if (sqlBuilderNode.getBooleanCriteria() !=null ){
            List<com.synchronoss.querybuilder.model.report.Filter> filters = sqlBuilderNode.getFilters();
            List<QueryBuilder> builder = new ArrayList<QueryBuilder>();

            if (dataSecurityKeyNode!=null) {
                for (DataSecurityKeyDef dsk : dataSecurityKeyNode.getDataSecuritykey()){
                    TermsQueryBuilder dataSecurityBuilder = new TermsQueryBuilder(dsk.getName().concat(BuilderUtil.SUFFIX), dsk.getValues());
                    builder.add(dataSecurityBuilder);
                }
            }

            for (com.synchronoss.querybuilder.model.report.Filter item : filters)
            {
                if (!item.getIsRuntimeFilter().value()){
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
                        builder = QueryBuilderUtil.stringFilterReport(item, builder);
                    }

                    if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase()) || item
                            .getType().value().toLowerCase().equals(Filter.Type.INT.value().toLowerCase()))
                            || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
                            || item.getType().value().toLowerCase().equals(Filter.Type.LONG.value().toLowerCase())) {
                        builder = QueryBuilderUtil.numericFilterReport(item, builder);
                    }

                }
                if (item.getIsRuntimeFilter().value() && item.getModel()!=null)
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
                        builder = QueryBuilderUtil.stringFilterReport(item, builder);
                    }
                    if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase()) || item
                            .getType().value().toLowerCase().equals(Filter.Type.INT.value().toLowerCase()))
                            || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
                            || item.getType().value().toLowerCase().equals(Filter.Type.LONG.value().toLowerCase())) {
                        builder = QueryBuilderUtil.numericFilterReport(item, builder);
                    }
                }
            }
            if (sqlBuilderNode.getBooleanCriteria().value().equals(SqlBuilder.BooleanCriteria.AND.value())) {
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

        List<DataField> dataFields =
                sqlBuilderNode.getDataFields();
        ReportAggregationBuilder reportAggregationBuilder = new ReportAggregationBuilder(size);
        List<DataField> aggregationFields = reportAggregationBuilder.getAggregationField(dataFields);
        AggregationBuilder finalAggregationBuilder =null;

        boolean isPercentage = dataFields.stream().anyMatch(dataField ->
                dataField.getAggregate()!=null && dataField.getAggregate().value().equalsIgnoreCase(DataField.Aggregate.PERCENTAGE.value()));

        //pre-calculation for percentage.
        if(isPercentage)
        {
            SearchSourceBuilder preSearchSourceBuilder = new SearchSourceBuilder();
            preSearchSourceBuilder.query(boolQueryBuilder);
            preSearchSourceBuilder.aggregation(QueryBuilderUtil.getAggregationBuilder(dataFields));
            String result = SAWElasticTransportService.executeReturnAsString(preSearchSourceBuilder.toString(),jsonString,"dummy",
                    "system","analyse");
            // Set total sum for dataFields will be used for percentage calculation.
            objectMapper = new ObjectMapper();
            JsonNode objectNode = objectMapper.readTree(result).get("data_fields");
            dataFields.forEach (dataField -> {
                String columnName = dataField.getColumnName();
                if(dataField.getAggregate()!=null && dataField.getAggregate().equals(DataField.Aggregate.PERCENTAGE))
                dataField.getAdditionalProperties()
                        .put(columnName+SUM, String.valueOf(objectNode.get(columnName
                        ).get(VALUE)));
            });

        }
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
                finalAggregationBuilder=  reportAggregationBuilder.reportAggregationBuilder(dataFields
                ,aggregationFields);
            }
            else {
                finalAggregationBuilder = reportAggregationBuilder.reportAggregationBuilder(
                        dataFields, aggregationFields, 0, 0, aggregationBuilder);
            }
            // set the size zero for aggregation query .
            searchSourceBuilder.size(0);
            searchSourceBuilder.aggregation(finalAggregationBuilder);
        }
        setSearchSourceBuilder(searchSourceBuilder);
        return searchSourceBuilder.toString();
    }

    public SearchSourceBuilder getSearchSourceBuilder(Integer size) throws IOException, ProcessingException {
        buildDataQuery(size);
        return searchSourceBuilder;
    }

    void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
        this.searchSourceBuilder = searchSourceBuilder;
    }

    /**
     * This method will return all the list of columns which required for ES report.
     * @param dataFields
     */
    private String [] getFieldsInclude( List<DataField> dataFields)
    {
        String [] fieldsIncludes = new String[dataFields.size()];
        int count =0;
        /** Iterate the Data fields to include */
        for (DataField dataField : dataFields)
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
