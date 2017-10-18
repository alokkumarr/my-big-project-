package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class AxesFieldDataFieldsAvailable {

	public static SearchSourceBuilder rowDataFieldsAvailable(
			List<com.synchronoss.querybuilder.model.chart.NodeField> nodeField,
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields,
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
		if (nodeField.size() == 1) {
			searchSourceBuilder = axesDataFieldsAvailableRowFieldOne(nodeField, dataFields, searchSourceBuilder,
					boolQueryBuilder);
		}
		if (nodeField.size() == 2) {
			searchSourceBuilder = axesDataFieldsAvailableRowFieldTwo(nodeField, dataFields, searchSourceBuilder,
					boolQueryBuilder);
		}
		if (nodeField.size() == 3) {
			searchSourceBuilder = axesDataFieldsAvailableRowFieldThree(nodeField, dataFields, searchSourceBuilder,
					boolQueryBuilder);
		}
		return searchSourceBuilder;
	}

	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldOne(
			List<com.synchronoss.querybuilder.model.chart.NodeField> nodeFields,
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields,
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
		AggregationBuilder aggregation = addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderChart(nodeFields.get(0), "node_field_1"));
		if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
			searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		} else {
			searchSourceBuilder.query(boolQueryBuilder);
		}
		return searchSourceBuilder;
	}

	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldTwo(
			List<com.synchronoss.querybuilder.model.chart.NodeField> nodeFields,
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields,
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
		if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
			AggregationBuilder aggregation = QueryBuilderUtil.aggregationBuilderChart(nodeFields.get(0), "node_field_1").
				subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderChart(nodeFields.get(1), "node_field_2")));
			searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		} else {
			AggregationBuilder aggregation = QueryBuilderUtil.aggregationBuilderChart(nodeFields.get(0), "node_field_1").
					subAggregation(QueryBuilderUtil.aggregationBuilderChart(nodeFields.get(1), "node_field_2"));
			searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		}
		return searchSourceBuilder;
	}

	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldThree(
			List<com.synchronoss.querybuilder.model.chart.NodeField> axesfields,
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields,
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
		if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
		AggregationBuilder aggregation = QueryBuilderUtil.aggregationBuilderChart(axesfields.get(0), "node_field_1")
				.subAggregation(QueryBuilderUtil.aggregationBuilderChart(axesfields.get(0), "node_field_2"))
						.subAggregation(addSubaggregation(dataFields, QueryBuilderUtil.aggregationBuilderChart(axesfields.get(0), "node_field_3")));
		searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		} else {
			searchSourceBuilder.query(boolQueryBuilder);
		}
		return searchSourceBuilder;
	}
	private static AggregationBuilder addSubaggregation(
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, AggregationBuilder aggregation) {
		for (int i = 0; i < dataFields.size(); i++) {
			aggregation = aggregation
					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(i)));
		}
		return aggregation;
	}

}
