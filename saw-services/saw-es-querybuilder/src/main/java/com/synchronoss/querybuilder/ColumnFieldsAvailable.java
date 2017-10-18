package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class ColumnFieldsAvailable {

	public static SearchSourceBuilder columnFieldsAvailable(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
	    List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
			 SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	if (columnFields.size()==1)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder)
				        .aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"));
	
	    	} // end of columnFields.size()==1
	    	
	    	
	    	if (columnFields.size()==2)
	    	{
				searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1),  "column_level_2")		

    							));

	    	}// end of columnFields.size()==2
	    	
	    	if (columnFields.size()==3)
	    	{
				searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1),  "column_level_2")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2),  "column_level_3")			
    							)));
	    		
	    	}// end of columnFields.size()==3
	    	if (columnFields.size()==4)
	    	{
				searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1),  "column_level_2")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2),  "column_level_3")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3),  "column_level_4")
    							))));

	    	} // end of columnFields.size()==4
	    	
	    	if (columnFields.size()==5)
	    	{
				searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2),  "column_level_3")
    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3),  "column_level_4")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(4),  "column_level_5")	    	    							
    							)))));

	    	} // end of columnFields.size()==5
	    	
	    return searchSourceBuilder;
	}
}
