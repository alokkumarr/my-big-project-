package com.synchronoss.querybuilder.report;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReportParametersUnitTest {

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
    dataResource = classLoader.getResource("data/report_type_data.json");
    dataFile = new File(dataResource.getPath());
    schemaResource = classLoader.getResource("schema/report_querybuilder_schema.json");
    schemaFile = new File(schemaResource.getPath());
  }

  @Test
  public void loadReportJSONTest() {
    try {
      JsonNode objectNode = objectMapper.readTree(dataFile);
      Assert.assertNotNull(objectNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("File not found"));
    }

  }
  @Test
  public void validateReportJSONTest() {
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
  public void reportNodeNotNullTest(){
    JsonNode objectNode =null;
    try {
      objectNode = objectMapper.readTree(dataFile);
      String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
      JsonNode objectNode1 = objectMapper.readTree(json);
      com.synchronoss.querybuilder.model.report.SqlBuilderReport sqlBuilderNodeReport =
          objectMapper.treeToValue(objectNode1, com.synchronoss.querybuilder.model.report.SqlBuilderReport.class);
      com.synchronoss.querybuilder.model.report.SqlBuilder sqlBuilderNode = sqlBuilderNodeReport.getSqlBuilder();
      Assert.assertNotNull(sqlBuilderNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    }
    
  }
}


