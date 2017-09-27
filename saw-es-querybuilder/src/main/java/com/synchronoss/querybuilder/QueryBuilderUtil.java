package com.synchronoss.querybuilder;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Order;

import com.synchronoss.querybuilder.model.chart.NodeField;
import com.synchronoss.querybuilder.model.pivot.ColumnField;

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
		        .format(DATE_FORMAT).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false));
		  }
		}
		else {
          aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName());
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
	                .format(DATE_FORMAT).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false));
	          }
			}
			else {
	          aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(rowField.getColumnName());
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
                .format(DATE_FORMAT).order(org.elasticsearch.search.aggregations.bucket.terms.Terms.Order.term(false));
          }
		}
		else{
			aggregationBuilder = AggregationBuilders.terms(nodeName).field(nodeField.getColumnName());
		}
		return aggregationBuilder;
	}	
	
}
