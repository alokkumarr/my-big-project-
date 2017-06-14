package com.synchronoss;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BuilderUtil 
{

	/**
	 * This method is used to load the json string to object tree
	 * @param jsonString
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static com.synchronoss.querybuilder.model.pivot.SqlBuilder getNodeTree (String jsonString, String node) throws JsonProcessingException, IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		JsonNode objectNode = objectMapper.readTree(jsonString);
		JsonNode sqlNode = objectNode.get(node);
		com.synchronoss.querybuilder.model.pivot.SqlBuilder sqlBuilderNode = objectMapper.treeToValue(sqlNode, com.synchronoss.querybuilder.model.pivot.SqlBuilder.class);
		return sqlBuilderNode;
	}

    public static com.synchronoss.querybuilder.model.chart.SqlBuilder getNodeTreeChart(String jsonString, String node) throws JsonProcessingException, IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        JsonNode objectNode = objectMapper.readTree(jsonString);
        JsonNode sqlNode = objectNode.get(node);
        com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode = objectMapper.treeToValue(sqlNode, com.synchronoss.querybuilder.model.chart.SqlBuilder.class);
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
