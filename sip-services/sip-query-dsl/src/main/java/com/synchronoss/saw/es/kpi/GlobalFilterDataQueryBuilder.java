package com.synchronoss.saw.es.kpi;

import static com.synchronoss.saw.es.ElasticSearchQueryBuilder.buildBooleanQuery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.saw.es.QueryBuilderUtil;

import com.synchronoss.saw.model.SipQuery.BooleanCriteria;
import com.synchronoss.saw.model.globalfilter.Filter;
import com.synchronoss.saw.model.globalfilter.Filter.Order;
import com.synchronoss.saw.model.globalfilter.Filter.Type;
import com.synchronoss.saw.model.globalfilter.GlobalFilter;
import com.synchronoss.saw.model.globalfilter.GlobalFilterExecutionObject;
import com.synchronoss.saw.model.globalfilter.GlobalFilters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class GlobalFilterDataQueryBuilder {

    private static final String GLOBAL_FILTER_VALUES= "global_filter_values";
    private static final Logger logger = LoggerFactory.getLogger(
            GlobalFilterDataQueryBuilder.class);

    /**
     * This method is used to generate the query to build elastic search query for<br/>
     * global filter data set
     *
     * @return query
     * @throws IOException
     * @throws JsonProcessingException
     * @throws ProcessingException
     */
    public List<GlobalFilterExecutionObject> buildQuery(GlobalFilters globalFilters,
        SipDskAttribute dskAttribute) {

        List<GlobalFilterExecutionObject> executionObjectList = new ArrayList<>();
        int size = 0;

        for(GlobalFilter globalFilter :globalFilters.getGlobalFilterList()) {
            GlobalFilterExecutionObject globalFilterExecutionObject = new GlobalFilterExecutionObject();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.size(size);

            if (globalFilter.getFilters() == null) {
                throw new NullPointerException(
                        "Please add filter[] block.It can be empty but these blocks are important.");
            }

          List<Filter> filters = globalFilter.getFilters();
          final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
          AggregationBuilder aggregationBuilder = null;
          BoolQueryBuilder boolQueryBuilderDsk;
          if (dskAttribute != null && dskAttribute.getBooleanCriteria() != null && !CollectionUtils
              .isEmpty(dskAttribute.getBooleanQuery())) {
            boolQueryBuilderDsk = QueryBuilderUtil.queryDSKBuilder(dskAttribute);
            boolQueryBuilder.must(boolQueryBuilderDsk);
          }

      for (Filter item : filters) {
        List<AggregationBuilder> aggregationBuilders = filterAggregationBuilder(item);
        for (AggregationBuilder aggregationBuilder1 : aggregationBuilders) {
          if (item.getType() == Type.STRING) {
            if (aggregationBuilder == null) {
              aggregationBuilder = aggregationBuilder1;
            } else {
              aggregationBuilder.subAggregation(aggregationBuilder1);
            }
          } else if (aggregationBuilder == null) {
            aggregationBuilder = AggregationBuilders.global(GLOBAL_FILTER_VALUES);
            aggregationBuilder.subAggregation(aggregationBuilder1);
          } else {
            aggregationBuilder.subAggregation(aggregationBuilder1);
          }
        }
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
        BucketOrder sortOrder =
                (filter.getOrder()!=null && filter.getOrder().equals(Order.ASC.name())) ? BucketOrder.key(true) : BucketOrder.key(false);
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
