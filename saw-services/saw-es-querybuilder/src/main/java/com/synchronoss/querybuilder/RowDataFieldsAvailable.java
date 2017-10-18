package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class RowDataFieldsAvailable {

	public static SearchSourceBuilder rowDataFieldsAvailable(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	if (rowfield.size()==1)
	    	{
	    		searchSourceBuilder = rowDataFieldsAvailableRowFieldOne(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);	
	    	} // end of rowfield.size()==1
	    	
	    	
	    	if (rowfield.size()==2)
	    	{
	    	   searchSourceBuilder = rowDataFieldsAvailableRowFieldTwo(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	}// end of rowfield.size()==2
	    	
	    	if (rowfield.size()==3)
	    	{
		    	   searchSourceBuilder = rowDataFieldsAvailableRowFieldThree(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);	    		
	    		
	    	}// end of rowfield.size()==3
	    	if (rowfield.size()==4)
	    	{
	    		searchSourceBuilder = rowDataFieldsAvailableRowFieldFour(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	} // end of rowfield.size()==4
	    	
	    	if (rowfield.size()==5)
	    	{
	    		searchSourceBuilder = rowDataFieldsAvailableRowFieldFive(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	} // end of rowfield.size()==5
	    	
	    return searchSourceBuilder;
	}
	
	
	private static AggregationBuilder addSubaggregation(
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, AggregationBuilder aggregation) {
		for (int i = 0; i < dataFields.size(); i++) {
			aggregation = aggregation
					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(i)));
		}
		return aggregation;
	}
	
	private static SearchSourceBuilder rowDataFieldsAvailableRowFieldOne(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, 
	    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0), "row_level_1")));
			} 
			// !dataFields.isEmpty()
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder rowDataFieldsAvailableRowFieldTwo(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, 
	    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, 
	    SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if ((!dataFields.isEmpty()) && dataFields.size() >0)
		{
				searchSourceBuilder.query(boolQueryBuilder)
				.aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1").subAggregation(
						addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1), "row_level_2"))));
		} 	
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder rowDataFieldsAvailableRowFieldThree(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, 
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if ((!dataFields.isEmpty()) && dataFields.size() >0)
		{
				searchSourceBuilder.query(boolQueryBuilder)
				.aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1").
						subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2").subAggregation(
						addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2), "row_level_3")))));
		} 	
		return searchSourceBuilder;
	}

	private static SearchSourceBuilder rowDataFieldsAvailableRowFieldFour(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, 
	    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if ((!dataFields.isEmpty()) && dataFields.size() >0)
		{
				searchSourceBuilder.query(boolQueryBuilder)
				.aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1").
						subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2").
								subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3").subAggregation(
						addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3), "row_level_4"))))));
		} 	
		

		
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder rowDataFieldsAvailableRowFieldFive(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, 
	    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if ((!dataFields.isEmpty()) && dataFields.size() >0)
		{
				searchSourceBuilder.query(boolQueryBuilder)
				.aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1").
						subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2").
								subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3")
										.subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4")
										.subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4), "row_level_5")))))));
		} 	
		return searchSourceBuilder;
	}

	
}
