package com.synchronoss.saw.es;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.model.*;
import com.synchronoss.saw.util.BuilderUtil;
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
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import org.elasticsearch.search.builder.SearchSourceBuilder;

public class QueryBuilderUtil {
	
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String SPACE_REGX = "\\s+";
	public final static String EMPTY_STRING = "";
    private static String HITS= "hits";
    private static String _SOURCE ="_source";
	public static Map<String,String> dateFormats = new HashMap<String, String>();
	static {

      Map<String, String> formats = new HashMap<String, String>();
      formats.put("YYYY", "year");
      formats.put("MMMYYYY", "month");
      formats.put("MMYYYY", "month");
      formats.put("MMMdYYYY", "day");
      formats.put("MMMMdYYYY,h:mm:ssa", "hour");
      dateFormats = Collections.unmodifiableMap(formats);
  }

    /**
     *
     * @param field
     * @param aggregationName
     * @return
     */
	public static AggregationBuilder aggregationBuilder (Field field,
	     String aggregationName)

	{
		AggregationBuilder aggregationBuilder = null;
		
		if (field.getType().name().equals(Field.Type.DATE.name())
		    || field.getType().name().equals(Field.Type.TIMESTAMP.name()))
		{
		  if (field.getGroupInterval()!=null){
			aggregationBuilder = AggregationBuilders.
					dateHistogram(aggregationName).field(field.getColumnName()).format(DATE_FORMAT).
					dateHistogramInterval(groupInterval(field.getGroupInterval().value())).order(BucketOrder.key(false));
			}
		  else {
		    aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(field.getColumnName())
		        .format(DATE_FORMAT).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
		  }
		}
		else {
          aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(field.getColumnName()).size(BuilderUtil.SIZE);
		}

		return aggregationBuilder;
	}

    /**
     * Group interval for the DateHistogram.
     * @param groupInterval
     * @return
     */
     public static DateHistogramInterval groupInterval(String groupInterval)
     {
    	 DateHistogramInterval histogramInterval = null; 
    	    switch (groupInterval)
    	    {
    	    case "month" : histogramInterval =  DateHistogramInterval.MONTH; break;
    	    case "day" : histogramInterval =  DateHistogramInterval.DAY; break;
    	    case "year" : histogramInterval =  DateHistogramInterval.YEAR; break;
    	    case "quarter" : histogramInterval =  DateHistogramInterval.QUARTER; break;
    	    case "hour" : histogramInterval =  DateHistogramInterval.HOUR;break;
    	    case "week" : histogramInterval =  DateHistogramInterval.WEEK;break;
    	    }
    	    return histogramInterval;
     }

    /**
     * Aggregation builder for data fields.
     * @param field
     * @return
     */
	public static AggregationBuilder aggregationBuilderDataField(Field field)
	{
		AggregationBuilder aggregationBuilder = null;

		switch (field.getAggregate())
		{
			case SUM: aggregationBuilder = AggregationBuilders.sum(field.getDisplayName()).field(field.getColumnName()); break;
			case AVG: aggregationBuilder = AggregationBuilders.avg(field.getDisplayName()).field(field.getColumnName()); break;
			case MIN: aggregationBuilder = AggregationBuilders.min(field.getDisplayName()).field(field.getColumnName()); break;
			case MAX: aggregationBuilder = AggregationBuilders.max(field.getDisplayName()).field(field.getColumnName()); break;
			case COUNT: aggregationBuilder = AggregationBuilders.count(field.getDisplayName()).field(field.getColumnName()); break;
			case PERCENTAGE:
				Script script = new Script("_value*100/"+field.getAdditionalProperties().get(field.getColumnName()
						+"_sum"));
				aggregationBuilder = AggregationBuilders.sum(field.getDisplayName()).field(field.getColumnName()).script(script); break;
		}
		return aggregationBuilder;
	}

    /**
     * Set Group Interval.
     * @param field
     * @return
     */
  public static Field setGroupInterval(
      Field field) {
    String interval = dateFormats.get(field.getDateFormat().replaceAll(SPACE_REGX, EMPTY_STRING));
    switch (interval) {
      case "month":
          field.setGroupInterval(
            Field.GroupInterval.MONTH);
        break;
      case "year":
          field.setGroupInterval(
            Field.GroupInterval.YEAR);
        break;
      case "day":
          field
            .setGroupInterval(Field.GroupInterval.DAY);
        break;
      case "hour":
          field.setGroupInterval(
              Field.GroupInterval.HOUR);
        break;
      default:
        throw new IllegalArgumentException(interval + " not present");
    }
    return field;
  }

    /**
     * Build numeric filter to handle different preset values.
     * @param item
     * @param builder
     * @return
     */
	public static List<QueryBuilder> numericFilter(Filter item, List<QueryBuilder> builder)
	{

		if (item.getModel().getOperator().value().equals(Model.Operator.BTW.value())) {
			RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
			rangeQueryBuilder.lte(item.getModel().getValue());
			rangeQueryBuilder.gte(item.getModel().getOtherValue());
			builder.add(rangeQueryBuilder);
		}
		if (item.getModel().getOperator().value().equals(Model.Operator.GT.value())) {
			RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
			rangeQueryBuilder.gt(item.getModel().getValue());
			builder.add(rangeQueryBuilder);
		}
		if (item.getModel().getOperator().value().equals(Model.Operator.GTE.value())) {
			RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
			rangeQueryBuilder.gte(item.getModel().getValue());
			builder.add(rangeQueryBuilder);
		}
		if (item.getModel().getOperator().value().equals(Model.Operator.LT.value())) {

			RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
			rangeQueryBuilder.lt(item.getModel().getValue());
			builder.add(rangeQueryBuilder);
		}
		if (item.getModel().getOperator().value().equals(Model.Operator.LTE.value())) {
			RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(item.getColumnName());
			rangeQueryBuilder.lte(item.getModel().getValue());
			builder.add(rangeQueryBuilder);
		}
		if (item.getModel().getOperator().value().equals(Model.Operator.EQ.value())) {
			TermQueryBuilder termQueryBuilder =
					new TermQueryBuilder(item.getColumnName(), item.getModel().getValue());
			builder.add(termQueryBuilder);
		}
		if (item.getModel().getOperator().value().equals(Model.Operator.NEQ.value())) {
			BoolQueryBuilder boolQueryBuilderIn = new BoolQueryBuilder();
			boolQueryBuilderIn.mustNot(new TermQueryBuilder(item.getColumnName(), item.getModel()
					.getValue()));
			builder.add(boolQueryBuilderIn);
		}
		return builder;
	}

    /**
     *  Build String filter to handle case insensitive filter.
     * @param item
     * @param builder
     * @return
     */
	public static List<QueryBuilder> stringFilter(Filter item, List<QueryBuilder> builder)
	{
        if(item.getModel().getOperator().value().equals(Model.Operator.EQ.value()) ||
            item.getModel().getOperator().value().equals(Model.Operator.ISIN.value())) {
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

        if (item.getModel().getOperator().value().equals(Model.Operator.NEQ.value()) ||
            item.getModel().getOperator().value().equals(Model.Operator.ISNOTIN.value())) {
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
		if (item.getModel().getOperator().value().equals(Model.Operator.SW.value())) {
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
		if (item.getModel().getOperator().value().equals(Model.Operator.EW.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*"+item.getModel().getModelValues().get(0));
            WildcardQueryBuilder wqb1 = new WildcardQueryBuilder(buildFilterColumn(item.getColumnName()),
                "*"+  (String) ((String) item.getModel().getModelValues().get(0)).toLowerCase());
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(wqb);
            boolQueryBuilder.should(wqb1);
            builder.add(boolQueryBuilder);
		}

		// same for contains clause - not analyzed query
		if (item.getModel().getOperator().value().equals(Model.Operator.CONTAINS.value())) {
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

    /**
     *  To get the aggregation builder for data fields.
     * @param dataFields
     * @param preSearchSourceBuilder
     * @return
     */
	public static void getAggregationBuilder(List<?> dataFields, SearchSourceBuilder preSearchSourceBuilder)
    {

                    for (Object dataField : dataFields) {
                        if (dataField instanceof com.synchronoss.saw.model.Field) {
                            Field field = (Field) dataField;
                            if (field.getAggregate().value().equalsIgnoreCase(Field.Aggregate.PERCENTAGE.value())) {
                                preSearchSourceBuilder.aggregation(AggregationBuilders.sum(
                                    field.getDisplayName()).field(field.getColumnName()));
                            }
                        }
                    }
              //  return aggregationBuilder;
    }

   /**
     * query builder for DSK node.
     * TODO: Original DSK was supporting only string format, So query builder is in place only for String.
     * @param dataSecurityKeyNode
     * @param builder
     */
    public static List<QueryBuilder> queryDSKBuilder(DataSecurityKey dataSecurityKeyNode , List<QueryBuilder> builder) {
        if (dataSecurityKeyNode != null) {
            for (DataSecurityKeyDef dsk : dataSecurityKeyNode.getDataSecuritykey()) {
                TermsQueryBuilder termsQueryBuilder =
                    new TermsQueryBuilder(dsk.getName().concat(BuilderUtil.SUFFIX), dsk.getValues());
                List<?> modelValues = QueryBuilderUtil.buildStringTermsfilter(dsk.getValues());
                TermsQueryBuilder termsQueryBuilder1 =
                    new TermsQueryBuilder(QueryBuilderUtil.buildFilterColumn(dsk.getName()), modelValues);
                BoolQueryBuilder dataSecurityBuilder = new BoolQueryBuilder();
                dataSecurityBuilder.should(termsQueryBuilder);
                dataSecurityBuilder.should(termsQueryBuilder1);
                builder.add(dataSecurityBuilder);
            }
        }
        return builder;
    }

    /**
     *  To build the report data ( Without elasticsearch aggregation).
     * @param jsonNode
     * @return
     */
    public static List<Object> buildReportData(JsonNode jsonNode)
    {
        Iterator<JsonNode> recordIterator = jsonNode.get(HITS).get(HITS).iterator();
        List<Object> data = new ArrayList<>();
        while(recordIterator.hasNext())
        {
            JsonNode source = recordIterator.next();
            data.add(source.get(_SOURCE));
        }
        return data;
    }
}
