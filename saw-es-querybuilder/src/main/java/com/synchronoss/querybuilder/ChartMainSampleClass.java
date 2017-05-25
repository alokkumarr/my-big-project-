package com.synchronoss.querybuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.querybuilder.model.DataField;
import com.synchronoss.querybuilder.model.Filter;
import com.synchronoss.querybuilder.model.GroupBy;
import com.synchronoss.querybuilder.model.Sort;
import com.synchronoss.querybuilder.model.SplitBy;
import com.synchronoss.querybuilder.model.SqlBuilder;

public class ChartMainSampleClass {

  public static void main(String[] args) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    System.setProperty("host", "10.48.72.74");
    System.setProperty("port", "9300");
    // System.setProperty("username", "elastic");
    // System.setProperty("password", "xuw3dUraHapret");
    System.setProperty("cluster", "sncr-salesdemo");


    // This is the entry point for /analysis service as JSONString not as file
    JsonNode objectNode =
        objectMapper.readTree(new File(
            "C:\\Users\\saurav.paul\\Desktop\\Sergey\\chart_type_data.json"));
    // JsonNode objectNode = objectMapper.readTree(new File(args[0]));
    JsonNode sqlNode = objectNode.get("sqlBuilder");
    SqlBuilder sqlBuilderNode = objectMapper.treeToValue(sqlNode, SqlBuilder.class);
    int size = 0;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(size);

    // The below block adding the sort block
    List<Sort> sortNode = sqlBuilderNode.getSort();
    for (Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }

    // The below block adding filter block
    List<Filter> filters = sqlBuilderNode.getFilters();
    List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
    for (Filter item : filters) {
      if (item.getType().equals("date")) {
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
        rangeQueryBuilder.lte(item.getRange().getLte());
        rangeQueryBuilder.gte(item.getRange().getGte());
        builder.add(rangeQueryBuilder);
      } else {
        TermsQueryBuilder termsQueryBuilder =
            new TermsQueryBuilder(item.getColumnName(), item.getValue());
        builder.add(termsQueryBuilder);
      }
    }
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    builder.forEach(item -> {
      boolQueryBuilder.must(item);
    });
    searchSourceBuilder.query(boolQueryBuilder);

    GroupBy groupBy = sqlBuilderNode.getGroupBy();
    SplitBy splitBy = sqlBuilderNode.getSplitBy();
    List<DataField> dataFields = sqlBuilderNode.getDataFields();

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
    String query = searchSourceBuilder.toString();
    System.out.println(query);
    System.setProperty("url", "http://mapr-dev02.sncrbda.dev.vacum-np.sncrcorp.net:9200/");
    // SAWElasticTransportService.executeReturnAsString(query, objectNode.toString(), "some",
    // "xssds", "login");
    String[] response = SAWElasticSearchQueryExecutor.executeReturnAsString(searchSourceBuilder, objectNode.toString());
    System.out.println(response[0]);
    

  }

}
