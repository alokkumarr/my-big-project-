package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class AllFieldsAvailablePivot {

  public static SearchSourceBuilder allFieldsAvailable(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
    if (rowfield.size() == 1) {
      searchSourceBuilder =
          allFieldsAvailableRowFieldOne(rowfield, columnFields, dataFields, searchSourceBuilder,
              boolQueryBuilder);
    } // end of rowfield.size()==1


    if (rowfield.size() == 2) {
      searchSourceBuilder =
          allFieldsAvailableRowFieldTwo(rowfield, columnFields, dataFields, searchSourceBuilder,
              boolQueryBuilder);
    }// end of rowfield.size()==2

    if (rowfield.size() == 3) {
      searchSourceBuilder =
          allFieldsAvailableRowFieldThree(rowfield, columnFields, dataFields, searchSourceBuilder,
              boolQueryBuilder);

    }// end of rowfield.size()==3
    if (rowfield.size() == 4) {
      searchSourceBuilder =
          allFieldsAvailableRowFieldFour(rowfield, columnFields, dataFields, searchSourceBuilder,
              boolQueryBuilder);
    } // end of rowfield.size()==4

    if (rowfield.size() == 5) {
      searchSourceBuilder =
          allFieldsAvailableRowFieldFive(rowfield, columnFields, dataFields, searchSourceBuilder,
              boolQueryBuilder);
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
  
  private static SearchSourceBuilder allFieldsAvailableRowFieldOne(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
          searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
                  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
          searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
                  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))));
      } 
    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
          searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
                  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))));

    	  
      }
    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
          searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_3"))
                  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(4), "column_level_4"))));
    } // end of columnFields.size()==4

    }
    if (columnFields.size() == 5) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        
    	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))
                  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(4), "column_level_5"))));
    	  
 
    } // end of columnFields.size()==5
    }
    
    return searchSourceBuilder;
  }

  private static SearchSourceBuilder allFieldsAvailableRowFieldTwo(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_1"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
    	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))));

      } // !dataFields.isEmpty()
    } // end of columnFields.size()==2
  

    if (columnFields.size() == 3) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
       	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))));
    } // end of columnFields.size()==3
    }
    if (columnFields.size() == 4) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) { 
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))));
    } // end of columnFields.size()==4
    }

    if (columnFields.size() == 5) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(4), "column_level_5"))));
    } // end of columnFields.size()==5
    }
    return searchSourceBuilder;
  }

  private static SearchSourceBuilder allFieldsAvailableRowFieldThree(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))));
    } // end of columnFields.size()==1
    }

    if (columnFields.size() == 2) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))));
    } // end of columnFields.size()==2
    }
    
    if (columnFields.size() == 3) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))));
    } // end of columnFields.size()==3
    }
      
      if (columnFields.size() == 4) {
          if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
          	  searchSourceBuilder.query(boolQueryBuilder)
              .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
            		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
            		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
            		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
            		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
            		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
            		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))));
        } // end of columnFields.size()==4  
      }

    if (columnFields.size() == 5) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(4), "column_level_5"))));

      }
    } // end of columnFields.size()==5
    
    return searchSourceBuilder;
  }

  private static SearchSourceBuilder allFieldsAvailableRowFieldFour(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	  
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_2"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))));

      } // !dataFields.isEmpty()
    } // end of columnFields.size()==3

    
    if (columnFields.size() == 4) {
        if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
          
        	  searchSourceBuilder.query(boolQueryBuilder)
            .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
          		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
          		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
          		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
          		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
          		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
          		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
          		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))));

        } // !dataFields.isEmpty()
      } // end of columnFields.size()==4


    if (columnFields.size() == 5) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
    	 
    	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(4), "column_level_5"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }

  private static SearchSourceBuilder allFieldsAvailableRowFieldFive(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4),"row_level_5"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
 
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4),"row_level_5"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_1"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4),"row_level_5"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_3"))));
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4),"row_level_5"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))));
    } // end of columnFields.size()==4

    }
    if (columnFields.size() == 5) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
       
      	  searchSourceBuilder.query(boolQueryBuilder)
          .aggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(0),"row_level_1")
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(1),"row_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(2),"row_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(3),"row_level_4"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilderRow(rowfield.get(4),"row_level_5"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(0), "column_level_1"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(1), "column_level_2"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(2), "column_level_3"))
        		  .subAggregation(QueryBuilderUtil.aggregationBuilder(columnFields.get(3), "column_level_4"))
        		  .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilder(columnFields.get(4), "column_level_5"))));

      } // !dataFields.isEmpty()
    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }


}

