package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
		TermsAggregationBuilder aggregation = addSubaggregation(dataFields, AggregationBuilders.terms("node_field_1")
				.field(nodeFields.get(0).getColumnName()));
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
			TermsAggregationBuilder aggregation = AggregationBuilders.terms("node_field_1")
					.field(nodeFields.get(0).getColumnName()).subAggregation(addSubaggregation(dataFields,
							AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName())));
			searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		} else {
			TermsAggregationBuilder aggregation = AggregationBuilders.terms("node_field_1")
					.field(nodeFields.get(0).getColumnName())
					.subAggregation(AggregationBuilders.terms("node_field_2").field(nodeFields.get(1).getColumnName()));
			searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		}
		return searchSourceBuilder;
	}

	private static SearchSourceBuilder axesDataFieldsAvailableRowFieldThree(
			List<com.synchronoss.querybuilder.model.chart.NodeField> axesfields,
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields,
			SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
		if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
		TermsAggregationBuilder aggregation = AggregationBuilders.terms("node_field_1")
				.field(axesfields.get(0).getColumnName())
				.subAggregation(AggregationBuilders.terms("node_field_2").field(axesfields.get(1).getColumnName())
						.subAggregation(addSubaggregation(dataFields,
								AggregationBuilders.terms("node_field_3").field(axesfields.get(2).getColumnName()))));
			searchSourceBuilder.query(boolQueryBuilder).aggregation(aggregation);
		} else {
			searchSourceBuilder.query(boolQueryBuilder);
		}
		return searchSourceBuilder;
	}
	private static TermsAggregationBuilder addSubaggregation(
			List<com.synchronoss.querybuilder.model.chart.DataField> dataFields, TermsAggregationBuilder aggregation) {
		for (int i = 0; i < dataFields.size(); i++) {
			aggregation = aggregation
					.subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(i)));
		}
		return aggregation;
	}

}
