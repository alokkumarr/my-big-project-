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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.BuilderUtil;
import com.synchronoss.DynamicConvertor;
import com.synchronoss.querybuilder.model.chart.Filter.Type;
import com.synchronoss.querybuilder.model.chart.Model;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import com.synchronoss.querybuilder.model.pivot.SqlBuilder.BooleanCriteria;

/**
 * @author saurav.paul
 */
class SAWChartTypeElasticSearchQueryBuilder {


  String jsonString;
  String dataSecurityString;

  SearchSourceBuilder searchSourceBuilder;

 private final static String DATE_FORMAT="yyyy-MM-dd HH:mm:ss||yyyy-MM-dd";

  public SAWChartTypeElasticSearchQueryBuilder(String jsonString) {
    super();
    this.jsonString = jsonString;
  }
  
  public SAWChartTypeElasticSearchQueryBuilder(String jsonString, String dataSecurityKey) {
	    super();
	    this.dataSecurityString = dataSecurityKey;
	    this.jsonString = jsonString;
  }

  public String getDataSecurityString() {
	return dataSecurityString;
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
   * @throws ProcessingException
   */
  public String buildQuery() throws JsonProcessingException, IOException, ProcessingException {

    String query = null;
    com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode =
        BuilderUtil.getNodeTreeChart(getJsonString(), "sqlBuilder");
    int size = 0;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(size);

    if (sqlBuilderNode.getSorts() == null || sqlBuilderNode.getFilters() == null) {
      throw new NullPointerException(
          "Please add sort[] & filter[] block.It can be empty but these blocks are important.");
    }
    // The below block adding the sort block
    List<com.synchronoss.querybuilder.model.chart.Sort> sortNode = sqlBuilderNode.getSorts();
    for (com.synchronoss.querybuilder.model.chart.Sort item : sortNode) {
      SortOrder sortOrder =
          item.getOrder().equals(SortOrder.ASC.name()) ? SortOrder.ASC : SortOrder.DESC;
      FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
      searchSourceBuilder.sort(sortBuilder);
    }
    DataSecurityKey dataSecurityKeyNode = null;
    ObjectMapper objectMapper = null;
    if (getDataSecurityString()!=null && !getDataSecurityString().trim().equals("")){		
	    objectMapper= new ObjectMapper();
	    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
	    JsonNode objectNode = objectMapper.readTree(getDataSecurityString());
	    dataSecurityKeyNode = objectMapper.treeToValue(objectNode, DataSecurityKey.class);
    }
    // The below block adding filter block
    final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    if (sqlBuilderNode.getBooleanCriteria() != null) {
      List<com.synchronoss.querybuilder.model.chart.Filter> filters = sqlBuilderNode.getFilters();
      List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
      if (dataSecurityKeyNode!=null) {
    	  for (DataSecurityKeyDef dsk : dataSecurityKeyNode.getDataSecuritykey()){
    	      TermsQueryBuilder dataSecurityBuilder = new TermsQueryBuilder(dsk.getName().concat(BuilderUtil.SUFFIX), dsk.getValues());
    	      builder.add(dataSecurityBuilder);
    	      }
      }
      for (com.synchronoss.querybuilder.model.chart.Filter item : filters) {
        System.out.println(item.toString());
        if (!item.getIsRuntimeFilter().value() && !item.getIsGloblFilter().value()) {
          if (item.getType().value().equals(Type.DATE.value())
              || item.getType().value().equals(Type.TIMESTAMP.value())) 
          {
            if (item.getModel().getPreset()!=null && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
            {
              DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              if(item.getType().value().equals(Type.DATE.value())) {
                rangeQueryBuilder.format(DATE_FORMAT);
              }
              rangeQueryBuilder.lte(dynamicConvertor.getLte());
              rangeQueryBuilder.gte(dynamicConvertor.getGte());
              builder.add(rangeQueryBuilder);
            }
            else {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              if(item.getType().value().equals(Type.DATE.value())) {
                rangeQueryBuilder.format(DATE_FORMAT);
              }
              rangeQueryBuilder.lte(item.getModel().getLte());
              rangeQueryBuilder.gte(item.getModel().getGte());
              builder.add(rangeQueryBuilder);
            }
          }
          if (item.getType().value().equals(Type.STRING.value())) {
            TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            builder.add(termsQueryBuilder);
          }
          if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item
              .getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
              || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase())
              || item.getType().value().toLowerCase().equals(Type.LONG.value().toLowerCase())) {
            
            builder = QueryBuilderUtil.numericFilterChart(item, builder);
            
          }
        }
        if (item.getIsRuntimeFilter().value() && item.getModel() != null) {
          if (item.getType().value().equals(Type.DATE.value())
              || item.getType().value().equals(Type.TIMESTAMP.value())) 
          {
            if (item.getModel().getPreset()!=null && !item.getModel().getPreset().value().equals(Model.Preset.NA.toString()))
            {
              DynamicConvertor dynamicConvertor = BuilderUtil.dynamicDecipher(item.getModel().getPreset().value());
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              if(item.getType().value().equals(Type.DATE.value())) {
                rangeQueryBuilder.format(DATE_FORMAT);
              }
              rangeQueryBuilder.lte(dynamicConvertor.getLte());
              rangeQueryBuilder.gte(dynamicConvertor.getGte());
              builder.add(rangeQueryBuilder);
            }
            else {
              RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
              if(item.getType().value().equals(Type.DATE.value())) {
                rangeQueryBuilder.format(DATE_FORMAT);
              }
              rangeQueryBuilder.lte(item.getModel().getLte());
              rangeQueryBuilder.gte(item.getModel().getGte());
              builder.add(rangeQueryBuilder);
            }
          }
          if (item.getType().value().equals(Type.STRING.value())) {
            TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            builder.add(termsQueryBuilder);
          }
          if ((item.getType().value().toLowerCase().equals(Type.DOUBLE.value().toLowerCase()) || item
              .getType().value().toLowerCase().equals(Type.INT.value().toLowerCase()))
              || item.getType().value().toLowerCase().equals(Type.FLOAT.value().toLowerCase())
              || item.getType().value().toLowerCase().equals(Type.LONG.value().toLowerCase())) {
            
            builder = QueryBuilderUtil.numericFilterChart(item, builder);
          }
        }
      }
      if (sqlBuilderNode.getBooleanCriteria().value().equals(BooleanCriteria.AND.value())) {
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
    List<com.synchronoss.querybuilder.model.chart.NodeField>  nodeFields = sqlBuilderNode.getNodeFields();
    List<com.synchronoss.querybuilder.model.chart.DataField> dataFields =  sqlBuilderNode.getDataFields();
    if (nodeFields != null && dataFields !=null)
    {
      if ((!nodeFields.isEmpty() && nodeFields.size() <=3) && !dataFields.isEmpty()){
      searchSourceBuilder = AxesFieldDataFieldsAvailable.rowDataFieldsAvailable
          (nodeFields, dataFields, searchSourceBuilder, boolQueryBuilder);
      }
      else {
        
          throw new IllegalArgumentException("nodeFields & dataFields cannot be empty");
      }
    } 
    else {
        throw new IllegalArgumentException("Please select appropriate value for the axes & metrices");
    }

    setSearchSourceBuilder(searchSourceBuilder);
    query = searchSourceBuilder.toString();
    return query;
  }



  void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
    this.searchSourceBuilder = searchSourceBuilder;
  }

  public SearchSourceBuilder getSearchSourceBuilder() throws JsonProcessingException, IOException,
      ProcessingException {
    buildQuery();
    return searchSourceBuilder;
  }


}
