package com.synchronoss.querybuilder;

import java.io.IOException;

import org.elasticsearch.search.builder.SearchSourceBuilder;

public class SAWElasticSearchQueryBuilder {

  
  /**
   * This method will generate the Elastic Search Query based<br/>
   * on the {@link EntityType}
   * 
   * @param type
   * @param jsonString
   * @return query
   * @throws AssertionError
   */
  public String getQuery(EntityType type, String jsonString) throws AssertionError {
    String query = null;
    try {
      assert (type.find(type) == null);
      assert (jsonString == null || jsonString.equals(""));

      query =
          type.equals(EntityType.CHART) ? new SAWChartTypeElasticSearchQueryBuilder(jsonString)
              .buildQuery() : new SAWPivotTypeElasticSearchQueryBuilder(jsonString).buildQuery();
    } catch (IllegalStateException | IOException exception) {
      throw new AssertionError("Type not supported :" + exception.getMessage());
    }
    return query;
  }

  /**
   * This method will generate the Elastic Search Query based<br/>
   * on the {@link EntityType}
   * 
   * @param type
   * @param jsonString
   * @return query
   * @throws AssertionError
   */
  public SearchSourceBuilder getSearchSourceBuilder(EntityType type, String jsonString)
      throws AssertionError {
    SearchSourceBuilder query = null;
    try {
      assert (type.find(type) == null);
      assert (jsonString == null || jsonString.equals(""));

      query =
          type.equals(EntityType.CHART) ? new SAWChartTypeElasticSearchQueryBuilder(jsonString)
              .getSearchSourceBuilder() : new SAWPivotTypeElasticSearchQueryBuilder(jsonString)
              .getSearchSourceBuilder();
    } catch (IllegalStateException | IOException exception) {
      throw new AssertionError("Type not supported :" + exception.getMessage());
    }
    return query;
  }


}
