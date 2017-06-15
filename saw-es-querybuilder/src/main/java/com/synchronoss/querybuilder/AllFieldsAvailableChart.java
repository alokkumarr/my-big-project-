package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class AllFieldsAvailableChart {

	public static SearchSourceBuilder allFieldsAvailable(com.synchronoss.querybuilder.model.chart.GroupBy groupBy, com.synchronoss.querybuilder.model.chart.SplitBy splitBy, 
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	
	{
		if (!dataFields.isEmpty() && dataFields.size() <=5){
	    	if (dataFields.size()==1)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
				                        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
				        		        .subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)
				        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0))
				        						
				        						)));

	    	} // end of dataFields.size()==1
	    	
	    	if (dataFields.size()==2)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						                .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
				        		        .subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)
				        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
				        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))));

	    	} // end of dataFields.size()==2
	    	
	    	if (dataFields.size()==3)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
		        		        .subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
		        				
		        		        		));
	    		
	    	}// end of dataFields.size()==3
	    	
	    	if (dataFields.size()==4)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
		        		        .subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))		
		        						));

	    	} // end of dataFields.size()==4
	    	
	    	if (dataFields.size()==5)
	    	{
				searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
						        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
		        		        .subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
		        				.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
		        				));
	    	} // end of dataFields.size()==5
		}
		else 
		{
			throw new IllegalArgumentException("dataField size is greater than 5 levels");
		}
	    return searchSourceBuilder;
	}
	
}
