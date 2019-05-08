package com.synchronoss.saw;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.es.ESResponseParser;
import com.synchronoss.saw.es.ElasticSearchQueryBuilder;
import com.synchronoss.saw.es.SIPAggregationBuilder;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.SIPDSL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;

/** Unit test . */
public class SIPDSLTest {

  /** Query Builder Tests with aggregation. */
  @Test
  public void testQueryWithAggregationBuilder() throws IOException, ProcessingException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("sample.json").getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
    DataSecurityKey dataSecurityKey = null;
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    String query =
        elasticSearchQueryBuilder.buildDataQuery(sipdsl.getSipQuery(), 100, dataSecurityKey);
    Assert.assertNotNull(query);
  }

  /** Query Builder Tests for parsing the data : */
  @Test
  public void testResultParser() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("sample.json").getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
    List<Field> dataFields = sipdsl.getSipQuery().getArtifacts().get(0).getFields();
    List<Field> aggregationFields = SIPAggregationBuilder.getAggregationField(dataFields);
    JsonNode jsonNode =
        objectMapper.readTree(new File(classLoader.getResource("response_sample.json").getPath()));
    ESResponseParser esResponseParser = new ESResponseParser(dataFields, aggregationFields);
    List<Object> result = esResponseParser.parseData(jsonNode);
    Assert.assertTrue(result.size() > 0);
  }

  /** Query Builder Test for building Sort */
  @Test
  public void testBuildQuery() throws IOException {
    SIPDSL sipdsl = getSipDsl();
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder =
        elasticSearchQueryBuilder.buildSortQuery(sipdsl.getSipQuery(), searchSourceBuilder);
    Assert.assertNotNull(searchSourceBuilder);
  }

  /** Query Builder Test for building Filter with boolean */
  @Test
  public void testBuildFilter() throws IOException {
    SIPDSL sipdsl = getSipDsl();
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder boolQueryBuilder1 = null;
    if (sipdsl.getSipQuery().getBooleanCriteria() != null) {
      List<Filter> filters = sipdsl.getSipQuery().getFilters();
      List<QueryBuilder> builder = new ArrayList<QueryBuilder>();

      builder = elasticSearchQueryBuilder.buildFilters(filters, builder);
      boolQueryBuilder1 =
          elasticSearchQueryBuilder.buildBooleanQuery(sipdsl.getSipQuery(), builder);
      searchSourceBuilder.query(boolQueryBuilder1);
    }
    Assert.assertNotNull(boolQueryBuilder1);
    Assert.assertNotNull(searchSourceBuilder);
  }

  public SIPDSL getSipDsl() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("sample.json").getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
    return sipdsl;
  }
}
