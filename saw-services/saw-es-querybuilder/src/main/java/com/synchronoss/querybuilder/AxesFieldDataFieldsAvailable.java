package com.synchronoss.querybuilder;

import java.util.Arrays;
import java.util.List;

import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.DataField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import static org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders.bucketSort;

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
        SortOrder sortOrder;
        for (int i = 0; i < dataFields.size(); i++) {
            aggregation = aggregation
                .subAggregation(QueryBuilderUtil.aggregationBuilderDataFieldChart(dataFields.get(i)));
            DataField.LimitType limitType = dataFields.get(i).getLimitType();
            if(limitType!=null) {
                // Default Order will be descending order.
                sortOrder = SortOrder.DESC;
                if (dataFields.get(i).getLimitType() == DataField.LimitType.BOTTOM)
                    sortOrder = SortOrder.ASC;
                Integer size = new Integer(BuilderUtil.SIZE);
                if (dataFields.get(i).getLimitValue() != null && dataFields.get(i).getLimitValue() > 0)
                    size = dataFields.get(i).getLimitValue();
                aggregation.subAggregation(bucketSort("bucketSort", Arrays.asList(
                    new FieldSortBuilder(dataFields.get(i).getColumnName()).order(sortOrder))).size(size));
            }
        }
        return aggregation;
    }
}
