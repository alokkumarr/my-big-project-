package com.synchronoss.querybuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.ColumnField;
import com.synchronoss.querybuilder.model.DataField;
import com.synchronoss.querybuilder.model.Filter;
import com.synchronoss.querybuilder.model.RowField;
import com.synchronoss.querybuilder.model.Sort;
import com.synchronoss.querybuilder.model.SqlBuilder;

/**
 * @author saurav.paul
 */
class SAWPivotTypeElasticSearchQueryBuilder {

  String jsonString;
  SearchSourceBuilder searchSourceBuilder;

  public SAWPivotTypeElasticSearchQueryBuilder(String jsonString) {
    super();
    assert (this.jsonString == null && this.jsonString.trim().equals(""));
    this.jsonString = jsonString;
  }

  public String getJsonString() {
    return jsonString;
  }

  /**
   * This method is used to generate the query to build elastic search query for<br/>
   * pivot data set
   * 
   * @return query
   * @throws IOException
   * @throws JsonProcessingException
   */
  public String buildQuery() throws JsonProcessingException, IOException {
    String query = null;
    SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTree(getJsonString(), "sqlBuilder");
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



    List<RowField> rowfield = sqlBuilderNode.getRowFields();
    List<ColumnField> columnFields = sqlBuilderNode.getColumnFields();
    List<DataField> dataFields = sqlBuilderNode.getDataFields();

    // Use case I: The below block is only when column & Data Field is not empty & row field is
    // empty
    if ((rowfield.isEmpty() && rowfield.size() == 0)) {
      if ((columnFields != null && columnFields.size() <= 5)
          && (dataFields != null && dataFields.size() <= 5)) {
        searchSourceBuilder =
            ColumnDataFieldsAvailable.columnDataFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in column/data fields wise are allowed until five levels. Please verify & recreate your request.");
      }
    }

    // Use case II: The below block is only when column & row Field
    if (((dataFields.isEmpty()) && dataFields.size() == 0)) {
      if ((rowfield != null && rowfield.size() <= 5)
          && (columnFields != null && columnFields.size() <= 5)) {
        searchSourceBuilder =
            RowColumnFieldsAvailable.rowColumnFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in row/column wise are allowed until five levels. Please verify & recreate your request.");
      }
    }

    // Use case III: The below block is only when row field with column field & data field
    if ((rowfield != null && rowfield.size() <= 5)
        && ((columnFields != null && columnFields.size() <= 5) && ((dataFields != null && dataFields
            .size() <= 5)))) {
      searchSourceBuilder =
          AllFieldsAvailablePivot.allFieldsAvailable(rowfield, columnFields, dataFields,
              searchSourceBuilder, boolQueryBuilder);
    } // end of rowField, columnField & dataField
    else {
      throw new IllegalArgumentException(
          "Pivot in row/column/datafield wise are allowed until five levels. Please verify & recreate your request.");
    }

    // Use case IV: The below block is only when row field is not empty but column field & data
    // field are empty
    if ((columnFields.isEmpty() && columnFields.size() == 0)
        && (dataFields.isEmpty() && dataFields.size() == 0)) {
      if ((!rowfield.isEmpty()) && rowfield.size() <= 5) {
        searchSourceBuilder =
            RowFieldsAvailable.rowFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in row wise are allowed until five levels. Please verify & recreate your request.");
      }
    }


    // Use case V: The below block is only when row field is not empty but column field & data field
    // are empty
    if ((rowfield.isEmpty() && rowfield.size() == 0)
        && (dataFields.isEmpty() && dataFields.size() == 0)) {

      if ((!columnFields.isEmpty()) && columnFields.size() <= 5) {
        searchSourceBuilder =
            ColumnFieldsAvailable.columnFieldsAvailable(rowfield, columnFields, dataFields,
                searchSourceBuilder, boolQueryBuilder);
      } else {
        throw new IllegalArgumentException(
            "Pivot in column wise are allowed until five levels. Please verify & recreate your request.");
      }
    }
    // Generated Query
    setSearchSourceBuilder(searchSourceBuilder);
    query = searchSourceBuilder.toString();
    return query;
  }

  public SearchSourceBuilder getSearchSourceBuilder() throws JsonProcessingException, IOException {
    buildQuery();
    return searchSourceBuilder;
  }

  void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
    this.searchSourceBuilder = searchSourceBuilder;
  }
}
