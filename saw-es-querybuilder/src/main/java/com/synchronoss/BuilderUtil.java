package com.synchronoss;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

public class BuilderUtil 
{

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
}
