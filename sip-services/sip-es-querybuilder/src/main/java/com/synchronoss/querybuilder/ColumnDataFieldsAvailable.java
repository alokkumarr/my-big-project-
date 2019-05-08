package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class ColumnDataFieldsAvailable {

    public static SearchSourceBuilder columnDataFieldsAvailable(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
                                                                List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
                                                                List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
                                                                SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
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

    private static AggregationBuilder addSubaggregation(
        List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, AggregationBuilder aggregation) {
        for (int i = 0; i < dataFields.size(); i++) {
            aggregation = aggregation
                .subAggregation(QueryBuilderUtil.aggregationBuilderDataField(dataFields.get(i)));
        }
        return aggregation;
    }


    private static SearchSourceBuilder columnDataFieldsAvailableRowFieldOne(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
                                                                            List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,  SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder){

        if (columnFields.size() == 1) {
            if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
                searchSourceBuilder.query(boolQueryBuilder).aggregation(addSubaggregation(dataFields,
                    QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(0), "column_level_1")));
            }
        }
        return searchSourceBuilder;
    }

    private static SearchSourceBuilder columnDataFieldsAvailableRowFieldTwo(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
                                                                            List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder){
        if (columnFields.size() == 2) {
            if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
                searchSourceBuilder.query(boolQueryBuilder)
                    .aggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(0), "column_level_1")
                        .subAggregation(addSubaggregation(dataFields,
                            QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(1), "column_level_2"))));
            }
        }
        return searchSourceBuilder;
    }

    private static SearchSourceBuilder columnDataFieldsAvailableRowFieldThree(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
                                                                              List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder){

        if (columnFields.size() == 3) {
            if ((!dataFields.isEmpty()) && dataFields.size() > 0) {

                searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil
                    .aggregationBuilderColumn(columnFields.get(0), "column_level_1")
                    .subAggregation(QueryBuilderUtil
                        .aggregationBuilderColumn(columnFields.get(1), "column_level_2")
                        .subAggregation(addSubaggregation(dataFields,
                            QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(2), "column_level_3")))));
            }
        } // end of columnFields.size()==3

        return searchSourceBuilder;
    }

    private static SearchSourceBuilder columnDataFieldsAvailableRowFieldFour(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield, List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
                                                                             List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
    {

        if (columnFields.size()==4){
            if ((!dataFields.isEmpty()) && dataFields.size() >0)
            {
                searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(0),"column_level_1").
                    subAggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(1),"column_level_2").
                        subAggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(2),"column_level_3").subAggregation(
                            addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(3), "column_level_4"))))));
            }
        }	 // end of columnFields.size()==4
        return searchSourceBuilder;
    }

    private static SearchSourceBuilder columnDataAvailableRowFieldFive(List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
                                                                       List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
                                                                       List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,  SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder)
    {
        if (columnFields.size()==5){
            if ((!dataFields.isEmpty()) && dataFields.size() >0){
                searchSourceBuilder.query(boolQueryBuilder).aggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(0),"column_level_1").
                    subAggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(1),"column_level_2").
                        subAggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(2),"column_level_3")
                            .subAggregation(QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(3),"column_level_4")
                                .subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderColumn(columnFields.get(4), "column_level_5")))))));
            }
        }	 // end of columnFields.size()==5
        return searchSourceBuilder;
    }

}
