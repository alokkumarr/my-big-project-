package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.synchronoss.querybuilder.model.DataField;
import com.synchronoss.querybuilder.model.GroupBy;

class GroupByAndFieldsAvailableChart {

	public static SearchSourceBuilder allFieldsAvailable(GroupBy groupBy, 
			List<DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	
	{
		if (!dataFields.isEmpty() && dataFields.size() <=5){
	    	if (dataFields.size()==1)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
				                        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
				        		        
				        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0))));

	    	} // end of dataFields.size()==1
	    	
	    	if (dataFields.size()==2)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						                .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
				        		        
				        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
				        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1))));

	    	} // end of dataFields.size()==2
	    	
	    	if (dataFields.size()==3)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
		        		        
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2))
		        						));
	    		
	    	}// end of dataFields.size()==3
	    	
	    	if (dataFields.size()==4)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
		        		        
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))		
		        						);

	    	} // end of dataFields.size()==4
	    	
	    	if (dataFields.size()==5)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
		        		        
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4))));
	    	} // end of dataFields.size()==5
		}
		else 
		{
			throw new IllegalArgumentException("dataField size is greater than 5 levels");
		}
	    return searchSourceBuilder;
	}
	
}
