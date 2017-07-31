package com.synchronoss.querybuilder.pivot;

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

public class PivotJsonStructureUnitTest {

  private ObjectMapper objectMapper = null;
  private File dataFile = null;
  private URL dataResource = null;
  private com.synchronoss.querybuilder.model.pivot.SqlBuilderPivot sqlBuilderNodePivot =  null;

  @Before
  public void beforeTest() {
    objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    ClassLoader classLoader = getClass().getClassLoader();
    dataResource = classLoader.getResource("data/pivot_type_data.json");
    dataFile = new File(dataResource.getPath());
    JsonNode objectNode =null;
    try {
      objectNode = objectMapper.readTree(dataFile);
      String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
      JsonNode objectNode1 = objectMapper.readTree(json);
      sqlBuilderNodePivot = objectMapper.treeToValue(objectNode1, com.synchronoss.querybuilder.model.pivot.SqlBuilderPivot.class);
      com.synchronoss.querybuilder.model.pivot.SqlBuilder sqlBuilderNode = sqlBuilderNodePivot.getSqlBuilder();
      Assert.assertNotNull(sqlBuilderNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    }

  }

  @Test
  public void checkMandatoryFieldsForPivotQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getBooleanCriteria());
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getDataFields());
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getFilters());
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getSorts().size());
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getRowFields());
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getColumnFields());
  }
  
  @Test
  public void checkMinimumFieldsWithValuesForPivotQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getBooleanCriteria());
    Assert.assertTrue(sqlBuilderNodePivot.getSqlBuilder().getDataFields().size()>0);
  }

  @Test
  public void checkCriterionForPivotQueryBuilderTest()
  {
    Assert.assertTrue("Boolean Criteria in sqlbuilder can be either AND or OR", sqlBuilderNodePivot.getSqlBuilder().getBooleanCriteria().value().equals("AND") 
        || sqlBuilderNodePivot.getSqlBuilder().getBooleanCriteria().value().equals("OR"));
    Assert.assertTrue("Row Fields array cannot be greater than 3 because it's represents 3 axes.", sqlBuilderNodePivot.getSqlBuilder().getRowFields().size()<=5);
    Assert.assertTrue("Column Fields array cannot be greater than 3 because it's represents 3 axes.", sqlBuilderNodePivot.getSqlBuilder().getColumnFields().size()<=5);
    Assert.assertTrue("Data Fields array cannot be greater than 3 because it's represents 3 axes.", sqlBuilderNodePivot.getSqlBuilder().getDataFields().size()<=5);

  }

  @Test
  public void checkFilterCriterionTypeForPivotQueryBuilderTest()
  {
    Assert.assertTrue("Boolean Criteria in sqlbuilder can be either AND or OR", sqlBuilderNodePivot.getSqlBuilder().getBooleanCriteria().value().equals("AND") 
        || sqlBuilderNodePivot.getSqlBuilder().getBooleanCriteria().value().equals("OR"));
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getFilters());
    Assert.assertTrue(!sqlBuilderNodePivot.getSqlBuilder().getFilters().isEmpty());
    Assert.assertTrue((sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("integer"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("string"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("double"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("long"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("date"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("timestamp")));
  }
  
  @Test
  public void checkAggregateFunctionSupportedForChartQueryBuilderTest(){
    Assert.assertTrue(!sqlBuilderNodePivot.getSqlBuilder().getDataFields().isEmpty());
    Assert.assertTrue(sqlBuilderNodePivot.getSqlBuilder().getDataFields().size()<=3);
    Assert.assertTrue(
       (sqlBuilderNodePivot.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("sum"))
    || (sqlBuilderNodePivot.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("max"))
    || (sqlBuilderNodePivot.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("min")) 
    || (sqlBuilderNodePivot.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("count"))
    || (sqlBuilderNodePivot.getSqlBuilder().getDataFields().get(0).getAggregate().value().equals("avg")));
  }
  
  @Test
  public void checkSortCriterionTypeForPivotQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodePivot.getSqlBuilder().getSorts());
    Assert.assertTrue(!sqlBuilderNodePivot.getSqlBuilder().getSorts().isEmpty());
    Assert.assertTrue((sqlBuilderNodePivot.getSqlBuilder().getSorts().get(0).getType().value().equals("integer"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("string"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("double"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("long"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("date"))
    ||(sqlBuilderNodePivot.getSqlBuilder().getFilters().get(0).getType().value().equals("timestamp")));
    
    Assert.assertTrue((sqlBuilderNodePivot.getSqlBuilder().getSorts().get(0).getOrder().name().equals(com.synchronoss.querybuilder.model.pivot.Sort.Order.ASC.name()))
        ||((sqlBuilderNodePivot.getSqlBuilder().getSorts().get(0).getOrder().name().equals(com.synchronoss.querybuilder.model.pivot.Sort.Order.DESC.name()))));
  }

  
}


