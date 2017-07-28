package com.synchronoss.querybuilder.chart;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

public class ChartParametersUnitTest {

  private ObjectMapper objectMapper = null;
  private JsonSchemaFactory factory = null;
  private JsonValidator validator = null;
  private File dataFile = null;
  private File schemaFile = null;
  private URL dataResource = null;
  private URL schemaResource = null;

  @Before
  public void beforeTest() {
    objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    factory = JsonSchemaFactory.byDefault();
    validator = factory.getValidator();
    ClassLoader classLoader = getClass().getClassLoader();
    dataResource = classLoader.getResource("data/chart_type_data.json");
    dataFile = new File(dataResource.getPath());
    schemaResource = classLoader.getResource("schema/chart_querybuilder_schema.json");
    schemaFile = new File(schemaResource.getPath());
  }

  @Test
  public void loadChartJSONTest() {
    try {
      JsonNode objectNode = objectMapper.readTree(dataFile);
      Assert.assertNotNull(objectNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("File not found"));
    }

  }
  @Test
  public void validateChartJSONTest() {
    JsonNode objectNode =null;
    try {
      objectNode = objectMapper.readTree(dataFile);
      String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
      validator = factory.getValidator();
      final JsonNode data = JsonLoader.fromString(json);
      final JsonNode schema =JsonLoader.fromFile(schemaFile);
      ProcessingReport report = validator.validate(schema, data);
      Assert.assertTrue(report.isSuccess());
    } catch (IOException | ProcessingException e) {
      assertThat(e.getMessage(), is("IOException"));    }

  }
  
  @Test
  public void chartNodeNotNullTest(){
    JsonNode objectNode =null;
    try {
      objectNode = objectMapper.readTree(dataFile);
      String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
      JsonNode objectNode1 = objectMapper.readTree(json);
      com.synchronoss.querybuilder.model.chart.SqlBuilderChart sqlBuilderNodeChart =
          objectMapper.treeToValue(objectNode1, com.synchronoss.querybuilder.model.chart.SqlBuilderChart.class);
      com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode = sqlBuilderNodeChart.getSqlBuilder();
      Assert.assertNotNull(sqlBuilderNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    }
    
  }
}


