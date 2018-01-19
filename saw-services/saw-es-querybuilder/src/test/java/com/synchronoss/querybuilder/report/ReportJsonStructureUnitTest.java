package com.synchronoss.querybuilder.report;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.querybuilder.model.report.SqlBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.synchronoss.querybuilder.model.report.SqlBuilderReport;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReportJsonStructureUnitTest {

  private ObjectMapper objectMapper = null;
  private File dataFile = null;
  private URL dataResource = null;
  private SqlBuilderReport sqlBuilderNodeReport =  null;

  @Before
  public void beforeTest() {
    objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    ClassLoader classLoader = getClass().getClassLoader();
    dataResource = classLoader.getResource("data/report_type_data.json");
    dataFile = new File(dataResource.getPath());
    JsonNode objectNode =null;
    try {
      objectNode = objectMapper.readTree(dataFile);
      String json = "{ \"sqlBuilder\" :" + objectNode.get("sqlBuilder").toString() + "}";
      JsonNode objectNode1 = objectMapper.readTree(json);
      sqlBuilderNodeReport = objectMapper.treeToValue(objectNode1, SqlBuilderReport.class);
      SqlBuilder sqlBuilderNode = sqlBuilderNodeReport.getSqlBuilder();
      Assert.assertNotNull(sqlBuilderNode);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    }

  }

  @Test
  public void checkMandatoryFieldsForReportQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getBooleanCriteria());
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getDataFields());
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getFilters());
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getSorts().size());
  }
  
  @Test
  public void checkMinimumFieldsWithValuesForReportQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getBooleanCriteria());
    Assert.assertTrue(sqlBuilderNodeReport.getSqlBuilder().getDataFields().size()>0);
  }

  @Test
  public void checkCriterionForReportQueryBuilderTest()
  {
    Assert.assertTrue("Boolean Criteria in sqlbuilder can be either AND or OR", sqlBuilderNodeReport.getSqlBuilder().getBooleanCriteria().value().equals("AND") 
        || sqlBuilderNodeReport.getSqlBuilder().getBooleanCriteria().value().equals("OR")) ;

  }

  @Test
  public void checkFilterCriterionTypeForReportQueryBuilderTest()
  {
    Assert.assertTrue("Boolean Criteria in sqlbuilder can be either AND or OR", sqlBuilderNodeReport.getSqlBuilder().getBooleanCriteria().value().equals("AND") 
        || sqlBuilderNodeReport.getSqlBuilder().getBooleanCriteria().value().equals("OR"));
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getFilters());
    Assert.assertTrue(!sqlBuilderNodeReport.getSqlBuilder().getFilters().isEmpty());
    Assert.assertTrue((sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("integer"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("string"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("double"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("long"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("date"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("timestamp")));
  }
  
  @Test
  public void checkSortCriterionTypeForReportQueryBuilderTest()
  {
    Assert.assertNotNull(sqlBuilderNodeReport.getSqlBuilder().getSorts());
    Assert.assertTrue(!sqlBuilderNodeReport.getSqlBuilder().getSorts().isEmpty());
    Assert.assertTrue((sqlBuilderNodeReport.getSqlBuilder().getSorts().get(0).getType().value().equals("integer"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("string"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("double"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("long"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("date"))
    ||(sqlBuilderNodeReport.getSqlBuilder().getFilters().get(0).getType().value().equals("timestamp")));
    
    Assert.assertTrue((sqlBuilderNodeReport.getSqlBuilder().getSorts().get(0).getOrder().name().equals(com.synchronoss.querybuilder.model.report.Sort.Order.ASC.name()))
        ||((sqlBuilderNodeReport.getSqlBuilder().getSorts().get(0).getOrder().name().equals(com.synchronoss.querybuilder.model.report.Sort.Order.DESC.name()))));
  }

  
}


