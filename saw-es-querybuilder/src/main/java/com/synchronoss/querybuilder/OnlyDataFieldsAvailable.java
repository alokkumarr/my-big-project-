package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class OnlyDataFieldsAvailable {

	public static SearchSourceBuilder dataFieldsAvailable( 
			List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
	{
	    	
      if (dataFields.size()==1)
      {
          searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)));
      }// dataFields.size() == 1

      if (dataFields.size()==2)
      {
          searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)));
          
      }// dataFields.size() == 2

      if (dataFields.size()==3)
      {
          searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)));
          
      }// dataFields.size() == 3
      
      if (dataFields.size()==4)
      {
          searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)));
          
      }// dataFields.size() == 4
      
      if (dataFields.size()==5)
      {
          searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(0)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(1)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(2)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(3)))
                  .aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(4)));          
      }// dataFields.size() == 5
	    return searchSourceBuilder;
	}
}
