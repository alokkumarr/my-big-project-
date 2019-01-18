package com.synchronoss.querybuilder;

import com.synchronoss.BuilderUtil;
import com.synchronoss.querybuilder.model.chart.DataField;
import com.synchronoss.querybuilder.model.chart.Filter;
import com.synchronoss.querybuilder.model.chart.NodeField;
import com.synchronoss.querybuilder.model.pivot.ColumnField;
import com.synchronoss.querybuilder.model.pivot.Model.Operator;
import com.synchronoss.querybuilder.model.report.Column;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.*;

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

  public static String ifDateFormatNull(String format)  {
	    if ( format == null )   {
	        format = DATE_FORMAT;
        }
        return format;
  }
	
	public static AggregationBuilder aggregationBuilder (com.synchronoss.querybuilder.model.pivot.ColumnField columnField, 
	     String aggregationName)

	{
		AggregationBuilder aggregationBuilder = null;
        String dateFormat = ifDateFormatNull(columnField.getDateFormat());
		
		if (columnField.getType().name().equals(ColumnField.Type.DATE.name()) 
		    || columnField.getType().name().equals(ColumnField.Type.TIMESTAMP.name()))
		{
		  if (columnField.getGroupInterval()!=null){
		      aggregationBuilder = AggregationBuilders.
					dateHistogram(aggregationName).field(columnField.getColumnName()).format(dateFormat).
					dateHistogramInterval(groupInterval(columnField.getGroupInterval().value())).order(BucketOrder.key(false));
			}
		  else {
		    aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName())
		        .format(dateFormat).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
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
            String dateFormat = ifDateFormatNull(rowField.getDateFormat());

			if (rowField.getType().name().equals(ColumnField.Type.DATE.name()) || rowField.getType().name().equals(ColumnField.Type.TIMESTAMP.name())){
			  if (rowField.getGroupInterval()!=null){
	            aggregationBuilder = AggregationBuilders.
	                    dateHistogram(aggregationName).field(rowField.getColumnName()).format(dateFormat).
	                    dateHistogramInterval(groupInterval(rowField.getGroupInterval().value())).order(BucketOrder.key(false));
	            }
	          else {
	            aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(rowField.getColumnName())
	                .format(dateFormat).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
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
                case DISTINCT_COUNT: aggregationBuilder = AggregationBuilders.cardinality(data.getName()).field(data.getColumnName()); break;
                case PERCENTAGE:
					Script script = new Script("_value*100/"+data.getAdditionalProperties().get(data.getColumnName()
                            +"_sum"));
					aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()).script(script); break;
 			}
 		
 		return aggregationBuilder;
 	}

    public static AggregationBuilder aggregationBuilderColumn(com.synchronoss.querybuilder.model.pivot.ColumnField columnField,
                                                             String aggregationName)

    {
        AggregationBuilder aggregationBuilder = null;
        String dateFormat = ifDateFormatNull(columnField.getDateFormat());

        if (columnField.getType().name().equals(ColumnField.Type.DATE.name()) ||
            columnField.getType().name().equals(ColumnField.Type.TIMESTAMP.name())){
            if (columnField.getGroupInterval()!=null){
                aggregationBuilder = AggregationBuilders.
                    dateHistogram(aggregationName).field(columnField.getColumnName()).format(dateFormat).
                    dateHistogramInterval(groupInterval(columnField.getGroupInterval().value())).order(BucketOrder.key(false));
            }
            else {
                aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName())
                    .format(dateFormat).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
            }
        }
        else {
            aggregationBuilder =  AggregationBuilders.terms(aggregationName).field(columnField.getColumnName()).size(BuilderUtil.SIZE);
        }

        return aggregationBuilder;
    }

	public static AggregationBuilder aggregationBuilderDataFieldReport(Column data)
	{
		AggregationBuilder aggregationBuilder = null;
		switch (data.getAggregate())
		{
			case SUM: aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
			case AVG: aggregationBuilder = AggregationBuilders.avg(data.getName()).field(data.getColumnName()); break;
			case MIN: aggregationBuilder = AggregationBuilders.min(data.getName()).field(data.getColumnName()); break;
			case MAX: aggregationBuilder = AggregationBuilders.max(data.getName()).field(data.getColumnName()); break;
			case COUNT: aggregationBuilder = AggregationBuilders.count(data.getName()).field(data.getColumnName()); break;
            case DISTINCT_COUNT: aggregationBuilder = AggregationBuilders.cardinality(data.getName()).field(data.getColumnName()); break;
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
                case DISTINCT_COUNT:
                    aggregationBuilder = AggregationBuilders.cardinality(data.getName()+_COUNT).field(data.getColumnName());
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
                case DISTINCT_COUNT: aggregationBuilder = AggregationBuilders.cardinality(data.getName()).field(data.getColumnName()); break;
                case PERCENTAGE_BY_ROW: aggregationBuilder = AggregationBuilders.sum(data.getName()).field(data.getColumnName()); break;
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
	 * @return
	 */
	public static AggregationBuilder aggregationBuilderChart(com.synchronoss.querybuilder.model.chart.NodeField nodeField, String nodeName)
	{
		AggregationBuilder aggregationBuilder = null;
        String dateFormat = ifDateFormatNull(nodeField.getDateFormat());
		
		if (nodeField.getType().name().equals(NodeField.Type.DATE.name()) || nodeField.getType().name().equals(NodeField.Type.TIMESTAMP.name()) ){
		  nodeField = setGroupIntervalChart(nodeField);
  		  if (nodeField.getGroupInterval()!=null){
              aggregationBuilder = AggregationBuilders.
                      dateHistogram(nodeName).field(nodeField.getColumnName()).format(dateFormat).
                      dateHistogramInterval(groupInterval(nodeField.getGroupInterval().value())).order(BucketOrder.key(false));
              }
            else {
              aggregationBuilder =  AggregationBuilders.terms(nodeName).field(nodeField.getColumnName())
                  .format(dateFormat).order(BucketOrder.key(false)).size(BuilderUtil.SIZE);
            }
		}
		else{
			aggregationBuilder = AggregationBuilders.terms(nodeName).field(nodeField.getColumnName()).size(BuilderUtil.SIZE);
		}
		return aggregationBuilder;
	}	

  public static com.synchronoss.querybuilder.model.chart.NodeField setGroupIntervalChart(
      com.synchronoss.querybuilder.model.chart.NodeField nodeField) {
      String dateFormat = ifDateFormatNull(nodeField.getDateFormat());

    String interval = dateFormats.get(dateFormat.replaceAll(SPACE_REGX, EMPTY_STRING));
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
					"*"+item.getModel().getModelValues().get(0));
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
					"*"+item.getModel().getModelValues().get(0));
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

	public static List<QueryBuilder> stringFilterReport (com.synchronoss.querybuilder.model.report.Filter item, List<QueryBuilder> builder)
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
					"*"+item.getModel().getModelValues().get(0));
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
                        } else if (dataField instanceof com.synchronoss.querybuilder.model.report.Column) {
                            Column data =
                                    (Column) dataField;
                            if (data.getAggregate()!=null && data.getAggregate().value().equalsIgnoreCase(DataField.Aggregate.PERCENTAGE.value())) {
                                preSearchSourceBuilder.aggregation(AggregationBuilders.sum(
                                        data.getName()).field(data.getColumnName()));
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
}
