package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class AxesFieldDataFieldsAvailable {

	public static SearchSourceBuilder rowDataFieldsAvailable(List<com.synchronoss.querybuilder.model.chart.NodeField> nodeField,  
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
      	  if (nodeField.size()==1)
            {
               searchSourceBuilder =    axesDataFieldsAvailableRowFieldOne(nodeField, dataFields, searchSourceBuilder, boolQueryBuilder);
            }
	        if (nodeField.size()==2)
	    	{
	    	   searchSourceBuilder =    axesDataFieldsAvailableRowFieldTwo(nodeField, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	}
	    	if (nodeField.size()==3)
	    	{
		    	   searchSourceBuilder = axesDataFieldsAvailableRowFieldThree(nodeField, dataFields, searchSourceBuilder, boolQueryBuilder);	    		
	    	}	
	    return searchSourceBuilder;
	}
	
	   private static SearchSourceBuilder axesDataFieldsAvailableRowFieldOne(List<com.synchronoss.querybuilder.model.chart.NodeField> nodeFields, 
	        List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, 
	        SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	    {
	      
 		   if ((!dataFields.isEmpty()) && dataFields.size() >0)
	            {
	                if (dataFields.size()==1)
	                {
	                    searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                                    );
	                }// dataFields.size() == 1

	                if (dataFields.size()==2)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            );
	                    
	                }// dataFields.size() == 2

	                if (dataFields.size()==3)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))       
	                                    );
	                    
	                }// dataFields.size() == 3
	                
	                if (dataFields.size()==4)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                     
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                                    );
	                    
	                }// dataFields.size() == 4
	                
	                if (dataFields.size()==5)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                                    );         
	                }// dataFields.size() == 5
	                
	                if (dataFields.size()==6)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                                    );         
	                }// dataFields.size() == 6
	                
	                if (dataFields.size()==7)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder)
	                  			.aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                                    );         
	                }// dataFields.size() == 7

	                    if (dataFields.size()==8)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                                    );         
	                }// dataFields.size() == 8
	                if (dataFields.size()==9)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                                    );         
	                }// dataFields.size() == 9

	                if (dataFields.size()==10)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	                                    );         
	                }// dataFields.size() == 10

	                if (dataFields.size()==11)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	                                    );         
	                }// dataFields.size() == 11

	                if (dataFields.size()==12)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	                                    );         
	                }// dataFields.size() == 12

	                if (dataFields.size()==13)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	                                    );         
	                }// dataFields.size() == 13

	                if (dataFields.size()==14)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(13)))
	                                    );         
	                }// dataFields.size() == 14
	                
	                if (dataFields.size()==15)
	                {
	                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
	                      
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(13)))
	                            .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(14)))
	                                    );         
	                }// dataFields.size() == 14


	            } // !dataFields.isEmpty()
	        
	        return searchSourceBuilder;
	    }

	
	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldTwo(List<com.synchronoss.querybuilder.model.chart.NodeField> nodeFields, 
	    List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, 
	    SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
							.subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    							));
				}// dataFields.size() == 1

				
				if (dataFields.size()==2)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))		
	    							));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    							));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    							));			
				}// dataFields.size() == 5
				

				if (dataFields.size()==6)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    							));			
				}// dataFields.size() == 6

				if (dataFields.size()==7)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    							));			
				}// dataFields.size() == 7

				if (dataFields.size()==8)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    							));			
				}// dataFields.size() == 8

				if (dataFields.size()==9)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    							));			
				}// dataFields.size() == 9
				if (dataFields.size()==10)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    							));			
				}// dataFields.size() == 10
				if (dataFields.size()==11)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    							));			
				}// dataFields.size() == 11

				if (dataFields.size()==12)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    							));			
				}// dataFields.size() == 12
				if (dataFields.size()==13)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	    							));			
				}// dataFields.size() == 12

				if (dataFields.size()==14)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(13)))
	    							));			
				}// dataFields.size() == 14

				if (dataFields.size()==15)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(nodeFields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(13)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(14)))
	    							));			
				}// dataFields.size() == 14

			} // !dataFields.isEmpty()
		
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldThree(List<com.synchronoss.querybuilder.model.chart.NodeField> axesfields, 
	        List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, 
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
							.subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
							.subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())		
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    							)));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					)));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))		
	    							)));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    							)));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    							)));			
				}// dataFields.size() == 5
				
				if (dataFields.size()==6)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    							)));			
				}// dataFields.size() == 6
				if (dataFields.size()==7)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    							)));			
				}// dataFields.size() == 7
				if (dataFields.size()==8)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    							)));			
				}// dataFields.size() == 8
				if (dataFields.size()==9)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    							)));			
				}// dataFields.size() == 9
				
				if (dataFields.size()==10)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    							)));			
				}// dataFields.size() == 10

				if (dataFields.size()==11)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    							)));			
				}// dataFields.size() == 11
				
				if (dataFields.size()==12)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    							)));			
				}// dataFields.size() == 12

				if (dataFields.size()==13)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	    							)));			
				}// dataFields.size() == 13

				if (dataFields.size()==14)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(13)))
	    							)));			
				}// dataFields.size() == 14

				if (dataFields.size()==15)
				{
                  searchSourceBuilder.query(boolQueryBuilder).aggregation(AggregationBuilders.terms("node_field_1").field(axesfields.get(0).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
                      .subAggregation(AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName())      
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(4)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(5)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(6)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(7)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(8)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(9)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(10)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(11)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(12)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(13)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(14)))
	    							)));			
				}// dataFields.size() == 15

			} // !dataFields.isEmpty()
		return searchSourceBuilder;
	}
}
