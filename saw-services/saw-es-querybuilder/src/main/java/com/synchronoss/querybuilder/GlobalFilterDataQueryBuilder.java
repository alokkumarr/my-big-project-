package com.synchronoss.querybuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.globalfilter.Filter;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilter;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilterExecutionObject;
import com.synchronoss.querybuilder.model.globalfilter.GlobalFilters;

public class GlobalFilterDataQueryBuilder {

    private static final String GLOBAL_FILTER_VALUES= "global_filter_values";
    private static final Logger logger = LoggerFactory.getLogger(
            GlobalFilterDataQueryBuilder.class);

    String jsonString;

    public GlobalFilterDataQueryBuilder(String jsonString) {
        super();
        this.jsonString = jsonString;
    }
    public String getJsonString() {
        return jsonString;
    }
    /**
     * This method is used to generate the query to build elastic search query for<br/>
     * global filter data set
     *
     * @return query
     * @throws IOException
     * @throws JsonProcessingException
     * @throws ProcessingException
     */
    public List<GlobalFilterExecutionObject> buildQuery() throws IOException, ProcessingException {

        List<GlobalFilterExecutionObject> executionObjectList = new ArrayList<>();
        GlobalFilters globalFilterNode =
                BuilderUtil.getNodeTreeGlobalFilters (getJsonString(), "globalFilters");
        int size = 0;

        for(GlobalFilter globalFilter :globalFilterNode.getGlobalFilterList()) {
            GlobalFilterExecutionObject globalFilterExecutionObject = new GlobalFilterExecutionObject();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(size);

            if (globalFilter.getFilters() == null) {
                throw new NullPointerException(
                        "Please add filter[] block.It can be empty but these blocks are important.");
            }

            List<com.synchronoss.querybuilder.model.globalfilter.Filter> filters = globalFilter.getFilters();

            final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            AggregationBuilder aggregationBuilder = AggregationBuilders.global(GLOBAL_FILTER_VALUES);
            for (com.synchronoss.querybuilder.model.globalfilter.Filter item : filters) {
             List<AggregationBuilder> aggregationBuilders = filterAggregationBuilder(item);
            for(AggregationBuilder aggregationBuilder1: aggregationBuilders)
                aggregationBuilder.subAggregation(aggregationBuilder1);
            }
            searchSourceBuilder.aggregation(aggregationBuilder);
            searchSourceBuilder.query(boolQueryBuilder);
            globalFilterExecutionObject.setEsRepository(globalFilter.getEsRepository());
            globalFilterExecutionObject.setSearchSourceBuilder(searchSourceBuilder);
            globalFilterExecutionObject.setGlobalFilter(globalFilter);
            executionObjectList.add(globalFilterExecutionObject);
        }
        return executionObjectList;

    }

    private List<AggregationBuilder> filterAggregationBuilder(Filter
                                                                 filter)
    {
        List<AggregationBuilder> aggregationBuilderList = new ArrayList<>();
        AggregationBuilder aggregationBuilder = null;
        AggregationBuilder aggregationBuilder1 = null;
        Terms.Order sortOrder =
                (filter.getOrder()!=null && filter.getOrder().equals(SortOrder.ASC.name())) ? Terms.Order.term(true) : Terms.Order.term(false);
            switch(filter.getType())
            {
                case INT:
                   aggregationBuilder = AggregationBuilders.min(filter.getColumnName()+"_min").field(filter.getColumnName());
                    aggregationBuilder1=  AggregationBuilders.max(filter.getColumnName()+"_max")
                            .field(filter.getColumnName());
                    aggregationBuilderList.add(aggregationBuilder);
                    aggregationBuilderList.add(aggregationBuilder1);
                    break;
                case LONG:
                    aggregationBuilder = AggregationBuilders.min(filter.getColumnName()+"_min").field(filter.getColumnName());
                    aggregationBuilder1=  AggregationBuilders.max(filter.getColumnName()+"_max")
                            .field(filter.getColumnName());
                    aggregationBuilderList.add(aggregationBuilder);
                    aggregationBuilderList.add(aggregationBuilder1);
                    break;
                case FLOAT:
                    aggregationBuilder = AggregationBuilders.min(filter.getColumnName()+"_min").field(filter.getColumnName());
                    aggregationBuilder1=  AggregationBuilders.max(filter.getColumnName()+"_max")
                            .field(filter.getColumnName());
                    aggregationBuilderList.add(aggregationBuilder);
                    aggregationBuilderList.add(aggregationBuilder1);
                    break;
                case DOUBLE:
                    aggregationBuilder = AggregationBuilders.min(filter.getColumnName()+"_min").field(filter.getColumnName());
                    aggregationBuilder1=  AggregationBuilders.max(filter.getColumnName()+"_max")
                            .field(filter.getColumnName());
                    aggregationBuilderList.add(aggregationBuilder);
                    aggregationBuilderList.add(aggregationBuilder1);
                    break;
                case DATE:
                    aggregationBuilder = AggregationBuilders.min(filter.getColumnName()+"_min").field(filter.getColumnName());
                    aggregationBuilder1=  AggregationBuilders.max(filter.getColumnName()+"_max")
                            .field(filter.getColumnName());
                    aggregationBuilderList.add(aggregationBuilder);
                    aggregationBuilderList.add(aggregationBuilder1);
                    break;
                case TIMESTAMP:
                    aggregationBuilder = AggregationBuilders.min(filter.getColumnName()+"_min").field(filter.getColumnName());
                    aggregationBuilder1=  AggregationBuilders.max(filter.getColumnName()+"_max")
                            .field(filter.getColumnName());
                    aggregationBuilderList.add(aggregationBuilder);
                    aggregationBuilderList.add(aggregationBuilder1);
                    break;
                case STRING:
                    aggregationBuilder = AggregationBuilders.terms(filter.getColumnName())
                            .field(filter.getColumnName()).size(filter.getSize()).order(sortOrder);
                    aggregationBuilderList.add(aggregationBuilder);
                    break;
                    default:
                        aggregationBuilder = AggregationBuilders.terms(filter.getColumnName())
                                .field(filter.getColumnName()).order(sortOrder).size(filter.getSize());
                        aggregationBuilderList.add(aggregationBuilder);
            }
       return aggregationBuilderList;
    }
}
