package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class ColumnDataFieldsAvailable {

	public static SearchSourceBuilder columnDataFieldsAvailable(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	if (columnFields.size()==1)
	    	{
	    		searchSourceBuilder = columnDataFieldsAvailableRowFieldOne(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);	
	    	} // end of rowfield.size()==1
	    	
	    	
	    	if (columnFields.size()==2)
	    	{
	    	   searchSourceBuilder = columnDataFieldsAvailableRowFieldTwo(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	}// end of rowfield.size()==2
	    	
	    	if (columnFields.size()==3)
	    	{
		    	   searchSourceBuilder = columnDataFieldsAvailableRowFieldThree(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);	    		
	    		
	    	}// end of rowfield.size()==3
	    	if (columnFields.size()==4)
	    	{
	    		searchSourceBuilder = columnDataFieldsAvailableRowFieldFour(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	} // end of rowfield.size()==4
	    	
	    	if (columnFields.size()==5)
	    	{
	    		searchSourceBuilder = columnDataAvailableRowFieldFive(rowfield, columnFields, dataFields, searchSourceBuilder, boolQueryBuilder);
	    	} // end of rowfield.size()==5
	    	
	    return searchSourceBuilder;
	}
	
	
	
	private static SearchSourceBuilder columnDataFieldsAvailableRowFieldOne(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if (columnFields.size()==1)
		{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder)
					        .aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    							);
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					);
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))		
	    							);
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    							);
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4)))
	    							);			
				}// dataFields.size() == 5
			} // !dataFields.isEmpty()
		} // end of columnFields.size()==1
		
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder columnDataFieldsAvailableRowFieldTwo(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if (columnFields.size()==2)
		{
		if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")		
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    							));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")		
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")	    	    							
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))		
	    							));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    							));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4)))
	    							));			
				}// dataFields.size() == 5
			}	// !dataFields.isEmpty()
		}  // columnFields.size() ==2
		
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder columnDataFieldsAvailableRowFieldThree(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if (columnFields.size()==3)
		{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")			
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    							)));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    							.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")			
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					)));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3") 					
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))		
	    							)));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")			
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    							)));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")			
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4)))
	    							)));			
				}// dataFields.size() == 5
			}	// !dataFields.isEmpty()
		} // end of columnFields.size()==3
		
		return searchSourceBuilder;
	}

	private static SearchSourceBuilder columnDataFieldsAvailableRowFieldFour(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if (columnFields.size()==4)
		{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    							))));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")				
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					))));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3") 	
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")							
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))		
	    							))));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")				
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    							))));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")		
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4)))
	    							))));			
				}// dataFields.size() == 5
			}	// !dataFields.isEmpty()
		  }	 // end of columnFields.size()==4
		
		return searchSourceBuilder;
	}
	
	private static SearchSourceBuilder columnDataAvailableRowFieldFive(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, 
	    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields, 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	
		if (columnFields.size()==5)
		{
			if ((!dataFields.isEmpty()) && dataFields.size() >0)
			{
				if (dataFields.size()==1)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")
   	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(4), dataFields, "column_level_5")	    	    							
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    							)))));
				}// dataFields.size() == 1

				if (dataFields.size()==2)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    					.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")				
   	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(4), dataFields, "column_level_5")	    	    							
   							.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					)))));
	    			
				}// dataFields.size() == 2

				if (dataFields.size()==3)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3") 	
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")	
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(4), dataFields, "column_level_5")					
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))		
	    							)))));
	    			
				}// dataFields.size() == 3
				
				if (dataFields.size()==4)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")
	    	    	    	.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(4), dataFields, "column_level_5")	    	    	    					
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    							)))));
	    			
				}// dataFields.size() == 4
				
				if (dataFields.size()==5)
				{
					searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields, "column_level_1")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields, "column_level_2")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields, "column_level_3")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields, "column_level_4")
	    	    			.subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(4), dataFields, "column_level_5")					
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
	    					.subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4)))
	    							)))));			
				}// dataFields.size() == 5
			}	// !dataFields.isEmpty()
		  }	 // end of columnFields.size()==5
		
		return searchSourceBuilder;
	}

	
}
