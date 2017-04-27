package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.synchronoss.querybuilder.model.ColumnField;
import com.synchronoss.querybuilder.model.DataField;
import com.synchronoss.querybuilder.model.RowField;

class RowFieldsAvailable {

	public static SearchSourceBuilder rowFieldsAvailable(List<RowField> rowfield, List<ColumnField> columnFields, List<DataField> dataFields,
			 SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	if (rowfield.size()==1)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("row_level_1").field(rowfield.get(0).getColumnName()));	
	    	} // end of rowfield.size()==1
	    	
	    	
	    	if (rowfield.size()==2)
	    	{
	    	   searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("row_level_1").field(rowfield.get(0).getColumnName())
						.subAggregation(AggregationBuilders.terms("row_level_2").field(rowfield.get(1).getColumnName())));

	    	}// end of rowfield.size()==2
	    	
	    	if (rowfield.size()==3)
	    	{
		    	   searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("row_level_1").field(rowfield.get(0).getColumnName())
							.subAggregation(AggregationBuilders.terms("row_level_2").field(rowfield.get(1).getColumnName())
									.subAggregation(AggregationBuilders.terms("row_level_3").field(rowfield.get(2).getColumnName()))));
	    		
	    	}// end of rowfield.size()==3
	    	if (rowfield.size()==4)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("row_level_1").field(rowfield.get(0).getColumnName())
						.subAggregation(AggregationBuilders.terms("row_level_2").field(rowfield.get(1).getColumnName())
								.subAggregation(AggregationBuilders.terms("row_level_3").field(rowfield.get(2).getColumnName())	
										.subAggregation(AggregationBuilders.terms("row_level_4").field(rowfield.get(2).getColumnName())))));

	    	} // end of rowfield.size()==4
	    	
	    	if (rowfield.size()==5)
	    	{
	    		searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("row_level_1").field(rowfield.get(0).getColumnName())
						.subAggregation(AggregationBuilders.terms("row_level_2").field(rowfield.get(1).getColumnName())
						.subAggregation(AggregationBuilders.terms("row_level_3").field(rowfield.get(2).getColumnName())	
								.subAggregation(AggregationBuilders.terms("row_level_4").field(rowfield.get(2).getColumnName())
										.subAggregation(AggregationBuilders.terms("row_level_5").field(rowfield.get(2).getColumnName())		
    							)))));

	    	} // end of rowfield.size()==5
	    	
	    return searchSourceBuilder;
	}
}
