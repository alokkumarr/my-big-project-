package com.synchronoss.querybuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.DynamicConvertor;
import com.synchronoss.querybuilder.model.report.*;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SAWReportTypeElasticSearchQueryBuilder {


    private final static String DATE_FORMAT="yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";
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

    public String buildDataQuery() throws IOException, ProcessingException {
        SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTreeReport(getJsonString(), "sqlBuilder");
        /** TO DO: (Temporary check-in for size variable) next iterator size value will be implemented
         * with UI parameter */
        int size = 1000;
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
                    if (item.getType().value().equals(Filter.Type.STRING.value())) {
                        BoolQueryBuilder qb = QueryBuilders.boolQuery();
                        for (Object s : item.getModel().getModelValues()) {
                            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(item.getColumnName(), s);
                            matchQueryBuilder.analyzer("standard");
                            matchQueryBuilder.fuzzyTranspositions(false);
                            qb.should(matchQueryBuilder);
                        }
                        builder.add(qb);
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
                        BoolQueryBuilder qb = QueryBuilders.boolQuery();
                        for (Object s : item.getModel().getModelValues()) {
                            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(item.getColumnName(), s);
                            matchQueryBuilder.analyzer("standard");
                            matchQueryBuilder.fuzzyTranspositions(false);
                            qb.should(matchQueryBuilder);
                        }
                        builder.add(qb);
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

        // Generated Query
        String[] excludes = null;
        String[] includes = getFieldsInclude(dataFields);
        searchSourceBuilder.fetchSource(includes,excludes);
        setSearchSourceBuilder(searchSourceBuilder);
        return searchSourceBuilder.toString();
    }

    public SearchSourceBuilder getSearchSourceBuilder() throws IOException, ProcessingException {
        buildDataQuery();
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
        { fieldsIncludes[count++] = dataField.getColumnName(); }
        return fieldsIncludes;
    }
}
