package com.synchronoss.querybuilder;

import java.io.IOException;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class SAWElasticSearchQueryBuilder {

  public static org.apache.logging.log4j.Logger logger = ESLoggerFactory.getLogger(SAWElasticSearchQueryBuilder.class);
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
              .buildQuery() : new SAWChartTypeElasticSearchQueryBuilder(jsonString).buildQuery();
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
    logger.info("JSONString", jsonString);
    logger.info("EntityType : ", type.toString());
    SearchSourceBuilder query = null;
    try {
      assert (type.find(type) == null);
      assert (jsonString == null || jsonString.equals(""));

      query =
          type.equals(EntityType.CHART) ? new SAWChartTypeElasticSearchQueryBuilder(jsonString)
              .getSearchSourceBuilder() : new SAWChartTypeElasticSearchQueryBuilder(jsonString)
              .getSearchSourceBuilder();
    } catch (IllegalStateException | IOException exception) {
      throw new AssertionError("Type not supported :" + exception.getMessage());
    }
    return query;
  }


}
