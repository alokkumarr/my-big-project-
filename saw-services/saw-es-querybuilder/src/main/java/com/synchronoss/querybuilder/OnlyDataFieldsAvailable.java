package com.synchronoss.querybuilder;

import java.util.List;

import com.synchronoss.querybuilder.model.pivot.DataField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

class OnlyDataFieldsAvailable {

    public static SearchSourceBuilder dataFieldsAvailable(
        List<com.synchronoss.querybuilder.model.pivot.DataField> dataFields, SearchSourceBuilder searchSourceBuilder, BoolQueryBuilder boolQueryBuilder) {
        if ((!dataFields.isEmpty()) && dataFields.size() > 0) {
            searchSourceBuilder.query(boolQueryBuilder);
             addSubaggregation(dataFields,searchSourceBuilder);
        }
        return searchSourceBuilder;
    }

    private static void addSubaggregation(List<DataField> dataFields, SearchSourceBuilder searchSourceBuilder) {
        if (dataFields.size() > 0) {
            for (DataField dataField : dataFields) {
                searchSourceBuilder.aggregation(QueryBuilderUtil.aggregationBuilderDataField(dataField));
            }
        }
    }
}
