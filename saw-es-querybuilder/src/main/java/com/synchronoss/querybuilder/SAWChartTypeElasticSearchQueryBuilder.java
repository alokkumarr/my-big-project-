package com.synchronoss.querybuilder;

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
import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.DataField;
import com.synchronoss.querybuilder.model.Filter;
import com.synchronoss.querybuilder.model.GroupBy;
import com.synchronoss.querybuilder.model.Sort;
import com.synchronoss.querybuilder.model.SplitBy;
import com.synchronoss.querybuilder.model.SqlBuilder;

/**
 * @author saurav.paul
 */
class SAWChartTypeElasticSearchQueryBuilder {
	 
	
	String jsonString;

	SearchSourceBuilder searchSourceBuilder;
	
	public SAWChartTypeElasticSearchQueryBuilder(String jsonString) 
	{
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
	 * @return query
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public String buildQuery() throws JsonProcessingException, IOException 
	{

		String query = null;
		SqlBuilder sqlBuilderNode = BuilderUtil.getNodeTree(getJsonString(), "sqlBuilder");
	    int size = 0;
	    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
	    searchSourceBuilder.size(size);

		   // The below block adding the sort block
		   List<Sort> sortNode =  sqlBuilderNode.getSort();
		   for (Sort item : sortNode )
		   {
				SortOrder sortOrder = item.getOrder().equals
						(SortOrder.ASC.name())? SortOrder.ASC : SortOrder.DESC;
				FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item.getColumnName()).order(sortOrder);
				searchSourceBuilder.sort(sortBuilder);
		   }
		   
		   // The below block adding filter block 
		   List<Filter> filters = sqlBuilderNode.getFilters();
		   List<QueryBuilder> builder = new ArrayList<QueryBuilder>();
		   for (Filter item : filters)
		   {
				if (item.getType().equals("date"))
				{
					RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
					rangeQueryBuilder.lte(item.getRange().getLte());
					rangeQueryBuilder.gte(item.getRange().getGte());
					builder.add(rangeQueryBuilder);
				}
				else 
				{
					TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder(item.getColumnName(), item.getValue());
					builder.add(termsQueryBuilder);
				}
		   }
		   final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		   builder.forEach(item->
			{
				boolQueryBuilder.must(item);
			});
		   searchSourceBuilder.query(boolQueryBuilder);

		    GroupBy groupBy = sqlBuilderNode.getGroupBy();
		    SplitBy splitBy = sqlBuilderNode.getSplitBy();
		    List<DataField> dataFields = sqlBuilderNode.getDataFields();

		    
		      
		    
		    
		    // Use case I: The below block is only when groupBy is available
		    if (groupBy!=null)
		    {
		    	if (splitBy ==null && dataFields.isEmpty())
		    	{
					searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
			        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName()));
		    	}
		    }

		 // Use case II: The below block is only when groupBy is available & columnBy is available
		    if (groupBy!=null && splitBy!=null)
		    {
		    	if (dataFields.isEmpty())
		    	{
					searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
			        .aggregation(AggregationBuilders.terms("group_by").field(groupBy.getColumnName())
			        		.subAggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy)));
		    	}
		    }

		    
		 // Use case III: The below block is only when groupBy, splitBy are available

		    if (groupBy!=null && splitBy!=null)
		    {
		    	if (!(dataFields.isEmpty()))
		    	{
					searchSourceBuilder = AllFieldsAvailableChart.allFieldsAvailable(groupBy, splitBy, dataFields, searchSourceBuilder, boolQueryBuilder);
		    	}
		    }

		 // Use case IV: The below block is only when splitBy are available

		    if (splitBy!=null)
		    {
		    	if (groupBy ==null && dataFields.isEmpty())
		    	{
					searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder)
			        .aggregation(QueryBuilderUtil.aggregationBuilderChart(splitBy));
		    	}
		    }

		 // Use case V: The below block is only when splitBy & dataField are available
		    if (splitBy!=null)
		    {
		    	if (groupBy ==null && !(dataFields.isEmpty()))
		    	{
					searchSourceBuilder = SpiltByAndDataFieldsAvailableChart.allFieldsAvailable(splitBy, dataFields, searchSourceBuilder, boolQueryBuilder);
		    	}
		    }	    

		 // Use case VI: The below block is only when groupBy & dataField are available

		    if (groupBy!=null)
		    {
		    	if (splitBy ==null && !(dataFields.isEmpty()))
		    	{
					searchSourceBuilder = GroupByAndFieldsAvailableChart.allFieldsAvailable(groupBy, dataFields, searchSourceBuilder, boolQueryBuilder);
		    	}
		    }	    
		   
		setSearchSourceBuilder(searchSourceBuilder);    
		query = searchSourceBuilder.toString();    
		return query;
	}
	
	
	
	void setSearchSourceBuilder(SearchSourceBuilder searchSourceBuilder) {
		this.searchSourceBuilder = searchSourceBuilder;
	}

	public SearchSourceBuilder getSearchSourceBuilder() throws JsonProcessingException, IOException 
	{
		buildQuery();
		return searchSourceBuilder;
	}

	
}
