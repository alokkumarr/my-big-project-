package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class RowColumnFieldsAvailable {

  public static SearchSourceBuilder rowColumnFieldsAvailable(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
    if (rowfield.size() == 1) {
      searchSourceBuilder =
          rowColumnFieldsAvailableRowFieldOne(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);
    } // end of rowfield.size()==1


    if (rowfield.size() == 2) {
      searchSourceBuilder =
          rowColumnFieldsAvailableRowFieldTwo(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);
    }// end of rowfield.size()==2

    if (rowfield.size() == 3) {
      searchSourceBuilder =
          rowColumnFieldsAvailableRowFieldThree(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);

    }// end of rowfield.size()==3
    if (rowfield.size() == 4) {
      searchSourceBuilder =
          rowColumnFieldsAvailableRowFieldFour(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);
    } // end of rowfield.size()==4

    if (rowfield.size() == 5) {
      searchSourceBuilder =
          rowColumnFieldsAvailableRowFieldFive(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);
    } // end of rowfield.size()==5

    return searchSourceBuilder;
  }



  private static SearchSourceBuilder rowColumnFieldsAvailableRowFieldOne(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                      "column_level_1")));
    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                      "column_level_1").subAggregation(
                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                          "column_level_2"))));
    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                      "column_level_1").subAggregation(
                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                          "column_level_2").subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields,
                              "column_level_3")))));
    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                      "column_level_1").subAggregation(
                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                          "column_level_2").subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields,
                              "column_level_3").subAggregation(
                              QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields,
                                  "column_level_4"))))));

    } // end of columnFields.size()==4


    if (columnFields.size() == 5) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                      "column_level_1").subAggregation(
                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                          "column_level_2").subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(2), dataFields,
                              "column_level_3").subAggregation(
                              QueryBuilderUtil.aggregationBuilder(columnFields.get(3), dataFields,
                                  "column_level_4").subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(4),
                                      dataFields, "column_level_5")))))));
    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }

  private static SearchSourceBuilder rowColumnFieldsAvailableRowFieldTwo(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                              "column_level_1")

                      )));

    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                              "column_level_1").subAggregation(
                              QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                                  "column_level_2")

                          ))));


    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                              "column_level_1").subAggregation(
                              QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                                  "column_level_2").subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(2),
                                      dataFields, "column_level_3")

                              )))));

    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                              "column_level_1").subAggregation(
                              QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                                  "column_level_2").subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(2),
                                      dataFields, "column_level_3").subAggregation(
                                      QueryBuilderUtil.aggregationBuilder(columnFields.get(3),
                                          dataFields, "column_level_4")

                                  ))))));

    } // end of columnFields.size()==4


    if (columnFields.size() == 5) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0), dataFields,
                              "column_level_1").subAggregation(
                              QueryBuilderUtil.aggregationBuilder(columnFields.get(1), dataFields,
                                  "column_level_2").subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(2),
                                      dataFields, "column_level_3").subAggregation(
                                      QueryBuilderUtil.aggregationBuilder(columnFields.get(3),
                                          dataFields, "column_level_4").subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(4),
                                              dataFields, "column_level_5")

                                      )))))));

    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }

  private static SearchSourceBuilder rowColumnFieldsAvailableRowFieldThree(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                      dataFields, "column_level_1")

                              ))));

    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                      dataFields, "column_level_1").subAggregation(
                                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1),
                                          dataFields, "column_level_2")

                                  )))));

    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                      dataFields, "column_level_1").subAggregation(
                                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1),
                                          dataFields, "column_level_2").subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(2),
                                              dataFields, "column_level_3")

                                      ))))));

    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {
      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                      dataFields, "column_level_1").subAggregation(
                                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1),
                                          dataFields, "column_level_2").subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(2),
                                              dataFields, "column_level_3")
                                              .subAggregation(
                                                  QueryBuilderUtil.aggregationBuilder(
                                                      columnFields.get(3), dataFields,
                                                      "column_level_4")

                                              )))))));



    } // end of columnFields.size()==4


    if (columnFields.size() == 5) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                      dataFields, "column_level_1").subAggregation(
                                      QueryBuilderUtil.aggregationBuilder(columnFields.get(1),
                                          dataFields, "column_level_2").subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(2),
                                              dataFields, "column_level_3").subAggregation(
                                              QueryBuilderUtil
                                                  .aggregationBuilder(columnFields.get(3),
                                                      dataFields, "column_level_4").subAggregation(
                                                      QueryBuilderUtil.aggregationBuilder(
                                                          columnFields.get(4), dataFields,
                                                          "column_level_5")

                                                  ))))))));

    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }

  private static SearchSourceBuilder rowColumnFieldsAvailableRowFieldFour(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_4")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                              dataFields, "column_level_1")

                                      )))));

    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_4")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                              dataFields, "column_level_1")
                                              .subAggregation(
                                                  QueryBuilderUtil.aggregationBuilder(
                                                      columnFields.get(1), dataFields,
                                                      "column_level_2")

                                              ))))));

    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_4")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                              dataFields, "column_level_1").subAggregation(
                                              QueryBuilderUtil
                                                  .aggregationBuilder(columnFields.get(1),
                                                      dataFields, "column_level_2").subAggregation(
                                                      QueryBuilderUtil.aggregationBuilder(
                                                          columnFields.get(2), dataFields,
                                                          "column_level_3")

                                                  )))))));

    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_4")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                              dataFields, "column_level_1").subAggregation(
                                              QueryBuilderUtil
                                                  .aggregationBuilder(columnFields.get(1),
                                                      dataFields, "column_level_2").subAggregation(
                                                      QueryBuilderUtil.aggregationBuilder(
                                                          columnFields.get(2), dataFields,
                                                          "column_level_3").subAggregation(
                                                          QueryBuilderUtil.aggregationBuilder(
                                                              columnFields.get(3), dataFields,
                                                              "column_level_4")

                                                      ))))))));

    } // end of columnFields.size()==4


    if (columnFields.size() == 5) {

      searchSourceBuilder.query(boolQueryBuilder).aggregation(
          AggregationBuilders
              .terms("row_level_1")
              .field(rowfield.get(0).getColumnName())
              .subAggregation(
                  AggregationBuilders
                      .terms("row_level_2")
                      .field(rowfield.get(1).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_3")
                              .field(rowfield.get(2).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_4")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          QueryBuilderUtil.aggregationBuilder(columnFields.get(0),
                                              dataFields, "column_level_1").subAggregation(
                                              QueryBuilderUtil
                                                  .aggregationBuilder(columnFields.get(1),
                                                      dataFields, "column_level_2").subAggregation(
                                                      QueryBuilderUtil.aggregationBuilder(
                                                          columnFields.get(2), dataFields,
                                                          "column_level_3").subAggregation(
                                                          QueryBuilderUtil.aggregationBuilder(
                                                              columnFields.get(3), dataFields,
                                                              "column_level_4").subAggregation(
                                                              QueryBuilderUtil.aggregationBuilder(
                                                                  columnFields.get(4), dataFields,
                                                                  "column_level_5")

                                                          )))))))));

    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }

  private static SearchSourceBuilder rowColumnFieldsAvailableRowFieldFive(
      List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield,
      List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields,
      List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields,
      SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {

    if (columnFields.size() == 1) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        if (dataFields.size() == 1) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(0)))))))));
        }// dataFields.size() == 1

        if (dataFields.size() == 2) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(0)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(1)))))))));

        }// dataFields.size() == 2

        if (dataFields.size() == 3) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(0)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(1)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(2)))))))));

        }// dataFields.size() == 3

        if (dataFields.size() == 4) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(0)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(1)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(2)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(3)))))))));

        }// dataFields.size() == 4

        if (dataFields.size() == 5) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(0)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(1)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(2)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(3)))
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilderDataField(dataFields
                                                                          .get(4)))))))));
        }// dataFields.size() == 5
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==1


    if (columnFields.size() == 2) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        if (dataFields.size() == 1) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(0))))))))));
        }// dataFields.size() == 1

        if (dataFields.size() == 2) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(0)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(1))))))))));

        }// dataFields.size() == 2

        if (dataFields.size() == 3) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(0)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(1)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(2))))))))));

        }// dataFields.size() == 3

        if (dataFields.size() == 4) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(0)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(1)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(2)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(3))))))))));

        }// dataFields.size() == 4

        if (dataFields.size() == 5) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(0)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(1)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(2)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(3)))
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilderDataField(dataFields
                                                                                  .get(4))))))))));
        }// dataFields.size() == 5
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==2


    if (columnFields.size() == 3) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        if (dataFields.size() == 1) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")

                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(0)))))))))));
        }// dataFields.size() == 1

        if (dataFields.size() == 2) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(0)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(1)))))))))));

        }// dataFields.size() == 2

        if (dataFields.size() == 3) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(0)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(1)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(2)))))))))));

        }// dataFields.size() == 3

        if (dataFields.size() == 4) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(0)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(1)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(2)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(3)))))))))));

        }// dataFields.size() == 4

        if (dataFields.size() == 5) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(0)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(1)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(2)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(3)))
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilderDataField(dataFields
                                                                                          .get(4)))))))))));
        }// dataFields.size() == 5
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==3

    if (columnFields.size() == 4) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        if (dataFields.size() == 1) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(0))))))))))));
        }// dataFields.size() == 1

        if (dataFields.size() == 2) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(0)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(1))))))))))));

        }// dataFields.size() == 2

        if (dataFields.size() == 3) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(0)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(1)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(2))))))))))));

        }// dataFields.size() == 3

        if (dataFields.size() == 4) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(0)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(1)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(2)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(3))))))))))));

        }// dataFields.size() == 4

        if (dataFields.size() == 5) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(0)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(1)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(2)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(3)))
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilderDataField(dataFields
                                                                                                  .get(4))))))))))));
        }// dataFields.size() == 5
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==4


    if (columnFields.size() == 5) {
      if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
        if (dataFields.size() == 1) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilder(
                                                                                                  columnFields
                                                                                                      .get(4),
                                                                                                  dataFields,
                                                                                                  "column_level_5")
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(0)))))))))))));
        }// dataFields.size() == 1

        if (dataFields.size() == 2) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilder(
                                                                                                  columnFields
                                                                                                      .get(4),
                                                                                                  dataFields,
                                                                                                  "column_level_5")
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(0)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(1)))))))))))));

        }// dataFields.size() == 2

        if (dataFields.size() == 3) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilder(
                                                                                                  columnFields
                                                                                                      .get(4),
                                                                                                  dataFields,
                                                                                                  "column_level_5")
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(0)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(1)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(2)))))))))))));

        }// dataFields.size() == 3

        if (dataFields.size() == 4) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilder(
                                                                                                  columnFields
                                                                                                      .get(4),
                                                                                                  dataFields,
                                                                                                  "column_level_5")
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(0)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(1)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(2)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(3)))))))))))));

        }// dataFields.size() == 4

        if (dataFields.size() == 5) {
          searchSourceBuilder
              .query(boolQueryBuilder)
              .aggregation(
                  AggregationBuilders
                      .terms("row_level_1")
                      .field(rowfield.get(0).getColumnName())
                      .subAggregation(
                          AggregationBuilders
                              .terms("row_level_2")
                              .field(rowfield.get(1).getColumnName())
                              .subAggregation(
                                  AggregationBuilders
                                      .terms("row_level_3")
                                      .field(rowfield.get(2).getColumnName())
                                      .subAggregation(
                                          AggregationBuilders
                                              .terms("row_level_4")
                                              .field(rowfield.get(2).getColumnName())
                                              .subAggregation(
                                                  AggregationBuilders
                                                      .terms("row_level_5")
                                                      .field(rowfield.get(2).getColumnName())
                                                      .subAggregation(
                                                          QueryBuilderUtil
                                                              .aggregationBuilder(
                                                                  columnFields.get(0), dataFields,
                                                                  "column_level_1")
                                                              .subAggregation(
                                                                  QueryBuilderUtil
                                                                      .aggregationBuilder(
                                                                          columnFields.get(1),
                                                                          dataFields,
                                                                          "column_level_2")
                                                                      .subAggregation(
                                                                          QueryBuilderUtil
                                                                              .aggregationBuilder(
                                                                                  columnFields
                                                                                      .get(2),
                                                                                  dataFields,
                                                                                  "column_level_3")
                                                                              .subAggregation(
                                                                                  QueryBuilderUtil
                                                                                      .aggregationBuilder(
                                                                                          columnFields
                                                                                              .get(3),
                                                                                          dataFields,
                                                                                          "column_level_4")
                                                                                      .subAggregation(
                                                                                          QueryBuilderUtil
                                                                                              .aggregationBuilder(
                                                                                                  columnFields
                                                                                                      .get(4),
                                                                                                  dataFields,
                                                                                                  "column_level_5")
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(0)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(1)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(2)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(3)))
                                                                                              .subAggregation(
                                                                                                  QueryBuilderUtil
                                                                                                      .aggregationBuilderDataField(dataFields
                                                                                                          .get(4)))))))))))));
        }// dataFields.size() == 5
      } // !dataFields.isEmpty()
    } // end of columnFields.size()==5

    return searchSourceBuilder;
  }


}
