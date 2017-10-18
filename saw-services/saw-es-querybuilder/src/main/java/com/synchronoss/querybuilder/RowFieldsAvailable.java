package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class RowFieldsAvailable {

	public static SearchSourceBuilder rowFieldsAvailable(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
			 SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	if (rowfield.size()==1)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0), "row_level_1"));	
	    	} // end of rowfield.size()==1
	    	
	    	
	    	if (rowfield.size()==2)
	    	{
	    	   searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0), "row_level_1")
						.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1), "row_level_2")));

	    	}// end of rowfield.size()==2
	    	
	    	if (rowfield.size()==3)
	    	{
		    	   searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0), "row_level_1")
							.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1), "row_level_2")
									.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2), "row_level_3"))));
	    		
	    	}// end of rowfield.size()==3
	    	if (rowfield.size()==4)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0), "row_level_1")
						.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1), "row_level_2")
								.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2), "row_level_3")	
										.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3), "row_level_4")))));

	    	} // end of rowfield.size()==4
	    	
	    	if (rowfield.size()==5)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0), "row_level_1")
						.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1), "row_level_2")
						.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2), "row_level_3")	
								.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3), "row_level_4")
										.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4), "row_level_5")	
    							)))));

	    	} // end of rowfield.size()==5
	    	
	    return searchSourceBuilder;
	}
}
