package com.synchronoss.querybuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.DynamicConvertor;
import com.synchronoss.querybuilder.model.kpi.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KPIDataQueryBuilder {

    private static final String KPI_VALUES= "kpi_values";
    private static final Logger logger = LoggerFactory.getLogger(
        KPIDataQueryBuilder.class);
    private final static String DATE_FORMAT="yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";

    String jsonString;

    public KPIDataQueryBuilder(String jsonString) {
        super();
        this.jsonString = jsonString;
    }
    public String getJsonString() {
        return jsonString;
    }
    /**
     * This method is used to generate the query to build elastic search query for<br/>
     * KPI builder data set
     *
     * @return query
     * @throws IOException
     * @throws JsonProcessingException
     * @throws ProcessingException
     */
    public KPIExecutionObject buildQuery() throws IOException, ProcessingException {

        KPIBuilder kpiBuilder = BuilderUtil.getNodeTreeKPIBuilder(getJsonString());
            if (kpiBuilder.getKpi().getFilters() == null) {
                throw new NullPointerException(
                    "Please add filter[] block.It can be empty but these blocks are important.");
            }
        KPIExecutionObject kpiExecutionObject = new KPIExecutionObject();
        kpiExecutionObject.setCurrentSearchSourceBuilder(buildKPIQuery(kpiBuilder,true));
        kpiExecutionObject.setPriorSearchSourceBuilder(buildKPIQuery(kpiBuilder,false));
        kpiExecutionObject.setEsRepository(kpiBuilder.getKpi().getEsRepository());
        kpiExecutionObject.setDataFields(kpiBuilder.getKpi().getDataFields());
        return kpiExecutionObject;
    }

    /**
     *
     * @param kpiBuilder
     * @param current
     * @return
     */
    private SearchSourceBuilder buildKPIQuery(KPIBuilder kpiBuilder, boolean current)
    {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        List<com.synchronoss.querybuilder.model.kpi.Filter> filters = kpiBuilder.getKpi().getFilters();
        List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        for (com.synchronoss.querybuilder.model.kpi.Filter item : filters)
        {
            if (item.getType().value().equals(Filter.Type.DATE.value()) || item.getType().value().equals(Filter.Type.TIMESTAMP.value())) {
                if (item.getModel().getPreset()!=null && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
                {  DynamicConvertor dynamicConvertor = null;
                    if(current)
                     dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
                    else
                        dynamicConvertor = BuilderUtil.dynamicDecipherForPrior(item.getModel().getPreset().value());
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
                builder = QueryBuilderUtil.stringFilterKPI(item, builder);
            }
            if ((item.getType().value().toLowerCase().equals(Filter.Type.DOUBLE.value().toLowerCase()) || item
                .getType().value().toLowerCase().equals(Filter.Type.INT.value().toLowerCase()))
                || item.getType().value().toLowerCase().equals(Filter.Type.FLOAT.value().toLowerCase())
                || item.getType().value().toLowerCase().equals(Filter.Type.LONG.value().toLowerCase())) {
                builder = QueryBuilderUtil.numericFilterKPI(item, builder);
            }
        }
        builder.forEach(item -> {
            boolQueryBuilder.must(item);
        });
        searchSourceBuilder.query(boolQueryBuilder);
        List<DataField> dataFields = kpiBuilder.getKpi().getDataFields();
        for(DataField dataField : dataFields) {
            searchSourceBuilder = QueryBuilderUtil.aggregationBuilderDataFieldKPI(
                dataField, searchSourceBuilder);
        }
     return searchSourceBuilder;
    }
}
