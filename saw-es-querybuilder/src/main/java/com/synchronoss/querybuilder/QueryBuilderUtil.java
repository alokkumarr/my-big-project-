package com.synchronoss.querybuilder;

import java.util.List;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Order;

import com.synchronoss.querybuilder.model.ColumnField;
import com.synchronoss.querybuilder.model.DataField;

public class QueryBuilderUtil {
	
	public static AggregationBuilder aggregationBuilder (ColumnField columnField, List<DataField> dataField, String aggregationName)

	{
		AggregationBuilder aggregationBuilder = null;
		
		if (columnField.getType().equals("date"))
		{
			aggregationBuilder = AggregationBuilders.
					dateHistogram(aggregationName).field(columnField.getColumnName()).
					dateHistogramInterval(groupInterval(columnField.getGroupInterval())).order(Order.KEY_ASC);
			
			if (!(dataField.isEmpty())&& dataField.size()>0)
			{
				aggregationBuilder = AggregationBuilders.
						dateHistogram(aggregationName).field(columnField.getColumnName()).
						dateHistogramInterval(groupInterval(columnField.getGroupInterval())).order(Order.KEY_ASC);
	
			}
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
     
 	public static AggregationBuilder aggregationBuilderDataField(DataField data)

 	{
 		AggregationBuilder aggregationBuilder = null;
 			switch (data.getAggregate().trim())
 			{
 			case "sum" : aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
 			case "avg" : aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
 			case "min" : aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
 			case "max" : aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
 			case "count" : aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
 			}
 		
 		return aggregationBuilder;
 	}
 
	
	
}
