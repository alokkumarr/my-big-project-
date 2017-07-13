package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class AxesFieldDataFieldsAvailable {

	public static SearchSourceBuilder rowDataFieldsAvailable(List<com.synchronoss.querybuilder.model.chart.AxesField> axesField,  
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	if (axesField.size()==2)
	    	{
	    	   searchSourceBuilder =    axesDataFieldsAvailableRowFieldTwo(axesField, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	}// end of rowfield.size()==2
	    	if (axesField.size()==3)
	    	{
		    	   searchSourceBuilder = axesDataFieldsAvailableRowFieldThree(axesField, dataFields, searchSourceBuilder, boolQueryBuilder);	    		
	    	}	
	    return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldTwo(List<com.synchronoss.querybuilder.model.chart.AxesField> axesFields, 
	    List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, 
	    SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesFields.get(0).getColumnName())
							.subAggregation(AggregationBuilders.terms("y_axis").field(axesFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    							));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))		
	    							));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    							));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    							));			
				}// dataFields.size() == 5
			} // !dataFields.isEmpty()
		
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldThree(List<com.synchronoss.querybuilder.model.chart.AxesField> axesfields, 
	        List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, 
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesfields.get(0).getColumnName())
							.subAggregation(AggregationBuilders.terms("y_axis").field(axesfields.get(1).getColumnName())
							.subAggregation(AggregationBuilders.terms("z_axis").field(axesfields.get(2).getColumnName())		
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    							)));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("z_axis").field(axesfields.get(2).getColumnName())        
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					)));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("z_axis").field(axesfields.get(2).getColumnName())        
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))		
	    							)));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("z_axis").field(axesfields.get(2).getColumnName())        
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    							)));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("x_axis").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("y_axis").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("z_axis").field(axesfields.get(2).getColumnName())        
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    							)));			
				}// dataFields.size() == 5
			} // !dataFields.isEmpty()
		return searchSourceBuilder;
	}
}
