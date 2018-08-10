package com.synchronoss.querybuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synchronoss.querybuilder.model.chart.DataField;
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

import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.Filter;
import com.synchronoss.querybuilder.model.chart.NodeField;
import com.synchronoss.querybuilder.model.pivot.ColumnField;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import static com.synchronoss.AggregationConstants.*;


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
					dateHistogramInterval(groupInterval(columnField.getGroupInterval().value())).order(BucketOrder.key(false));
			}
		  else {
		    aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName())
		        .format(DATE_FORMAT).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
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
	                    dateHistogramInterval(groupInterval(rowField.getGroupInterval().value())).order(BucketOrder.key(false));
	            }
	          else {
	            aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(rowField.getColumnName())
	                .format(DATE_FORMAT).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
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
 			switch (data.getAggregate())
 			{
				case SUM: aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
				case AVG: aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
				case MIN: aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
				case MAX: aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
				case COUNT: aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
				case PERCENTAGE:
					Script script = new Script("_value*100/"+data.getAdditionalProperties().get(data.getColumnName()
                            +"_sum"));
					aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()).script(script); break;
 			}
 		
 		return aggregationBuilder;
 	}

	public static AggregationBuilder aggregationBuilderDataFieldReport(com.synchronoss.querybuilder.model.report.DataField data)
	{
		AggregationBuilder aggregationBuilder = null;
		switch (data.getAggregate())
		{
			case SUM: aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
			case AVG: aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
			case MIN: aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
			case MAX: aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
			case COUNT: aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
			case PERCENTAGE:
				Script script = new Script("_value*100/"+data.getAdditionalProperties().get(data.getColumnName()
						+"_sum"));
				aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()).script(script); break;
		}

		return aggregationBuilder;
	}

    public static SearchSourceBuilder aggregationBuilderDataFieldKPI(com.synchronoss.querybuilder.model.kpi.DataField data,
                                                                    SearchSourceBuilder searchSourceBuilder
    )
    {
        for(com.synchronoss.querybuilder.model.kpi.DataField.Aggregate aggregate : data.getAggregate()) {

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

    public static AggregationBuilder aggregationBuilderDataFieldChart(com.synchronoss.querybuilder.model.chart.DataField data)

    {
        AggregationBuilder aggregationBuilder = null;
            switch (data.getAggregate())
            {
				case SUM: aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
				case AVG: aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
				case MIN: aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
				case MAX: aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
				case COUNT: aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
				case PERCENTAGE:
					Script script = new Script("_value*100/"+data.getAdditionalProperties().get(data.getColumnName()
                            +"_sum"));
					aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()).script(script); break;
            }
        
        return aggregationBuilder;
    }


	/**
	 * This aggregation framework
	 * @param nodeField
	 * @param nodeName
	 * @param b
     * @return
	 */
	public static AggregationBuilder aggregationBuilderChart(NodeField nodeField, String nodeName,
                                                             DataField.LimitType type, Integer size, boolean apply)
	{
		AggregationBuilder aggregationBuilder = null;
		boolean bucketOrder = false;

		// check if we need to apply top/Bottom size. If N/A then
        // apply standard builder limit.
		if(!apply)
		    size = BuilderUtil.SIZE;

		if (type==DataField.LimitType.BOTTOM)
		    bucketOrder=true;

		if (nodeField.getType().name().equals(NodeField.Type.DATE.name()) || nodeField.getType().name().equals(NodeField.Type.TIMESTAMP.name()) ){
		  nodeField = setGroupIntervalChart(nodeField);
  		  if (nodeField.getGroupInterval()!=null){
              aggregationBuilder = AggregationBuilders.
                      dateHistogram(nodeName).field(nodeField.getColumnName()).format(nodeField.getDateFormat()).
                      dateHistogramInterval(groupInterval(nodeField.getGroupInterval().value())).order(BucketOrder.key(bucketOrder));
              }
            else {
              aggregationBuilder =  AggregationBuilders.terms(nodeName).field(nodeField.getColumnName())
                  .format(nodeField.getDateFormat()).order(BucketOrder.key(bucketOrder)).size(size);
            }
		}
		else{
		    if (type==null)
			aggregationBuilder = AggregationBuilders.terms(nodeName).field(nodeField.getColumnName()).size(size);
		    else
		        aggregationBuilder = AggregationBuilders.terms(nodeName).field(nodeField.getColumnName()).
                    order(BucketOrder.key(bucketOrder)).size(size);
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
		if(item.getModel().getOperator().value().equals(Operator.EQ.value()) ||
				item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			builder.add(termsQueryBuilder);
		}

		if (item.getModel().getOperator().value().equals(Operator.NEQ.value()) ||
				item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}


		// prefix query builder - not analyzed
		if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
			PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
					(String) item.getModel().getModelValues().get(0));
			builder.add(pqb);
		}

		// using wildcard as there's no suffix query type provided by
		// elasticsearch
		if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*"+item.getModel().getModelValues().get(0));
			builder.add(wqb);
		}

		// same for contains clause - not analyzed query
		if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*" + item.getModel().getModelValues().get(0)+"*");
			builder.add(wqb);
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
	public static List<QueryBuilder> numericFilterReport (com.synchronoss.querybuilder.model.report.Filter item, List<QueryBuilder> builder)
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

    public static List<QueryBuilder> numericFilterKPI (com.synchronoss.querybuilder.model.kpi.Filter item, List<QueryBuilder> builder)
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
		if(item.getModel().getOperator().value().equals(Operator.EQ.value()) ||
				item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			builder.add(termsQueryBuilder);
		}

		if (item.getModel().getOperator().value().equals(Operator.NEQ.value()) ||
				item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}


		// prefix query builder - not analyzed
		if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
			PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
					(String) item.getModel().getModelValues().get(0));
			builder.add(pqb);
		}

		// using wildcard as there's no suffix query type provided by
		// elasticsearch
		if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*"+item.getModel().getModelValues().get(0));
			builder.add(wqb);
		}

		// same for contains clause - not analyzed query
		if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*" + item.getModel().getModelValues().get(0)+"*");
			builder.add(wqb);
		}

		return builder;
	}

	public static List<QueryBuilder> stringFilterReport (com.synchronoss.querybuilder.model.report.Filter item, List<QueryBuilder> builder)
	{
		if(item.getModel().getOperator().value().equals(Operator.EQ.value()) ||
				item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
			TermsQueryBuilder termsQueryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			builder.add(termsQueryBuilder);
		}

		if (item.getModel().getOperator().value().equals(Operator.NEQ.value()) ||
				item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
			QueryBuilder qeuryBuilder =
					new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			boolQueryBuilder.mustNot(qeuryBuilder);
			builder.add(boolQueryBuilder);
		}

		// prefix query builder - not analyzed
		if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
			PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
					(String) item.getModel().getModelValues().get(0));
			builder.add(pqb);
		}

		// using wildcard as there's no suffix query type provided by
		// elasticsearch
		if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*"+item.getModel().getModelValues().get(0));
			builder.add(wqb);
		}

		// same for contains clause - not analyzed query
		if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
			WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
					"*" + item.getModel().getModelValues().get(0)+"*");
			builder.add(wqb);
		}

		return builder;
	}


    /**
     * string filter for the KPI builder.
     * @param item
     * @param builder
     * @return
     */
    public static List<QueryBuilder> stringFilterKPI(com.synchronoss.querybuilder.model.kpi.Filter item, List<QueryBuilder> builder)
    {
        if(item.getModel().getOperator().value().equals(Operator.EQ.value()) ||
            item.getModel().getOperator().value().equals(Operator.ISIN.value())) {
            TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            builder.add(termsQueryBuilder);
        }

        if (item.getModel().getOperator().value().equals(Operator.NEQ.value()) ||
            item.getModel().getOperator().value().equals(Operator.ISNOTIN.value())) {
            QueryBuilder qeuryBuilder =
                new TermsQueryBuilder(item.getColumnName(), item.getModel().getModelValues());
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.mustNot(qeuryBuilder);
            builder.add(boolQueryBuilder);
        }

        // prefix query builder - not analyzed
        if (item.getModel().getOperator().value().equals(Operator.SW.value())) {
            PrefixQueryBuilder pqb = new PrefixQueryBuilder(item.getColumnName(),
                (String) item.getModel().getModelValues().get(0));
            builder.add(pqb);
        }

        // using wildcard as there's no suffix query type provided by
        // elasticsearch
        if (item.getModel().getOperator().value().equals(Operator.EW.value())) {
            WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
                "*"+item.getModel().getModelValues().get(0));
            builder.add(wqb);
        }

        // same for contains clause - not analyzed query
        if (item.getModel().getOperator().value().equals(Operator.CONTAINS.value())) {
            WildcardQueryBuilder wqb = new WildcardQueryBuilder(item.getColumnName(),
                "*" + item.getModel().getModelValues().get(0)+"*");
            builder.add(wqb);
        }

        return builder;
    }

    /**
     *
     * @param dataFields
     * @param preSearchSourceBuilder
     * @return
     */
	public static void getAggregationBuilder(List<?> dataFields, SearchSourceBuilder preSearchSourceBuilder)
    {

                    for (Object dataField : dataFields) {
                        if (dataField instanceof com.synchronoss.querybuilder.model.chart.DataField) {
                            com.synchronoss.querybuilder.model.chart.DataField data =
                                    (com.synchronoss.querybuilder.model.chart.DataField) dataField;
                            if (data.getAggregate().value().equalsIgnoreCase(DataField.Aggregate.PERCENTAGE.value())) {
                                preSearchSourceBuilder.aggregation(AggregationBuilders.sum(
                                        data.getName()).field(data.getColumnName()));
                            }
                        } else if (dataField instanceof com.synchronoss.querybuilder.model.pivot.DataField) {
                            com.synchronoss.querybuilder.model.pivot.DataField data =
                                    (com.synchronoss.querybuilder.model.pivot.DataField) dataField;
                            if (data.getAggregate().value().equalsIgnoreCase(DataField.Aggregate.PERCENTAGE.value())) {
                                preSearchSourceBuilder.aggregation(AggregationBuilders.sum(
                                        data.getName()).field(data.getColumnName()));
                            }
                        } else if (dataField instanceof com.synchronoss.querybuilder.model.report.DataField) {
                            com.synchronoss.querybuilder.model.report.DataField data =
                                    (com.synchronoss.querybuilder.model.report.DataField) dataField;
                            if (data.getAggregate()!=null && data.getAggregate().value().equalsIgnoreCase(DataField.Aggregate.PERCENTAGE.value())) {
                                preSearchSourceBuilder.aggregation(AggregationBuilders.sum(
                                        data.getName()).field(data.getColumnName()));
                            }
                        }
                    }
              //  return aggregationBuilder;
    }
}
