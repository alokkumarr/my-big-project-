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

public class ChartJsonStructureUnitTest {

  private ObjectMapper objectMapper = null;
  private File dataFile = null;
  private URL dataResource = null;
  private com.synchronoss.querybuilder.model.chart.SqlBuilderChart sqlBuilderNodeChart =  null;

  @Before
  public void beforeTest() {
    objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    ClassLoader classLoader = getClass().getClassLoader();
    dataResource = classLoader.getResource("data/chart_type_data.json");
    dataFile = new File(dataResource.getPath());
    JsonNode objectNode =null;
    try {
      objectNode = objectMapper.readTree(dataFile);
      String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
      JsonNode objectNode1 = objectMapper.readTree(json);
      sqlBuilderNodeChart = objectMapper.treeToValue(objectNode1, com.synchronoss.querybuilder.model.chart.SqlBuilderChart.class);
      com.synchronoss.querybuilder.model.chart.SqlBuilder sqlBuilderNode = sqlBuilderNodeChart.getSqlBuilder();
      Assert.assertNotNull(sqlBuilderNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    }

  }

  @Test
  public void checkMandatoryFieldsForChartQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodeChart.getSqlBuilder().getBooleanCriteria());
    Assert.assertNotNull(sqlBuilderNodeChart.getSqlBuilder().getDataFields());
    Assert.assertNotNull(sqlBuilderNodeChart.getSqlBuilder().getFilters());
    Assert.assertNotNull(sqlBuilderNodeChart.getSqlBuilder().getSorts());
    Assert.assertNotNull(sqlBuilderNodeChart.getSqlBuilder().getNodeFields());
  }
  @Test
  public void checkCriterionForChartQueryBuilderTest()
  {
    Assert.assertTrue("Boolean Criteria in sqlbuilder can be either AND or OR", sqlBuilderNodeChart.getSqlBuilder().getBooleanCriteria().value().equals("AND") 
        || sqlBuilderNodeChart.getSqlBuilder().getBooleanCriteria().value().equals("OR"));
    Assert.assertTrue("Node Fields array cannot be greater than 3 because it's represents 3 axes.", sqlBuilderNodeChart.getSqlBuilder().getNodeFields().size()<=3);
    Assert.assertTrue("Data Fields array cannot be greater than 3 because it's represents 3 axes.", sqlBuilderNodeChart.getSqlBuilder().getDataFields().size()<=3);
  }
  
  @Test
  public void checkDataTypeSupportedForChartQueryBuilderTest(){
    Assert.assertTrue(!sqlBuilderNodeChart.getSqlBuilder().getNodeFields().isEmpty());
    Assert.assertTrue((sqlBuilderNodeChart.getSqlBuilder().getNodeFields().get(0).getType().value().equals("integer"))
    ||(sqlBuilderNodeChart.getSqlBuilder().getNodeFields().get(0).getType().value().equals("string"))
    ||(sqlBuilderNodeChart.getSqlBuilder().getNodeFields().get(0).getType().value().equals("double"))
    ||(sqlBuilderNodeChart.getSqlBuilder().getNodeFields().get(0).getType().value().equals("long"))
    ||(sqlBuilderNodeChart.getSqlBuilder().getNodeFields().get(0).getType().value().equals("date"))
    ||(sqlBuilderNodeChart.getSqlBuilder().getNodeFields().get(0).getType().value().equals("timestamp")));
  }
  @Test
  public void checkAggregateFunctionSupportedForChartQueryBuilderTest(){
    Assert.assertTrue(!sqlBuilderNodeChart.getSqlBuilder().getDataFields().isEmpty());
    Assert.assertTrue(sqlBuilderNodeChart.getSqlBuilder().getDataFields().size()<=3);
    Assert.assertTrue(
       (sqlBuilderNodeChart.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("sum"))
    || (sqlBuilderNodeChart.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("max"))
    || (sqlBuilderNodeChart.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("min")) 
    || (sqlBuilderNodeChart.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("count"))
    || (sqlBuilderNodeChart.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("avg")));
  }
  
}


