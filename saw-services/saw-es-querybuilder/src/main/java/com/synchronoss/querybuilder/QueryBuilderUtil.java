package com.synchronoss.querybuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Order;

import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.Filter;
import com.synchronoss.querybuilder.model.chart.NodeField;
import com.synchronoss.querybuilder.model.pivot.ColumnField;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;

public class QueryBuilderUtil {
	
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String SPACE_REGX = "\\s+";
	public final static String EMPTY_STRING = "";
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
	
	public static AggregationBuilder aggregationBuilder (com.synchronoss.querybuilder.model.pivot.ColumnField columnField, 
	     String aggregationName)

	{
		AggregationBuilder aggregationBuilder = null;
		
		if (columnField.getType().name().equals(ColumnField.Type.DATE.name()) 
		    || columnField.getType().name().equals(ColumnField.Type.TIMESTAMP.name()))
		{
		  if (columnField.getGroupInterval()!=null){
			aggregationBuilder = AggregationBuilders.
					dateHistogram(aggregationName).field(columnField.getColumnName()).format(DATE_FORMAT).
					dateHistogramInterval(groupInterval(columnField.getGroupInterval().value())).order(Order.KEY_DESC);
			}
		  else {
		    aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName())
		        .format(DATE_FORMAT).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false)).size(BuilderUtil.SIZE);
		  }
		}
		else {
          aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName()).size(BuilderUtil.SIZE);
		}
		
		return aggregationBuilder;
	}
	
	public static AggregationBuilder aggregationBuilderRow(com.synchronoss.querybuilder.model.pivot.RowField rowField, 
		     String aggregationName)

		{
			AggregationBuilder aggregationBuilder = null;
			
			if (rowField.getType().name().equals(ColumnField.Type.DATE.name()) || rowField.getType().name().equals(ColumnField.Type.TIMESTAMP.name())){
			  if (rowField.getGroupInterval()!=null){
	            aggregationBuilder = AggregationBuilders.
	                    dateHistogram(aggregationName).field(rowField.getColumnName()).format(DATE_FORMAT).
	                    dateHistogramInterval(groupInterval(rowField.getGroupInterval().value())).order(Order.KEY_DESC);
	            }
	          else {
	            aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(rowField.getColumnName())
	                .format(DATE_FORMAT).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false)).size(BuilderUtil.SIZE);
	          }
			}
			else {
	          aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(rowField.getColumnName()).size(BuilderUtil.SIZE);
			}
			
			return aggregationBuilder;
		}
	
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
     
 	public static AggregationBuilder aggregationBuilderDataField(com.synchronoss.querybuilder.model.pivot.DataField data)

 	{
 		AggregationBuilder aggregationBuilder = null;
 			switch (data.getAggregate().value())
 			{
 			case "sum" : aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
 			case "avg" : aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
 			case "min" : aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
 			case "max" : aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
 			case "count" : aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
 			}
 		
 		return aggregationBuilder;
 	}

    public static AggregationBuilder aggregationBuilderDataFieldChart(com.synchronoss.querybuilder.model.chart.DataField data)

    {
        AggregationBuilder aggregationBuilder = null;
            switch (data.getAggregate().value())
            {
            case "sum" : aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
            case "avg" : aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
            case "min" : aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
            case "max" : aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
            case "count" : aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
            }
        
        return aggregationBuilder;
    }
 	
 	
 	/**
 	 * This aggregation framework
 	 * @param splitBy
 	 * @return
 	 */
	public static AggregationBuilder aggregationBuilderChart(com.synchronoss.querybuilder.model.chart.NodeField nodeField, String nodeName)
	{
		AggregationBuilder aggregationBuilder = null;
		
		if (nodeField.getType().name().equals(NodeField.Type.DATE.name()) || nodeField.getType().name().equals(NodeField.Type.TIMESTAMP.name()) ){
		  nodeField = setGroupIntervalChart(nodeField);
  		  if (nodeField.getGroupInterval()!=null){
              aggregationBuilder = AggregationBuilders.
                      dateHistogram(nodeName).field(nodeField.getColumnName()).format(nodeField.getDateFormat()).
                      dateHistogramInterval(groupInterval(nodeField.getGroupInterval().value())).order(Order.KEY_DESC);
              }
            else {
              aggregationBuilder =  AggregationBuilders.terms(nodeName).field(nodeField.getColumnName())
                  .format(nodeField.getDateFormat()).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false)).size(BuilderUtil.SIZE);
            }
		}
		else{
			aggregationBuilder = AggregationBuilders.terms(nodeName).field(nodeField.getColumnName()).size(BuilderUtil.SIZE);
		}
		return aggregationBuilder;
	}	

  public static com.synchronoss.querybuilder.model.chart.NodeField setGroupIntervalChart(
      com.synchronoss.querybuilder.model.chart.NodeField nodeField) {
    String interval = dateFormats.get(nodeField.getDateFormat().replaceAll(SPACE_REGX, EMPTY_STRING));
    switch (interval) {
      case "month":
        nodeField.setGroupInterval(
            com.synchronoss.querybuilder.model.chart.NodeField.GroupInterval.MONTH);
        break;
      case "year":
        nodeField.setGroupInterval(
            com.synchronoss.querybuilder.model.chart.NodeField.GroupInterval.YEAR);
        break;
      case "day":
        nodeField
            .setGroupInterval(com.synchronoss.querybuilder.model.chart.NodeField.GroupInterval.DAY);
        break;
      case "hour":
        nodeField.setGroupInterval(
            com.synchronoss.querybuilder.model.chart.NodeField.GroupInterval.HOUR);
        break;
      default:
        throw new IllegalArgumentException(interval + " not present");
    }
    return nodeField;
  }

	public static List<QueryBuilder> numericFilterChart (Filter item, List<QueryBuilder> builder)
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

	public static List<QueryBuilder> stringFilterChart (Filter item, List<QueryBuilder> builder)
	{
		// EQ
		if(item.getModel().getOperator().value().equals(Operator.EQ.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getStringValue());
			builder.add(termsQueryBuilder);
		}

		// NEQ
		if (item.getModel().getOperator().value().equals(Operator.NEQ.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getStringValue());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}


		// prefix query builder - not analyzed
		if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
			PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
					item.getModel().getStringValue());
			builder.add(pqb);
		}

		// using wildcard as there's no suffix query type provided by
		// elasticsearch
		if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*"+item.getModel().getStringValue());
			builder.add(wqb);
		}

		// same for contains clause - not analyzed query
		if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*" + item.getModel().getStringValue()+"*");
			builder.add(wqb);
		}


		if (item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			builder.add(termsQueryBuilder);
		}

		if (item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}

		return builder;
	}
	

	public static List<QueryBuilder> numericFilterPivot (com.synchronoss.querybuilder.model.pivot.Filter item, List<QueryBuilder> builder)
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

	public static List<QueryBuilder> stringFilterPivot (com.synchronoss.querybuilder.model.pivot.Filter item, List<QueryBuilder> builder)
	{
		// EQ
		if(item.getModel().getOperator().value().equals(Operator.EQ.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getStringValue());
			builder.add(termsQueryBuilder);
		}

		// NEQ
		if (item.getModel().getOperator().value().equals(Operator.NEQ.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getStringValue());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}


		// prefix query builder - not analyzed
		if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
			PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
					item.getModel().getStringValue());
			builder.add(pqb);
		}

		// using wildcard as there's no suffix query type provided by
		// elasticsearch
		if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*"+item.getModel().getStringValue());
			builder.add(wqb);
		}

		// same for contains clause - not analyzed query
		if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*" + item.getModel().getStringValue()+"*");
			builder.add(wqb);
		}


		if (item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			builder.add(termsQueryBuilder);
		}

		if (item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}

		return builder;
	}
}
