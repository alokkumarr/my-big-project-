package com.synchronoss.querybuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.Filter.Type;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import com.synchronoss.querybuilder.model.pivot.SqlBuilder;
import com.synchronoss.querybuilder.model.pivot.SqlBuilder.BooleanCriteria;

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
   * @throws ProcessingException 
   */
  public String buildQuery() throws JsonProcessingException, IOException, ProcessingException {
    String query = null;
    SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTree(getJsonString(), "sqlBuilder");
    int size = 0;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(size);
    if (sqlBuilderNode.getSorts() == null && sqlBuilderNode.getFilters() == null) {
      throw new NullPointerException(
          "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
    }
    // The below block adding the sort block
    List<com.synchronoss.querybuilder.model.pivot.Sort> sortNode = sqlBuilderNode.getSorts();
    for (com.synchronoss.querybuilder.model.pivot.Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().name().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }

    // The below block adding filter block
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();;
    if (sqlBuilderNode.getBooleanCriteria() !=null){
    List<com.synchronoss.querybuilder.model.pivot.Filter> filters = sqlBuilderNode.getFilters();
    List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
    for (com.synchronoss.querybuilder.model.pivot.Filter item : filters) {
      if (item.getType().value().toLowerCase().equals(Type.DATE.value().toLowerCase()) || item.getType().value().toLowerCase().equals(Type.TIMESTAMP.value().toLowerCase())) {
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
        rangeQueryBuilder.lte(item.getModel().getLte());
        rangeQueryBuilder.gte(item.getModel().getGte());
        if(item.getModel().getFormat()!=null)
        {
          rangeQueryBuilder.format(item.getModel().getFormat());
        }
        builder.add(rangeQueryBuilder);
      }
      if (item.getType().value().toLowerCase().equals(Type.STRING.value().toLowerCase())) {
        TermsQueryBuilder termsQueryBuilder =
            new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
        builder.add(termsQueryBuilder);
      }
      if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item.getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
          || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase())) {
        if (item.getModel().getOperator().value().equals(Operator.BTW.value())) {
          RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
          rangeQueryBuilder.lte(item.getModel().getOtherValue());
          rangeQueryBuilder.gte(item.getModel().getValue());
          builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().value().toLowerCase().equals(Operator.EQ.value().toLowerCase())) {
          TermQueryBuilder termQueryBuilder =
              new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
          builder.add(termQueryBuilder);
        }
        if (item.getModel().getOperator().value().toLowerCase().equals(Operator.NEQ.value().toLowerCase())) {
          BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
          boolQueryBuilderIn.mustNot(new TermQueryBuilder(item.getColumnName(), item.getModel()
              .getValue()));
          builder.add(boolQueryBuilderIn);
        }
      }
    }
    if (sqlBuilderNode.getBooleanCriteria().value().toLowerCase().equals(BooleanCriteria.AND.value().toLowerCase())) {
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
    List<com.synchronoss.querybuilder.model.pivot.RowField> rowfield =
        sqlBuilderNode.getRowFields();
    List<com.synchronoss.querybuilder.model.pivot.ColumnField> columnFields =
        sqlBuilderNode.getColumnFields();
    List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields =
        sqlBuilderNode.getDataFields();

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

  public SearchSourceBuilder getSearchSourceBuilder() throws JsonProcessingException, IOException, ProcessingException {
    buildQuery();
    return searchSourceBuilder;
  }

  void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
    this.searchSourceBuilder = searchSourceBuilder;
  }
}
