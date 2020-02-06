package com.synchronoss.saw.es.kpi;

import static com.synchronoss.saw.es.kpi.AggregationConstants._AVG;
import static com.synchronoss.saw.es.kpi.AggregationConstants._COUNT;
import static com.synchronoss.saw.es.kpi.AggregationConstants._DISTINCTCOUNT;
import static com.synchronoss.saw.es.kpi.AggregationConstants._MAX;
import static com.synchronoss.saw.es.kpi.AggregationConstants._MIN;
import static com.synchronoss.saw.es.kpi.AggregationConstants._PERCENTAGE;
import static com.synchronoss.saw.es.kpi.AggregationConstants._SUM;

import com.synchronoss.saw.model.kpi.DataField;
import com.synchronoss.saw.model.kpi.Filter;
import com.synchronoss.saw.model.kpi.Model.Operator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class KPIQueryBuilderUtil {

  private static Map<String, String> dateFormats = new HashMap();
  private static String HITS = "hits";
  private static String _SOURCE = "_source";

  static {
    Map<String, String> formats = new HashMap();
    formats.put("YYYY", "year");
    formats.put("MMMYYYY", "month");
    formats.put("MMYYYY", "month");
    formats.put("MMMdYYYY", "day");
    formats.put("MMMMdYYYY,h:mm:ssa", "hour");
    dateFormats = Collections.unmodifiableMap(formats);
  }

    public static List<QueryBuilder> numericFilterKPI (Filter item, List<QueryBuilder> builder)
    {

        if (item.getModel().getOperator().value().equals(Operator.BTW.value())) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(item.getModel().getValue());
            rangeQueryBuilder.gte(item.getModel().getOtherValue());
            builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().value().equals(Operator.GT.value())) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.gt(item.getModel().getValue());
            builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().value().equals(Operator.GTE.value())) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.gte(item.getModel().getValue());
            builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().value().equals(Operator.LT.value())) {

            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lt(item.getModel().getValue());
            builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().value().equals(Operator.LTE.value())) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
            rangeQueryBuilder.lte(item.getModel().getValue());
            builder.add(rangeQueryBuilder);
        }
        if (item.getModel().getOperator().value().equals(Operator.EQ.value())) {
            TermQueryBuilder termQueryBuilder =
                new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
            builder.add(termQueryBuilder);
        }
        if (item.getModel().getOperator().value().equals(Operator.NEQ.value())) {
            BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
            boolQueryBuilderIn.mustNot(new TermQueryBuilder(item.getColumnName(), item.getModel()
                .getValue()));
            builder.add(boolQueryBuilderIn);
        }
        return builder;
    }

    /**
     * string filter for the KPI builder.
     * @param item
     * @param builder
     * @return
     */
    public static List<QueryBuilder> stringFilterKPI(Filter item, List<QueryBuilder> builder)
    {
        if(item.getModel().getOperator().value().equals(Operator.EQ.value()) ||
            item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
            TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            List<?> modelValues = buildStringTermsfilter(item.getModel().getModelValues());
            TermsQueryBuilder termsQueryBuilder1 =
                new TermsQueryBuilder(buildFilterColumn(item.getColumnName()), modelValues);
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(termsQueryBuilder);
            boolQueryBuilder.should(termsQueryBuilder1);
            builder.add(boolQueryBuilder);
        }

        if (item.getModel().getOperator().value().equals(Operator.NEQ.value()) ||
            item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
            List<?> modelValues = buildStringTermsfilter(item.getModel().getModelValues());
            QueryBuilder qeuryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.mustNot(qeuryBuilder);
            QueryBuilder qeuryBuilder1 =
                new TermsQueryBuilder(buildFilterColumn(item.getColumnName()), modelValues);
            boolQueryBuilder.mustNot(qeuryBuilder1);
            builder.add(boolQueryBuilder);
        }

        // prefix query builder - not analyzed
        if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
            PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
                (String) item.getModel().getModelValues().get(0));
            PrefixQueryBuilder pqb1 = new PrefixQueryBuilder(buildFilterColumn(item.getColumnName()),
                (String) ((String) item.getModel().getModelValues().get(0)).toLowerCase());
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(pqb);
            boolQueryBuilder.should(pqb1);
            builder.add(boolQueryBuilder);
        }

        // using wildcard as there's no suffix query type provided by
        // elasticsearch
        if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
            WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
                "*"+ item.getModel().getModelValues().get(0));
            WildcardQueryBuilder wqb1 = new WildcardQueryBuilder(buildFilterColumn(item.getColumnName()),
                "*"+  (String) ((String) item.getModel().getModelValues().get(0)).toLowerCase());
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(wqb);
            boolQueryBuilder.should(wqb1);
            builder.add(boolQueryBuilder);
        }

        // same for contains clause - not analyzed query
        if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
            WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
                "*" + item.getModel().getModelValues().get(0)+"*");
            WildcardQueryBuilder wqb1 = new WildcardQueryBuilder(buildFilterColumn(item.getColumnName()),
                "*" +  (String)((String) item.getModel().getModelValues().get(0)).toLowerCase()+"*");
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(wqb);
            boolQueryBuilder.should(wqb1);
            builder.add(boolQueryBuilder);
        }

        return builder;
    }

    /**
     *  Build the terms values to support case insensitive
     *  search options.
     * @param modelValues
     */
    private static List <?> buildStringTermsfilter(List<?> modelValues )
    {
        List<Object> stringValues = new ArrayList<>();
        modelValues.forEach((val) -> {
            // Add the lowercase value as terms to lookup based on custom analyser.
            if (val instanceof String) {
                stringValues.add(((String) val).toLowerCase());
            }
        });
        return stringValues;
    }

    /**
     *  Build the search column to support case insensitive
     *  search options.
     * @param columnName
     */
    private static String buildFilterColumn(String columnName)
    {
        if (columnName.contains(".keyword"))
        {
            return columnName.replace(".keyword", ".filter");
        }
        else {
            return columnName+".filter";
        }
    }

    public static SearchSourceBuilder aggregationBuilderDataFieldKPI(DataField data,
        SearchSourceBuilder searchSourceBuilder
    )
    {
        for(DataField.Aggregate aggregate : data.getAggregate()) {

            AggregationBuilder aggregationBuilder = null;
            switch (aggregate) {
                case SUM:
                    aggregationBuilder = AggregationBuilders.sum(data.getName()+_SUM).field(data.getColumnName());
                    break;
                case AVG:
                    aggregationBuilder = AggregationBuilders.avg(data.getName()+_AVG).field(data.getColumnName());
                    break;
                case MIN:
                    aggregationBuilder = AggregationBuilders.min(data.getName()+_MIN).field(data.getColumnName());
                    break;
                case MAX:
                    aggregationBuilder = AggregationBuilders.max(data.getName()+_MAX).field(data.getColumnName());
                    break;
                case COUNT:
                    aggregationBuilder = AggregationBuilders.count(data.getName()+_COUNT).field(data.getColumnName());
                    break;
                case DISTINCT_COUNT:
                    aggregationBuilder = AggregationBuilders.cardinality(data.getName()+_DISTINCTCOUNT).field(data.getColumnName());
                    break;
                case PERCENTAGE:
                    Script script = new Script("_value*100/" + data.getAdditionalProperties().get(data.getColumnName()
                        + "_sum"));
                    aggregationBuilder = AggregationBuilders.sum(data.getName()+_PERCENTAGE).field(data.getColumnName()).script(script);
                    break;
            }
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        return searchSourceBuilder;
    }

}
