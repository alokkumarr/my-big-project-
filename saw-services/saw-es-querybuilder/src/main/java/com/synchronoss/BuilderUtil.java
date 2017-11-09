package com.synchronoss;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.threeten.extra.YearQuarter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

/**
 * @author spau0004
 *
 */
public class BuilderUtil 
{

     public static final String SUFFIX = ".keyword";
     public static final int SIZE = ((System.getProperty("aggr.es.size")!=null && !System.getProperty("aggr.es.size").equals("")) ? Integer.parseInt(System.getProperty("aggr.es.size")):1000);
  
	/**
	 * This method is used to load the json string to object tree
	 * @param jsonString
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws ProcessingException 
	 */
	public static com.synchronoss.querybuilder.model.pivot.SqlBuilder getNodeTree (String jsonString, String node) throws JsonProcessingException, IOException, ProcessingException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		JsonNode objectNode = objectMapper.readTree(jsonString);
		JsonNode sqlNode = objectNode.get(node);
		// schema validation block starts here
        String json = "{ \"sqlBuilder\" :" + sqlNode.toString() + "}";
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonValidator validator = factory.getValidator();
        final JsonNode data = JsonLoader.fromString(json);
        String pivot = System.getProperty("schema.pivot");
        final JsonNode schema = JsonLoader.fromFile(new File(pivot));
        ProcessingReport report = validator.validate(schema, data);
        if (report.isSuccess() == false) {
          throw new ProcessingException(report.toString());
        }
        // schema validation block ends here
		com.synchronoss.querybuilder.model.pivot.SqlBuilder sqlBuilderNode = 
		    objectMapper.treeToValue(sqlNode, com.synchronoss.querybuilder.model.pivot.SqlBuilder.class);
		return sqlBuilderNode;
	}

    public static com.synchronoss.querybuilder.model.chart.SqlBuilder getNodeTreeChart(String jsonString, String node) 
        throws JsonProcessingException, IOException, ProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        JsonNode objectNode = objectMapper.readTree(jsonString);
        JsonNode sqlNode = objectNode.get(node);
        // schema validation block starts here
        String json = "{ \"sqlBuilder\" :" + sqlNode.toString() + "}";
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonValidator validator = factory.getValidator();
        String chart = System.getProperty("schema.chart");
        if (chart == null){throw new NullPointerException("schema.chart property is not set.");}
        final JsonNode data = JsonLoader.fromString(json);
        final JsonNode schema = JsonLoader.fromFile(new File(chart));
        ProcessingReport report = validator.validate(schema, data);
        if (report.isSuccess() == false) {
          throw new ProcessingException(report.toString());
        }
        // schema validation block ends here
        JsonNode objectNode1 = objectMapper.readTree(json);
        com.synchronoss.querybuilder.model.chart.SqlBuilderChart sqlBuilderNodeChart =
            objectMapper.treeToValue(objectNode1, com.synchronoss.querybuilder.model.chart.SqlBuilderChart.class);
        com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode = sqlBuilderNodeChart.getSqlBuilder();
        return sqlBuilderNode;
    }
	
	public static JsonNode getRepositoryNodeTree (String jsonString, String node) throws JsonProcessingException, IOException
	
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		JsonNode objectNode = objectMapper.readTree(jsonString);
		JsonNode repository = objectNode.get(node);
        return repository;
		
	}
	
	public static String listToJSONString (List<Object> objects) throws JsonProcessingException
	{
	  String jsonString = null;
	  ObjectMapper objectMapper = new ObjectMapper();
	  objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
	  jsonString = objectMapper.writeValueAsString(objects);
	  return jsonString;
	} 
	
	   public static String constructDSKCompatibleString (String dskJSONString) throws JsonProcessingException
	    {
	      return "{\"dataSecurityKey\":" + dskJSONString + "}";
	    } 


  public static DynamicConvertor dynamicDecipher(String dynamic)

  {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String DATE_FORMAT_LTE = "23:59:59";
    String DATE_FORMAT_GTE = "00:00:00";
    String SPACE = " ";
    DynamicConvertor dynamicConvertor = new DynamicConvertor();

    switch (dynamic) {
      case "YTD": {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDay = now.with(TemporalAdjusters.firstDayOfYear());
        dynamicConvertor.setLte(now.format(dateTimeFormatter) + SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(firstDay.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "MTD": {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        dynamicConvertor.setLte(now.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(firstDayOfMonth.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LTM": {
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last3Month = now.minusMonths(3);
        dynamicConvertor.setLte(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(last3Month.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LSM": {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last6Months = now.minusMonths(6);
        dynamicConvertor.setLte(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(last6Months.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LM": {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        dynamicConvertor.setLte(lastMonth.with(TemporalAdjusters.lastDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(lastMonth.with(TemporalAdjusters.firstDayOfMonth()).format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LQ": {
        YearQuarter quarter = YearQuarter.now();
        dynamicConvertor.setLte(quarter.minusQuarters(1).atEndOfQuarter().toString()+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(quarter.minusQuarters(1).atDay(1).toString()+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LW": {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusWeeks(1);
        LocalDateTime startOfWeek =
            lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
        dynamicConvertor.setLte(endOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LSW": {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now.minusWeeks(6);
        LocalDateTime startOfWeek =
            lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        LocalDateTime endOfWeek = lastWeek.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
        dynamicConvertor.setLte(endOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "TW": {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastWeek = now;
        LocalDateTime startOfWeek =
            lastWeek.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(1)));
        dynamicConvertor.setLte(now.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_LTE);
        dynamicConvertor.setGte(startOfWeek.format(dateTimeFormatter)+ SPACE + DATE_FORMAT_GTE);
        break;
      }
      case "LTW": {
     LocalDateTime now = LocalDateTime.now();
     LocalDateTime last2Week = now.minusWeeks(2);
     DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
     LocalDateTime startOfWeek =
         last2Week.with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(2)));
     dynamicConvertor.setLte(startOfWeek.format(dateTimeFormatter));
     dynamicConvertor.setGte(startOfWeek.plusWeeks(2).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).format(dateTimeFormatter));
     break;
   }
      default : throw new IllegalArgumentException(dynamic + " not present");

    }

    return dynamicConvertor;
  }
}
