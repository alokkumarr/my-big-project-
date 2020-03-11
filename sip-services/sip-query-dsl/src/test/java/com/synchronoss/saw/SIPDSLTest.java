package com.synchronoss.saw;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.saw.dl.spark.DLSparkQueryBuilder;
import com.synchronoss.saw.es.ESResponseParser;
import com.synchronoss.saw.es.ElasticSearchQueryBuilder;
import com.synchronoss.saw.es.SIPAggregationBuilder;
import com.synchronoss.saw.model.Aggregate;
import com.synchronoss.saw.model.Artifact;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.Field.Type;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.Model.Operator;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.SipQuery.BooleanCriteria;
import com.synchronoss.saw.model.Sort;
import com.synchronoss.saw.model.Sort.Order;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;

/** Unit test . */
public class SIPDSLTest {
  private static final String esFileName = "sample.json";
  private static final String dlFileName = "sample_dl.json";

  /** Query Builder Tests with aggregation. */
  @Test
  public void testQueryWithAggregationBuilder() throws IOException, ProcessingException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("sample.json").getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    SipDskAttribute sipDskAttribute = new SipDskAttribute();
    String query =
        elasticSearchQueryBuilder
            .buildDataQuery(sipdsl.getSipQuery(), 100, sipDskAttribute);
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
    String[] groupByFields = new String[2];
    groupByFields[1] = "date";
    groupByFields[0] = "string";

    ESResponseParser esResponseParser = new ESResponseParser(aggregationFields, groupByFields);
    List<Object> result = esResponseParser.parseData(jsonNode);
    Assert.assertTrue(result.size() > 0);
  }

  /** Query Builder Test for building Sort */
  @Test
  public void testBuildQuery() throws IOException {
    SIPDSL sipdsl = getSipDsl(esFileName);
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder =
        elasticSearchQueryBuilder.buildSortQuery(sipdsl.getSipQuery(), searchSourceBuilder);
    Assert.assertNotNull(searchSourceBuilder);
  }

  /** Query Builder Test for building Filter with boolean */
  @Test
  public void testBuildFilter() throws IOException {
    SIPDSL sipdsl = getSipDsl(esFileName);
    ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder boolQueryBuilder1 = null;
    if (sipdsl.getSipQuery().getBooleanCriteria() != null) {
      List<Filter> filters = sipdsl.getSipQuery().getFilters();
      List<QueryBuilder> builder = new ArrayList<QueryBuilder>();

      boolQueryBuilder1 =
          elasticSearchQueryBuilder.buildFilterQuery(
              elasticSearchQueryBuilder.buildNestedFilter(
                  filters, sipdsl.getSipQuery().getBooleanCriteria()));
      searchSourceBuilder.query(boolQueryBuilder1);
    }
    Assert.assertNotNull(boolQueryBuilder1);
    Assert.assertNotNull(searchSourceBuilder);
  }

  public SIPDSL getSipDsl(String fileName) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
    return sipdsl;
  }

  public static SIPDSL testUtil() {
    List<Field> fieldList = new ArrayList<>();

    Field field = new Field();
    field.setColumnName("string.keyword");
    field.setType(Type.STRING);
    fieldList.add(field);

    field = new Field();
    field.setColumnName("integer");
    field.setType(Type.INTEGER);
    fieldList.add(field);

    Artifact artifact = new Artifact();
    List<Artifact> artifactList = new ArrayList<>();

    artifact.setArtifactsName("SALES");
    artifact.setFields(fieldList);
    artifactList.add(artifact);

    SipQuery sipQuery = new SipQuery();
    sipQuery.setArtifacts(artifactList);
    sipQuery.setBooleanCriteria(BooleanCriteria.AND);
    sipQuery.setFilters(new ArrayList<Filter>());
    sipQuery.setSorts(new ArrayList<Sort>());

    SIPDSL sipdsl = new SIPDSL();
    sipdsl.setSipQuery(sipQuery);

    return sipdsl;
  }

  @Test
  public void testSelectWithoutAgg() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();
    String queryWithoutFilters = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String assertSelect = "SELECT SALES.string, SALES.integer FROM SALES";
    Assert.assertEquals(queryWithoutFilters, assertSelect);
  }

  @Test
  public void testSelectWithOneAgg() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    List<Field> fieldList = sipdsl.getSipQuery().getArtifacts().get(0).getFields();

    Field field = new Field();
    field.setColumnName("double");
    field.setType(Type.DOUBLE);
    field.setAggregate(Aggregate.SUM);
    field.setDataField("double");
    fieldList.add(field);

    sipdsl.getSipQuery().getArtifacts().get(0).setFields(fieldList);
    String assertForOneAgg = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String selectQuery =
        "SELECT SALES.string, SALES.integer, sum(SALES.double) FROM SALES GROUP BY SALES.string, SALES.integer";
    Assert.assertEquals(selectQuery, assertForOneAgg);
  }

  @Test
  public void testSort() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    Sort sort = new Sort();
    sort.setArtifactsName("SALES");
    sort.setColumnName("integer");
    sort.setOrder(Order.DESC);
    sort.setType(Sort.Type.INTEGER);
    sipdsl.getSipQuery().setSorts(Collections.singletonList(sort));

    String assertForOneSort = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String sortQuery = "SELECT SALES.string, SALES.integer FROM SALES ORDER BY SALES.integer desc";
    Assert.assertEquals(sortQuery, assertForOneSort);
  }

  @Test
  public void testSortWithAgg() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    Sort sort = new Sort();
    sort.setArtifactsName("SALES");
    sort.setColumnName("integer");
    sort.setOrder(Order.DESC);
    sort.setType(Sort.Type.INTEGER);
    sort.setAggregate(Aggregate.SUM);
    sipdsl.getSipQuery().setSorts(Collections.singletonList(sort));

    String assertForAggSort = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String sortQuery =
        "SELECT SALES.string, SALES.integer FROM SALES ORDER BY sum(SALES.integer) desc";
    Assert.assertEquals(sortQuery, assertForAggSort);
  }

  @Test
  public void testPercentage() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    List<Field> fieldList = sipdsl.getSipQuery().getArtifacts().get(0).getFields();

    Field field = new Field();
    field.setColumnName("double");
    field.setType(Type.DOUBLE);
    field.setAggregate(Aggregate.SUM);
    field.setDataField("double");
    field.setAggregate(Aggregate.PERCENTAGE);
    fieldList.add(field);

    sipdsl.getSipQuery().getArtifacts().get(0).setFields(fieldList);
    String assertForAggSort = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String percentQuery =
        "SELECT SALES.string, SALES.integer, (SALES.double*100)/(Select sum(SALES.double) FROM SALES) as `percentage(double)` FROM SALES GROUP BY SALES.string, SALES.integer, SALES.double";
    Assert.assertEquals(percentQuery, assertForAggSort);
  }

  @Test
  public void testPercentageWithAlias() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    List<Field> fieldList = sipdsl.getSipQuery().getArtifacts().get(0).getFields();

    Field field = new Field();
    field.setColumnName("double");
    field.setType(Type.DOUBLE);
    field.setAggregate(Aggregate.SUM);
    field.setDataField("double");
    field.setAggregate(Aggregate.PERCENTAGE);
    field.setAlias("DOUBLE_PERCENTAGE");
    fieldList.add(field);

    sipdsl.getSipQuery().getArtifacts().get(0).setFields(fieldList);
    String assertForAggSort = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String percentQuery =
        "SELECT SALES.string, SALES.integer, (SALES.double*100)/(Select sum(SALES.double) FROM SALES) as `DOUBLE_PERCENTAGE` FROM SALES GROUP BY SALES.string, SALES.integer, SALES.double";
    Assert.assertEquals(percentQuery, assertForAggSort);
  }

  @Test
  public void testDistinctCount() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    List<Field> fieldList = sipdsl.getSipQuery().getArtifacts().get(0).getFields();

    Field field = new Field();
    field.setColumnName("double");
    field.setType(Type.DOUBLE);
    field.setAggregate(Aggregate.DISTINCTCOUNT);
    fieldList.add(field);

    sipdsl.getSipQuery().getArtifacts().get(0).setFields(fieldList);
    String assertForAggSort = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String percentQuery =
        "SELECT SALES.string, SALES.integer, count(distinct SALES.double) as `distinctCount(double)` FROM SALES GROUP BY SALES.string, SALES.integer";
    Assert.assertEquals(percentQuery, assertForAggSort);
  }

    @Test
    public void testDistinctCountWithAlias() {
        SIPDSL sipdsl = testUtil();
        DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

        List<Field> fieldList = sipdsl.getSipQuery().getArtifacts().get(0).getFields();

        Field field = new Field();
        field.setColumnName("double");
        field.setType(Type.DOUBLE);
        field.setAggregate(Aggregate.DISTINCTCOUNT);
        field.setAlias("DISTINCT_COUNT_DOUBLE");
        fieldList.add(field);

        sipdsl.getSipQuery().getArtifacts().get(0).setFields(fieldList);
        String assertForAggSort = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
        String percentQuery =
            "SELECT SALES.string, SALES.integer, count(distinct SALES.double) as `DISTINCT_COUNT_DOUBLE` FROM SALES GROUP BY SALES.string, SALES.integer";
        Assert.assertEquals(percentQuery, assertForAggSort);
    }

  @Test
  public void testFilter() {
    SIPDSL sipdsl = testUtil();
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    Model model = new Model();
    model.setOperator(Operator.GTE);
    model.setValue(Double.valueOf(1));

    List<Filter> filterList = sipdsl.getSipQuery().getFilters();
    Filter oldFilter1 = new Filter();
    oldFilter1.setArtifactsName("SALES");
    oldFilter1.setColumnName("integer");
    oldFilter1.setIsGlobalFilter(false);
    oldFilter1.setType(Filter.Type.INTEGER);
    model = new Model();
    model.setOperator(Operator.GTE);
    model.setValue((double) 1);
    oldFilter1.setModel(model);
    filterList.add(oldFilter1);

    Filter filter = new Filter();
    Filter filter1 = new Filter();
    filter1.setArtifactsName("SALES");
    filter1.setColumnName("double");
    filter1.setIsGlobalFilter(false);
    filter1.setType(Filter.Type.DOUBLE);
    filter1.setModel(model);
    filter.setBooleanCriteria(BooleanCriteria.OR);
    Filter filter2 = new Filter();
    filter2.setArtifactsName("SALES");
    filter2.setColumnName("string");
    filter2.setIsGlobalFilter(false);
    filter2.setType(Filter.Type.STRING);
    model = new Model();
    model.setOperator(Operator.ISIN);
    model.setModelValues(Collections.singletonList("string 123"));
    filter2.setModel(model);
    List<Filter> filterSubList = new ArrayList<>();
    filterSubList.add(filter1);
    filterSubList.add(filter2);
    filter.setFilters(filterSubList);
    filterList.add(filter);

    sipdsl.getSipQuery().setFilters(filterList);

    String assertQuerytFilter = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String queryWithFilter =
        "SELECT SALES.string, SALES.integer FROM SALES WHERE ((SALES.integer >= 1.0) AND SALES.double >= 1.0 OR SALES.string IN ('string 123') )";
    Assert.assertEquals(queryWithFilter, assertQuerytFilter.trim());
  }

  @Test
  public void testDlSelect() throws IOException {
    SIPDSL sipdsl = getSipDsl(dlFileName);
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    String query = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String assertion =
        "SELECT SALES.string AS `String`, avg(SALES.integer), avg(SALES.long), SALES.date, avg(SALES.double), count(distinct SALES.float) as `distinctCount(float)` FROM SALES INNER JOIN PRODUCT ON SALES.string = PRODUCT.string_2 WHERE ((SALES.long = 1000.0) AND SALES.Double = 2000.0)  GROUP BY SALES.string, SALES.date ORDER BY sum(SALES.long) asc, avg(SALES.double) desc";
    Assert.assertEquals(assertion, query);

    sipdsl.getSipQuery().setFilters(new ArrayList<Filter>());
    String queryWithoutFilters = dlSparkQueryBuilder.buildDataQuery(sipdsl.getSipQuery());
    String assertionQueryWithoutFilters =
        "SELECT SALES.string AS `String`, avg(SALES.integer), avg(SALES.long), SALES.date, avg(SALES.double), count(distinct SALES.float) as `distinctCount(float)` FROM SALES INNER JOIN PRODUCT ON SALES.string = PRODUCT.string_2 GROUP BY SALES.string, SALES.date ORDER BY sum(SALES.long) asc, avg(SALES.double) desc";
    Assert.assertEquals(queryWithoutFilters, assertionQueryWithoutFilters);
  }

  @Test
  public void testDlWithDSK() throws IOException {
    SIPDSL sipdsl = getSipDsl(dlFileName);
    DLSparkQueryBuilder dlSparkQueryBuilder = new DLSparkQueryBuilder();

    SipDskAttribute sipDskAttribute = new SipDskAttribute();
    sipDskAttribute.setBooleanCriteria(com.synchronoss.bda.sip.dsk.BooleanCriteria.AND);

    SipDskAttribute subAtt = new SipDskAttribute();
    subAtt.setColumnName("string");
    subAtt.setBooleanCriteria(com.synchronoss.bda.sip.dsk.BooleanCriteria.AND);
    com.synchronoss.bda.sip.dsk.Model model = new com.synchronoss.bda.sip.dsk.Model();
    model.setOperator(com.synchronoss.bda.sip.dsk.Operator.ISIN);
    List<String> modelValues = new ArrayList<>();
    modelValues.add("String 1");
    modelValues.add("str");
    model.setValues(modelValues);
    subAtt.setModel(model);
    List<SipDskAttribute> dskAttributes = new ArrayList<>();
    dskAttributes.add(subAtt);

    subAtt = new SipDskAttribute();
    subAtt.setColumnName("string");
    subAtt.setBooleanCriteria(com.synchronoss.bda.sip.dsk.BooleanCriteria.AND);
    model = new com.synchronoss.bda.sip.dsk.Model();
    model.setOperator(com.synchronoss.bda.sip.dsk.Operator.ISIN);
    modelValues = new ArrayList<>();
    modelValues.add("String 123");
    modelValues.add("string 456");
    model.setValues(modelValues);
    subAtt.setModel(model);
    dskAttributes.add(subAtt);
    sipDskAttribute.setBooleanQuery(dskAttributes);

    String query = dlSparkQueryBuilder.buildDskQuery(sipdsl.getSipQuery(), sipDskAttribute);
    String assertQuery = "SELECT SALES.string AS `String`, avg(SALES.integer), avg(SALES.long),"
        + " SALES.date, avg(SALES.double), count(distinct SALES.float) as `distinctCount(float)`"
        + " FROM SALES"
        + " INNER JOIN PRODUCT"
        + " ON SALES.string = PRODUCT.string_2"
        + " WHERE (((SALES.long = 1000.0) AND SALES.Double = 2000.0))"
        + "  AND  (upper(SALES.string) IN (upper('String 1'), upper('str') )"
        + " AND upper(SALES.string) IN (upper('String 123'), upper('string 456') ))"
        + " GROUP BY SALES.string, SALES.date"
        + " ORDER BY sum(SALES.long) asc, avg(SALES.double) desc";
    Assert.assertEquals(query,assertQuery);
  }
}
