package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Order;

import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.Filter;
import com.synchronoss.querybuilder.model.chart.NodeField;
import com.synchronoss.querybuilder.model.pivot.ColumnField;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;

public class QueryBuilderUtil {
	
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	
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
			
		  if (nodeField.getGroupInterval()!=null){
            aggregationBuilder = AggregationBuilders.
                    dateHistogram(nodeName).field(nodeField.getColumnName()).format(DATE_FORMAT).
                    dateHistogramInterval(groupInterval(nodeField.getGroupInterval().value())).order(Order.KEY_DESC);
            }
          else {
            aggregationBuilder =  AggregationBuilders.terms(nodeName).field(nodeField.getColumnName())
                .format(DATE_FORMAT).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false)).size(BuilderUtil.SIZE);
          }
		}
		else{
			aggregationBuilder = AggregationBuilders.terms(nodeName).field(nodeField.getColumnName()).size(BuilderUtil.SIZE);
		}
		return aggregationBuilder;
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
	
}
