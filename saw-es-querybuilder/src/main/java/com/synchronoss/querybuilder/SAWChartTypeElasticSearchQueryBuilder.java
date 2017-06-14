package com.synchronoss.querybuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.Filter.Type;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import com.synchronoss.querybuilder.model.pivot.SqlBuilder.BooleanCriteria;

/**
 * @author saurav.paul
 */
class SAWChartTypeElasticSearchQueryBuilder {


  String jsonString;

  SearchSourceBuilder searchSourceBuilder;

  public SAWChartTypeElasticSearchQueryBuilder(String jsonString) {
    super();
    assert (this.jsonString == null && this.jsonString.trim().equals(""));
    this.jsonString = jsonString;
  }

  public String getJsonString() {
    return jsonString;
  }

  /**
   * This method is used to generate the query to build elastic search query for<br/>
   * chart data set
   * 
   * @return query
   * @throws IOException
   * @throws JsonProcessingException
   */
  public String buildQuery() throws JsonProcessingException, IOException {

    String query = null;
    com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTreeChart(getJsonString(), "sqlBuilder");
    int size = 0;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(size);

    // The below block adding the sort block
    List<com.synchronoss.querybuilder.model.chart.Sort> sortNode = sqlBuilderNode.getSorts();
    for (com.synchronoss.querybuilder.model.chart.Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }

    // The below block adding filter block
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();;
    if (sqlBuilderNode.getBooleanCriteria() !=null){
    List<com.synchronoss.querybuilder.model.chart.Filter> filters = sqlBuilderNode.getFilters();
    List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
    for (com.synchronoss.querybuilder.model.chart.Filter item : filters) {
      if (item.getType().equals(Type.DATE) || item.getType().equals(Type.TIMESTAMP)) {
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
        rangeQueryBuilder.lte(item.getModel().getLte());
        rangeQueryBuilder.gte(item.getModel().getGte());
        builder.add(rangeQueryBuilder);
      }
      if (item.getType().equals(Type.STRING)) {
        TermsQueryBuilder termsQueryBuilder =
            new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
        builder.add(termsQueryBuilder);
      }
      if ((item.getType().equals(Type.DOUBLE) || item.getType().equals(Type.INT))
          || item.getType().equals(Type.FLOAT)) {
        if (item.getModel().getOperator().equals(Operator.BTW)) {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.lte(item.getModel().getOtherValue());
          rangeQueryBuilder.gte(item.getModel().getValue());
          builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().equals(Operator.EQ)) {
          TermQueryBuilder termQueryBuilder =
              new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
          builder.add(termQueryBuilder);
        }
        if (item.getModel().getOperator().equals(Operator.NEQ)) {
          BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
          boolQueryBuilderIn.mustNot(new TermQueryBuilder(item.getColumnName(), item.getModel()
              .getValue()));
          builder.add(boolQueryBuilderIn);
        }
      }
    }
    if (sqlBuilderNode.getBooleanCriteria().equals(BooleanCriteria.AND)) {
      builder.forEach(item -> {
        boolQueryBuilder.must(item);
      });
    } else {
      builder.forEach(item -> {
        boolQueryBuilder.should(item);
      });
    }
    searchSourceBuilder.query(boolQueryBuilder);
   }

    com.synchronoss.querybuilder.model.chart.GroupBy groupBy = sqlBuilderNode.getGroupBy();
    com.synchronoss.querybuilder.model.chart.SplitBy splitBy = sqlBuilderNode.getSplitBy();
    List<com.synchronoss.querybuilder.model.chart.DataField> dataFields = sqlBuilderNode.getDataFields();



    // Use case I: The below block is only when groupBy is available
    if (groupBy != null) {
      if (splitBy == null && dataFields.isEmpty()) {
        searchSourceBuilder =
            searchSourceBuilder.query(boolQueryBuilder).aggregation(
                AggregationBuilders.terms("group_by").field(groupBy.getColumnName()));
      }
    }

    // Use case II: The below block is only when groupBy is available & columnBy is available
    if (groupBy != null && splitBy != null) {
      if (dataFields.isEmpty()) {
        searchSourceBuilder =
            searchSourceBuilder.query(boolQueryBuilder).aggregation(
                AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
                    .subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)));
      }
    }


    // Use case III: The below block is only when groupBy, splitBy are available

    if (groupBy != null && splitBy != null) {
      if (!(dataFields.isEmpty())) {
        searchSourceBuilder =
            AllFieldsAvailableChart.allFieldsAvailable(groupBy, splitBy, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      }
    }

    // Use case IV: The below block is only when splitBy are available

    if (splitBy != null) {
      if (groupBy == null && dataFields.isEmpty()) {
        searchSourceBuilder =
            searchSourceBuilder.query(boolQueryBuilder).aggregation(
                QueryBuilderUtil.aggregationBuilderChart(splitBy));
      }
    }

    // Use case V: The below block is only when splitBy & dataField are available
    if (splitBy != null) {
      if (groupBy == null && !(dataFields.isEmpty())) {
        searchSourceBuilder =
            SpiltByAndDataFieldsAvailableChart.allFieldsAvailable(splitBy, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      }
    }

    // Use case VI: The below block is only when groupBy & dataField are available

    if (groupBy != null) {
      if (splitBy == null && !(dataFields.isEmpty())) {
        searchSourceBuilder =
            GroupByAndFieldsAvailableChart.allFieldsAvailable(groupBy, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      }
    }

    setSearchSourceBuilder(searchSourceBuilder);
    query = searchSourceBuilder.toString();
    return query;
  }



  void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
    this.searchSourceBuilder = searchSourceBuilder;
  }

  public SearchSourceBuilder getSearchSourceBuilder() throws JsonProcessingException, IOException {
    buildQuery();
    return searchSourceBuilder;
  }


}
